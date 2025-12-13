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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.ultrastudios.lorelink.utils.platform.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public class ForgePlatformHelper implements IPlatformHelper {

    @Mod.EventBusSubscriber
    public static class ForgeScheduler {

        private static final Queue<Runnable> nextTick = new ArrayDeque<>();

        private static final class DelayedTask {
            int ticks;
            Runnable task;

            DelayedTask(int ticks, Runnable task) {
                this.ticks = ticks;
                this.task = task;
            }
        }

        private static final Queue<DelayedTask> delayed = new ArrayDeque<>();

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

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

        public static void runOnServer(Runnable task) {
            if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer().isSameThread()) {
                task.run();
            } else {
                runNextTick(task);
            }
        }
    }

    private final String MOD_ID;
    private final DeferredRegister<Block> BLOCKS;
    private final DeferredRegister<Item> ITEMS;
    private final DeferredRegister<EntityType<?>> ENTITIES;
    private final DeferredRegister<ParticleType<?>> PARTICLES;

    public ForgePlatformHelper(String modID) {
        MOD_ID = modID;
        BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
        ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
        ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);
        PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MOD_ID);
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
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> task::run);
    }

    @Override
    public void runOnServer(Runnable task) {
        ForgeScheduler.runOnServer(task);
    }

    @Override
    public void runNextTick(Runnable task) {
        ForgeScheduler.runNextTick(task);
    }

    @Override
    public void runLater(Runnable task, int ticks) {
        ForgeScheduler.runLater(task, ticks);
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
