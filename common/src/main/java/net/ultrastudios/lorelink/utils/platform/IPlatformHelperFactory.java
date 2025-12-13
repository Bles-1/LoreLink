package net.ultrastudios.lorelink.utils.platform;

import org.slf4j.Logger;

public interface IPlatformHelperFactory {
    IPlatformHelper get(String modID, Logger logger);
}
