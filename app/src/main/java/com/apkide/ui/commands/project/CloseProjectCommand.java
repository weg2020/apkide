package com.apkide.ui.commands.project;

import androidx.annotation.IdRes;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

public class CloseProjectCommand implements MenuCommand {
	@IdRes
	@Override
	public int getId() {
		return R.id.commandCloseProject;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return App.getProjectService().isProjectOpened();
	}

	@Override
	public boolean run() {
		App.getProjectService().closeProject();
		return true;
	}
}
