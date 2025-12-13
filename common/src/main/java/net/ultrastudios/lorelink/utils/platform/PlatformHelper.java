package net.ultrastudios.lorelink.utils.platform;

import net.ultrastudios.lorelink.Services;

public final class PlatformHelper {
    public static final IPlatformHelperFactory FACTORY = Services.load(IPlatformHelperFactory.class);
}
