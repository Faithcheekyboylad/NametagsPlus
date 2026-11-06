package com.yourname.nametags;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NametagsPlus implements ModInitializer {
    public static final String MOD_ID = "nametags_plus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Nametags+ initialized successfully for Minecraft 26.2.");
    }
}
