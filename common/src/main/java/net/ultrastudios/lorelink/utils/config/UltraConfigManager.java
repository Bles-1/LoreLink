package net.ultrastudios.lorelink.utils.config;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;

public class UltraConfigManager {
    private static final HashMap<String, UltraConfig<?>> configs = new HashMap<>();

    public static <T> void register(String modID, @NotNull Path configDir, Class<T> tClass, T defaultConfig) {
        configs.put(modID, new UltraConfig<T>(tClass, configDir
                .resolve("ultraconfig")
                .resolve(modID + ".json"),defaultConfig));
        configs.get(modID).load();
    }

    public static <T> @NotNull UltraConfig<T> get(String modID, Class<T> tClass) throws IllegalArgumentException {
        UltraConfig<?> config = configs.get(modID);
        if (config != null && config.getType().equals(tClass)) {
            //noinspection unchecked
            return (UltraConfig<T>) config;
        }
        else throw new IllegalArgumentException("Config not found for modID: " + modID + " with type: " + tClass.getName());
    }

    public static boolean contains(String modID) {
        return configs.containsKey(modID);
    }

    public static void reload(String modID) {
        configs.get(modID).load();
    }

    public static UltraConfig<?> getRaw(String modID) {
        return configs.get(modID);
    }

    public static <T> Optional<UltraConfig<T>> getOptional(String modID, Class<T> tClass) {
        UltraConfig<?> config = configs.get(modID);
        if (config != null && config.getType().equals(tClass)) {
            //noinspection unchecked
            return Optional.of((UltraConfig<T>) config);
        }
        else return Optional.empty();
    }
}
