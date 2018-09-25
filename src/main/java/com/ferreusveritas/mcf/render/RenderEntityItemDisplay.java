package com.ferreusveritas.mcf.render;

import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderEntityItemDisplay extends Render<EntityFallingTree>{
	
	protected RenderEntityItemDisplay(RenderManager renderManager) {
		super(renderManager);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityFallingTree entity) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
