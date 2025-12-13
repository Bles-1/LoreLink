package net.ultrastudios.lorelink.utils.platform;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Supplier;

public interface IPlatformHelper {

    // -----------------------------------------------------
    //  ENVIRONMENT + MOD LOADING
    // -----------------------------------------------------

    /**
     * Returns the name of the current platform.
     *
     * @return the platform name
     */
    String getPlatformName();

    /**
     * Checks whether a mod with the given namespace is loaded.
     *
     * @param modId the namespace (mod id) to check
     * @return true if the mod is loaded, otherwise false
     */
    boolean isModLoaded(String modId);

    /**
     * Checks whether the game is running in a development environment.
     *
     * @return true if running in a development environment, otherwise false
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
     * @param id    block identifier (without namespace)
     * @param block the block factory
     * @param <T>   block class type
     * @return a supplier returning the registered block
     */
    <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> block);

    /**
     * Registers a new item.
     *
     * @param id    item identifier (without namespace)
     * @param item  the item factory
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

    /**
     * Registers a new creative mode inventory tab.
     * @param id    tab identifier (without namespace)
     * @param title visible tittle of tab
     * @param icon  visible icon of tab
     * @return the created creative tab
     */
    CreativeModeTab registerCreativeTab(String id, Component title, Supplier<ItemStack> icon);

    /**
     * Registers a new entity type.
     *
     * @param id       entity identifier (without namespace)
     * @param builder  an EntityType.Builder instance
     * @param <T>      entity class type
     * @return the registered EntityType supplier
     */
    <T extends Entity> Supplier<EntityType<T>> registerEntity(
            String id,
            EntityType.Builder<T> builder
    );

    /**
     * Registers a new particle type.
     *
     * @param id        identifier (without namespace)
     * @param factory   the particle type factory
     * @return the registered ParticleType supplier
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
     * Returns the directory for configuration files.
     *
     * @return the config directory
     */
    Path getConfigDir();

    /**
     * Returns the currently running server instance.
     *
     * @return current server, or null if no server is running
     */
    @Nullable MinecraftServer getCurrentServer();


    // -----------------------------------------------------
    //  SCHEDULING / SIDING HELPERS
    // -----------------------------------------------------

    /**
     * Executes the given task client-side.
     *
     * @param task code to run on client
     */
    void runOnClient(Runnable task);

    /**
     * Executes the given task server-side.
     *
     * @param task code to run on server
     */
    void runOnServer(Runnable task);

    /**
     * Schedules a task to run on the next tick
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

