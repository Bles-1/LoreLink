# Lore Link Config
Lore Link's config file is stored in `.minecraft/config/ultraconfig/lorelink.json` (or `server/config/ultraconfig/lorelink.json`) and is set per instance/server.

## 👯 Duplicate Strategy

This setting determines behaviour when there are more than one mod trying to add extension to the same mod.

Syntax:
```
duplicate_strategy
├── default_strategy: {Duplicate Strategy object}
└── mods
    ├── mod_id: {Duplicate Strategy object}
    ├── mod_2_id: {Duplicate Strategy object}
    └── ...
```

You can set default strategy for all mods, and/or separate strategies for different mods.

Duplicate Strategy object syntax:
```
root
├── strategy: {string/enum - Strategy}
├── (optional) id: {string}
└── (optional) order: [strings list]
```

`Strategy` enum can be one of these values:

| Value     | Description                                                                                                                                                                                                                                            |
|-----------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `FAIL`    | Default strategy. Lore Link crashes when more than one extension is installed.                                                                                                                                                                         |
| `FIRST`   | First loaded extension will load, other will be skipped.                                                                                                                                                                                               |
| `LAST`    | Last loaded extension will load, other will be skipped.                                                                                                                                                                                                |
| `ONLY`    | Only extension with id matching the one given in `id` field will be loaded. If `id` field is skipped, then Lore Link uses `order` list as a priority list. (First is highest priority.) If `order` list is also skipped, no extensions will be loaded. |
| `COMBINE` | All installed extensions will be combined in order given in `order` list. It depends of extension if it can be combined, so **use it only if you know what are you doing!**                                                                            |
| `NONE`    | No extension will be loaded.                                                                                                                                                                                                                           |

Example config:
```json
{
  "duplicate_strategy":
  {
    "default_strategy": {
      "strategy": "NONE"
    },
    "mods": {
      "simple_hardcore_respawn": {
        "strategy": "ONLY",
        "order": [
          "epic_mechanics_shr",
          "fortnite_style",
          "another_example_extension"
        ]
      }
    }
  }
}
```