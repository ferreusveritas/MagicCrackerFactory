package com.ferreusveritas.mcf.block;

import com.ferreusveritas.mcf.network.Networking;
import com.ferreusveritas.mcf.network.ServerBoundTouchMapMessage;
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

public class TouchButtonBlock extends AbstractButtonBlock {

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

    public TouchButtonBlock() {
        super(false, Properties.of(Material.HEAVY_METAL, MaterialColor.METAL).strength(3.0F, 10.0F).randomTicks());
        registerDefaultState(getStateDefinition().any().setValue(FACE, AttachFace.WALL).setValue(FACING, Direction.NORTH).setValue(AbstractButtonBlock.POWERED, Boolean.FALSE));
    }

    @Override
    public void press(BlockState state, World world, BlockPos pos) {
        super.press(state, world, pos);
    }

    public void touchPress(World world, BlockPos pos, BlockState state) {
        if (!state.getValue(POWERED)) {
            this.press(state, world, pos);
            this.playSound(null, world, pos, true);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hitResult) {
        if (state.getValue(POWERED)) {
            return ActionResultType.CONSUME;
        } else {
            this.press(state, world, pos);
            this.playSound(player, world, pos, true);
            if (world.isClientSide) {
                Networking.sendToServer(new ServerBoundTouchMapMessage(hitResult.getLocation(), pos, hitResult.getDirection()));
            }
            return ActionResultType.sidedSuccess(world.isClientSide);
        }
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
    protected int getPressDuration() {
        return 10;
    }

    @Override
    protected SoundEvent getSound(boolean on) {
        return on ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF;
    }

}