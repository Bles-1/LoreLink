package net.ultrastudios.lorelink;

import net.ultrastudios.lorelink.internal.config.Config;
import net.ultrastudios.lorelink.utils.config.UltraConfigManager;
import net.ultrastudios.lorelink.utils.platform.IPlatformHelper;
import net.ultrastudios.lorelink.utils.platform.PlatformHelper;

public class CommonClass {

    public static final IPlatformHelper PLATFORM = PlatformHelper.FACTORY.get(Constants.MOD_ID, Constants.LOG);

    public static void init() {
        UltraConfigManager.register(Constants.MOD_ID, PLATFORM.getConfigDir(), Config.class, new Config());
        UltraConfigManager.reload(Constants.MOD_ID);
    }
}
