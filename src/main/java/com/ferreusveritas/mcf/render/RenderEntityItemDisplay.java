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
import net.minecraft.util.math.Rotations;

public class RenderEntityItemDisplay extends Render<EntityItemDisplay>{
	
    private final RenderItem itemRenderer;
	
    public RenderEntityItemDisplay(RenderManager renderManager) {
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
		GlStateManager.translate(x, y + 0.5, z);
		
		Rotations rot = entity.getRotation();
		
		if (rot.getY() != 0.0F) {
			GlStateManager.rotate(rot.getY(), 0.0F, 1.0F, 0.0F);
		}
		
		if (rot.getX() != 0.0F) {
			GlStateManager.rotate(rot.getX(), 1.0F, 0.0F, 0.0F);
		}
		
		if (rot.getZ() != 0.0F) {
			GlStateManager.rotate(rot.getZ(), 0.0F, 0.0F, 1.0F);
		}
		
		float scale = entity.getScale();
		
		if (scale != 1.0F) {
			GlStateManager.scale(scale, scale, scale);
		}
		
		ItemStack stack = entity.getItemStack();

		itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.NONE);
		
		GlStateManager.popMatrix();
	}

}
