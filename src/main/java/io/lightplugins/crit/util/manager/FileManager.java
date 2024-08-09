package io.lightplugins.crit.util.manager;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

public class FileManager {

    private final String configPath;
    private Map<String, Object> config;
    private final Yaml yaml;

    public FileManager(String configPath) {
        this.configPath = configPath;
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // Ensure block style
        LoaderOptions loaderOptions = new LoaderOptions();
        this.yaml = new Yaml(new Constructor(Map.class, loaderOptions), new Representer(dumperOptions), dumperOptions);
        copyConfigFileFromResources();
        loadConfig();
    }

    private void copyConfigFileFromResources() {
        if (!Files.exists(Paths.get(configPath))) {
            try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(configPath)) {
                if (resourceStream == null) {
                    throw new RuntimeException("Resource " + configPath + " not found");
                }
                Files.copy(resourceStream, Paths.get(configPath), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy config file from resources", e);
            }
        }
    }

    private void loadConfig() {
        try (InputStream in = Files.newInputStream(Paths.get(configPath))) {
            config = yaml.load(in);
            System.out.println("Config loaded: " + config); // Debug statement
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadConfig() {
        loadConfig();
    }

    public void saveConfig() {
        try (Writer writer = new FileWriter(configPath)) {
            yaml.dump(config, writer);
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong while saving the config file.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getValue(String key, Class<T> type) {
        String[] keys = key.split("\\.");
        Map<String, Object> currentMap = config;
        for (int i = 0; i < keys.length - 1; i++) {
            currentMap = (Map<String, Object>) currentMap.get(keys[i]);
            if (currentMap == null) {
                return null;
            }
        }
        return (T) currentMap.get(keys[keys.length - 1]);
    }

    @SuppressWarnings("unchecked")
    private <T> void setValue(String key, T value) {
        String[] keys = key.split("\\.");
        Map<String, Object> currentMap = config;
        for (int i = 0; i < keys.length - 1; i++) {
            currentMap = (Map<String, Object>) currentMap.get(keys[i]);
            if (currentMap == null) {
                throw new RuntimeException("Configuration key '" + key + "' is missing or null");
            }
        }
        currentMap.put(keys[keys.length - 1], value);
        saveConfig();
    }

    public String getString(String key) {
        return getValue(key, String.class);
    }

    public void setString(String key, String value) {
        setValue(key, value);
    }

    public long getLong(String key) {
        return getValue(key, Long.class);
    }

    public void setLong(String key, long value) {
        setValue(key, value);
    }

    public int getInt(String key) {
        return getValue(key, Integer.class);
    }

    public void setInt(String key, int value) {
        setValue(key, value);
    }

    public boolean getBoolean(String key) {
        return getValue(key, Boolean.class);
    }

    public void setBoolean(String key, boolean value) {
        setValue(key, value);
    }

    public List<String> getStringList(String key) {
        return getValue(key, List.class);
    }

    public void setStringList(String key, List<String> value) {
        setValue(key, value);
    }

    public Map<String, Object> getSection(String key) {
        return getValue(key, Map.class);
    }
}