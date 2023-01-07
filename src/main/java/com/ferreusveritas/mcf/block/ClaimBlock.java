package com.ferreusveritas.mcf.block;

import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ClaimBlock extends Block {

    public ClaimBlock() {
        super(AbstractBlock.Properties.of(Material.STONE).strength(8.0F, 50.0F));
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!world.isClientSide && placer instanceof PlayerEntity) {
            RemoteReceiverPeripheral.broadcastClaimEvents((PlayerEntity) placer, pos, true);
        }
    }

    @Override
    public void destroy(IWorld world, BlockPos pos, BlockState state) {
        if (!world.isClientSide()) {
            RemoteReceiverPeripheral.broadcastClaimEvents(null, pos, false);
        }
        super.destroy(world, pos, state);
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return 15;
    }

}
