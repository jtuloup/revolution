package com.speed.run.screens;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.speed.run.IndieSpeedRun;
import com.speed.run.tweens.BaseScreenAccessor;

public class BaseScreen implements Screen {

	protected IndieSpeedRun game;
	protected SpriteBatch batch;
	protected OrthographicCamera camera;
	protected Stage stage;
	protected Table baseTable;
	
	protected float elapsed;
	protected float alpha;
	
	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public BaseScreen(IndieSpeedRun game) {
		this.game = game;
		this.batch = game.getSpriteBatch();
		this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		// stage
		this.stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		Gdx.input.setInputProcessor(stage);
		baseTable = new Table();
		baseTable.setFillParent(true);
		stage.addActor(baseTable);
		
		this.elapsed = 0;
		this.alpha = 0.0f;
	}
	
	@Override
	public void render(float delta) {
		elapsed += delta;

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		Tween.to(this, BaseScreenAccessor.ALPHA, 2.0f)
		 .target(1.0f)
		 .start(IndieSpeedRun.tweenManager);
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}
