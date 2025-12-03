package net.ultrastudios.lorelink;

import net.ultrastudios.lorelink.internal.config.Config;
import net.ultrastudios.lorelink.platform.services.IPlatformHelper;
import net.ultrastudios.lorelink.utils.config.UltraConfigManager;

public class CommonClass {

    public static IPlatformHelper PLATFORM;

    public static void init(IPlatformHelper platformHelperInstance) {
        PLATFORM = platformHelperInstance;
        UltraConfigManager.register(Constants.MOD_ID, PLATFORM.getConfigDir(), Config.class, new Config());
        UltraConfigManager.reload(Constants.MOD_ID);
    }
}
