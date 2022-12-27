package com.ferreusveritas.mcf.util.bounds;

import com.ferreusveritas.mcf.util.VoidMap;
import com.ferreusveritas.mcf.util.filter.EntityFilter;
import com.ferreusveritas.mcf.util.filter.EntityFilterAll;
import com.ferreusveritas.mcf.util.filter.HostileEntityFilter;
import net.minecraft.nbt.CompoundNBT;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class StorageBounds {

    //Simple Bounds type factory registry
    public static Map<String, Function<CompoundNBT, Bounds>> boundsProviders = new HashMap<>();

    static { //This gives us the ability to add new bound types in the future
        boundsProviders.put("cuboid", n -> new CuboidBounds(n));
        boundsProviders.put("cylinder", n -> new CylinderBounds(n));
        boundsProviders.put("any", n -> new AnyBounds(n));
    }

    public Map<String, Bounds> breakBounds = new HashMap<>();
    public Map<String, Bounds> placeBounds = new HashMap<>();
    public Map<String, Bounds> blastBounds = new HashMap<>();
    public Map<String, Bounds> spawnBounds = new HashMap<>();
    public Map<String, Bounds> enderBounds = new HashMap<>();
    public Map<String, Bounds> identBounds = new HashMap<>();
    public Map<String, Bounds>[] allBounds = new Map[]{
            new VoidMap<>(), breakBounds, placeBounds, blastBounds, spawnBounds, enderBounds, identBounds
    };
    public StorageBounds(CompoundNBT tag) {

        BoundsType.valid.forEach(
                type -> {
                    CompoundNBT n = tag.getCompound(type.getLabel());
                    n.getAllKeys().forEach(key -> getByType(type).put(key, loadBounds(n.getCompound(key))));
                }
        );

    }

    public static Bounds loadBounds(CompoundNBT nbt) {
        return boundsProviders.getOrDefault(nbt.getString("type").toLowerCase(), n -> Bounds.INVALID).apply(nbt);
    }

    public Map<String, Bounds> getByType(BoundsType type) {
        return allBounds[type.ordinal()];
    }

    public Map<String, Bounds> getByType(String type) {
        return allBounds[BoundsType.getType(type).ordinal()];
    }

    public CompoundNBT toCompoundTag() {
        CompoundNBT tag = new CompoundNBT();

        BoundsType.valid.forEach(
                type -> {
                    CompoundNBT n = new CompoundNBT();
                    getByType(type).forEach((key, value) -> n.put(key, value.toCompoundTag()));
                    tag.put(type.getLabel(), n);
                }
        );

        return tag;
    }

    public enum BoundsType {
        EMPTY,
        BREAK,
        PLACE,
        BLAST(new EntityFilterAll()),
        SPAWN(new HostileEntityFilter()),
        ENDER(new EntityFilterAll()),
        IDENT;

        public final static List<BoundsType> valid = Arrays.asList(BREAK, PLACE, BLAST, SPAWN, ENDER, IDENT);
        private final EntityFilter defaultEntityFilter;

        BoundsType() {
            defaultEntityFilter = null;
        }

        BoundsType(EntityFilter defaultFilter) {
            this.defaultEntityFilter = defaultFilter;
        }

        public static BoundsType getType(String type) {
            for (BoundsType t : BoundsType.values()) {
                if (t.toString().equals(type.toUpperCase())) {
                    return t;
                }
            }
            return EMPTY;
        }

        public EntityFilter getDefaultEntityFilter() {
            return defaultEntityFilter;
        }

        public String getLabel() {
            return toString().toLowerCase();
        }
    }

}
