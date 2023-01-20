package com.ferreusveritas.mcf.block;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.entity.ItemDisplayEntity;
import com.ferreusveritas.mcf.network.Networking;
import com.ferreusveritas.mcf.network.ServerBoundTouchMapMessage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AbstractMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEntityReader;
import net.minecraft.world.World;

import java.util.List;

import static net.minecraft.util.Direction.Axis.*;

@SuppressWarnings("deprecation")
public class MapGuardBlock extends Block {

    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public MapGuardBlock() {
        super(Properties.of(Material.GLASS).strength(-1.0F, 3600000.0F).noOcclusion().lightLevel(state -> state.getValue(LIT) ? 15 : 0));
        registerDefaultState(this.getStateDefinition().any().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        if (reader instanceof IEntityReader) {
            IEntityReader world = (IEntityReader) reader;
            List<ItemFrameEntity> itemFrames = world.getEntitiesOfClass(ItemFrameEntity.class, new AxisAlignedBB(pos), null);
            if (!itemFrames.isEmpty()) {
                BlockPos offPos = new BlockPos(-pos.getX(), -pos.getY(), -pos.getZ());

                VoxelShape union = VoxelShapes.empty();
                for (ItemFrameEntity frame : itemFrames) {
                    ItemStack displayedItem = frame.getItem();
                    if (displayedItem.getItem() instanceof AbstractMapItem) {
                        union = VoxelShapes.or(union, VoxelShapes.create(frame.getBoundingBox().move(offPos)));
                        union = VoxelShapes.box(
                                union.min(X) == 0.125 ? 0 : union.min(X),
                                union.min(Y) == 0.125 ? 0 : union.min(Y),
                                union.min(Z) == 0.125 ? 0 : union.min(Z),
                                union.max(X) == 0.875 ? 1 : union.max(X),
                                union.max(Y) == 0.875 ? 1 : union.max(Y),
                                union.max(Z) == 0.875 ? 1 : union.max(Z)
                        );
                    } else {
                        union = VoxelShapes.or(union, VoxelShapes.create(frame.getBoundingBox().move(offPos)));
                    }

                }

                return union;
            }

            List<ItemDisplayEntity> itemDisplays = world.getEntitiesOfClass(ItemDisplayEntity.class, new AxisAlignedBB(pos), null);
            if (!itemDisplays.isEmpty()) {
                BlockPos offPos = new BlockPos(-pos.getX(), -pos.getY(), -pos.getZ());

                VoxelShape union = VoxelShapes.empty();
                for (ItemDisplayEntity display : itemDisplays) {
                    union = VoxelShapes.or(union, VoxelShapes.create(display.getBoundingBox().move(offPos)));
                }

                return union;
            }

        }

        return VoxelShapes.block();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, IBlockReader pLevel, BlockPos pPos, ISelectionContext pContext) {
        return VoxelShapes.empty();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (heldItem.getItem() != Registry.UNIVERSAL_REMOTE.get()) {
            if (world.isClientSide) {
                Networking.sendToServer(new ServerBoundTouchMapMessage(new Vector3d(hit.getLocation().y, hit.getLocation().y, hit.getLocation().z), pos, hit.getDirection()));
            }
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Override
    public ItemStack getCloneItemStack(IBlockReader world, BlockPos pos, BlockState state) {
        return new ItemStack(state.getValue(LIT) ? Registry.LIT_MAP_GUARD_ITEM.get() : Registry.MAP_GUARD_ITEM.get());
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader level, BlockPos pos) {
        return true;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

}
