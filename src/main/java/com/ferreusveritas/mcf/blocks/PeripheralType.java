package com.ferreusveritas.mcf.blocks;

import com.ferreusveritas.mcf.tileentity.TileCartographer;
import com.ferreusveritas.mcf.tileentity.TileSentinel;
import com.ferreusveritas.mcf.tileentity.TileTerraformer;

import net.minecraft.tileentity.TileEntity;

public enum PeripheralType {
	
	CARTOGRAPHER(TileCartographer.class),
	SENTINEL(TileSentinel.class),
	TERRAFORMER(TileTerraformer.class);
	
	private Class tileEntityClass;
	
	private PeripheralType(Class teClass) {
		tileEntityClass = teClass;
	}

	public Class getTileEntityClass() {
		return tileEntityClass;
	}
	
	public String getName() {
		return this.name().toLowerCase();
	}
	
	public TileEntity newTileEntity() {
		try {
			return (TileEntity) getTileEntityClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) { }

		return null;
	}
}
