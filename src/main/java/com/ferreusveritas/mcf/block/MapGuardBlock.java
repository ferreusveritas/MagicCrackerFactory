package com.ferreusveritas.mcf.block;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.entity.ItemDisplayEntity;
import com.ferreusveritas.mcf.network.Networking;
import com.ferreusveritas.mcf.network.ServerBoundTouchMapMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

import static net.minecraft.core.Direction.Axis.*;

@SuppressWarnings("deprecation")
public class MapGuardBlock extends Block {

    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public MapGuardBlock() {
        super(Properties.of(Material.GLASS).strength(-1.0F, 3600000.0F).noOcclusion().lightLevel(state -> state.getValue(LIT) ? 15 : 0));
        registerDefaultState(this.getStateDefinition().any().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        if (reader instanceof EntityGetter level) {
            List<ItemFrame> itemFrames = level.getEntitiesOfClass(ItemFrame.class, new AABB(pos), e -> true);
            if (!itemFrames.isEmpty()) {
                BlockPos offPos = new BlockPos(-pos.getX(), -pos.getY(), -pos.getZ());

                VoxelShape union = Shapes.empty();
                for (ItemFrame frame : itemFrames) {
                    ItemStack displayedItem = frame.getItem();
                    if (displayedItem.getItem() instanceof MapItem) {
                        union = Shapes.or(union, Shapes.create(frame.getBoundingBox().move(offPos)));
                        union = Shapes.box(
                                union.min(X) == 0.125 ? 0 : union.min(X),
                                union.min(Y) == 0.125 ? 0 : union.min(Y),
                                union.min(Z) == 0.125 ? 0 : union.min(Z),
                                union.max(X) == 0.875 ? 1 : union.max(X),
                                union.max(Y) == 0.875 ? 1 : union.max(Y),
                                union.max(Z) == 0.875 ? 1 : union.max(Z)
                        );
                    } else {
                        union = Shapes.or(union, Shapes.create(frame.getBoundingBox().move(offPos)));
                    }

                }

                return union;
            }

            List<ItemDisplayEntity> itemDisplays = level.getEntitiesOfClass(ItemDisplayEntity.class, new AABB(pos), e -> true);
            if (!itemDisplays.isEmpty()) {
                BlockPos offPos = new BlockPos(-pos.getX(), -pos.getY(), -pos.getZ());

                VoxelShape union = Shapes.empty();
                for (ItemDisplayEntity display : itemDisplays) {
                    union = Shapes.or(union, Shapes.create(display.getBoundingBox().move(offPos)));
                }

                return union;
            }

        }

        return Shapes.block();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (heldItem.getItem() != Registry.UNIVERSAL_REMOTE.get()) {
            if (level.isClientSide) {
                Networking.sendToServer(new ServerBoundTouchMapMessage(new Vec3(hit.getLocation().y, hit.getLocation().y, hit.getLocation().z), pos, hit.getDirection()));
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(state.getValue(LIT) ? Registry.LIT_MAP_GUARD_ITEM.get() : Registry.MAP_GUARD_ITEM.get());
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

}
