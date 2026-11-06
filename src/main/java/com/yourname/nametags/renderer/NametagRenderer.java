package com.yourname.nametags.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.yourname.nametags.NametagConfig;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class NametagRenderer {

    public static void onRenderWorld(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        Camera camera = context.camera();
        Vec3d cameraPos = camera.getPos();
        MatrixStack matrices = context.matrixStack();
        TextRenderer textRenderer = client.textRenderer;

        for (Entity entity : client.world.getEntities()) {
            if (entity == client.player) continue;

            boolean isPlayer = entity instanceof PlayerEntity;
            boolean isAnimal = entity instanceof AnimalEntity;
            boolean isMonster = entity instanceof MobEntity && !isAnimal;
            boolean isItem = entity instanceof ItemEntity;

            if (isPlayer && !NametagConfig.showPlayers) continue;
            if (isAnimal && !NametagConfig.showAnimals) continue;
            if (isMonster && !NametagConfig.showMonsters) continue;
            if (isItem && !NametagConfig.showItems) continue;
            if (!isPlayer && !isAnimal && !isMonster && !isItem) continue;

            renderTagForEntity(client, textRenderer, matrices, cameraPos, entity);
        }
    }

    private static void renderTagForEntity(MinecraftClient client, TextRenderer textRenderer, MatrixStack matrices, Vec3d cameraPos, Entity entity) {
        String displayName = getEntityDisplayName(entity);
        if (displayName == null || displayName.isEmpty()) return;

        double x = entity.getX() - cameraPos.x;
        double y = entity.getY() + entity.getHeight() + 0.5 - cameraPos.y;
        double z = entity.getZ() - cameraPos.z;

        matrices.push();
        matrices.translate(x, y, z);
        matrices.multiply(client.gameRenderer.getCamera().getRotation());
        matrices.scale(-0.025f * NametagConfig.overallScale, -0.025f * NametagConfig.overallScale, 0.025f * NametagConfig.overallScale);

        if (NametagConfig.seeThroughWalls) {
            RenderSystem.disableDepthTest();
        }

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        float textWidth = textRenderer.getWidth(displayName);
        float halfWidth = textWidth / 2.0f;

        textRenderer.draw(displayName, -halfWidth, 0, NametagConfig.textColor, false, matrix4f, client.getBufferBuilders().getEntityVertexConsumers(), TextRenderer.TextLayerType.NORMAL, NametagConfig.backgroundColor, 0xF000F0);

        if (NametagConfig.showHealth && entity instanceof LivingEntity living) {
            String healthStr = String.format("%.1f HP", living.getHealth());
            float healthWidth = textRenderer.getWidth(healthStr);
            matrices.push();
            matrices.scale(NametagConfig.healthScale, NametagConfig.healthScale, 1.0f);
            textRenderer.draw(healthStr, -healthWidth / 2.0f, 10, 0xFFFF5555, false, matrix4f, client.getBufferBuilders().getEntityVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
            matrices.pop();
        }

        if (NametagConfig.seeThroughWalls) {
            RenderSystem.enableDepthTest();
        }

        matrices.pop();
    }

    private static String getEntityDisplayName(Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            return itemEntity.getStack().getName().getString() + " x" + itemEntity.getStack().getCount();
        }
        if (entity instanceof LivingEntity living) {
            return living.getName().getString();
        }
        return entity.getType().getName().getString();
    }
}
