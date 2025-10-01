package net.ultrastudios.lorelink.internal.config;

import net.ultrastudios.lorelink.internal.config.duplicatestrategy.DuplicateStrategyConfig;
import org.jetbrains.annotations.NotNull;

public class Config {
    @NotNull
    public Extensions extensions = new Extensions();
    @NotNull
    public DuplicateStrategyConfig duplicate_strategy = new DuplicateStrategyConfig();
}
