package net.ultrastudios.lorelink.utils.actioncontext.blockusecontext;

import net.minecraft.server.level.ServerLevel;

public record ServerBlockUseContext(BlockUseContext base) {
    public ServerLevel level() {
        return (ServerLevel) base.level();
    }
}
