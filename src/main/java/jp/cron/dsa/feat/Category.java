package jp.cron.dsa.feat;


import com.lukflug.panelstudio.setting.ICategory;
import com.lukflug.panelstudio.setting.IClient;
import com.lukflug.panelstudio.setting.IModule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public enum Category implements ICategory {
	Main("Main");

	public final String displayName;
	public final List<Feature> modules= new ArrayList<>();

	Category(String displayName) {
		this.displayName=displayName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public Stream<IModule> getModules() {
		return modules.stream().map(module->module);
	}
}
