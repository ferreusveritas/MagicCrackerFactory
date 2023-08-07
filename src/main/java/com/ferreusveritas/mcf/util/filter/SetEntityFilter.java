package com.ferreusveritas.mcf.util.filter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

public class SetEntityFilter {

    // Simple EntityFilter type factory registry
    public final static Map<String, Function<String, EntityFilter>> FILTER_PROVIDERS = new HashMap<>();

    static { // This gives us the ability to add new EntityFilter types in the future
        FILTER_PROVIDERS.put("hostile", d -> new HostileEntityFilter());
        FILTER_PROVIDERS.put("peaceful", d -> new PeacefulEntityFilter());
        FILTER_PROVIDERS.put("entity", EntityTypeFilter::new);
        FILTER_PROVIDERS.put("all", d -> new EntityFilterAll());
        FILTER_PROVIDERS.put("none", d -> new NoneEntityFilter());
    }

    public Map<String, EntityFilter> filters = new HashMap<>();

    public static boolean isMobHostile(LivingEntity entity) {
        return entity instanceof Enemy;
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

    public CompoundTag saveFilters() {
        CompoundTag tag = new CompoundTag();
        for (Entry<String, EntityFilter> filterEntry : filters.entrySet()) {
            String name = filterEntry.getKey();
            EntityFilter filter = filterEntry.getValue();
            CompoundTag filterTag = new CompoundTag();
            filterTag.putString("type", filter.getType());
            filterTag.putString("data", filter.getFilterData());
            tag.put(name, filterTag);
        }
        return tag;
    }

    public void loadFilters(CompoundTag tag) {
        if (tag.contains("filters")) {
            CompoundTag list = (CompoundTag) tag.get("filters");
            for (String name : list.getAllKeys()) {
                Tag filterTag = list.get(name);
                if (filterTag instanceof CompoundTag) {
                    String type = ((CompoundTag) filterTag).getString("type");
                    String data = ((CompoundTag) filterTag).getString("data");
                    EntityFilter filter = makeEntityFilter(type, data);
                    setFilter(name, filter);
                }
            }
        }
    }

    public EntityFilter makeEntityFilter(String type, String data) {
        return FILTER_PROVIDERS.getOrDefault(type, d -> EntityFilter.INVALID).apply(data);
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
