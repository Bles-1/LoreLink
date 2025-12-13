package net.ultrastudios.lorelink;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class Lorelink {

    public Lorelink(IEventBus eventBus) {
        CommonClass.init();
    }
}
