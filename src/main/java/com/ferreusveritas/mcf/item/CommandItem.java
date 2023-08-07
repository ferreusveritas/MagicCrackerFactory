package com.ferreusveritas.mcf.item;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class CommandItem extends Item implements ColoredItem {

    public CommandItem(Properties properties) {
        super(properties);
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (allowdedIn(tab)) {
            ItemStack stack = new ItemStack(this);
            // Add test command to command items in creative menu
            stack.addTagElement("command", StringTag.valueOf("test"));
            items.add(stack);
        }
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            CompoundTag tag = getTag(stack);
            int color = 0xFF00FFFF;// 0xAARRGGBB

            if (tag.contains("color", Tag.TAG_STRING)) {
                try {
                    color = Color.decode(tag.getString("color")).getRGB();
                } catch (NumberFormatException e) {
                    tag.remove("color");
                }
            }

            return color;
        }

        return 0xFFFFFFFF;//White
    }

    public CommandItem setColor(ItemStack stack, String colStr) {
        CompoundTag tag = getTag(stack);
        tag.putString("color", colStr);
        stack.setTag(tag); // TODO: Is this really necessary?
        return this;
    }

    public CompoundTag getTag(ItemStack stack) {
        return stack.hasTag() ? stack.getTag() : new CompoundTag();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getName(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        if (tag.contains("label", Tag.TAG_STRING)) {
            return new TextComponent(tag.getString("label"));
        }
        return super.getName(stack);
    }

    public String getCommand(ItemStack commandPotion) {
        CompoundTag tag = getTag(commandPotion);
        if (tag.contains("command", Tag.TAG_STRING)) {
            return tag.getString("command");
        }
        return "";
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = getTag(stack);
        if (tag.contains("info", Tag.TAG_STRING)) {
            String info = tag.getString("info");
            tooltip.add(new TextComponent(info));
        }
    }

}
