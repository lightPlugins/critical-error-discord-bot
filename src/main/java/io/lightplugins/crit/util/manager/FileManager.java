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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {

    private final String configPath;
    private Map<String, Object> config;
    private final Yaml yaml;

    public FileManager(String configPath) {
        this.configPath = configPath;
        DumperOptions dumperOptions = new DumperOptions();
        LoaderOptions loaderOptions = new LoaderOptions();
        this.yaml = new Yaml(new Constructor(Map.class, loaderOptions), new Representer(dumperOptions), dumperOptions);
        copyConfigFileFromResources();
        loadConfig();
    }

    private void copyConfigFileFromResources() {
        if (!Files.exists(Paths.get(configPath))) {
            try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                if (resourceStream == null) {
                    throw new RuntimeException("Resource config.yml not found");
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

    public String getString(String key) {
        return (String) config.get(key);
    }

    public void setString(String key, String value) {
        config.put(key, value);
        saveConfig();
    }

    public int getInt(String key) {
        return (Integer) config.get(key);
    }

    public void setInt(String key, int value) {
        config.put(key, value);
        saveConfig();
    }

    public boolean getBoolean(String key) {
        return (Boolean) config.get(key);
    }

    public void setBoolean(String key, boolean value) {
        config.put(key, value);
        saveConfig();
    }

    @SuppressWarnings("unchecked")
    public List<String> getStringList(String key) {
        return (List<String>) config.get(key);
    }

    public void setStringList(String key, List<String> value) {
        config.put(key, value);
        saveConfig();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getSection(String key) {
        return (Map<String, Object>) config.get(key);
    }
}