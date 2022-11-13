package jp.cron.dsa.config;

import com.google.gson.*;
import jp.cron.dsa.DekitateAddonClient;
import jp.cron.dsa.feat.Feature;
import jp.cron.dsa.feat.setting.*;
import jp.cron.dsa.util.ClientUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Hoosiers
 * @since 10/15/2020
 */

public class SaveConfig {

    private static final String fileName = "dsa/";
    private static final String moduleName = "modules/";
    private static final String mainName = "main/";
    private static final String miscName = "misc/";

    final DekitateAddonClient client;
    public SaveConfig(DekitateAddonClient client) {
        this.client = client;
    }

    public void save() {
        try {
            saveConfig();
            saveModules();
            saveEnabledModules();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClientUtils.getLogger().info("Saved Config!");
    }

    private void saveConfig() throws IOException {
        if (!Files.exists(Paths.get(fileName))) {
            Files.createDirectories(Paths.get(fileName));
        }
        if (!Files.exists(Paths.get(fileName + moduleName))) {
            Files.createDirectories(Paths.get(fileName + moduleName));
        }
        if (!Files.exists(Paths.get(fileName + mainName))) {
            Files.createDirectories(Paths.get(fileName + mainName));
        }
        if (!Files.exists(Paths.get(fileName + miscName))) {
            Files.createDirectories(Paths.get(fileName + miscName));
        }
    }

    private void registerFiles(String location, String name) throws IOException {
        if (Files.exists(Paths.get(fileName + location + name + ".json"))) {
            File file = new File(fileName + location + name + ".json");

            file.delete();

        }
        Files.createFile(Paths.get(fileName + location + name + ".json"));
    }

    private void saveModules() throws IOException {
        for (Feature feature: client.getFeatureManager().getFeatures()) {
            try {
                saveModuleDirect(feature);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveModuleDirect(Feature feature) throws IOException {
        registerFiles(moduleName, feature.getConfigName());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + moduleName + feature.getConfigName() + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject settingObject = new JsonObject();
        moduleObject.add("Module", new JsonPrimitive(feature.getConfigName()));

        feature.getSettings().map(s -> (Setting<?>) s).forEach(setting -> {
            if (setting instanceof BooleanSetting) {
                settingObject.add(setting.getConfigName(), new JsonPrimitive(((BooleanSetting) setting).getValue()));
            } else if (setting instanceof IntegerSetting) {
                settingObject.add(setting.getConfigName(), new JsonPrimitive(((IntegerSetting) setting).getValue()));
            } else if (setting instanceof DoubleSetting) {
                settingObject.add(setting.getConfigName(), new JsonPrimitive(((DoubleSetting) setting).getValue()));
            } else if (setting instanceof ColorSetting) {
                settingObject.add(setting.getConfigName(), new JsonPrimitive(((ColorSetting) setting).toInteger()));
            } else if (setting instanceof EnumSetting<?>) {
                settingObject.add(setting.getConfigName(), new JsonPrimitive(((EnumSetting<?>) setting).getValueIndex()));
            } else if (setting instanceof KeybindSetting) {
                settingObject.add(setting.getConfigName(), new JsonPrimitive(((KeybindSetting) setting).getKey()));
            }
        });
        moduleObject.add("Settings", settingObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private void saveEnabledModules() throws IOException {

        registerFiles(mainName, "Toggle");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + mainName + "Toggle" + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject enabledObject = new JsonObject();

        for (Feature feature: client.getFeatureManager().getFeatures()) {
            if (feature.isEnabled()==null)
                continue;
            enabledObject.add(feature.getConfigName(), new JsonPrimitive(feature.isEnabled().isOn()));
        }
        moduleObject.add("Modules", enabledObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

}