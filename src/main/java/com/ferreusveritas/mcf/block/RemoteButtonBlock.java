package com.ferreusveritas.mcf.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RemoteButtonBlock extends ButtonBlock implements ActivatableRemote {

    private static final VoxelShape CEILING_AABB = Block.box(5.0D, 14.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    private static final VoxelShape FLOOR_AABB = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D);
    private static final VoxelShape NORTH_AABB = Block.box(5.0D, 5.0D, 14.0D, 11.0D, 11.0D, 16.0D);
    private static final VoxelShape SOUTH_AABB = Block.box(5.0D, 5.0D, 0.0D, 11.0D, 11.0D, 2.0D);
    private static final VoxelShape WEST_AABB = Block.box(14.0D, 5.0D, 5.0D, 16.0D, 11.0D, 11.0D);
    private static final VoxelShape EAST_AABB = Block.box(0.0D, 5.0D, 5.0D, 2.0D, 11.0D, 11.0D);

    public RemoteButtonBlock() {
        super(false, BlockBehaviour.Properties.of(Material.METAL, MaterialColor.LAPIS).strength(3.0F, 10.0F).randomTicks());
        registerDefaultState(getStateDefinition().any().setValue(FACE, AttachFace.WALL).setValue(FACING, Direction.NORTH).setValue(ButtonBlock.POWERED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        switch (state.getValue(FACE)) {
            case FLOOR:
                return FLOOR_AABB;
            case WALL:
                switch (facing) {
                    case EAST:
                        return EAST_AABB;
                    case WEST:
                        return WEST_AABB;
                    case SOUTH:
                        return SOUTH_AABB;
                    case NORTH:
                    default:
                        return NORTH_AABB;
                }
            case CEILING:
            default:
                return CEILING_AABB;
        }
    }

    @Override
    public InteractionResult activate(Level level, BlockPos pos, BlockState state, Player player, BlockHitResult hit) {
        return super.use(state, level, pos, player, InteractionHand.MAIN_HAND, hit);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.FAIL; // Fail on normal press.
    }

    @Override
    protected int getPressDuration() {
        return 10;
    }

    @Override
    protected SoundEvent getSound(boolean on) {
        return on ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF;
    }

}