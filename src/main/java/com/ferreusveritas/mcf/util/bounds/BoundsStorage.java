package com.ferreusveritas.mcf.util.bounds;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.ferreusveritas.mcf.util.VoidMap;
import com.ferreusveritas.mcf.util.filters.EntityFilterAll;
import com.ferreusveritas.mcf.util.filters.EntityFilterHostile;
import com.ferreusveritas.mcf.util.filters.IEntityFilter;

import net.minecraft.nbt.NBTTagCompound;

public class BoundsStorage {
	
	public static enum EnumBoundsType {
		EMPTY,
		BREAK,
		PLACE,
		BLAST(new EntityFilterAll()),
		SPAWN(new EntityFilterHostile()),
		ENDER(new EntityFilterAll()),
		IDENT,
		SEEDS;
		
		private final IEntityFilter defaultEntityFilter;
		
		EnumBoundsType() {
			defaultEntityFilter = null;
		}
		
		EnumBoundsType(IEntityFilter defaultFilter) {
			this.defaultEntityFilter = defaultFilter;
		}
		
		public IEntityFilter getDefaultEntityFilter() {
			return defaultEntityFilter;
		}
		
		public final static List<EnumBoundsType> valid = Arrays.asList(BREAK, PLACE, BLAST, SPAWN, ENDER, IDENT, SEEDS);
		
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
	public static Map<String, Function<NBTTagCompound, BoundsBase> > boundsProviders = new HashMap<>();
	
	static { //This gives us the ability to add new bound types in the future
		boundsProviders.put("cuboid", n -> new BoundsCuboid(n) );
		boundsProviders.put("cylinder", n -> new BoundsCylinder(n) );
		boundsProviders.put("any", n -> new BoundsAny(n) );
	}
	
	public static BoundsBase loadBounds(NBTTagCompound nbt) {
		return boundsProviders.getOrDefault(nbt.getString("type").toLowerCase(), n -> BoundsBase.INVALID ).apply(nbt);
	}
	
	public Map<String, BoundsBase> breakBounds = new HashMap<>();
	public Map<String, BoundsBase> placeBounds = new HashMap<>();
	public Map<String, BoundsBase> blastBounds = new HashMap<>();
	public Map<String, BoundsBase> spawnBounds = new HashMap<>();
	public Map<String, BoundsBase> enderBounds = new HashMap<>();
	public Map<String, BoundsBase> identBounds = new HashMap<>();
	public Map<String, BoundsBase> seedsBounds = new HashMap<>();
	
	public Map<String, BoundsBase>[] allBounds = new Map[] {
			new VoidMap<>(), breakBounds, placeBounds, blastBounds, spawnBounds, enderBounds, identBounds, seedsBounds
	};
	
	public Map<String, BoundsBase> getByType(EnumBoundsType type) {
		return allBounds[type.ordinal()];
	}
	
	public Map<String, BoundsBase> getByType(String type) {
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
