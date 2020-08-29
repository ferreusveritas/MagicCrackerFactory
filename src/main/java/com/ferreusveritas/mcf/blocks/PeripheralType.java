package com.ferreusveritas.mcf.blocks;

import com.ferreusveritas.mcf.tileentity.TileCartographer;
import com.ferreusveritas.mcf.tileentity.TileDendrocoil;
import com.ferreusveritas.mcf.tileentity.TileRemoteReceiver;
import com.ferreusveritas.mcf.tileentity.TileSentinel;
import com.ferreusveritas.mcf.tileentity.TileTerraformer;
import com.ferreusveritas.mcf.tileentity.TileWebModem;

import net.minecraft.tileentity.TileEntity;

public enum PeripheralType {
	
	CARTOGRAPHER(TileCartographer.class, "Used to manipulate maps"),
	SENTINEL(TileSentinel.class, "Used to secure areas against changes or mob spawns"),
	TERRAFORMER(TileTerraformer.class, "Used to get/set world biomes or get biome information"),
	DENDROCOIL(TileDendrocoil.class, "Used to manipulate Dynamic Trees"),
	REMOTERECEIVER(TileRemoteReceiver.class, "Used to receive remote signals from universal remotes or touch sensitive objects"),
	WEBMODEM(TileWebModem.class, "Used to serve data to real world web connections");
	
	private Class tileEntityClass;
	private String description;
	
	private PeripheralType(Class teClass, String desc) {
		tileEntityClass = teClass;
		description = desc;
	}

	public Class getTileEntityClass() {
		return tileEntityClass;
	}
	
	public String getName() {
		return this.name().toLowerCase();
	}
	
	public String getDesc() {
		return description;
	}
	
	public TileEntity newTileEntity() {
		try {
			return (TileEntity) getTileEntityClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) { }

		return null;
	}
}
