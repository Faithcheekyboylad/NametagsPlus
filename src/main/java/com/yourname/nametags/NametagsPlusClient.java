package com.yourname.nametags;

import com.yourname.nametags.renderer.NametagRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class NametagsPlusClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_ENTITIES.register(NametagRenderer::onRenderWorld);
    }
}
