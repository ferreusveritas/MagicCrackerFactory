package com.ferreusveritas.mcf.item;

import com.ferreusveritas.mcf.network.Networking;
import com.ferreusveritas.mcf.network.ServerBoundRemoteClickMessage;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class UniversalRemote extends Item implements ColoredItem {

    public UniversalRemote(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack remoteStack = player.getItemInHand(hand);

        if (world.isClientSide) {
            double range = getRange(remoteStack);

            RayTraceResult result = player.pick(range, 0, false);
            if (result.getType() == RayTraceResult.Type.BLOCK) {
                BlockRayTraceResult blockResult = ((BlockRayTraceResult) result);
                sendPacketToServer(blockResult.getLocation(), blockResult.getBlockPos(), blockResult.getDirection());
            }
        }

        return ActionResult.success(remoteStack);
    }

    public CompoundNBT getTag(ItemStack itemStack) {
        return itemStack.hasTag() ? itemStack.getTag() : new CompoundNBT();
    }

    private double getRange(ItemStack remoteStack) {
        CompoundNBT tag = getTag(remoteStack);
        if (tag.contains("range")) {
            return tag.getDouble("range");
        }

        return 16;
    }

    @Override
    public int getColor(ItemStack itemStack, int tintIndex) {

        if (tintIndex != 1) {
            return 0xFFFFFFFF;
        }

        CompoundNBT tag = getTag(itemStack);

        int color = 0x0000FFFF;

        if (tag.contains("color")) {
            try {
                color = Color.decode(tag.getString("color")).getRGB();
            } catch (NumberFormatException e) {
                tag.remove("color");
            }
        }

        return color;
    }

    public UniversalRemote setColor(ItemStack itemStack, String colStr) {
        CompoundNBT tag = getTag(itemStack);
        tag.putString("color", colStr);
        itemStack.setTag(tag);
        return this;
    }

    public String getRemoteId(ItemStack remoteStack) {
        CompoundNBT tag = getTag(remoteStack);
        return tag.getString("id");
    }

    private void sendPacketToServer(Vector3d hitPos, BlockPos blockPos, Direction sideHit) {
        Networking.sendToServer(new ServerBoundRemoteClickMessage(hitPos, blockPos, sideHit));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        String id = getRemoteId(stack);
        tooltip.add(new StringTextComponent("Id: §6" + (id.isEmpty() ? "<none>" : id)));
    }

}