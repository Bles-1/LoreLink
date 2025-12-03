package net.ultrastudios.lorelink;

import net.fabricmc.api.ModInitializer;
import net.ultrastudios.lorelink.platform.FabricPlatformHelper;

public class Lorelink implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init(new FabricPlatformHelper(Constants.MOD_ID, Constants.LOG));
    }
}
