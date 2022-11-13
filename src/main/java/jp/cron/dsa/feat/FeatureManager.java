package jp.cron.dsa.feat;

import jp.cron.dsa.DekitateAddonClient;
import jp.cron.dsa.feat.impl.gui.ClickGUIModule;
import jp.cron.dsa.feat.impl.util.*;

import java.util.ArrayList;
import java.util.List;

public class FeatureManager {

    private final List<Feature> features = new ArrayList<>();

    public FeatureManager() {
    }

    public void init() {
        register(new ClickGUIModule());
        register(new AfkNotifMod());
        register(new OldBalCommand());
        register(new OldPayCommand());
        register(new HideVoteNotifyMod());
        register(new ShortLobbyCommand());
    }

    public void register(Feature feature) {
        DekitateAddonClient.EVENT_BUS.subscribe(feature);
        Category.Main.modules.add(feature);
        features.add(feature);
    }

    public <T extends Feature> T getFeatureByClass(Class<T> clazz) {
        return features.stream().filter(feature -> feature.getClass() == clazz).map(feature -> (T) feature).findFirst().orElse(null);
    }

    public List<Feature> getFeatures() {
        return features;
    }

}
