package jp.cron.dsa.feat;

import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.setting.IModule;
import com.lukflug.panelstudio.setting.ISetting;
import jp.cron.dsa.feat.setting.*;
import me.zero.alpine.listener.EventSubscriber;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

public abstract class Feature implements EventSubscriber, IModule {
    protected final MinecraftClient mc = MinecraftClient.getInstance();
    private final List<Setting<?>> settings = new ArrayList<>();

    private final String name,description;
    private final String configName;
    private final IBoolean visible;
    private final boolean toggleable;

    private boolean isEnabled = false;

    public Feature(String name, String configName, String description, IBoolean visible, boolean toggleable) {
        this.name = name;
        this.configName = configName;
        this.description = description;
        this.visible = visible;
        this.toggleable = toggleable;
    }

    protected abstract void onEnable();
    protected abstract void onDisable();

    public void enable() {
        if (isEnabled) return;
        isEnabled = true;
        onEnable();
    }

    public void disable() {
        if (!isEnabled) return;
        isEnabled = false;
        onDisable();
    }

    @Override
    public IToggleable isEnabled() {
        if (!toggleable)
            return null;
        return new IToggleable() {
            @Override
            public void toggle() {
                if (isEnabled) {
                    disable();
                } else {
                    enable();
                }
            }

            @Override
            public boolean isOn() {
                return isEnabled;
            }
        };
    }

    protected BooleanSetting registerBooleanSetting(String displayName, String configName, String description, IBoolean visible, boolean defaultValue) {
        BooleanSetting s = new BooleanSetting(displayName, configName, description, visible, defaultValue);
        settings.add(s);
        return s;
    }

    protected IntegerSetting registerIntegerSetting(String displayName, String configName, String description, IBoolean visible, int defaultValue, int min, int max) {
        IntegerSetting s = new IntegerSetting(displayName, configName, description, visible, defaultValue, min, max);
        settings.add(s);
        return s;
    }

    protected KeybindSetting registerKeybindSetting(String displayName, String configName, String description, IBoolean visible, int defaultValue) {
        KeybindSetting s = new KeybindSetting(displayName, configName, description, visible, defaultValue);
        settings.add(s);
        return s;
    }

    protected <T extends Enum<T>> EnumSetting<T> registerEnumSetting(String displayName, String configName, String description, IBoolean visible, T defaultValue, Class<T> clazz) {
        EnumSetting<T> s = new EnumSetting<>(displayName, configName, description, visible, defaultValue, clazz);
        settings.add(s);
        return s;
    }

    public ColorSetting registerColorSetting(String displayName, String configName, String description, IBoolean visible, boolean hasAlpha, boolean allowRainbow, Color defaultValue, boolean rainbow) {
        ColorSetting s = new ColorSetting(displayName, configName, description, visible, hasAlpha, allowRainbow, defaultValue, rainbow);
        settings.add(s);
        return s;
    }

    @Override
    public Stream<ISetting<?>> getSettings() {
        return settings.stream().filter(s -> s instanceof ISetting)
                .sorted(Comparator.comparing(a -> a.displayName))
                .map(s -> (ISetting<?>) s);
    }

    @Override
    public String getDisplayName() {
        return this.name;
    }

    public String getConfigName() {
        return this.configName;
    }

    @Override
    public IBoolean isVisible() {
        return this.visible;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
