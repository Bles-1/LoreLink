package net.ultrastudios.lorelink.platform;

import net.ultrastudios.lorelink.utils.platform.IPlatformHelper;
import net.ultrastudios.lorelink.utils.platform.IPlatformHelperFactory;
import org.slf4j.Logger;

public class FabricPlatformHelperFactory implements IPlatformHelperFactory {
    @Override
    public IPlatformHelper get(String modID, Logger logger) {
        return new FabricPlatformHelper(modID, logger);
    }
}
