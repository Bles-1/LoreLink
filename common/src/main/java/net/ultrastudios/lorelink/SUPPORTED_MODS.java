package net.ultrastudios.lorelink;

public enum SUPPORTED_MODS {
    SimpleHardcoreRespawn("simple_hardcore_respawn", "Simple Hardcore Respawn");

    private final String modId;
    private final String displayName;

    SUPPORTED_MODS(String Id, String display) {
        modId = Id;
        displayName = display;
    }

    public String getModId() {
        return modId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
