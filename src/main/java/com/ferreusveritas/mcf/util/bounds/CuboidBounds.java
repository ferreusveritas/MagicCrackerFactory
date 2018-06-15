package com.ferreusveritas.mcf.util.bounds;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class CuboidBounds extends BaseBounds {

	private int minX, minY, minZ;
	private int maxX, maxY, maxZ;
		
	public CuboidBounds(BlockPos pos) {
		minX = maxX = pos.getX();
		minY = maxY = pos.getY();
		minZ = maxZ = pos.getZ();
	}

	public CuboidBounds(ChunkPos cPos) {
		minX = cPos.getXStart();
		minY = 0;
		minZ = cPos.getZStart();

		maxX = cPos.getXEnd();
		maxY = 255;
		maxZ = cPos.getZEnd();
	}
	
	public CuboidBounds(CuboidBounds other) {
		minX = other.minX;
		minY = other.minY;
		minZ = other.minZ;
		maxX = other.maxX;
		maxY = other.maxY;
		maxZ = other.maxZ;
	}
	
	public CuboidBounds(List<BlockPos> blockPosList) {
		this(blockPosList.get(0));
		union(blockPosList);
	}
	
	public CuboidBounds(NBTTagCompound nbt) {
		int[] bounds = nbt.getIntArray("bounds");
		if(bounds.length == 6) {
			minX = bounds[0];
			minY = bounds[1];
			minZ = bounds[2];
			maxX = bounds[3];
			maxY = bounds[4];
			maxZ = bounds[5];
		}
	}
	
	public CuboidBounds union(BlockPos pos) {
		
		if(pos.getX() < minX) {
			minX = pos.getX();
		}
		else
		if(pos.getX() > maxX) {
			maxX = pos.getX();
		}
		
		if(pos.getY() < minY) {
			minY = pos.getY();
		}
		else
		if(pos.getY() > maxY) {
			maxY = pos.getY();
		}

		if(pos.getZ() < minZ) {
			minZ = pos.getZ();
		}
		else
		if(pos.getZ() > maxZ) {
			maxZ = pos.getZ();
		}
		
		return this;
	}
	
	public CuboidBounds union(List<BlockPos> blockPosList) {
		blockPosList.forEach(b -> union(b));
		return this;
	}
	
	@Override
	public boolean inBounds(BlockPos pos) {
		return !(	pos.getX() < minX ||
					pos.getX() > maxX ||
					pos.getZ() < minZ ||
					pos.getZ() > maxZ || 
					pos.getY() < minY ||
					pos.getY() > maxY );
	}
	
	public BlockPos getMin() {
		return new BlockPos(minX, minY, minZ);
	}
	
	public BlockPos getMax() {
		return new BlockPos(maxX, maxY, maxZ);
	}

	public CuboidBounds shrink(EnumFacing dir, int amount) {
		switch(dir) {
			case DOWN: minY += amount; break;
			case UP: maxY -= amount; break;
			case NORTH: minZ += amount; break;
			case SOUTH: maxZ -= amount; break;
			case WEST: minX += amount; break;
			case EAST: maxX -= amount; break;
		}
		return this;
	}
	
	public CuboidBounds move(int x, int y, int z) {
		minX += x;
		minY += y;
		minZ += z;
		maxX += x;
		maxY += y;
		maxZ += z;
		return this;
	}
	
	public CuboidBounds expand(int amount) {
		minX -= amount;
		minY -= amount;
		minZ -= amount;
		maxX += amount;
		maxY += amount;
		maxZ += amount;
		return this;
	}
	
	public CuboidBounds shrink(int amount) {
		return expand(-amount);
	}
	
	public Iterable<BlockPos.MutableBlockPos> iterate() {
		return BlockPos.getAllInBoxMutable(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public int getXSize() {
		return maxX - minX + 1;
	}
	
	public int getYSize() {
		return maxY - minY + 1;
	}
	
	public int getZSize() {
		return maxZ - minZ + 1;
	}
	
	@Override
	public String getBoundType() {
		return "cuboid";
	}
	
	@Override
	public NBTTagCompound toNBTTagCompound() {
		NBTTagCompound nbt = super.toNBTTagCompound();
		nbt.setIntArray("bounds", new int[] {minX, minY, minZ, maxX, maxY, maxZ});
		return nbt;
	}
	
	@Override
	public Object[] toLuaObject() {
		Map<String, Object> contents = new HashMap<>();
		contents.put("type", getBoundType());
		contents.put("minX", minX);
		contents.put("minY", minY);
		contents.put("minZ", minZ);
		contents.put("maxX", maxX);
		contents.put("maxY", maxY);
		contents.put("maxZ", maxZ);
		return new Object[] { contents }; 
	}
	
	@Override
	public String toString() {
		return "Bounds {x1=" + minX + ", y1=" + minY + ", z1=" + minZ + " -> x2=" + maxX + ", y2=" + maxY + ", z2=" + maxZ + "}";
	}
	
	@Override
	public int hashCode() {
		return minX ^ minY ^ minZ ^ maxX ^ maxY ^ maxZ;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		CuboidBounds obb = (CuboidBounds) obj;
		
		return minX == obb.minX && maxX == obb.maxX && minZ == obb.minZ && maxZ == obb.maxZ && minY == obb.minY && maxY == obb.maxY; 
	}
}
