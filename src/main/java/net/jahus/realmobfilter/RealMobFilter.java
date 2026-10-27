package net.jahus.realmobfilter;

import com.mojang.logging.LogUtils;
import net.jahus.realmobfilter.config.ModConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(RealMobFilter.MOD_ID)
public class RealMobFilter {
    public static final String MOD_ID = "realmobfilter";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RealMobFilter(IEventBus modEventBus) {
        ModConfig.load();
        LOGGER.info("Real Mob Filter loaded (enabled={}, entries={})", ModConfig.isEnabled(), ModConfig.size());
    }
}
