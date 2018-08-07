package com.ferreusveritas.mcf.util.filters;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class EntityFilterSet {
	
	public Map<String, IEntityFilter> filters = new HashMap<>();
	
	public boolean isEntityDenied(EntityLivingBase entity) {
		return filters.values().parallelStream().anyMatch(f -> f.isEntityDenied(entity));
	}
	
	public static boolean isMobHostile(EntityLivingBase entity) {
		return entity instanceof EntityMob || entity instanceof EntitySlime;
	}
	
	public void setFilter(String name, String type, String data) {
		setFilter(name, makeEntityFilter(type, data));
	}
	
	public void setFilter(String name, IEntityFilter filter) {
		filters.put(name, filter);
	}
	
	public void remFilter(String name) {
		filters.remove(name);
	}

	public NBTTagCompound saveFilters() {
		NBTTagCompound nbt = new NBTTagCompound();
		for(Entry<String, IEntityFilter> filterEntry: filters.entrySet()) {
			String name = filterEntry.getKey();
			IEntityFilter filter = filterEntry.getValue();
			NBTTagCompound nbtFilter = new NBTTagCompound();
			nbtFilter.setString("type", filter.getType());
			nbtFilter.setString("data", filter.getFilterData());
			nbt.setTag(name, nbtFilter);
		}
		return nbt;
	}
	
	public void loadFilters(NBTTagCompound nbt) {
		if(nbt.hasKey("filters")) {
			NBTTagCompound list = (NBTTagCompound) nbt.getTag("filters");
			for(String name : list.getKeySet()) {
				NBTBase nbtFilter = list.getTag(name);
				if(nbtFilter instanceof NBTTagCompound) {
					String type = ((NBTTagCompound) nbtFilter).getString("type");
					String data = ((NBTTagCompound) nbtFilter).getString("data");
					IEntityFilter filter = makeEntityFilter(type, data);
					setFilter(name, filter);
				}
			}
		}
	}
	
	//Simple EntityFilter type factory registry
	public static Map<String, Function<String, IEntityFilter> > filterProviders = new HashMap<>();
	
	static { //This gives us the ability to add new EntityFilter types in the future
		filterProviders.put("hostile", d -> new EntityFilterHostile() );
		filterProviders.put("peaceful", d -> new EntityFilterPeaceful() );
		filterProviders.put("entity", d -> new EntityFilterEntity(d) );
		filterProviders.put("all", d -> new EntityFilterAll() );
		filterProviders.put("none", d -> new EntityFilterNone() );
	}
	
	public IEntityFilter makeEntityFilter(String type, String data) {
		return filterProviders.getOrDefault(type, d -> IEntityFilter.INVALID).apply(data);
	}

	public Map<String, Map<String, String>> filtersToLuaObject() {
		Map<String, Map<String, String>> contents = new HashMap<>();
		
		for(Entry<String, IEntityFilter> filterEntry: filters.entrySet()) {
			IEntityFilter filter = filterEntry.getValue();
			Map<String, String> entry = new HashMap<>();
			entry.put("type", filter.getType());
			entry.put("data", filter.getFilterData());
			contents.put(filterEntry.getKey(), entry);
		}
		
		return contents;
	}
}
