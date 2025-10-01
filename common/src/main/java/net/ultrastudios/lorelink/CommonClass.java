package net.ultrastudios.lorelink;

import net.ultrastudios.lorelink.internal.config.Config;
import net.ultrastudios.lorelink.platform.Services;
import net.ultrastudios.lorelink.utils.config.UltraConfigManager;

public class CommonClass {

    public static void init() {
        UltraConfigManager.register(Constants.MOD_ID, Services.PLATFORM.getConfigDir(), Config.class, new Config());
        UltraConfigManager.reload(Constants.MOD_ID);
    }
}
