package com.ferreusveritas.mcf.util.bounds;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class CylinderBounds extends BaseBounds {

	private int posX, posZ, minY, maxY, radius;
		
	public CylinderBounds(BlockPos pos) {
		posX = pos.getX();
		posZ = pos.getZ();
		minY = 0;
		maxY = 255;
		radius = 8;
	}
	
	public CylinderBounds(BlockPos pos, int h, int radius) {
		posX = pos.getX();
		posZ = pos.getZ();
		minY = pos.getY();
		maxY = pos.getY() + h;
		this.radius = radius;
	}
	
	public CylinderBounds(CylinderBounds other) {
		posX = other.posX;
		posZ = other.posZ;
		minY = other.minY;
		maxY = other.maxY;
		radius = other.radius;
	}
	
	public CylinderBounds(NBTTagCompound nbt) {
		int[] bounds = nbt.getIntArray("bounds");
		if(bounds.length == 6) {
			posX = bounds[0];
			posZ = bounds[1];
			minY = bounds[2];
			maxY = bounds[3];
			radius = bounds[4];
		}
	}
	
	@Override
	public boolean inBounds(BlockPos pos) {
		
		int xd = pos.getX() - posX;
		int zd = pos.getZ() - posZ;
		
		return !(	((xd * xd) + (zd * zd)) > (radius * radius) ||
					pos.getY() < minY ||
					pos.getY() > maxY
				);
	}
	
	public CylinderBounds move(int x, int y, int z) {
		posX += x;
		posZ += z;
		minY += y;
		maxY += y;
		return this;
	}
	
	public CylinderBounds expand(int amount) {
		radius += amount;
		return this;
	}
	
	public CylinderBounds shrink(int amount) {
		return expand(-amount);
	}
	
	public int getXSize() {
		return radius * 2;
	}
	
	public int getYSize() {
		return maxY - minY + 1;
	}
	
	public int getZSize() {
		return radius * 2;
	}
	
	@Override
	public String getBoundType() {
		return "cylinder";
	}
	
	@Override
	public AxisAlignedBB getAABB() {
		return new AxisAlignedBB(new BlockPos(posX - radius, minY, posZ - radius), new BlockPos(posX + radius, maxY, posZ + radius));
	}
	
	@Override
	public NBTTagCompound toNBTTagCompound() {
		NBTTagCompound nbt = super.toNBTTagCompound();
		nbt.setIntArray("bounds", new int[] {posX, posZ, minY, maxY, radius});
		return nbt;
	}
	
	@Override
	public Object[] toLuaObject() {
		Map<String, Object> contents = new HashMap<>();
		contents.put("type", getBoundType());
		contents.put("posX", posX);
		contents.put("posZ", posZ);
		contents.put("minY", minY);
		contents.put("maxY", maxY);
		contents.put("radius", radius);
		return new Object[] { contents }; 
	}
	
	@Override
	public String toString() {
		return "Bounds {x=" + posX + ", z=" + posZ + ", y1=" + minY + ", y2=" + maxY + ", radius=" + radius + "}";
	}
	
	@Override
	public int hashCode() {
		return posX ^ posZ ^ minY ^ maxY ^ radius;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		CylinderBounds obb = (CylinderBounds) obj;
		
		return posX == obb.posX && posZ == obb.posZ && minY == obb.minY && maxY == obb.maxY && radius == obb.radius; 
	}
}
