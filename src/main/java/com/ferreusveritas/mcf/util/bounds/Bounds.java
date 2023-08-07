package com.ferreusveritas.mcf.util.bounds;

import com.ferreusveritas.mcf.util.filter.SetEntityFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.Map;

public abstract class Bounds {

    public static Bounds INVALID = new NullBounds();

    private final SetEntityFilter filterSet = new SetEntityFilter();

    public Bounds() {
    }

    public Bounds(CompoundTag tag) {
        filterSet.loadFilters(tag);
    }

    public abstract boolean inBounds(BlockPos pos);

    public abstract String getBoundType();

    public abstract AABB getAABB();

    public CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", getBoundType());
        tag.put("filters", filterSet.saveFilters());
        return tag;
    }

    public Map<String, Object> collectLuaData() {
        Map<String, Object> contents = new HashMap<>();
        contents.put("type", getBoundType());
        contents.put("filters", filterSet.filtersToLuaObject());
        return contents;
    }

    public SetEntityFilter getFilterSet() {
        return filterSet;
    }

}
