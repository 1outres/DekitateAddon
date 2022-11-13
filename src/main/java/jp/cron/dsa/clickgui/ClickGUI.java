package jp.cron.dsa.clickgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.lukflug.panelstudio.setting.*;
import jp.cron.dsa.DekitateAddon;
import jp.cron.dsa.DekitateAddonClient;
import jp.cron.dsa.feat.Category;
import jp.cron.dsa.feat.impl.gui.ClickGUIModule;
import jp.cron.dsa.feat.impl.gui.ClickGUIModule.Theme;
import jp.cron.dsa.feat.setting.ColorSetting;
import org.lwjgl.glfw.GLFW;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.base.SettingsAnimation;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.component.IResizable;
import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.container.IContainer;
import com.lukflug.panelstudio.hud.HUDGUI;
import com.lukflug.panelstudio.layout.ChildUtil.ChildMode;
import com.lukflug.panelstudio.layout.ComponentGenerator;
import com.lukflug.panelstudio.layout.IComponentAdder;
import com.lukflug.panelstudio.layout.IComponentGenerator;
import com.lukflug.panelstudio.layout.ILayout;
import com.lukflug.panelstudio.layout.PanelAdder;
import com.lukflug.panelstudio.layout.SearchableLayout;
import com.lukflug.panelstudio.mc19.MinecraftHUDGUI;
import com.lukflug.panelstudio.popup.CenteredPositioner;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import com.lukflug.panelstudio.popup.MousePositioner;
import com.lukflug.panelstudio.popup.PanelPositioner;
import com.lukflug.panelstudio.popup.PopupTuple;
import com.lukflug.panelstudio.theme.GameSenseTheme;
import com.lukflug.panelstudio.theme.IColorScheme;
import com.lukflug.panelstudio.theme.ITheme;
import com.lukflug.panelstudio.theme.OptimizedTheme;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.ColorPickerComponent;
import com.lukflug.panelstudio.widget.DropDownList;
import com.lukflug.panelstudio.widget.ITextFieldKeys;
import com.lukflug.panelstudio.widget.Spinner;
import com.lukflug.panelstudio.widget.ToggleSwitch;

import net.minecraft.util.Formatting;

public class ClickGUI extends MinecraftHUDGUI {
	private final GUIInterface inter;
	private final HUDGUI gui;
	public static final int WIDTH=120,HEIGHT=12,DISTANCE=6,BORDER=2;

	ClickGUIModule clickGUIModule;

	public ClickGUI(DekitateAddonClient addonClient) {
		clickGUIModule = addonClient.getFeatureManager().getFeatureByClass(ClickGUIModule.class);

		IClient client = () -> Arrays.stream(Category.values());
		inter=new GUIInterface(true) {
			@Override
			protected String getResourcePrefix() {
				return "dsa:";
			}
		};
		ITheme theme=new OptimizedTheme(new GameSenseTheme(new ThemeScheme(),9,4,5,": "+Formatting.GRAY));
		// Instantiating GUI ...
		IToggleable guiToggle=new SimpleToggleable(false);
		IToggleable hudToggle=new SimpleToggleable(false);

		gui=new HUDGUI(inter,theme.getDescriptionRenderer(),(IPopupPositioner)new MousePositioner(new Point(10,10)),guiToggle,hudToggle);
		Supplier<Animation> animation=()->new SettingsAnimation(clickGUIModule.animationSpeed::getValue, inter::getTime);

		BiFunction<Context,Integer,Integer> scrollHeight=(context,componentHeight)->Math.min(componentHeight,Math.max(HEIGHT*4,ClickGUI.this.height-context.getPos().y-HEIGHT));
		PopupTuple colorPopup=new PopupTuple(new CenteredPositioner(()->new Rectangle(new Point(0,0),inter.getWindowSize())),true,new IScrollSize() {
			@Override
			public int getScrollHeight (Context context, int componentHeight) {
				return scrollHeight.apply(context,componentHeight);
			}
		});
		IntFunction<IResizable> resizable=width->new IResizable() {
			final Dimension size=new Dimension(width,320);

			@Override
			public Dimension getSize() {
				return new Dimension(size);
			}

			@Override
			public void setSize (Dimension size) {
				this.size.width=size.width;
				this.size.height=size.height;
				if (size.width<75) this.size.width=75;
				if (size.height<50) this.size.height=50;
			}
		};
		// Defining scroll behavior ...
		Function<IResizable,IScrollSize> resizableHeight=size->new IScrollSize() {
			@Override
			public int getScrollHeight (Context context, int componentHeight) {
				return size.getSize().height;
			}
		};
		// Defining function keys ...
		IntPredicate keybindKey=scancode->scancode==GLFW.GLFW_KEY_DELETE;
		IntPredicate charFilter=character->{
			return character>=' ';
		};
		ITextFieldKeys keys=new ITextFieldKeys() {
			@Override
			public boolean isBackspaceKey (int scancode) {
				return scancode==GLFW.GLFW_KEY_BACKSPACE;
			}

			@Override
			public boolean isDeleteKey (int scancode) {
				return scancode==GLFW.GLFW_KEY_DELETE;
			}

			@Override
			public boolean isInsertKey (int scancode) {
				return scancode==GLFW.GLFW_KEY_INSERT;
			}

			@Override
			public boolean isLeftKey (int scancode) {
				return scancode==GLFW.GLFW_KEY_LEFT;
			}

			@Override
			public boolean isRightKey (int scancode) {
				return scancode==GLFW.GLFW_KEY_RIGHT;
			}

			@Override
			public boolean isHomeKey (int scancode) {
				return scancode==GLFW.GLFW_KEY_HOME;
			}

			@Override
			public boolean isEndKey (int scancode) {
				return scancode==GLFW.GLFW_KEY_END;
			}

			@Override
			public boolean isCopyKey (int scancode) {
				return scancode==GLFW.GLFW_KEY_C;
			}

			@Override
			public boolean isPasteKey (int scancode) {
				return scancode==GLFW.GLFW_KEY_V;
			}

			@Override
			public boolean isCutKey (int scancode) {
				return scancode==GLFW.GLFW_KEY_X;
			}

			@Override
			public boolean isAllKey (int scancode) {
				return scancode==GLFW.GLFW_KEY_A;
			}
		};

		IComponentGenerator generator=new ComponentGenerator(keybindKey,charFilter,keys) {
			@Override
			public IComponent getBooleanComponent (IBooleanSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
				return new ToggleSwitch(setting,theme.getToggleSwitchRenderer(isContainer));
			}

			@Override
			public IComponent getEnumComponent (IEnumSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
				return new DropDownList(setting,theme,isContainer,false,keys,new IScrollSize(){},adder::addPopup) {
					@Override
					protected Animation getAnimation() {
						return animation.get();
					}

					@Override
					public boolean allowCharacter (char character) {
						return charFilter.test(character);
					}

					@Override
					protected boolean isUpKey (int key) {
						return key==GLFW.GLFW_KEY_UP;
					}

					@Override
					protected boolean isDownKey (int key) {
						return key==GLFW.GLFW_KEY_DOWN;
					}

					@Override
					protected boolean isEnterKey (int key) {
						return key==GLFW.GLFW_KEY_ENTER;
					}
				};
			}

			@Override
			public IComponent getNumberComponent (INumberSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
				return new Spinner(setting,theme,isContainer,true,keys);
			}

			@Override
			public IComponent getColorComponent (IColorSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
				return new ColorPickerComponent(setting,new ThemeTuple(theme.theme,theme.logicalLevel,colorLevel));
			}
		};

		AtomicReference<IResizable> searchableResizable=new AtomicReference<IResizable>(null);
		IComponentAdder searchableAdder=new PanelAdder(gui,true,()->true,title->title) {
			@Override
			protected IResizable getResizable (int width) {
				searchableResizable.set(resizable.apply(width));
				return searchableResizable.get();
			}
		};
		ILayout searchableLayout=new SearchableLayout(new Labeled(DekitateAddon.MOD_NAME+" Settings",null,()->true),new Labeled("Search",null,()->true),new Point(100,100),480,WIDTH,animation,"Enabled",2,ChildMode.POPUP,colorPopup,(a, b)->a.getDisplayName().compareTo(b.getDisplayName()),charFilter,keys) {
			@Override
			public int getScrollHeight (Context context, int componentHeight) {
				return resizableHeight.apply(searchableResizable.get()).getScrollHeight(null,height);
			}
		};
		searchableLayout.populateGUI(searchableAdder,generator,client,theme);
	}

	@Override
	protected HUDGUI getGUI() {
		return gui;
	}

	@Override
	protected GUIInterface getInterface() {
		return inter;
	}

	@Override
	protected int getScrollSpeed() {
		return clickGUIModule.scrollSpeed.getValue();
	}

	private class ThemeScheme implements IColorScheme {
		@Override
		public void createSetting (ITheme theme, String name, String description, boolean hasAlpha, boolean allowsRainbow, Color color, boolean rainbow) {
			clickGUIModule.registerColorSetting(
					name, name, description, ()->true, hasAlpha, allowsRainbow, color, rainbow
			);
		}

		@Override
		public Color getColor (String name) {
			return clickGUIModule.getSettings().filter(s -> s.getDisplayName().equals(name)).filter(s -> s instanceof ColorSetting).map(s -> (ColorSetting) s).findFirst().orElse(null).getValue();
		}
	}
}
