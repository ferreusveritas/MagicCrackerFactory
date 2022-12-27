package com.ferreusveritas.mcf.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class CommandItem extends Item implements ColoredItem {

    public CommandItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            CompoundNBT tag = getTag(stack);
            int color = 0xFF00FFFF;// 0xAARRGGBB

            if (tag.contains("color", NBT.TAG_STRING)) {
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
        CompoundNBT tag = getTag(stack);
        tag.putString("color", colStr);
        stack.setTag(tag); // TODO: Is this really necessary?
        return this;
    }

    public CompoundNBT getTag(ItemStack stack) {
        return stack.hasTag() ? stack.getTag() : new CompoundNBT();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ITextComponent getName(ItemStack stack) {
        CompoundNBT tag = getTag(stack);
        if (tag.contains("label", NBT.TAG_STRING)) {
            return new StringTextComponent(tag.getString("label"));
        }
        return super.getName(stack);
    }

    public String getCommand(ItemStack commandPotion) {
        CompoundNBT tag = getTag(commandPotion);
        if (tag.contains("command", NBT.TAG_STRING)) {
            return tag.getString("command");
        }
        return "";
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        CompoundNBT tag = getTag(stack);
        if (tag.contains("info", NBT.TAG_STRING)) {
            String info = tag.getString("info");
            tooltip.add(new StringTextComponent(info));
        }
    }

}
