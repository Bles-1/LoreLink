package net.ultrastudios.lorelink.modsconfig.shr;

import net.ultrastudios.lorelink.Constants;
import net.ultrastudios.lorelink.SUPPORTED_MODS;
import net.ultrastudios.lorelink.internal.config.Config;
import net.ultrastudios.lorelink.internal.config.duplicatestrategy.DuplicateStrategy;
import net.ultrastudios.lorelink.internal.config.duplicatestrategy.DuplicateStrategyConfig;
import net.ultrastudios.lorelink.internal.config.duplicatestrategy.Strategy;
import net.ultrastudios.lorelink.utils.config.UltraConfigManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class Integrations {

    @Nullable
    private static Function<IActionHandler, IActionHandler> customActionHandlerFactory = null;
    @Nullable
    private static String activeExtensionID = null;
    @NotNull
    private static final Map<String, Function<IActionHandler, IActionHandler>> combinedActionHandlerFactories = new HashMap<>();

    public static IActionHandler getActionHandler(IActionHandler defaultHandler) {
        if (customActionHandlerFactory != null)
            return customActionHandlerFactory.apply(defaultHandler);
        if (!combinedActionHandlerFactories.isEmpty())
            return assemble(defaultHandler, combinedActionHandlerFactories);
        //
        //  Future mods auto-configuration logic.
        //
        return defaultHandler;
    }

    public static void registerCustomActionHandler(Function<IActionHandler, IActionHandler> factory, String modID) {
        DuplicateStrategyConfig strategyConfig = UltraConfigManager.get(Constants.MOD_ID, Config.class).get().duplicate_strategy;
        DuplicateStrategy modStrategy = strategyConfig.mods
                .getOrDefault(SUPPORTED_MODS.SimpleHardcoreRespawn.getModId(), strategyConfig.default_strategy);

        switch (modStrategy.strategy) {

            case Strategy.FAIL -> {
                if (customActionHandlerFactory == null) {
                    customActionHandlerFactory = factory;
                    activeExtensionID = modID;
                }
                else throw new RuntimeException("Multiple mods tried to register custom action handler for Simple Hardcore Respawn. To avoid this error, try deleting conflicted mods or choose one in config/ultraconfig/lorelink.json file in duplicate_strategy field.");
            }

            case Strategy.FIRST -> {
                if (customActionHandlerFactory == null) customActionHandlerFactory = factory;
            }

            case Strategy.LAST -> customActionHandlerFactory = factory;

            case Strategy.COMBINE -> {
                if (modStrategy.order != null && modStrategy.order.contains(modID))
                    combinedActionHandlerFactories.put(modID, factory);
            }

            case Strategy.ONLY -> {
                if (Objects.equals(modID, modStrategy.id)) customActionHandlerFactory = factory;
                else if (modStrategy.id == null && modStrategy.order != null) {

                    if (activeExtensionID == null) customActionHandlerFactory = factory;

                    if (modStrategy.order.indexOf(modID) < modStrategy.order.indexOf(activeExtensionID)) {
                        customActionHandlerFactory = factory;
                    }
                }
            }
        }
    }

    private static IActionHandler assemble(IActionHandler defaultHandler, Map<String, Function<IActionHandler, IActionHandler>> factories) {
        DuplicateStrategyConfig strategyConfig = UltraConfigManager.get(Constants.MOD_ID, Config.class).get().duplicate_strategy;
        DuplicateStrategy modStrategy = strategyConfig.mods
                .getOrDefault(SUPPORTED_MODS.SimpleHardcoreRespawn.getModId(), strategyConfig.default_strategy);
        List<String> order = modStrategy.order;
        if (order == null) return defaultHandler;

        IActionHandler result = defaultHandler;
        for (int i = order.size() - 1; i >= 0; i--) {
            result = factories.get(order.get(i)).apply(result);
        }
        return result;
    }
}
