package net.ultrastudios.lorelink.utils.actioncontext.blockusecontext;

import net.minecraft.client.multiplayer.ClientLevel;

public record ClientBlockUseContext(BlockUseContext base) {
    public ClientLevel level() {
        return (ClientLevel) base.level();
    }
}
