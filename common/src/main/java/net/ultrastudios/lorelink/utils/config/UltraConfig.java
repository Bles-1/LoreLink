package net.ultrastudios.lorelink.utils.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ultrastudios.lorelink.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Represents json config.
 * @param <T> Type of POJO class that is used for config format.
 */
public class UltraConfig<T> {
    private final Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapterFactory(new EnumDeserializerFactory()).create();
    private final Path configPath;
    private final Class<T> tClass;
    private T buffer;
    private final T defaultConfig;

    /**
     * Creates new {@link UltraConfig} object.
     * @param tClass POJO class that will be used as json format.
     * @param path Config file absolute path. Example: <i>"C:/users/admin/AppData/roaming/.minecraft/config/example.json"</i>
     * @param defaultValue Default config.
     */
    public UltraConfig(Class<T> tClass, Path path, T defaultValue) {
        configPath = path;
        this.tClass = tClass;
        defaultConfig = defaultValue;
        buffer = defaultValue;
    }

    /**
     * Get current configuration.
     * @return Buffered config.
     */
    public T get() {
        return buffer;
    }

    /**
     * Load config from file to buffer. Usually should be executed once on minecraft setup.
     */
    public void load() {
        try {
            if (!Files.exists(configPath)) {
                buffer = defaultConfig;
                save(defaultConfig);
                return;
            }
            buffer = gson.fromJson(new String(Files.readAllBytes(configPath)), tClass);
        }
        catch (IOException e) {
            Constants.LOG.error(e.getMessage());
            buffer = defaultConfig;
        }
    }

    /**
     * Save given config to file and buffer.
     * @param data Config to save.
     */
    public void save(T data) {
        try {
            Files.createDirectories(configPath.getParent());
            Files.write(configPath, gson.toJson(data).getBytes());
        }
        catch (IOException e) {
            Constants.LOG.error("Error during config file saving: {}", e.getMessage());
        }
        finally {
            buffer = data;
        }
    }

    /**
     * Save buffer to file. Useful when you are modifying buffer in program.
     */
    public void update() {
        save(buffer);
    }

    public Class<T> getType() { return tClass; }
}
