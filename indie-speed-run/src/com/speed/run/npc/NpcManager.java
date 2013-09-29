package com.speed.run.npc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.speed.run.IndieSpeedRun;
import com.speed.run.dialog.Sentence;
import com.speed.run.engine.MoveableEntity;
import com.speed.run.engine.Renderer;
import com.speed.run.engine.Util;
import com.speed.run.items.BusSign;
import com.speed.run.managers.Assets;
import com.speed.run.managers.Config;
import com.speed.run.managers.Dialogs;
import com.speed.run.tweens.MoveableEntityAccessor;

public class NpcManager {
	
	private ArrayList<Npc> npcs;
	private ArrayList<Pair> pairs;
	
	private BusSign busSign;
	private MoveableEntity bus;
	
	// timers
	private Timer busTimer;
	private Timer spawnTimer;
	private Timer busSignTimer;
	
	public NpcManager() {
		reset();
	}
	
	public boolean isBusHere() {
		return bus.getPosition().x == 0;
	}
	
	public void reset() {
		npcs = new ArrayList<Npc>();
		pairs = new ArrayList<Pair>();
		
		// retrieve pairs
		pairs = Dialogs.getInstance().getPairs();
		
		bus = new MoveableEntity();
		bus.setPosition(Config.INIT_POS_X_BUS, Config.POS_Y_BUS);
		bus.addAnimation("idle", Assets.getInstance().getAnimation("bus"));
		bus.setDepth(150);
		Renderer.getInstance().addEntity(bus);
		
		busSign = new BusSign();
		busSign.setPosition(Config.PANEL_X, Config.PANEL_Y);
		busSign.setDepth(149);
		Renderer.getInstance().addEntity(busSign);
		
		
		
		busTimer = new Timer();
		busTimer.scheduleTask(new Task() {
			@Override
			public void run() {
				showBus();
				busTimer.delay((long) MathUtils.random(Config.MIN_TIME_BUS_COMES, Config.MAX_TIME_BUS_COMES));
			}
		}, Config.MIN_TIME_BUS_COMES, 10);
		busTimer.start();
		
		spawnTimer = new Timer();
		spawnTimer.scheduleTask(new Task() {
			@Override
			public void run() {
				add();
			}
		}, 0, Config.SPAWN_INTERVAL);
		spawnTimer.start();
		
		busSignTimer = new Timer();
		busSignTimer.scheduleTask(new Task() {
			@Override
			public void run() {
				busSign.setWaitingTime(MathUtils.random(2, 15));
				busSignTimer.delay(MathUtils.random(10, 20));
			}
		}, 0, 5);
		busSignTimer.start();
		
	}
	
	public void update(float dt) {		
		Iterator<Npc> iterator = npcs.iterator();
		while (iterator.hasNext()) {
			Npc npc = iterator.next();
			npc.update(dt);
		}
	}
	
	private void showBus() {
		busSign.setWaitingTime(MathUtils.random(2, 15));
		Timeline.createSequence()
			.push(Tween.to(bus, MoveableEntityAccessor.POSITION_XY, 1.0f).target(0, bus.getPosition().y).ease(Quint.OUT).setCallback(new TweenCallback() {
				
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					for (Npc npc: npcs) {
						Renderer.getInstance().removeLayer(npc.getDepth());
					}
					npcs = new ArrayList<Npc>();
					
				}
			}))
			.pushPause(Config.WAITING_TIME_STOP)
			.push(Tween.to(bus, MoveableEntityAccessor.POSITION_XY, 1.0f).target(Config.END_POS_X_BUS, bus.getPosition().y).ease(Quint.IN).setCallback(new TweenCallback() {
				
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					bus.setPosition(Config.INIT_POS_X_BUS, Config.POS_Y_BUS);	
				}
			}))
			.start(IndieSpeedRun.tweenManager);
	}
	
	public void render(SpriteBatch batch) {
		Iterator<Npc> iterator = npcs.iterator();
		while (iterator.hasNext()) {
			Npc npc = iterator.next();
			npc.draw(batch);
		}
		bus.draw(batch);
	}
	
	public void add() {
		if (npcs.size() >= Config.MAX_NB) return;
		// TODO: choose random animation
		
		Npc npc = null;
		Npc npc2 = null;
		if (Math.random() > 0 && pairs.size() > 0) {
			int dudes = (int)Math.random() * pairs.size();
			Pair pair = pairs.remove(dudes);
			
			npc = new TalkingNpc("bro1", pair.getFirst());
			if (pair.getSecond() != null) {
				npc2 = new TalkingNpc("bro1", pair.getSecond());
			}
		} else {
			npc = new Npc("bro1");
		}
		
		// setting a speed
		npc.setSpeed(MathUtils.random(Config.INIT_SPEED, Config.MAX_SPEED));
		
		int layer = MathUtils.random(0, Config.NB_LAYERS);
		npc.setDepth(Config.START_DEPTH - layer);
		float newY = Config.NPC_Y_POS + Config.DEPTH_INCR * layer;
		Vector2 nPos = new Vector2(Util.randomSign() * Config.X_SPAWN, newY);
		Vector2 nTarget = new Vector2(MathUtils.random(-Config.WIDTH * 0.5f * 1.1f, Config.WIDTH * 0.5f * 1.1f), newY);
		npc.setPosition(nPos.x, nPos.y);
		npc.moveTo(nTarget.x, nTarget.y);
		
		if (npc2 != null) {
			npc2.setPosition(nPos.x + Config.DISTANCE_DIALOG, nPos.y);
			npc2.moveTo(nTarget.x + Config.DISTANCE_DIALOG, nTarget.y);
			npc2.setDepth(Config.START_DEPTH - layer);
			npc2.setSpeed(npc.getSpeed());
			npcs.add(npc2);
			Renderer.getInstance().addEntity(npc2);
		}
		
		npcs.add(npc);
		Renderer.getInstance().addEntity(npc);
		
	}
	
	private void loadDialogs(String jsonFile) {
		JsonReader jsonReader = new JsonReader();
		JsonValue root = jsonReader.parse(Gdx.files.internal(jsonFile));
		int n = root.getInt("n");
		LinkedList<Sentence> firstList = new LinkedList<Sentence>();
		LinkedList<Sentence> secondList = null;
		String firstName = root.getString("first");
		String secondName = null;
		if (n == 2) {
			secondList = new LinkedList<Sentence>();
			secondName = root.getString("second");
		}
		JsonValue dialogs = root.get("dialogs");
		for (int i = 0; i < dialogs.size; i++) {
			JsonValue v = dialogs.get(i);
			Sentence s = new Sentence(i * (Config.SENTENCE_DURATION + Config.PAUSE), Config.SENTENCE_DURATION, v.getString(1));
			
			if (v.getInt(0) == 0) firstList.add(s);
			else secondList.add(s);
		}
		
		pairs.add(new Pair(firstList, secondList, firstName, secondName));
	}
	
	public void removeRandom() {
		Npc npc = npcs.get((int)Math.random()*npcs.size());
		npc.kill();
		npcs.remove(npc);
	}
}
