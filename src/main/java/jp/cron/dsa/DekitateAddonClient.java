package jp.cron.dsa;

import jp.cron.dsa.clickgui.ClickGUI;
import jp.cron.dsa.config.LoadConfig;
import jp.cron.dsa.config.SaveConfig;
import jp.cron.dsa.feat.FeatureManager;
import jp.cron.dsa.feat.impl.gui.ClickGUIModule;
import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import javax.swing.text.JTextComponent;

@Environment(EnvType.CLIENT)
public class DekitateAddonClient implements ClientModInitializer {

	@Deprecated
	public static DekitateAddonClient INSTANCE;

    public static final EventBus EVENT_BUS = EventManager.builder()
            .setName(DekitateAddon.MOD_NAME)
            .build();

    private ClickGUI gui;
    private final boolean keys[]=new boolean[266];

    private final FeatureManager featureManager;

	public final LoadConfig loadConfig = new LoadConfig(this);
	public final SaveConfig saveConfig = new SaveConfig(this);

	private boolean firstTick = true;

	private static final KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.dekitateaddon.open_gui", GLFW.GLFW_KEY_O, "key.categories.dekitateaddon"));

    public DekitateAddonClient() {
		INSTANCE = this;
        featureManager = new FeatureManager();
    }

    @Override
    public void onInitializeClient() {
        featureManager.init();

		gui = new ClickGUI(this);

		ClickGUIModule clickGUIModule = featureManager.getFeatureByClass(ClickGUIModule.class);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			while (keyBinding.wasPressed()) {
				gui.enterGUI();
			}

			for (int i=32;i<keys.length;i++) {
				if (keys[i]!=(GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(),i)==GLFW.GLFW_PRESS)) {
					keys[i]=!keys[i];
					if (keys[i]) {
						gui.handleKeyEvent(i);
					}
				}
			}
		});

		loadConfig.load();
	}

	public FeatureManager getFeatureManager() {
		return featureManager;
	}
}
