package net.jahus.realmobfilter;

import com.mojang.logging.LogUtils;
import net.jahus.realmobfilter.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(RealMobFilter.MOD_ID)
public class RealMobFilter {
    public static final String MOD_ID = "realmobfilter";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RealMobFilter(FMLJavaModLoadingContext context) {
        ModConfig.load();
        LOGGER.info("Real Mob Filter loaded (enabled={}, entries={})", ModConfig.isEnabled(), ModConfig.size());
    }
}
