package it.unibo.pensilina14.bullet.ballet.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import it.unibo.pensilina14.bullet.ballet.common.ImmutablePosition2Dimpl;
import it.unibo.pensilina14.bullet.ballet.common.MutablePosition2D;
import it.unibo.pensilina14.bullet.ballet.graphics.scenes.GameView;
import it.unibo.pensilina14.bullet.ballet.graphics.scenes.MapScene;
import it.unibo.pensilina14.bullet.ballet.input.Command;
import it.unibo.pensilina14.bullet.ballet.input.Controller;
import it.unibo.pensilina14.bullet.ballet.logging.AppLogger;
import it.unibo.pensilina14.bullet.ballet.model.characters.Enemy;
import it.unibo.pensilina14.bullet.ballet.model.characters.Player;
import it.unibo.pensilina14.bullet.ballet.model.environment.Environment;
import it.unibo.pensilina14.bullet.ballet.model.environment.GameState;
import it.unibo.pensilina14.bullet.ballet.model.environment.Platform;
import it.unibo.pensilina14.bullet.ballet.model.environment.events.GameEvent;
import it.unibo.pensilina14.bullet.ballet.model.environment.events.GameEventListener;
import it.unibo.pensilina14.bullet.ballet.model.environment.events.PlayerHitsEnemyEvent;
import it.unibo.pensilina14.bullet.ballet.model.environment.events.PlayerHitsItemEvent;
import it.unibo.pensilina14.bullet.ballet.model.environment.events.PlayerHitsObstacleEvent;
import it.unibo.pensilina14.bullet.ballet.model.environment.events.PlayerHitsPlatformEvent;
import it.unibo.pensilina14.bullet.ballet.model.obstacle.ObstacleImpl;
import it.unibo.pensilina14.bullet.ballet.model.weapon.PickupItem;

public class GameEngine implements Controller, GameEventListener {
	
	private static final int QUEUE_CAPACITY = 100;
	
	//private final long period = 1000; // 20 ms = 50 FPS 
	
	private Optional<GameView> view;
	private Optional<GameState> gameState;
	private final BlockingQueue<Command> cmdQueue;
	private final List<GameEvent> eventQueue;
	
	public GameEngine() {
		this.cmdQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
		this.eventQueue = new LinkedList<>();
		this.view = Optional.empty();
		this.gameState = Optional.empty();
	}
	
	public GameEngine(final GameView view, final GameState game) {
		this.cmdQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
		this.eventQueue = new LinkedList<>();
		this.view = Optional.of(view);
		this.gameState = Optional.of(game);
	}
	
	public final void setup() {
		if (this.view.isEmpty()) {
			this.view = Optional.of(new MapScene(this.gameState.get(), this));
			this.view.get().setup(this);
			AppLogger.getAppLogger().debug("View was empty so it was initialized.");
		} else {
			this.view.get().setup(this);
			this.view.get().setInputController(this);
			AppLogger.getAppLogger().debug("View input controller set.");
		}

		if (this.gameState.isEmpty()) {
			this.gameState = Optional.of(new GameState());
			this.gameState.get().setEventListener(this);
			AppLogger.getAppLogger().debug("There was no game state, new one instantiated.");
		} else {
			this.gameState.get().setEventListener(this);
			AppLogger.getAppLogger().debug("Game state present, event listener set only.");
		}
	}
	
	public final void mainLoop() {
		while (!this.gameState.get().isGameOver()) {
			this.processInput();
			AppLogger.getAppLogger().debug("Input processed.");
			this.updateGame();
			AppLogger.getAppLogger().debug("Game model updated.");
			this.render();
			AppLogger.getAppLogger().debug("Rendering ultimated.");
		}
		// GAME OVER
	}
	
	public final void processInput() {
		final Command cmd = this.cmdQueue.poll();
		if (cmd != null) {
			cmd.execute(this.gameState.get());
		}
	}
	
	public void updateGame() {
		this.gameState.get().update();
		this.checkEvents();
	}
	
	public final void render() {
		this.view.get().draw();
	}
	
	@Override
	public final void notifyCommand(final Command cmd) {
        this.cmdQueue.add(cmd);
	}

	@Override
	public final void notifyEvent(final GameEvent e) {
		this.eventQueue.add(e);
	}
	
	private void checkEvents() {
		final Environment env = this.gameState.get().getGameEnvironment();
		this.eventQueue.stream().forEach(e -> {
			if (e instanceof PlayerHitsItemEvent) {
				playerHitsPickUpObjEventHandler(env, e);
			} else if (e instanceof PlayerHitsEnemyEvent) {
				playerHitsEnemyEventHandler(env, e);
			} else if (e instanceof PlayerHitsObstacleEvent) {
				playerHitsObstacleEventHandler(env, e);
			} else if (e instanceof PlayerHitsPlatformEvent) {
				playerHitsPlatformEventHandler(env, e);
			}
		});
		this.eventQueue.clear();
	}
	
	private void playerHitsPlatformEventHandler(final Environment env, final GameEvent e) {
		final Player player = ((PlayerHitsPlatformEvent) e).getPlayer();
		final Platform platform = ((PlayerHitsPlatformEvent) e).getPlatform();
		player.getSpeedVector().get().noSpeedVectorSum(0, -1.0);
		AppLogger.getAppLogger().info("player hits platform");
	}
	
	private void playerHitsObstacleEventHandler(final Environment env, final GameEvent e) {
		final Player player = ((PlayerHitsObstacleEvent) e).getPlayer();
		final ObstacleImpl obstacle = ((PlayerHitsObstacleEvent) e).getObstacle();
		player.decreaseHealth((double) (obstacle.getMass() / 50));
		if (!player.isAlive()) {
			env.deleteObjByPosition(new ImmutablePosition2Dimpl(player.getPosition().get().getX(),
					player.getPosition().get().getY()));
		}
		AppLogger.getAppLogger().info("player hits obstacle");
	}

	private void playerHitsEnemyEventHandler(final Environment env, final GameEvent e) {
		final Player player = ((PlayerHitsEnemyEvent) e).getPlayer();
		final Enemy enemy = ((PlayerHitsEnemyEvent) e).getEnemy();
		player.setHealth(player.getHealth() - 0.01);
		// TODO: enemy.setHealth(enemy.getHealth() - player.COLLISION_DAMAGE);
		if (!player.isAlive()) {
			env.deleteObjByPosition(new ImmutablePosition2Dimpl(player.getPosition().get().getX(),
					player.getPosition().get().getY()));
		}

		if (!enemy.isAlive()) {
			env.deleteObjByPosition(new ImmutablePosition2Dimpl(enemy.getPosition().get().getX(),
					enemy.getPosition().get().getY()));
			
		}
		// TODO: player.setHealth(player.getHealth() - enemy.COLLISION_DAMAGE);
		// TODO: enemy.setHealth(enemy.getHealth() - player.COLLISION_DAMAGE);
		AppLogger.getAppLogger().info("player hits enemy");
	}

	private void playerHitsPickUpObjEventHandler(final Environment env, final GameEvent e) {
		final Player player = ((PlayerHitsItemEvent) e).getPlayer();
		// Apply item effect on character
		((PlayerHitsItemEvent) e).getItem()
			.getEffect()
			.applyEffect(player);
		// Update environment
		final MutablePosition2D pickupPos = ((PlayerHitsItemEvent) e).getItem().getPosition().get();
		env.deleteObjByPosition(new ImmutablePosition2Dimpl(pickupPos.getX(), pickupPos.getY()));
		if (!player.isAlive()) {
			env.deleteObjByPosition(new ImmutablePosition2Dimpl(player.getPosition().get().getX(), 
					player.getPosition().get().getY()));
		}
		AppLogger.getAppLogger().info("player hits item");
	}
}
