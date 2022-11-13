package jp.cron.dsa.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lukflug.panelstudio.setting.ISetting;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import jp.cron.dsa.DekitateAddon;
import jp.cron.dsa.DekitateAddonClient;
import jp.cron.dsa.feat.Feature;
import jp.cron.dsa.feat.setting.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Hoosiers
 * @since 10/15/2020
 */

public class LoadConfig {

    final DekitateAddonClient client;

    private static final String fileName = "dsa/";
    private static final String moduleName = "modules/";
    private static final String mainName = "main/";
    private static final String miscName = "misc/";

    public LoadConfig(DekitateAddonClient client) {
        this.client = client;
    }

    public void load() {
        try {
            loadModules();
            loadEnabledModules();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadModules() throws IOException {
        String moduleLocation = fileName + moduleName;

        for (Feature feature: client.getFeatureManager().getFeatures()) {
            try {
                loadModuleDirect(moduleLocation, feature);
            } catch (IOException e) {
                System.out.println(feature.getConfigName());
                e.printStackTrace();
            }
        }
    }

    private void loadModuleDirect(String moduleLocation, Feature feature) throws IOException {
        if (!Files.exists(Paths.get(moduleLocation + feature.getConfigName() + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(moduleLocation + feature.getConfigName() + ".json"));
        JsonObject moduleObject;
        try {
            moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        }catch (IllegalStateException e) {
            return;
        }

        if (moduleObject.get("Module") == null) {
            return;
        }

        JsonObject settingObject = moduleObject.get("Settings").getAsJsonObject();
        feature.getSettings().map(s -> (Setting<?>)s).forEach(setting -> {
            JsonElement dataObject = settingObject.get(setting.getConfigName());
            try {
                if (dataObject != null && dataObject.isJsonPrimitive()) {
                    if (setting instanceof BooleanSetting) {
                        ((BooleanSetting) setting).setValue(dataObject.getAsBoolean());
                    } else if (setting instanceof IntegerSetting) {
                        ((IntegerSetting) setting).setValue(dataObject.getAsInt());
                    } else if (setting instanceof DoubleSetting) {
                        ((DoubleSetting) setting).setValue(dataObject.getAsDouble());
                    } else if (setting instanceof ColorSetting) {
                        ((ColorSetting) setting).fromInteger(dataObject.getAsInt());
                    } else if (setting instanceof EnumSetting<?>) {
                        ((EnumSetting<?>) setting).setValueIndex(dataObject.getAsInt());
                    } else if (setting instanceof KeybindSetting) {
                        ((KeybindSetting) setting).setValue(dataObject.getAsInt());
                    } else if (setting instanceof StringSetting) {
                        ((StringSetting) setting).setValue(dataObject.getAsString());
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println(setting.getConfigName() + " " + feature.getConfigName());
                System.out.println(dataObject);
            }
        });
        inputStream.close();
    }

    private void loadEnabledModules() throws IOException {
        String enabledLocation = fileName + mainName;

        if (!Files.exists(Paths.get(enabledLocation + "Toggle" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(enabledLocation + "Toggle" + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (moduleObject.get("Modules") == null) {
            return;
        }

        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (Feature feature: client.getFeatureManager().getFeatures()) {
            JsonElement dataObject = settingObject.get(feature.getConfigName());

            if (dataObject != null && dataObject.isJsonPrimitive()) {
                if (dataObject.getAsBoolean()) {
                    try {
                        feature.enable();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        inputStream.close();
    }
}