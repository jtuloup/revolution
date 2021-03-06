package com.speed.run.client;

import com.speed.run.IndieSpeedRun;
import com.speed.run.managers.Config;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class GwtLauncher extends GwtApplication {
	@Override
	public GwtApplicationConfiguration getConfig () {
		GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(480, 320);
		cfg.width = Config.WIDTH;
		cfg.height = Config.HEIGHT;
		return cfg;
	}

	@Override
	public ApplicationListener getApplicationListener () {
		return new IndieSpeedRun();
	}
}