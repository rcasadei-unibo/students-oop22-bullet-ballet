package it.unibo.pensilina14.bullet.ballet.graphics.scenes;

import it.unibo.pensilina14.bullet.ballet.common.MutablePosition2D;
import it.unibo.pensilina14.bullet.ballet.graphics.map.Map;
import it.unibo.pensilina14.bullet.ballet.graphics.map.PlatformSprite;
import it.unibo.pensilina14.bullet.ballet.graphics.sprite.MainEnemy;
import it.unibo.pensilina14.bullet.ballet.graphics.sprite.MainPlayer;
import it.unibo.pensilina14.bullet.ballet.graphics.sprite.PhysicalObjectSpriteFactory;
import it.unibo.pensilina14.bullet.ballet.graphics.sprite.PhysicalObjectSpriteFactoryImpl;
import it.unibo.pensilina14.bullet.ballet.input.Controller;
import it.unibo.pensilina14.bullet.ballet.input.Down;
import it.unibo.pensilina14.bullet.ballet.input.Left;
import it.unibo.pensilina14.bullet.ballet.input.Right;
import it.unibo.pensilina14.bullet.ballet.input.Up;
import it.unibo.pensilina14.bullet.ballet.logging.AppLogger;
import it.unibo.pensilina14.bullet.ballet.model.characters.Enemy;
import it.unibo.pensilina14.bullet.ballet.model.environment.Environment;
import it.unibo.pensilina14.bullet.ballet.model.environment.GameState;
import it.unibo.pensilina14.bullet.ballet.model.environment.Platform;
import javafx.scene.Camera;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MapScene extends AbstractScene implements GameView{

    private final Pane appPane = new StackPane();
    private final Pane gamePane = new Pane();
    private final Pane uiPane = new Pane(); 
    
    private Map map = new Map();

    private ImageView backgroundView;

    private Optional<MainPlayer> mainPlayer;

    private final GameState gameState;
    private Optional<Controller> controller;
    private final Set<MainEnemy> enemySprites;

    public MapScene(final GameState gameState) {
        this.gameState = gameState;
        this.controller = Optional.empty();
        this.appPane.setMaxWidth(AbstractScene.SCENE_WIDTH); // caso mai la mappa fosse più grande o anche più piccola.
        this.appPane.setMaxHeight(AbstractScene.SCENE_HEIGHT);
        this.enemySprites = new HashSet<>();
    }

    public MapScene(final GameState gameState, final Controller ctrlr) {
        this.gameState = gameState;
        this.controller = Optional.of(ctrlr);
        this.appPane.setMaxWidth(AbstractScene.SCENE_WIDTH); // caso mai la mappa fosse più grande o anche più piccola.
        this.appPane.setMaxHeight(AbstractScene.SCENE_HEIGHT);
        this.enemySprites = new HashSet<>();
    }

    public final void setup() {
    	this.initScene();
        this.root.getChildren().add(this.appPane);
        AppLogger.getAppLogger().debug("Inside MapScene setup() method."); 
        try {
            this.backgroundView = new ImageView(new Image(Files.newInputStream(Paths.get(this.map.getMap().getPath()))));
        } catch (final IOException e) {
            e.printStackTrace();
			AppLogger.getAppLogger().error("Failed to load background image.");
		}
        this.backgroundView.fitWidthProperty().bind(this.root.widthProperty()); // per quando si cambia la risoluzione dello schermo.
        this.backgroundView.fitHeightProperty().bind(this.root.heightProperty());

        this.mainPlayer = Optional.empty();

        this.appPane.getChildren().addAll(this.backgroundView, this.gamePane, this.uiPane);
        AppLogger.getAppLogger().debug("appPane children: " + this.appPane.getChildren().toString());
        try {
        	this.render();
        } catch (final IOException exc) {
        	exc.printStackTrace();
        	AppLogger.getAppLogger().error("IOException, probably caused by a problem with components sprite imgs.");
        }
        this.addCameraListenerToPlayer();
    }

    private void addCameraListenerToPlayer() {
        this.mainPlayer.get().translateXProperty().addListener((obs, oldPosition, newPosition) -> {
            final int playerPosition = newPosition.intValue();

            // this.map.getWidth() / 2 = metà della mappa.
            
            if (playerPosition > (this.map.getMapWidth() / 2) 
            		&&  playerPosition < (this.gameState.getEnvGenerator().getLevelWidth()) - (this.map.getMapWidth() / 2)) {
                this.root.setLayoutX(-(playerPosition - (int) (this.map.getMapWidth() / 2)));
            }
        });
    }

    @Override
    public final void draw() {
	    update();
	    try {
			render();
		} catch (IOException e) {
			AppLogger.getAppLogger().error("Couldn't load sprite images..");
			e.printStackTrace();
		}
    }

    private void update() {
    	AppLogger.getAppLogger().debug("Inside update() method, checks input keys.");
        if (this.keysPressed.contains(KeyCode.UP)) { 
        	AppLogger.getAppLogger().info("Key 'UP' pressed.");
        	this.mainPlayer.get().getSpriteAnimation().play();
            this.controller.get().notifyCommand(new Up());
        }

        if (this.keysPressed.contains(KeyCode.RIGHT)) {
        	AppLogger.getAppLogger().info("Key 'RIGHT' pressed.");
            this.mainPlayer.get().getSpriteAnimation().play();
            this.controller.get().notifyCommand(new Right());
        }

        if (this.keysPressed.contains(KeyCode.DOWN)) { 
        	AppLogger.getAppLogger().info("Key 'DOWN' pressed.");
            this.controller.get().notifyCommand(new Down());
        }

        if (this.keysPressed.contains(KeyCode.LEFT)) {
        	AppLogger.getAppLogger().info("Key 'LEFT' pressed.");
            this.controller.get().notifyCommand(new Left());
        }

        if (this.keysReleased.contains(KeyCode.UP)) {
        	AppLogger.getAppLogger().info("Key 'UP' released.");
        	this.mainPlayer.get().getSpriteAnimation().stop();
        	this.keysReleased.remove(KeyCode.UP);
        }

        if (this.keysReleased.contains(KeyCode.RIGHT)) {
        	AppLogger.getAppLogger().info("Key 'RIGHT' released.");
            this.mainPlayer.get().getSpriteAnimation().stop();
        	this.keysReleased.remove(KeyCode.RIGHT);
        }

        if (this.keysReleased.contains(KeyCode.DOWN)) { 
        	AppLogger.getAppLogger().info("Key 'DOWN' pressed.");
        	this.keysReleased.remove(KeyCode.DOWN);
        }

        if (this.keysReleased.contains(KeyCode.LEFT)) {
        	AppLogger.getAppLogger().info("Key 'LEFT' pressed.");
        	this.keysReleased.remove(KeyCode.LEFT); 
        }
    }

    private void render() throws IOException {
    	AppLogger.getAppLogger().debug("Inside render() method.");
    	AppLogger.getAppLogger().debug("appPane: " + this.appPane.getChildren().toString());
    	AppLogger.getAppLogger().debug("gamePane: " + this.gamePane.getChildren().toString());

    	this.gamePane.getChildren().clear();

    	final Environment world = this.gameState.getGameEnvironment();
    	final int platformSize = this.gameState.getEnvGenerator().getPlatformSize();

    	final PhysicalObjectSpriteFactory physObjSpriteFactory = new PhysicalObjectSpriteFactoryImpl(this, world);

    	if (world.getPlayer().isPresent()) {
    		final MutablePosition2D playerPos = world.getPlayer().get().getPosition();
    		AppLogger.getAppLogger().debug(String.format("X: %g\tY: %g", playerPos.getX(), playerPos.getY()));
    		if (this.mainPlayer.isEmpty()) {
    			this.mainPlayer = Optional.of(new MainPlayer(playerPos.getX() * platformSize, 
    				playerPos.getY() * platformSize));
    		} else {
    			this.mainPlayer.get().renderPosition(playerPos.getX() * platformSize, 
        				playerPos.getY() * platformSize);
    		}
    		this.gamePane.getChildren().add(this.mainPlayer.get());
    		AppLogger.getAppLogger().debug(String.format("Player %s rendered.", world.getPlayer().get()));
    	}

    	for (final Platform x : world.getPlatforms().get()) {
    		final MutablePosition2D xPos = x.getPosition();
    		final PlatformSprite newSprite = new PlatformSprite(this.map.getPlatformType(), 
    				xPos.getX() * platformSize, xPos.getY() * platformSize);
    		this.gamePane.getChildren().add(newSprite);
    	}
    	AppLogger.getAppLogger().debug("Platforms rendered.");
//
////    	for (final Weapon x : world.getWeapons().get()) {
////    		for (final WeaponsImg y : WeaponsImg.values()) {
////    			if (x.getName().equals(y.getName())) {
////    				this.gamePane.getChildren().add(new WeaponSprite(y, x, platformSize));
////    				AppLogger.getAppLogger().debug("Weapon rendered");
////    			}
////    		}
////    	}
//
    	for (final Enemy x : world.getEnemies().get()) {
    		final MainEnemy enemySprite = new MainEnemy((int) (x.getPosition().getX() * platformSize), 
    				(int) (x.getPosition().getY() * platformSize));
    		this.gamePane.getChildren().add(enemySprite);
    		this.enemySprites.add(enemySprite);
    		AppLogger.getAppLogger().debug("Enemy rendered");
    	}

//    	for (final PhysicalObject x : world.getObstacles().get()) {
//    		final MutablePosition2D xPos = x.getPosition();
//    		if (x instanceof StaticObstacle) {
//    			this.gamePane.getChildren().add(physObjSpriteFactory.generateStaticObstacleSprite((int) (xPos.getX() * platformSize), 
//    					(int) (xPos.getY() * platformSize)));
//    			AppLogger.getAppLogger().debug("Static Obstacle rendered");
//    		} 
//    		if (x instanceof DynamicObstacle) {
//    			this.gamePane.getChildren().add(physObjSpriteFactory.generateDynamicObstacleSprite((int) (xPos.getX() * platformSize), 
//    					(int) (xPos.getY() * platformSize)));
//    			AppLogger.getAppLogger().debug("Dynamic Obstacle rendered");
//    		}
//    	}
//
//    	for (final Item x : world.getItems().get()) {
//    		for (final Images y : Images.values()) {
//    			if (x.getItemId().toString().equals(y.getObjectName())) {
//    				this.gamePane.getChildren().add(new PhysicalObjectSprite(y, (int) (x.getPosition().getX() * platformSize),
//    						(int) (x.getPosition().getY() * platformSize), x, this));
//    				AppLogger.getAppLogger().debug("Item rendered");
//    			}
//    		}
//    	}
    	/*
    	for (final Bullet x : this.gameState.getGameEnvironment().getBullets().get().subList()) {
    		// TODO add algorithm for bullets
    	}
    	 */
    	//DEBUG ONLY: throw new NullPointerException();

    }
    
//    private void renderPosition() {
//    	final Environment world = this.gameState.getGameEnvironment();
//    	final int platformSize = this.gameState.getEnvGenerator().getPlatformSize();
//    	
//    	if (world.getPlayer().isPresent()) {
//    		final MutablePosition2D playerPos = world.getPlayer().get().getPosition();
//    		AppLogger.getAppLogger().debug(String.format("X: %g\tY: %g", playerPos.getX(), playerPos.getY()));
//    		this.mainPlayer.get().renderPosition((int) (playerPos.getX() * platformSize), 
//    				(int) (playerPos.getY() * platformSize));
//    	}
//    	
//    	//this.assPlatform.forEach((x, y) -> x.setTranslateX(y.getPosition().getX() * platformSize));
//    	//this.assPlatform.forEach((x, y) -> x.setTranslateY(y.getPosition().getY()));
//    	//this.assEnemy.forEach((x, y) -> x.setTranslateX(y.getPosition().getX() * platformSize));
//    	//this.assEnemy.forEach((x, y) -> x.setTranslateY(y.getPosition().getY()));
//    }

    public final void setMap(final Map.Maps map) {
        this.map.setMap(map);
    }

    public final Pane getAppPane() {
    	return this.appPane;
    }

    public final Pane getGamePane() {
    	return this.gamePane;
    }

    public final Pane getUiPane() {
    	return this.uiPane;
    }

	@Override
	public final void setInputController(final Controller controller) {
		this.controller = Optional.of(controller);
	}
}
