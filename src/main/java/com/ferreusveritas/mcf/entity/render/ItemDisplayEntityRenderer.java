package com.ferreusveritas.mcf.entity.render;

import com.ferreusveritas.mcf.entity.ItemDisplayEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3f;

public class ItemDisplayEntityRenderer extends EntityRenderer<ItemDisplayEntity> {

    private final ItemRenderer itemRenderer;

    public ItemDisplayEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
        itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ResourceLocation getTextureLocation(ItemDisplayEntity pEntity) {
        return AtlasTexture.LOCATION_BLOCKS;
    }

    @Override
    public void render(ItemDisplayEntity entity, float yaw, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        super.render(entity, yaw, partialTick, matrixStack, buffer, packedLight);

        matrixStack.pushPose();

        Rotations rotation = entity.getRotation();
        if (rotation.getY() != 0.0F) {
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(rotation.getY()));
        }
        if (rotation.getX() != 0.0F) {
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(rotation.getX()));
        }
        if (rotation.getZ() != 0.0F) {
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(rotation.getZ()));
        }

        float scale = entity.getScale();
        if (scale != 1.0F) {
            matrixStack.scale(scale, scale, scale);
        }

        ItemStack stack = entity.getItemStack();

        if (stack.getItem() instanceof SkullItem) {
            matrixStack.translate(0.0f, 0.5f, 0.0f);
            matrixStack.scale(2.0f, 2.0f, 2.0f);
        }

        itemRenderer.renderStatic(stack, ItemCameraTransforms.TransformType.NONE, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer);

        matrixStack.popPose();
    }

}
