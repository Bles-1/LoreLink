[![Discord](https://img.shields.io/discord/1179475877436858400?logo=discord&color=blue)](http://discord.gg/e2SE62WT6x)
[![CurseForge](https://img.shields.io/badge/Download-CurseForge-orange?logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/lore-link)
![Modrinth (Coming Soon)](https://img.shields.io/badge/Coming_Soon%E2%84%A2-Modrinth-green?logo=modrinth)
[![GitHub](https://img.shields.io/badge/Source_Code-GitHub-white?logo=github)](https://github.com/Bles-1/LoreLink)

---

# Lore Link Developer Guide
>This guide is made for minecraft `1.21.4`, and Lore Link `1.0.0`.
###### Lore Link is designed to autoconfigure some of ours mods in when they are installed together.
You can also add your own configurations, by **extensions**. You can do this as part of your mod or as separate compatibility mod.     
[🔗 Click to teleport to extensions section.](#-mods-configuration)

Lore Link also provides **Helper Classes**, that may be used in multiple mods.  
[🔗 Click to teleport to helper section.](#-helpers)

---

## ⚙️ Mods Configuration
Configuration systems for different mods may vary. Check below mod that you are interested in:  
- [📂 Basics & Setup](./docs/extensions.md#-setup)
- [Simple Hardcore Respawn](./docs/extensions.md#simple-hardcore-respawn)

---

## ➕ Helpers
To use helper classes from Lore Link, you have to add it as dependency to your mod. 

First, add a dependency in your `mods.toml` `neoforge.mods.toml` or `fabric.mod.json`.
##### Forge:
```toml
[[dependecies.<YOUR_MOD_ID>]]
modID = "lorelink"
mandatory = true
versionRange = "<LORELINK.VERSION>"
ordering = "AFTER"
side = "BOTH"
```
##### NeoForge:
```toml
[[dependecies.<YOUR_MOD_ID>]]
modID = "lorelink"
type="required"
versionRange = "<LORELINK.VERSION>"
ordering = "AFTER"
side = "BOTH"
```
##### Fabric:
```json
"depends": {
  "lorelink": "<LORELINK.VERSION>"
}
```
In Forge and NeoForge, put code into mods.toml file, at the end. In Fabric, add this value to the `"depends"` field. You can find latest version of Lore Link on CurseForge.

To use Lore Link's code, you also need to add dependencies to your project. Here short summarize how to do this in gradle
:  To use code in your gradle project and compile it, you have to add `compileOnly()` dependency in your `build.gradle` file. 
To run it in IDE, you will have also to add `implementation fg.deobf()` (forge), `implementation()` (neoforge), or `modImplementation()` (fabric).   
: You can import dependency using GitHub packages 
(https://github.com/Bles-1/LoreLink) 
or CurseMaven 
(https://www.curseforge.com/minecraft/mc-mods/lore-link).

### Usage
👉 See [full guide](./docs/helpers.md).