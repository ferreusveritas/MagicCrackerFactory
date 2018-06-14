package com.ferreusveritas.mcf.util;

import java.util.HashMap;

public class DimBoundsStorage {
	
	public HashMap<String, IBounds> breakDenyBounds = new HashMap<>();
	public HashMap<String, IBounds> placeDenyBounds = new HashMap<>();
	public HashMap<String, IBounds> explodeDenyBounds = new HashMap<>();
	public HashMap<String, IBounds> spawnDenyBounds = new HashMap<>();
	public int dim = 0;

	public DimBoundsStorage(int dim) {
		this.dim = dim;
	}
	
	@Override
	public int hashCode() {
		return dim;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null) {
			return false;
		}
		
		if(this == obj) {
			return true;
		}
		
		if(obj.getClass() != this.getClass()) {
			return false;
		}
		
		return this.dim == ((DimBoundsStorage)obj).dim;
	}
}
