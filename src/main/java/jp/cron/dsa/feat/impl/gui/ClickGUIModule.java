package jp.cron.dsa.feat.impl.gui;

import jp.cron.dsa.feat.Feature;
import jp.cron.dsa.feat.setting.EnumSetting;
import jp.cron.dsa.feat.setting.IntegerSetting;
import jp.cron.dsa.feat.setting.KeybindSetting;
import org.lwjgl.glfw.GLFW;


public class ClickGUIModule extends Feature {

	public final IntegerSetting scrollSpeed = registerIntegerSetting("Scroll Speed","scrollSpeed","GUIのスクロール速度を設定します。",()->true,0,20,10);
	public final IntegerSetting animationSpeed = registerIntegerSetting("Animation Speed","animationSpeed","GUIアニメーションの動きの速さを指定します。",()->true,0,1000,200);

	public ClickGUIModule() {
		super("GUI Settings","ClickGUIMod", "設定をするためのGUI",()->true,false);
	}

	@Override
	protected void onEnable() {
	}

	@Override
	protected void onDisable() {
	}

	public enum Theme {
		Clear,GameSense,Rainbow,Windows31,Impact;
	}
}
