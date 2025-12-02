package net.ultrastudios.lorelink.platform.services;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

public interface IPlatformHelper {

    // -----------------------------------------------------
    //  ENVIRONMENT + MOD LOADING
    // -----------------------------------------------------

    /**
     * Returns the name of the current platform (e.g. "forge", "neoforge", "fabric").
     *
     * @return the platform name
     */
    String getPlatformName();

    /**
     * Checks whether a mod with the given namespace is loaded.
     *
     * @param modId the namespace (mod id) to check
     * @return true if the mod is loaded, false otherwise
     */
    boolean isModLoaded(String modId);

    /**
     * Returns whether the game is running in a development environment.
     * This typically depends on the loader implementation.
     *
     * @return true if running in a development environment, false otherwise
     */
    boolean isDevelopmentEnvironment();

    /**
     * Convenience method returning a human-readable environment name.
     *
     * @return "development" if in dev environment, otherwise "production"
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }


    // -----------------------------------------------------
    //  BASIC REGISTRIES
    // -----------------------------------------------------

    /**
     * Registers a new block.
     *
     * @param id    block identifier (path only; namespace assumed to be your mod id)
     * @param block supplier creating the block instance
     * @param <T>   block class type
     * @return a supplier returning the registered block
     */
    <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> block);

    /**
     * Registers a new item.
     *
     * @param id    item identifier (path only; namespace assumed to be your mod id)
     * @param item  supplier creating the item instance
     * @param <T>   item class type
     * @return a supplier returning the registered item
     */
    <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item);


    /**
     * Registers a boolean game rule.
     *
     * @param id          name of the rule
     * @param category    rule category
     * @param defaultValue default value
     * @return the created game rule key
     */
    GameRules.Key<GameRules.BooleanValue> registerBooleanGameRule(
            String id,
            GameRules.Category category,
            boolean defaultValue
    );

    /**
     * Registers an integer game rule.
     *
     * @param id           name of the rule
     * @param category     rule category
     * @param defaultValue default value
     * @return the created game rule key
     */
    GameRules.Key<GameRules.IntegerValue> registerIntegerGameRule(
            String id,
            GameRules.Category category,
            int defaultValue
    );

    CreativeModeTab registerCreativeTab(String id, Component title, Supplier<ItemStack> icon);

    /**
     * Registers a new entity type.
     *
     * @param id       entity identifier (path only)
     * @param builder  a prepared EntityType.Builder instance
     * @param <T>      entity class type
     * @return the registered EntityType
     */
    <T extends Entity> Supplier<EntityType<T>> registerEntity(
            String id,
            EntityType.Builder<T> builder
    );

    /**
     * Registers a new particle type.
     *
     * @param id        identifier (path only)
     * @param factory   the codec or factory used to create the particle type
     * @return the registered ParticleType
     */
    Supplier<ParticleType<?>> registerParticle(
            String id,
            ParticleType<?> factory
    );


    // -----------------------------------------------------
    //  PATHS / DIRECTORIES
    // -----------------------------------------------------


    /**
     * Returns the directory where the game/server instance is located.
     * On clients, this is usually the .minecraft folder.
     *
     * @return the game root directory
     */
    Path getGameDir();

    /**
     * Returns the directory used for storing configuration files.
     *
     * @return the config directory
     */
    Path getConfigDir();

    /**
     * Returns the currently running dedicated or integrated server instance.
     *
     * @return current server, or null if no server is running
     */
    @Nullable MinecraftServer getCurrentServer();


    // -----------------------------------------------------
    //  SCHEDULING / SIDING HELPERS
    // -----------------------------------------------------

    /**
     * Executes the given task on the logical client thread.
     *
     * @param task code to run on client
     */
    void runOnClient(Runnable task);

    /**
     * Executes the given task on the logical server thread.
     *
     * @param task code to run on server
     */
    void runOnServer(Runnable task);

    /**
     * Schedules a task to run on the next tick (server or client depending on context).
     *
     * @param task task to execute next tick
     */
    void runNextTick(Runnable task);

    /**
     * Executes the given task after a specific number of ticks.
     *
     * @param task  code to run
     * @param ticks delay in ticks
     */
    void runLater(Runnable task, int ticks);

    /**
     * @return true if current code is executing on the logical client
     */
    boolean isClient();

    /**
     * @return true if current code is executing on the logical server
     */
    boolean isServer();
}

