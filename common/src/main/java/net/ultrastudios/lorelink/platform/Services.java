package net.ultrastudios.lorelink.platform;

import net.ultrastudios.lorelink.Constants;
import net.ultrastudios.lorelink.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public class Services {
    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
