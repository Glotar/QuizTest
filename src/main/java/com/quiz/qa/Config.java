package com.quiz.qa;

import com.google.common.io.ByteStreams;
import com.google.gson.*;
import org.testng.log4testng.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Config {
    private static Logger logger = Logger.getLogger(Config.class);
    private static final String INTERNAL = "internal";
    private static final String DEFAULT_SECTION = "default";
    private static final String VARS_SECTION_NAME = "vars";
    private static JsonObject initialConfig;
    private static ThreadLocal<JsonObject> configJson = ThreadLocal.withInitial(() -> new Gson().fromJson(initialConfig, JsonObject.class));
    private static ThreadLocal<String> configSection = ThreadLocal.withInitial(() -> {
        String configSect = System.getenv("CONFIG_SECTION") != null ? System.getenv("CONFIG_SECTION") : DEFAULT_SECTION;

        if (!INTERNAL.equalsIgnoreCase(configSect)) {
            JsonElement vars = getConfigJson().get(VARS_SECTION_NAME).getAsJsonObject();
            if (!vars.getAsJsonObject().has(configSect)) {
                logger.warn(String.format("Section '%s.%s' not found in the configJson file, try to use 'default' instead.", VARS_SECTION_NAME, configSect));
                configSect = DEFAULT_SECTION;
                if (!vars.getAsJsonObject().has(configSect)) {
                    throw new RuntimeException("Section 'vars.default' not found in the configJson file.");
                }
            }
        }
        return configSect;
    });

    private Config() {
        throw new IllegalAccessError("You can not instance Config class");
    }

    private static JsonObject getConfigJson() {
        return configJson.get();
    }

    public static void load(File configFile) throws IOException {
        try {
            load(readFromFile(configFile));
        } catch (IOException e) {
            throw new IOException(String.format("Can't read configJson file: '%s'",
                    configFile.getAbsolutePath()), e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void load(String data) throws IOException {
        try {
            initialConfig = new JsonParser().parse(data).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            throw new IOException(String.format("Can't parse configJson file: '%s'", data), e);
        }
    }

    public static String readFromFile(File file) throws IOException, InterruptedException {
        FileInputStream is = new FileInputStream(file);
        return inputStreamToString(is);
    }

    public static String inputStreamToString(InputStream is) throws IOException, InterruptedException {
        Thread.sleep(2000);
        if (is==null) logger.error("InputStream is NULL!");
        byte[] bytes = ByteStreams.toByteArray(is);
        return new String(bytes, "UTF-8");
    }

    public static String getEnvPropertyAsString(String key, String defaultValue) {
        if (System.getenv(key) == null) {
            return defaultValue;
        }
        return System.getenv(key);
    }

    public static void setConfigSection(String configSect) {
        configSection.set(configSect);
    }

    public static String getAsString(String path) {
        return getAsString(path, false);
    }

    public static String getAsString(String path, boolean throwError) {
        JsonElement pathContent = get(path, throwError);

        return (pathContent != null) ? pathContent.getAsString() : null;
    }

    private static JsonElement get(String path) {
        return get(path, false);
    }

    private static void addPropertyIfAbsent(Map.Entry<String, JsonElement> entry, String key, String value) {
        if (!entry.getValue().getAsJsonObject().has(key)) {
            entry.getValue().getAsJsonObject().addProperty(key, value);
        }
    }

    private static JsonElement get(String path, boolean throwError) {
        JsonElement result = getValue(path, getConfigJson());

        if (result == null) {
            String message = String.format("Parameter with path '%s' is absent in the current config", getModifiedPath(path));
            logger.info("config: " +getConfigSection());
            if (throwError) {
                throw new RuntimeException(message);
            } else {
                logger.warn(message);
            }
        }

        return result;
    }

    private static JsonElement getValue(String path, JsonObject currentCfgNode) {
        String[] pathParts = getModifiedPath(path).split("\\.");
        if (pathParts.length == 0) {
            return null;
        }

        JsonElement result = currentCfgNode;
        for (String key : pathParts) {
            if (result.getAsJsonObject().has(key)) {
                result = result.getAsJsonObject().get(key);
            } else {
                return null;
            }
        }

        return result;
    }

    private static String getModifiedPath(String path) {
        return path.replace(VARS_SECTION_NAME, String.format("%s.%s", VARS_SECTION_NAME, getConfigSection()));
    }

    public static String getConfigSection() {
        return configSection.get();
    }

    public static boolean has(String path) {
        return getValue(path, getConfigJson()) != null;
    }

    public static String getEnvName() {
        String result;

        if (Config.has("vars.envName")) {
            result = Config.getAsString("vars.envName");
        } else if (getEnvUrl().contains("https://")) {
            result = getEnvUrl().substring(getEnvUrl().indexOf("https://") + 8, getEnvUrl().indexOf(".reltio.com"));
        } else {
            throw new RuntimeException("Can't get name of environment from 'vars.envName' or from 'vars.serviceUrl'. Please set 'vars.envName' at your config-section. E.g. '\"envName\": \"tst-01\",'");
        }

        return result;
    }

    public static String getEnvUrl() {
        return Config.getAsString("vars.envUrl", true);
    }

    public static String getUserToken(){
        return Config.getAsString("vars.userToken", true);
    }

}
