package net.ultrastudios.lorelink.internal.config.duplicatestrategy;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DuplicateStrategyConfig {
    @NotNull
    public DuplicateStrategy default_strategy = new DuplicateStrategy();
    @NotNull
    public Map<String, DuplicateStrategy> mods = Map.of();
}
