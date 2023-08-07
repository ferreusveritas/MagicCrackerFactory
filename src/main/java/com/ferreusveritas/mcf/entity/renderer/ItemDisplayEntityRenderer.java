package com.ferreusveritas.mcf.entity.renderer;

import com.ferreusveritas.mcf.entity.ItemDisplayEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Rotations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SkullBlock;

public class ItemDisplayEntityRenderer extends EntityRenderer<ItemDisplayEntity> {

    private final ItemRenderer itemRenderer;

    public ItemDisplayEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        itemRenderer = context.getItemRenderer();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ResourceLocation getTextureLocation(ItemDisplayEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public void render(ItemDisplayEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);

        poseStack.pushPose();

        Rotations rotation = entity.getRotation();
        if (rotation.getY() != 0.0F) {
            poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation.getY()));
        }
        if (rotation.getX() != 0.0F) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(rotation.getX()));
        }
        if (rotation.getZ() != 0.0F) {
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotation.getZ()));
        }

        float scale = entity.getScale();
        if (scale != 1.0F) {
            poseStack.scale(scale, scale, scale);
        }

        ItemStack stack = entity.getItemStack();

        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof SkullBlock) {
            poseStack.translate(0.0f, 0.5f, 0.0f);
            poseStack.scale(2.0f, 2.0f, 2.0f);
        }

        itemRenderer.renderStatic(stack, ItemTransforms.TransformType.NONE, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, 0);

        poseStack.popPose();
    }

}
