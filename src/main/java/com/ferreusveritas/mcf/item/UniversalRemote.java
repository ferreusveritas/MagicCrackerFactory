package com.ferreusveritas.mcf.item;

import com.ferreusveritas.mcf.network.Networking;
import com.ferreusveritas.mcf.network.ServerBoundRemoteClickMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class UniversalRemote extends Item implements ColoredItem {

    public UniversalRemote(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack remoteStack = player.getItemInHand(hand);

        if (world.isClientSide) {
            double range = getRange(remoteStack);

            HitResult hit = player.pick(range, 0, false);
            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = ((BlockHitResult) hit);
                sendPacketToServer(blockHit.getLocation(), blockHit.getBlockPos(), blockHit.getDirection());
            }
        }

        return InteractionResultHolder.success(remoteStack);
    }

    public CompoundTag getTag(ItemStack itemStack) {
        return itemStack.hasTag() ? itemStack.getTag() : new CompoundTag();
    }

    private double getRange(ItemStack remoteStack) {
        CompoundTag tag = getTag(remoteStack);
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

        CompoundTag tag = getTag(itemStack);

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
        CompoundTag tag = getTag(itemStack);
        tag.putString("color", colStr);
        itemStack.setTag(tag);
        return this;
    }

    public String getId(ItemStack remoteStack) {
        CompoundTag tag = getTag(remoteStack);
        return tag.getString("id");
    }

    private void sendPacketToServer(Vec3 hitPos, BlockPos blockPos, Direction sideHit) {
        Networking.sendToServer(new ServerBoundRemoteClickMessage(hitPos, blockPos, sideHit));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String id = getId(stack);
        tooltip.add(new TextComponent("Id: §6" + (id.isEmpty() ? "<none>" : id)));
    }

}