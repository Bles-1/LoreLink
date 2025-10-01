package net.ultrastudios.lorelink.internal.config.duplicatestrategy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DuplicateStrategy {
    @NotNull
    public Strategy strategy = Strategy.FAIL;
    @Nullable
    public String id;
    @Nullable
    public List<String> order;
}
