package net.jahus.realmobfilter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.jahus.realmobfilter.RealMobFilter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.neoforged.fml.loading.FMLPaths;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@code enabled} is false until the user turns it on.
 * {@code entities} maps {@code namespace:path} to the chance a natural spawn is allowed.
 * Missing keys are allowed. 0 = never, 1 = always.
 */
public final class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path PATH = FMLPaths.CONFIGDIR.get().resolve("realmobfilter.json");

    private static Data data = Data.defaults();

    private ModConfig() {}

    public static void load() {
        if (!Files.exists(PATH)) {
            data = Data.defaults();
            save();
            RealMobFilter.LOGGER.info("Wrote default config to {}", PATH.toAbsolutePath());
            return;
        }
        try (Reader reader = Files.newBufferedReader(PATH)) {
            Data loaded = GSON.fromJson(reader, Data.class);
            data = loaded != null ? loaded : Data.defaults();
            if (data.entities == null) {
                data.entities = new LinkedHashMap<>();
            }
            Map<String, Double> parsed = new LinkedHashMap<>();
            for (Map.Entry<String, Double> e : data.entities.entrySet()) {
                String key = e.getKey();
                Double p = e.getValue();
                if (key == null || p == null) continue;
                Identifier id = Identifier.tryParse(key);
                if (id == null) {
                    RealMobFilter.LOGGER.warn("Skipping invalid entity id '{}'", key);
                    continue;
                }
                if (p < 0.0 || p > 1.0) {
                    RealMobFilter.LOGGER.warn("Clamping {} from {} into [0, 1]", id, p);
                    p = Math.clamp(p, 0.0, 1.0);
                }
                parsed.put(id.toString(), p);
            }
            data.entities = parsed;
            RealMobFilter.LOGGER.info(
                    "Loaded enabled={} entries={} from {}",
                    data.enabled,
                    data.entities.size(),
                    PATH.toAbsolutePath()
            );
            for (Map.Entry<String, Double> e : data.entities.entrySet()) {
                RealMobFilter.LOGGER.info("  {} = {}", e.getKey(), e.getValue());
            }
        } catch (Exception e) {
            RealMobFilter.LOGGER.error("Failed to load config, filter stays off", e);
            data = Data.defaults();
            data.enabled = false;
            data.entities = Map.of();
        }
    }

    private static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(data, writer);
            }
        } catch (Exception e) {
            RealMobFilter.LOGGER.error("Failed to save config", e);
        }
    }

    public static boolean isEnabled() {
        return data != null && data.enabled;
    }

    public static int size() {
        return data != null && data.entities != null ? data.entities.size() : 0;
    }

    /**
     * @return true if this natural spawn attempt may continue
     */
    public static boolean allow(EntityType<?> type) {
        if (!isEnabled() || data.entities == null || data.entities.isEmpty() || type == null) {
            return true;
        }
        Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (id == null) {
            return true;
        }
        Double p = data.entities.get(id.toString());
        if (p == null || p >= 1.0) {
            return true;
        }
        if (p <= 0.0) {
            return false;
        }
        return ThreadLocalRandom.current().nextDouble() < p;
    }

    public static final class Data {
        public boolean enabled = false;
        public Map<String, Double> entities = new LinkedHashMap<>();

        static Data defaults() {
            Data d = new Data();
            d.enabled = false;
            d.entities = new LinkedHashMap<>();
            d.entities.put("minecraft:phantom", 0.0);
            d.entities.put("minecraft:creeper", 0.25);
            return d;
        }
    }
}
