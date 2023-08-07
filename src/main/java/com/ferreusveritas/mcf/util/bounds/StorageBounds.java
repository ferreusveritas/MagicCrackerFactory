package com.ferreusveritas.mcf.util.bounds;

import com.ferreusveritas.mcf.util.VoidMap;
import com.ferreusveritas.mcf.util.filter.EntityFilter;
import com.ferreusveritas.mcf.util.filter.EntityFilterAll;
import com.ferreusveritas.mcf.util.filter.HostileEntityFilter;
import net.minecraft.nbt.CompoundTag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class StorageBounds {

    //Simple Bounds type factory registry
    public static Map<String, Function<CompoundTag, Bounds>> boundsProviders = new HashMap<>();

    static { //This gives us the ability to add new bound types in the future
        boundsProviders.put("cuboid", CuboidBounds::new);
        boundsProviders.put("cylinder", CylinderBounds::new);
        boundsProviders.put("any", AnyBounds::new);
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
    public StorageBounds(CompoundTag tag) {

        BoundsType.VALID.forEach(
                type -> {
                    CompoundTag n = tag.getCompound(type.getLabel());
                    n.getAllKeys().forEach(key -> getByType(type).put(key, loadBounds(n.getCompound(key))));
                }
        );

    }

    public static Bounds loadBounds(CompoundTag nbt) {
        return boundsProviders.getOrDefault(nbt.getString("type").toLowerCase(), n -> Bounds.INVALID).apply(nbt);
    }

    public Map<String, Bounds> getByType(BoundsType type) {
        return allBounds[type.ordinal()];
    }

    public Map<String, Bounds> getByType(String type) {
        return allBounds[BoundsType.getType(type).ordinal()];
    }

    public CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag();

        BoundsType.VALID.forEach(
                type -> {
                    CompoundTag n = new CompoundTag();
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

        public final static List<BoundsType> VALID = Arrays.asList(BREAK, PLACE, BLAST, SPAWN, ENDER, IDENT);
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
