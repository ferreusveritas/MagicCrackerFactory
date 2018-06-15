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
		SPAWN,
		ENDER;

		public final static List<EnumBoundsType> valid = Arrays.asList(BREAK, PLACE, BLAST, SPAWN, ENDER);
		
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
	public static Map<String, Function<NBTTagCompound, BaseBounds> > boundsProviders = new HashMap<>();
	
	static { //This gives us the ability to add new bound types in the future
		boundsProviders.put("cuboid", n -> new CuboidBounds(n) );
		boundsProviders.put("cylinder", n -> new CylinderBounds(n) );
	}
	
	public static BaseBounds loadBounds(NBTTagCompound nbt) {
		return boundsProviders.getOrDefault(nbt.getString("type").toLowerCase(), n -> BaseBounds.INVALID ).apply(nbt);
	}
	
	public Map<String, BaseBounds> breakBounds = new HashMap<>();
	public Map<String, BaseBounds> placeBounds = new HashMap<>();
	public Map<String, BaseBounds> blastBounds = new HashMap<>();
	public Map<String, BaseBounds> spawnBounds = new HashMap<>();
	public Map<String, BaseBounds> enderBounds = new HashMap<>();
	
	public Map<String, BaseBounds>[] allBounds = new Map[] {
			new VoidMap<>(), breakBounds, placeBounds, blastBounds, spawnBounds, enderBounds
	};
	
	public Map<String, BaseBounds> getByType(EnumBoundsType type) {
		return allBounds[type.ordinal()];
	}
	
	public Map<String, BaseBounds> getByType(String type) {
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
