# Extensions Guide
> #### ⚠️ **Extensions are not the same as the mods configuration.**

Extensions are used to extend or override our mods' behaviour. They are written in Java, just like regular minecraft mods, so you need a programming & modding knowledge.

> *Yeah, this is basically modding mods.* 
> 
> *I originally built this API for internal autoconfiguration, but decided to make it public for fun and flexibility.* 

### Table of Contents:
- [Extensions Guide](#extensions-guide)
- [Setup](#-setup)  
- [Mods](#mods)  
  - [Simple Hardcore Respawn](#simple-hardcore-respawn)

---
## ⚙️ Setup
Extensions are regular mods that interact with Lore Link’s internal systems.
Before coding your first extension, make sure you have a working development environment (Forge, NeoForge, or Fabric).

You can make an extension as a separate mod, or as a part of your mod. Regardless of what you choose, you should prepare default modding environment.

> If you decide to make your extension as a part of your mod, remember that there is more conflict probability.   
> If the user installs more than one mod containing an extension for the same target mod without setting the active extension in config, Lore Link will crash.

Your mod should depend on Lore Link. Make it just like every other library dependencies.

##### Forge:
```toml
[[dependencies.YOUR_MOD_ID]]
modID = "lorelink"
mandatory = true
versionRange = "<LORELINK.VERSION>"
ordering = "AFTER"
side = "BOTH"
```

##### NeoForge:
```toml
[[dependencies.YOUR_MOD_ID]]
modID = "lorelink"
type="required"
versionRange = "<LORELINK.VERSION>"
ordering = "AFTER"
side = "BOTH"
```

##### Fabric:
```json
{
  "depends": {
    "lorelink": "<LORELINK.VERSION>"
  }
}
```

Add `compileOnly()` dependency in your `build.gradle` file.  
To test it in IDE, add also `implementation fg.deobf()` (forge), `implementation()` (neoforge), or `modImplementation()` (fabric) with Lore Link and mod that you are modding.

You can import dependency using GitHub packages
(https://github.com/Bles-1/LoreLink)
or CurseMaven
(https://www.curseforge.com/minecraft/mc-mods/lore-link).

> You may also want to add mod that you are configuring as dependency.

> You can always make Lore Link not required, but you have to control your imports to not get `ClassNotFoundException`.

Sometimes you will also need `compileOnly()` dependency for mod you are configuring.

---
# Mods:
Supported mods:
- [Simple Hardcore Respawn](#simple-hardcore-respawn)

---
## Simple Hardcore Respawn

> ##### Extensions in SHR allows you to modify or override ReSpawner behaviour.
> 
> You **can** use it to:
> - Add custom particles
> - Add a special condition to respawn the player
> - Change charging mechanics. E.g., you charge ReSpawner with energy beams
> 
> If you want to:
> - Configure required items
> - Change recipe or textures
> 
> You should use configuration datapack (or resources pack) instead.

In SHR, you can modify ReSpawner behaviour, using `net.ultrastudios.lorelink.modsconfig.shr.IActionHandler` interface and `net.ultrastudios.lorelink.modsconfig.shr.Integrations` static class.

---
### IActionHandler

To override behaviour of ReSpawner, create an implementation of `IActionHandler`. If you want to change only some of the methods, you should use delegation (see below, [🔗Delegation](#delegation)).

Explaining how IActionHandler methods work:

| 🔧 Method                                                               | Type      | Description                                                                            |
|-------------------------------------------------------------------------|-----------|----------------------------------------------------------------------------------------|
| `boolean canCharge(ServerBlockUseContext context)`                      | Condition | Checks if charging can be attempted (e.g. ReSpawner state, gamerule).                  |
| `boolean canRespawn(ServerBlockUseContext context)`                     | Condition | Checks if respawning can be attempted.                                                 |
| `boolean isCharged(ServerBlockUseContext context)`                      | Condition | Returns whether ReSpawner has enough power to respawn player.                          |
| `boolean shouldTriggerEasterEgg(ServerBlockUseContext context)`         | Condition | Checks if Easter Egg should trigger after failed respawn.                              |
| `int getPowerToCharge(ServerBlockUseContext context)`                   | Data      | Returns power amount used to charge.                                                   |
| `InteractionResult getClientResults(ClientBlockUseContext context)`     | Data      | Predicts client-side result for `useItemOn` (controls animation/interaction behavior). |
| `InteractionResult doRespawn(ServerBlockUseContext context)`            | Action    | Handles player respawn logic. Runs respawn/failed effects depending on result.         |
| `InteractionResult doCharge(int pPower, ServerBlockUseContext context)` | Action    | Handles charging logic. Runs charge effects after successful execution.                |
| `InteractionResult doUnban(ServerBlockUseContext context)`              | Action    | Handles unbanning logic when ReSpawnerUnbanning gamerule is enabled.                   |
| `void respawnEffect(ServerBlockUseContext context)`                     | Effect    | Runs visual/sound effects when respawn succeeds.                                       |
| `void respawnFailedEffect(ServerBlockUseContext context)`               | Effect    | Runs visual/sound effects when respawn fails.                                          |
| `void chargeEffect(int pPower, ServerBlockUseContext context)`          | Effect    | Runs effects when charge succeeds.                                                     |
| `void chargeFailedEffect(int pPower, ServerBlockUseContext context)`    | Effect    | Runs effects when charge fails (unused by default).                                    |
| `void easterEggEffect(ServerBlockUseContext context)`                   | Effect    | Runs effects for Easter Egg event.                                                     |
| `void respawnParticlesEffect(ServerBlockUseContext context)`            | Effect    | Runs standard particle effect after successful respawn.                                |

You can check default implementation of `IActionHandler` [HERE](https://github.com/Bles-1/simple_hardcore_respawn/blob/master/common/src/main/java/net/ultrastudios/simplehardcorerespawn/respawner/DefaultActionHandler.java).  

You can check how are those methods executed [HERE](https://github.com/Bles-1/simple_hardcore_respawn/blob/master/common/src/main/java/net/ultrastudios/simplehardcorerespawn/block/ReSpawnerBlock.java).

---
### Overriding

To override default implementation of `IActionHandler` with your implementation, use `net.ultrastudios.lorelink.modsconfig.shr.Integrations#registerCustomActionHandler` method.

| Parameter                                          | Description                                                                     |
|----------------------------------------------------|---------------------------------------------------------------------------------|
| `Function<IActionHandler, IActionHandler> factory` | Factory for your custom handler. Provides default handler, allowing delegation. |
| `String modID`                                     | ID of your mod.                                                                 |

```java
import net.ultrastudios.lorelink.modsconfig.shr.Integrations;
import com.example.yourmod.YourActionHandler;

public class Extension {
    
    public static void Register() {
        Integrations.registerCustomActionHandler((_default) -> new YourActionHandler(), "Your_Mod");
    }
}
```

> Probably you will want to add Simple Hardcore Respawn as compile only dependency to use respawner-specific code like `ReSpawnerBlock#CHARGES` or `ReSpawnerBlock#POWER` block states.

---
### Delegation

If you want to override only some of `IActionHandler` methods and leave rest to default, you have to use delegation. Here an example:

```java
import net.minecraft.world.InteractionResult;
import net.ultrastudios.lorelink.utils.actioncontext.blockusecontext.ClientBlockUseContext;
import net.ultrastudios.lorelink.utils.actioncontext.blockusecontext.ServerBlockUseContext;
import org.jetbrains.annotations.NotNull;

public class DelegationActionHandler implements IActionHandler {
    private final IActionHandler inner;

    public DelegationActionHandler(IActionHandler _default) {
        inner = _default;
    }

    @Override
    public boolean canCharge(@NotNull ServerBlockUseContext context) {
        return inner.canCharge(context) /*&& Additional conditions*/;
    }

    @Override
    public boolean canRespawn(@NotNull ServerBlockUseContext context) {
        return inner.canRespawn(context); // Don't change anything
    }

    @Override
    public boolean isCharged(@NotNull ServerBlockUseContext context) {
        return inner.isCharged(context);
    }

    @Override
    public boolean shouldTriggerEasterEgg(@NotNull ServerBlockUseContext context) {
        return inner.shouldTriggerEasterEgg(context);
    }

    @Override
    public int getPowerToCharge(@NotNull ServerBlockUseContext context) {
        return inner.getPowerToCharge(context);
    }

    @Override
    public @NotNull InteractionResult getClientResults(@NotNull ClientBlockUseContext context) {
        return inner.getClientResults(context);
    }

    @Override
    public @NotNull InteractionResult doRespawn(@NotNull ServerBlockUseContext context) {
        // return inner.doRespawn(context);
        /*Your custom mechanics instead.*/
    }

    @Override
    public @NotNull InteractionResult doCharge(int pPower, @NotNull ServerBlockUseContext context) {
        return inner.doCharge(pPower, context);
    }

    @Override
    public @NotNull InteractionResult doUnban(@NotNull ServerBlockUseContext context) {
        return inner.doUnban(context);
    }

    @Override
    public void respawnEffect(@NotNull ServerBlockUseContext context) {
        inner.respawnEffect(context);
    }

    @Override
    public void respawnFailedEffect(@NotNull ServerBlockUseContext context) {
        inner.respawnFailedEffect(context);
        /*Additional effects*/
    }

    @Override
    public void chargeEffect(int pPower, @NotNull ServerBlockUseContext context) {
        inner.chargeEffect(pPower, context);
    }

    @Override
    public void chargeFailedEffect(int pPower, @NotNull ServerBlockUseContext context) {
        inner.chargeEffect(pPower, context);
    }

    @Override
    public void easterEggEffect(@NotNull ServerBlockUseContext context) {
        inner.easterEggEffect(context);
    }

    @Override
    public void respawnParticlesEffect(@NotNull ServerBlockUseContext context) {
        inner.respawnParticlesEffect(context);
    }
}
```

And register like this:

```java
import net.ultrastudios.lorelink.modsconfig.shr.Integrations;
import com.example.yourmod.DelegationActionHandler;

public class Extension {
    
    public static void Register() {
        Integrations.registerCustomActionHandler((_default) -> new DelegationActionHandler(_default), "Your_Mod");
    }
}
```

---
### Collisions and Chaining
Lore Link prevents multiple extensions from registering at the same time to avoid conflicts.
However, advanced users can enable chaining through config.

If there is more than one mod trying to inject custom `IActionHandler`, an error will occur to prevent unexpected behavior.

User can define how Lore Link should deal with such conflict, see [config guide](./config_guide.md).

If config will be set to `COMBINE`, handlers will be combined like this:

```java
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
```

It's advanced feature that **may** be useful in certain cases.

---
