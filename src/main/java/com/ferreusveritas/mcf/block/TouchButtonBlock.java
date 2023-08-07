package com.ferreusveritas.mcf.block;

import com.ferreusveritas.mcf.network.Networking;
import com.ferreusveritas.mcf.network.ServerBoundTouchMapMessage;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TouchButtonBlock extends ButtonBlock {

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
        registerDefaultState(getStateDefinition().any().setValue(FACE, AttachFace.WALL).setValue(FACING, Direction.NORTH).setValue(ButtonBlock.POWERED, Boolean.FALSE));
    }

    @Override
    public void press(BlockState state, Level level, BlockPos pos) {
        super.press(state, level, pos);
    }

    public void touchPress(Level level, BlockPos pos, BlockState state) {
        if (!state.getValue(POWERED)) {
            this.press(state, level, pos);
            this.playSound(null, level, pos, true);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(POWERED)) {
            return InteractionResult.CONSUME;
        } else {
            this.press(state, level, pos);
            this.playSound(player, level, pos, true);
            if (level.isClientSide) {
                Networking.sendToServer(new ServerBoundTouchMapMessage(hit.getLocation(), pos, hit.getDirection()));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
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