package com.ferreusveritas.mcf.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class RemoteButtonBlock extends AbstractButtonBlock implements ActivatableRemote {

    private static final VoxelShape CEILING_AABB = Block.box(5.0D, 14.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    private static final VoxelShape FLOOR_AABB = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D);
    private static final VoxelShape NORTH_AABB = Block.box(5.0D, 5.0D, 14.0D, 11.0D, 11.0D, 16.0D);
    private static final VoxelShape SOUTH_AABB = Block.box(5.0D, 5.0D, 0.0D, 11.0D, 11.0D, 2.0D);
    private static final VoxelShape WEST_AABB = Block.box(14.0D, 5.0D, 5.0D, 16.0D, 11.0D, 11.0D);
    private static final VoxelShape EAST_AABB = Block.box(0.0D, 5.0D, 5.0D, 2.0D, 11.0D, 11.0D);
    private static final VoxelShape PRESSED_CEILING_AABB = Block.box(5.0D, 15.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    private static final VoxelShape PRESSED_FLOOR_AABB = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 1.0D, 11.0D);
    private static final VoxelShape PRESSED_NORTH_AABB = Block.box(5.0D, 5.0D, 15.0D, 11.0D, 11.0D, 16.0D);
    private static final VoxelShape PRESSED_SOUTH_AABB = Block.box(5.0D, 5.0D, 0.0D, 11.0D, 11.0D, 1.0D);
    private static final VoxelShape PRESSED_WEST_AABB = Block.box(15.0D, 5.0D, 5.0D, 16.0D, 11.0D, 11.0D);
    private static final VoxelShape PRESSED_EAST_AABB = Block.box(0.0D, 5.0D, 5.0D, 1.0D, 11.0D, 11.0D);

    public RemoteButtonBlock() {
        super(false, AbstractBlock.Properties.of(Material.METAL, MaterialColor.LAPIS).strength(3.0F, 10.0F).randomTicks());
        registerDefaultState(getStateDefinition().any().setValue(FACE, AttachFace.WALL).setValue(FACING, Direction.NORTH).setValue(AbstractButtonBlock.POWERED, false));
    }

    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        Direction facing = state.getValue(FACING);
        boolean powered = state.getValue(POWERED);
        switch (state.getValue(FACE)) {
            case FLOOR:
                return powered ? PRESSED_FLOOR_AABB : FLOOR_AABB;
            case WALL:
                switch (facing) {
                    case EAST:
                        return powered ? PRESSED_EAST_AABB : EAST_AABB;
                    case WEST:
                        return powered ? PRESSED_WEST_AABB : WEST_AABB;
                    case SOUTH:
                        return powered ? PRESSED_SOUTH_AABB : SOUTH_AABB;
                    case NORTH:
                    default:
                        return powered ? PRESSED_NORTH_AABB : NORTH_AABB;
                }
            case CEILING:
            default:
                return powered ? PRESSED_CEILING_AABB : CEILING_AABB;
        }
    }

    @Override
    public ActionResultType activate(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockRayTraceResult result) {
        return super.use(state, world, pos, player, Hand.MAIN_HAND, result);
    }

    @Override
    public ActionResultType use(BlockState pState, World pLevel, BlockPos pPos, PlayerEntity pPlayer, Hand pHand, BlockRayTraceResult pHit) {
        return ActionResultType.FAIL; // Fail on normal press.
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