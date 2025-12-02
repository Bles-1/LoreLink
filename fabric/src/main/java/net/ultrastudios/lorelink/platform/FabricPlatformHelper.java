package net.ultrastudios.lorelink.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.ultrastudios.lorelink.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class FabricPlatformHelper implements IPlatformHelper {

    private final String MOD_ID;
    private final Queue<Runnable> nextTickTasks = new ConcurrentLinkedQueue<>();
    private final Map<Runnable, Integer> delayed = new ConcurrentHashMap<>();
    private final Logger LOGGER;

    private static MinecraftServer SERVER;
    static {
        ServerLifecycleEvents.SERVER_STARTED.register(s -> SERVER = s);
        ServerLifecycleEvents.SERVER_STOPPED.register(s -> SERVER = null);
    }

    public FabricPlatformHelper(String modId, Logger logger) {
        MOD_ID = modId;
        LOGGER = logger;
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // next tick tasks
            Runnable r;
            while ((r = nextTickTasks.poll()) != null) {
                server.submit(r).exceptionally(ex -> {
                    LOGGER.error("Error while performing next tick delayed action.", ex);
                    return null;
                });
            }

            // delayed tasks
            delayed.replaceAll((task, ticks) -> ticks - 1);
            delayed.entrySet().removeIf(entry -> {
                if (entry.getValue() <= 0) {
                    server.submit(entry.getKey()).exceptionally(ex -> {
                        LOGGER.error("Error while performing delayed action.", ex);
                        return null;
                    });
                    return true;
                }
                return false;
            });
        });
    }

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> block) {
        T registered = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(MOD_ID, id), block.get());
        return () -> registered;
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        T registered = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, id), item.get());
        return () -> registered;
    }

    @Override
    public GameRules.Key<GameRules.BooleanValue> registerBooleanGameRule(String id, GameRules.Category category, boolean defaultValue) {
        return GameRuleRegistry.register(id, category, GameRuleFactory.createBooleanRule(defaultValue));
    }

    @Override
    public GameRules.Key<GameRules.IntegerValue> registerIntegerGameRule(String id, GameRules.Category category, int defaultValue) {
        return GameRuleRegistry.register(id, category, GameRuleFactory.createIntRule(defaultValue));
    }

    @Override
    public CreativeModeTab registerCreativeTab(String id, Component title, Supplier<ItemStack> icon) {
        return FabricItemGroup.builder().icon(icon).title(title).build();
    }

    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public @Nullable MinecraftServer getCurrentServer() {
        return SERVER;
    }

    @Override
    public void runOnClient(Runnable task) {
        Minecraft.getInstance().submit(task).exceptionally(ex -> {
            LOGGER.error("Error while performing run client-side action.", ex);
            return null;
        });
    }

    @Override
    public void runOnServer(Runnable task) {
        var server = SERVER instanceof MinecraftServer s ? s : null;

        if (server != null) {
            server.submit(task).exceptionally(ex -> {
                LOGGER.error("Error while performing run server-side action.", ex);
                return null;
            });
        }
    }

    @Override
    public void runNextTick(Runnable task) {
        nextTickTasks.add(task);
    }

    @Override
    public void runLater(Runnable task, int ticks) {
        delayed.put(task, ticks);
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public boolean isServer() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }

    @Override
    public <T extends Entity> Supplier<EntityType<T>> registerEntity(String id, EntityType.Builder<T> builder) {
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
        var rk = ResourceKey.create(Registries.ENTITY_TYPE, rl);
        var registered = Registry.register(BuiltInRegistries.ENTITY_TYPE, rl, builder.build(rk));
        return () -> registered;
    }

    @Override
    public Supplier<ParticleType<?>> registerParticle(String id, ParticleType<?> factory) {
        var registered = Registry.register(BuiltInRegistries.PARTICLE_TYPE, ResourceLocation.fromNamespaceAndPath(MOD_ID, id), factory);
        return () -> registered;
    }
}
