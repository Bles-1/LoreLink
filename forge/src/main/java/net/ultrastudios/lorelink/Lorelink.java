package net.ultrastudios.lorelink;

import net.minecraftforge.fml.common.Mod;
import net.ultrastudios.lorelink.platform.ForgePlatformHelper;

@Mod(Constants.MOD_ID)
public class Lorelink {

    public Lorelink() {
        CommonClass.init(new ForgePlatformHelper(Constants.MOD_ID));

    }
}
