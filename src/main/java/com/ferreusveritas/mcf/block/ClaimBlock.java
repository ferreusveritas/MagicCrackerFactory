package com.ferreusveritas.mcf.block;

import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;

public class ClaimBlock extends Block {

    public ClaimBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE).strength(8.0F, 50.0F));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide && placer instanceof Player player) {
            RemoteReceiverPeripheral.broadcastClaimEvents(player, pos, true);
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (!level.isClientSide()) {
            RemoteReceiverPeripheral.broadcastClaimEvents(null, pos, false);
        }
        super.destroy(level, pos, state);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 15;
    }

}
