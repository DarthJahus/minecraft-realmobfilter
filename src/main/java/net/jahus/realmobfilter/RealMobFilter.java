package net.jahus.realmobfilter;

import net.fabricmc.api.ModInitializer;
import net.jahus.realmobfilter.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RealMobFilter implements ModInitializer {
    public static final String MOD_ID = "realmobfilter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModConfig.load();
        LOGGER.info("Real Mob Filter loaded (enabled={}, entries={})", ModConfig.isEnabled(), ModConfig.size());
    }
}
