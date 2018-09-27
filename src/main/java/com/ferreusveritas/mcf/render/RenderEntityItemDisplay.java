package com.ferreusveritas.mcf.render;

import com.ferreusveritas.mcf.entities.EntityItemDisplay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderEntityItemDisplay extends Render<EntityItemDisplay>{
	
    private final RenderItem itemRenderer;
	
	protected RenderEntityItemDisplay(RenderManager renderManager) {
		super(renderManager);
		itemRenderer = Minecraft.getMinecraft().getRenderItem();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityItemDisplay entity) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
	
	@Override
	public void doRender(EntityItemDisplay entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		
		ItemStack stack = entity.getItemStack();
		itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.NONE);
		
		GlStateManager.popMatrix();
	}
	
}
