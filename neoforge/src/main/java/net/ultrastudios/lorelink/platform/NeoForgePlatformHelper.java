package net.ultrastudios.lorelink.platform;

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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.ultrastudios.lorelink.platform.services.IPlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

public class NeoForgePlatformHelper implements IPlatformHelper {

    public static class NeoForgeScheduler {

        private static final Queue<Runnable> nextTick = new ArrayDeque<>();
        private static final Queue<DelayedTask> delayed = new ArrayDeque<>();

        public static void init() {
            NeoForge.EVENT_BUS.addListener(NeoForgeScheduler::onServerTick);
        }

        private static final class DelayedTask {
            int ticks;
            Runnable task;

            DelayedTask(int ticks, Runnable task) {
                this.ticks = ticks;
                this.task = task;
            }
        }

        public static void onServerTick(ServerTickEvent.Post event) {

            // --- Next tick tasks ---
            while (!nextTick.isEmpty()) {
                nextTick.poll().run();
            }

            // --- Delayed tasks ---
            delayed.removeIf(d -> {
                d.ticks--;
                if (d.ticks <= 0) {
                    d.task.run();
                    return true;
                }
                return false;
            });
        }

        public static void runNextTick(Runnable task) {
            nextTick.add(task);
        }

        public static void runLater(Runnable task, int ticks) {
            delayed.add(new DelayedTask(ticks, task));
        }
    }

    private final String MOD_ID;
    private final DeferredRegister<Block> BLOCKS;
    private final DeferredRegister<Item> ITEMS;
    private final DeferredRegister<EntityType<?>> ENTITIES;
    private final DeferredRegister<ParticleType<?>> PARTICLES;

    public NeoForgePlatformHelper(String modID) {
        MOD_ID = modID;
        BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, MOD_ID);
        ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MOD_ID);
        ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MOD_ID);
        PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MOD_ID);
    }

    public void initScheduler() {
        NeoForgeScheduler.init();
    }

    public void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        PARTICLES.register(bus);
    }

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> block) {
        return BLOCKS.register(id, block);
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        return ITEMS.register(id, item);
    }

    @Override
    public GameRules.Key<GameRules.BooleanValue> registerBooleanGameRule(String id, GameRules.Category category, boolean defaultValue) {
        return GameRules.register(id, category, GameRules.BooleanValue.create(defaultValue));
    }

    @Override
    public GameRules.Key<GameRules.IntegerValue> registerIntegerGameRule(String id, GameRules.Category category, int defaultValue) {
        return GameRules.register(id, category, GameRules.IntegerValue.create(defaultValue));
    }

    @Override
    public CreativeModeTab registerCreativeTab(String id, Component title, Supplier<ItemStack> icon) {
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(MOD_ID, id),
                CreativeModeTab.builder()
                        .icon(icon)
                        .displayItems((params, output) -> {})
                        .build());
    }

    @Override
    public <T extends Entity> Supplier<EntityType<T>> registerEntity(String id, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MOD_ID, id));
        return ENTITIES.register(id, () -> builder.build(key));
    }

    @Override
    public Supplier<ParticleType<?>> registerParticle(String id, ParticleType<?> factory) {
        return PARTICLES.register(id, () -> factory);
    }

    @Override
    public Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public @Nullable MinecraftServer getCurrentServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public void runOnClient(Runnable task) {
        if (net.neoforged.fml.loading.FMLLoader.getDist().isClient()) {
            task.run();
        }
    }

    @Override
    public void runOnServer(Runnable task) {
        NeoForgeScheduler.runNextTick(task);
    }

    @Override
    public void runNextTick(Runnable task) {
        NeoForgeScheduler.runNextTick(task);
    }

    @Override
    public void runLater(Runnable task, int ticks) {
        NeoForgeScheduler.runLater(task, ticks);
    }

    @Override
    public boolean isClient() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    @Override
    public boolean isServer() {
        return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
    }
}
