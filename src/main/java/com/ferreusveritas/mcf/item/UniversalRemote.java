package com.ferreusveritas.mcf.item;

import com.ferreusveritas.mcf.network.Networking;
import com.ferreusveritas.mcf.network.ServerBoundRemoteClickMessage;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
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
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        PlayerEntity player = context.getPlayer();

        if (world.isClientSide) {
            ItemStack remoteStack = player.getItemInHand(context.getHand());

            double range = getRange(remoteStack);

            Vector3d hitPos = context.getClickLocation();
            BlockPos blockPos = context.getClickedPos();
            Direction sideHit = context.getClickedFace();
            sendPacketToServer(hitPos, blockPos, sideHit);
        }

        return ActionResultType.SUCCESS;
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