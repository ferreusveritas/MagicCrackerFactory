package com.ferreusveritas.mcf.util.filter;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

public class SetEntityFilter {

    // Simple EntityFilter type factory registry
    public static Map<String, Function<String, EntityFilter>> filterProviders = new HashMap<>();

    static { // This gives us the ability to add new EntityFilter types in the future
        filterProviders.put("hostile", d -> new HostileEntityFilter());
        filterProviders.put("peaceful", d -> new PeacefulEntityFilter());
        filterProviders.put("entity", d -> new EntityTypeFilter(d));
        filterProviders.put("all", d -> new EntityFilterAll());
        filterProviders.put("none", d -> new NoneEntityFilter());
    }

    public Map<String, EntityFilter> filters = new HashMap<>();

    public static boolean isMobHostile(LivingEntity entity) {
        return entity instanceof IMob;
    }

    public boolean isEntityDenied(LivingEntity entity) {
        return filters.values().parallelStream().anyMatch(f -> f.isEntityDenied(entity));
    }

    public EntityFilter getFilter(String name) {
        return filters.get(name);
    }

    public Set<String> getFilterNames() {
        return filters.keySet();
    }

    public void setFilter(String name, String type, String data) {
        setFilter(name, makeEntityFilter(type, data));
    }

    public void setFilter(String name, EntityFilter filter) {
        filters.put(name, filter);
    }

    public void remFilter(String name) {
        filters.remove(name);
    }

    public CompoundNBT saveFilters() {
        CompoundNBT tag = new CompoundNBT();
        for (Entry<String, EntityFilter> filterEntry : filters.entrySet()) {
            String name = filterEntry.getKey();
            EntityFilter filter = filterEntry.getValue();
            CompoundNBT filterTag = new CompoundNBT();
            filterTag.putString("type", filter.getType());
            filterTag.putString("data", filter.getFilterData());
            tag.put(name, filterTag);
        }
        return tag;
    }

    public void loadFilters(CompoundNBT tag) {
        if (tag.contains("filters")) {
            CompoundNBT list = (CompoundNBT) tag.get("filters");
            for (String name : list.getAllKeys()) {
                INBT nbtFilter = list.get(name);
                if (nbtFilter instanceof CompoundNBT) {
                    String type = ((CompoundNBT) nbtFilter).getString("type");
                    String data = ((CompoundNBT) nbtFilter).getString("data");
                    EntityFilter filter = makeEntityFilter(type, data);
                    setFilter(name, filter);
                }
            }
        }
    }

    public EntityFilter makeEntityFilter(String type, String data) {
        return filterProviders.getOrDefault(type, d -> EntityFilter.INVALID).apply(data);
    }

    public Map<String, Map<String, String>> filtersToLuaObject() {
        Map<String, Map<String, String>> contents = new HashMap<>();

        for (Entry<String, EntityFilter> filterEntry : filters.entrySet()) {
            EntityFilter filter = filterEntry.getValue();
            Map<String, String> entry = new HashMap<>();
            entry.put("type", filter.getType());
            entry.put("data", filter.getFilterData());
            contents.put(filterEntry.getKey(), entry);
        }

        return contents;
    }
}
