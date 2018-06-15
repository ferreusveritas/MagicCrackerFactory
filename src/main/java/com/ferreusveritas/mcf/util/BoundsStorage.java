package com.ferreusveritas.mcf.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.nbt.NBTTagCompound;

public class BoundsStorage {
	
	public static enum EnumBoundsType {
		EMPTY,
		BREAK,
		PLACE,
		BLAST,
		SPAWN;

		public final static List<EnumBoundsType> valid = Arrays.asList(BREAK, PLACE, BLAST, SPAWN);
		
		public static EnumBoundsType getType(String type) {
			for(EnumBoundsType t: EnumBoundsType.values()) {
				if(t.toString().equals(type.toUpperCase())) {
					return t;
				}
			}
			return EMPTY;
		}
		
		public String getLabel() {
			return toString().toLowerCase();
		}
	}
	
	//Simple Bounds type factory registry
	public static Map<String, Function<NBTTagCompound, Bounds> > boundsProviders = new HashMap<>();
	
	static { //This gives us the ability to add new bound types in the future
		boundsProviders.put("cuboid", n -> new BlockBounds(n) );
	}
	
	public static Bounds loadBounds(NBTTagCompound nbt) {
		return boundsProviders.getOrDefault(nbt.getString("type").toLowerCase(), n -> Bounds.INVALID ).apply(nbt);
	}
	
	public Map<String, Bounds> breakBounds = new HashMap<>();
	public Map<String, Bounds> placeBounds = new HashMap<>();
	public Map<String, Bounds> blastBounds = new HashMap<>();
	public Map<String, Bounds> spawnBounds = new HashMap<>();
	
	public Map<String, Bounds>[] allBounds = new Map[] {
			new VoidMap<>(), breakBounds, placeBounds, blastBounds, spawnBounds
	};
	
	public Map<String, Bounds> getByType(EnumBoundsType type) {
		return allBounds[type.ordinal()];
	}
	
	public Map<String, Bounds> getByType(String type) {
		return allBounds[EnumBoundsType.getType(type).ordinal()];
	}
	
	public BoundsStorage(NBTTagCompound nbt) {
		
		EnumBoundsType.valid.forEach(
			type -> {
				NBTTagCompound n = nbt.getCompoundTag(type.getLabel());
				n.getKeySet().forEach( key -> getByType(type).put(key, loadBounds(n.getCompoundTag(key))) );
			}
		);
		
	}
	
	public NBTTagCompound toNBTTagCompound() {

		NBTTagCompound nbt = new NBTTagCompound();
		
		EnumBoundsType.valid.forEach(
			type -> {
				NBTTagCompound n = new NBTTagCompound();
				getByType(type).forEach((key, val) -> n.setTag(key, val.toNBTTagCompound()) );
				nbt.setTag(type.getLabel(), n);		
			}
		);
		
		return nbt;
	}
	
}
