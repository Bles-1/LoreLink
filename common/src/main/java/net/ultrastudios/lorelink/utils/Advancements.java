package net.ultrastudios.lorelink.utils;

import com.google.gson.*;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class Advancements {

    protected static final Gson GSON = new Gson();

    public static boolean grantOfflineAdvancement(MinecraftServer server, UUID playerUUID, String advancementId, Advancement advancement) {
        Path advancementsDir = server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR);

        if (Files.notExists(advancementsDir)) {
            try {
                Files.createDirectory(advancementsDir);
            } catch (IOException e) {
                System.err.println("Could not create advancements directory");
                return false;
            }
        }

        Path playerFile = advancementsDir.resolve(playerUUID.toString() + ".json");
        JsonObject playerData;

        if (Files.exists(playerFile)) {
            try (var reader = Files.newBufferedReader(playerFile)) {
                var json = GSON.fromJson(reader, JsonElement.class);
                if (json.isJsonObject()) {
                    playerData = json.getAsJsonObject();
                } else {
                    System.err.println("Wrong format in advancements file for player " + playerUUID);
                    return false;
                }
            } catch (IOException e) {
                System.err.println("Failed to read advancements file for player " + playerUUID);
                return false;
            }
        } else {
            playerData = new JsonObject();
        }

        if (playerData.has(advancementId)) {
            JsonObject existing = playerData.getAsJsonObject(advancementId);
            if (existing.has("done") && existing.get("done").getAsBoolean()) {
                return false;
            }
        }

        JsonObject advEntry = new JsonObject();
        Set<String> criteriaSet = advancement.criteria().keySet();
        var f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT).withZone(ZoneId.systemDefault());
        JsonObject criteria = new JsonObject();
        for (var c : criteriaSet) {
            criteria.addProperty(c, f.format(Instant.now()));
        }
        advEntry.add("criteria", criteria);
        advEntry.addProperty("done", true);
        playerData.add(advancementId, advEntry);

        try (var writer = Files.newBufferedWriter(playerFile)) {
            GSON.toJson(playerData, writer);
        } catch (IOException e) {
            System.err.println("Failed to write advancements file for player " + playerUUID);
            return false;
        }

        return true;
    }
}
