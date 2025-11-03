# Helpers documentation
This is documentation for Lore Link's util classes.

Helpers are localized in `net.ultrastudios.lorelink.utils` package.

## Table of contents:

- [Table of contents](#table-of-contents)
- [Action Context](#action-context)
- [Ultra Config](#ultra-config)
- [Advancements](#advancements)
- [BanListHelper](#ban-list-helper)

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