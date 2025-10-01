package net.ultrastudios.lorelink.utils;

import com.google.gson.*;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class BanListHelper {
    private static final Gson GSON = new Gson();

    public static UUID getBannedPlayerUuidByName(File list, String name) throws IOException {
        Path file = list.toPath();
        if (!Files.exists(file)) {
            return null;
        }

        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonArray arr = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                String bannedName = obj.get("name").getAsString();
                if (name.equalsIgnoreCase(bannedName)) {
                    return UUID.fromString(obj.get("uuid").getAsString());
                }
            }
        }
        return null;
    }
}
