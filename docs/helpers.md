# Helpers documentation
This is documentation for Lore Link's util classes.

Helpers are localized in `net.ultrastudios.lorelink.utils` package.

## Table of contents:

- [Platform helpers](#platform-helpers)
- [Table of contents](#table-of-contents)
- [Action Context](#action-context)
- [Ultra Config](#ultra-config)
- [Advancements](#advancements)
- [BanListHelper](#ban-list-helper)

## Platform helpers

Lore Link provides IPlatformHelper interface with Forge Neoforge and Fabric implementations. It is used to simplify platform specific actions, e.g., registering objects, checking environment, or delaying tasks.

Package: `net.ultrastudios.lorelink.utils.platform`

> Note that IPlatformHelper doesn't cover **all** functionalities. If you think there should be something more, feel free to open a pull request!

### Usage:
You can ger factory from `PlatformHelper#FACTORY`, and then run `IPlatformHelperFactory#get(String modID, Logger logger)` to get new object.

List of IPlatformHelper methods:

| Method                                                                                                                          | Description                                                                                                         | Parameters                                                                                                        | Returns                                                       |
|---------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------|
| `String getPlatformName()`                                                                                                      | Returns the name of the current platform.                                                                           | —                                                                                                                 | the platform name                                             |
| `boolean isModLoaded(String modId)`                                                                                             | Checks whether a mod with the given namespace is loaded.                                                            | modId – the namespace (mod id) to check                                                                           | true if the mod is loaded, otherwise false                    |
| `boolean isDevelopmentEnvironment()`                                                                                            | Checks whether the game is running in a development environment.                                                    | —                                                                                                                 | true if running in a development environment, otherwise false |
| `String getEnvironmentName()`                                                                                                   | Convenience method returning a human-readable environment name.                                                     | —                                                                                                                 | "development" if in dev environment, otherwise "production"   |
| `<T extends Block> Supplier<T> registerBlock(String id, Supplier<T> block)`                                                     | Registers a new block.                                                                                              | id – block identifier (without namespace)<br>block – the block factory<br><T> – block class type                  | a supplier returning the registered block                     |
| `<T extends Item> Supplier<T> registerItem(String id, Supplier<T> item)`                                                        | Registers a new item.                                                                                               | id – item identifier (without namespace)<br>item – the item factory<br><T> – item class type                      | a supplier returning the registered item                      |
| `GameRules.Key<GameRules.BooleanValue> registerBooleanGameRule( String id, GameRules.Category category, boolean defaultValue )` | Registers a boolean game rule.                                                                                      | id – name of the rule<br>category – rule category<br>defaultValue – default value                                 | the created game rule key                                     |
| `GameRules.Key<GameRules.IntegerValue> registerIntegerGameRule( String id, GameRules.Category category, int defaultValue )`     | Registers an integer game rule.                                                                                     | id – name of the rule<br>category – rule category<br>defaultValue – default value                                 | the created game rule key                                     |
| `CreativeModeTab registerCreativeTab(String id, Component title, Supplier<ItemStack> icon)`                                     | Registers a new creative mode inventory tab.                                                                        | id – tab identifier (without namespace)<br>title – visible tittle of tab<br>icon – visible icon of tab            | the created creative tab                                      |
| `<T extends Entity> Supplier<EntityType<T>> registerEntity( String id, EntityType.Builder<T> builder )`                         | Registers a new entity type.                                                                                        | id – entity identifier (without namespace)<br>builder – an EntityType.Builder instance<br><T> – entity class type | the registered EntityType supplier                            |
| `Supplier<ParticleType<?>> registerParticle( String id, ParticleType<?> factory )`                                              | Registers a new particle type.                                                                                      | id – identifier (without namespace)<br>factory – the particle type factory                                        | the registered ParticleType supplier                          |
| `Path getGameDir()`                                                                                                             | Returns the directory where the game/server instance is located. On clients, this is usually the .minecraft folder. | —                                                                                                                 | the game root directory                                       |
| `Path getConfigDir()`                                                                                                           | Returns the directory for configuration files.                                                                      | —                                                                                                                 | the config directory                                          |
| `@Nullable MinecraftServer getCurrentServer()`                                                                                  | Returns the currently running server instance.                                                                      | —                                                                                                                 | current server, or null if no server is running               |
| `void runOnClient(Runnable task)`                                                                                               | Executes the given task client-side.                                                                                | task – code to run on client                                                                                      | —                                                             |
| `void runOnServer(Runnable task)`                                                                                               | Executes the given task server-side.                                                                                | task – code to run on server                                                                                      | —                                                             |
| `void runNextTick(Runnable task)`                                                                                               | Schedules a task to run on the next tick                                                                            | task – task to execute next tick                                                                                  | —                                                             |
| `void runLater(Runnable task, int ticks)`                                                                                       | Executes the given task after a specific number of ticks.                                                           | task – code to run<br>ticks – delay in ticks                                                                      | —                                                             |
| `boolean isClient()`                                                                                                            | —                                                                                                                   | —                                                                                                                 | true if current code is executing on the logical client       |
| `boolean isServer()`                                                                                                            | —                                                                                                                   | —                                                                                                                 | true if current code is executing on the logical server       |

## Action Context

Action contexts are records used to wrap parameters. Instead of passing tons of parameters like level, player, or pos to your method, you can wrap them into small, clean data records.

Currently added records:

### Block Use context

This record is originally built for `Block#useItemOn` method.

`net.ultrastudios.lorelink.utils.actioncontext.BlockUseContext`     
Contains these parameters:

```java
@NotNull ItemStack pStack;
@NotNull BlockState pState;
@NotNull Level level;
@NotNull BlockPos pPos;
@NotNull Player pPlayer;
@NotNull InteractionHand pHand;
@NotNull BlockHitResult pHitResult;
```

`net.ultrastudios.lorelink.utils.actioncontext.ServerBlockUseContext`  
It's server-side version that helps you to make sure that method will be executed server-side. It contains:

```java
BlockUseContext base;
public ServerLevel level();
```

`level()` function returns `base.level` cast to `ServerLevel`.

`net.ultrastudios.lorelink.utils.actioncontext.ClientBlockUseContext`   
It's client-side version that helps you to make sure that method will be executed client-side. It works the same as `ServerBlockUseContext` but with `ClientLevel` of course.

## Ultra Config
We provide a simple JSON-based configuration system for mods that use Lore Link.

This system contains:
- `net.ultrastudios.lorelink.utils.config.UltraConfigManager` Contains static methods to manage configs.
- `net.ultrastudios.lorelink.utils.config.UltraConfig<T>` Represents config instance.
- `net.ultrastudios.lorelink.utils.config.IFallbackEnum` Additional interface that will return fallback value instead of crashing, when field in config is filled incorrectly.

### Usage

1. To create your config, first make a POJO class representing json structure.

    > TIP: You can use `@Nullable` and `@NotNull` annotations to remember which parameters are necessary and which optional.

2.  Register your config using `static <T> void register(String modID, @NotNull Path configDir, Class<T> tClass, T defaultConfig)`

    | Parameter     | Description                                                                                                                                                        |
    |---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
    | modID         | ID of your mod, used for config file name.                                                                                                                         |
    | configDir     | Directory of your config file. Usually `<instance>/config`. On neo-/forge use `FMLPaths.CONFIGDIR.get()` and on fabric `FabricLoader.getInstance().getConfigDir()` |
    | tClass        | `YourConfigClass.class`, used for casting and deserializing.                                                                                                       |
    | defaultConfig | Implementation of your class with default values.                                                                                                                  |

    ```java
    // ...
    UltraConfigManager.register(MOD_ID, FMLPaths.CONFIGDIR.get() /*or FabricLoader.getInstance().getConfigDir();*/, YourConfig.class, new YourConfig());
    // ...
    ```

3. After registration, a file will be read or created. You can now use you config by these methods in Config Manager:

| 🧩 Method     | Returns                    | Parameters                                                | Description                                                                                                                                                     |
|---------------|----------------------------|-----------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| get()         | `@NotNull UltraConfig<T>`  | `String modID`, `Class<T> tClass` (YourConfigClass.class) | Returns an instance of `UltraConfig<T>` containing current config.                                                                                              |
| getRaw()      | `UltraConfig<?>`           | `String modID`                                            | The same as `get()` but returns raw wildcard class, without suppressed cast.                                                                                    |
| getOptional() | `Optional<UltraConfig<T>>` | `String modID`, `Class<T> tClass`                         | The same as `get()` but returns `Optional.empty()` on eventual fail.                                                                                            |
| reload()      | `null`                     | `String modID`                                            | Reloads config from file. (Lore Link stores buffered data) Can be used for hot changes, but not recommended as configs are intended to be loaded on game start. |
| contains()    | `bool`                     | `String modID`                                            | Checks if config for this mod has been registered.                                                                                                              |

### UltraConfig class:

`UltraConfig<T>` is generic class that stores current config settings. It's methods:

| 🧩 Method   | Returns    | Parameters                                                                     | Description                                                                                          |
|-------------|------------|--------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------|
| constructor |            | `Class<T> tClass`, `Path path` (file where config is stored), `T defaultValue` | Constructor of this class. Usually UltraConfig objects are created via UltraConfigManager.           |
| get()       | `T`        |                                                                                | Returns buffer.                                                                                      |
| load()      | `void`     |                                                                                | Loads config from file to buffer. Usually should be executed once on minecraft setup.                |
| save()      | `void`     | `T data`                                                                       | Saves given data to file and buffer. (to change config in-game or during registration)               |
| update()    | `void`     |                                                                                | Saves buffer to file. (can be useful if you are manually modifying buffer, although not recommended) |
| getType()   | `Class<T>` |                                                                                | Returns type of your config class.                                                                   |

### IFallbackEnum

`IFallbackEnum<T extends Enum<T>>` interface that provides `T getFallback()` method to get default enum value. Supported by UltraConfig's deserializer so it won't throw exceptions if enum is typed wrong in json file.

Example:
```java
public enum Strategy implements IFallbackEnum<Strategy> {
    FAIL,
    FIRST,
    LAST,
    ONLY,
    COMBINE,
    NONE;

    @Override
    public Strategy getFallback() {
        return FAIL;
    }
}
```

## Advancements
`net.ultrastudios.lorelink.utils.Advancements` is class that contains utility methods to manage advancements.

### grantOfflineAdvancement()
Method to grant advancement for offline player.

Takes 4 parameters and returns boolean denoting success.

| Parameter                 | Description                                      |
|---------------------------|--------------------------------------------------|
| `MinecraftServer server`  | Current server instance. (used to get world dir) |
| `UUID PlayerUUID`         | UUID of target player.                           |
| `String advancementId`    | ID of advancement to grant.                      |
| `Advancement advancement` | Instance of advancement to grant.                |

Method returns true if advancement has been granted, and false if player have advancement or action was failed. (After fail method log the error cause.)

## Ban List Helper
`net.ultrastudios.lorelink.utils.BanListHelper` is class that contains utility methods for banned players list.

## getBannedPlayerUuidByName()
Method that returns `UUID` of player with given nick.

Takes 2 parameters:

| Parameter     | Description                                                                         |
|---------------|-------------------------------------------------------------------------------------|
| `File list`   | File containing ban list. (`level.getServer().getPlayerList().getBans().getFile()`) |
| `String name` | Nick of targeted player.                                                            |