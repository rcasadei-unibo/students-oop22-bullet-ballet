package it.unibo.pensilina14.bullet.ballet.graphics.sprite;

import java.io.IOException;

import it.unibo.pensilina14.bullet.ballet.common.MutablePosition2Dimpl;
import it.unibo.pensilina14.bullet.ballet.common.SpeedVector2DImpl;
import it.unibo.pensilina14.bullet.ballet.graphics.scenes.MapScene;
import it.unibo.pensilina14.bullet.ballet.model.entities.PhysicalObject;
import it.unibo.pensilina14.bullet.ballet.model.environment.GameEnvironment;
import it.unibo.pensilina14.bullet.ballet.model.obstacle.ObstacleFactory;
import it.unibo.pensilina14.bullet.ballet.model.obstacle.ObstacleFactoryImpl;
import it.unibo.pensilina14.bullet.ballet.model.weapon.ItemFactory;
import it.unibo.pensilina14.bullet.ballet.model.weapon.ItemFactoryImpl;

public class PhysicalObjectSpriteFactoryImpl implements PhysicalObjectSpriteFactory{

    private static final double SPEED = 1.5;
    private final ObstacleFactory obstacleFact = new ObstacleFactoryImpl();
    private final ItemFactory itemFact = new ItemFactoryImpl();
    private final MapScene mapScene;

    public PhysicalObjectSpriteFactoryImpl(final MapScene scene) {
        this.mapScene = scene;
    }

    @Override
    public PhysicalObjectSprite generateDynamicObstacleSprite(final int x, final int y) throws IOException {
        final PhysicalObject dynamicObstacle = obstacleFact
                .createDynamicObstacle(new GameEnvironment(), new SpeedVector2DImpl(new MutablePosition2Dimpl(x, y), SPEED));
        return new PhysicalObjectSprite(Images.DYNAMIC_OBSTACLE, x, y, dynamicObstacle, this.mapScene);
    }

    @Override
    public PhysicalObjectSprite generateStaticObstacleSprite(final int x, final int y) throws IOException {
        final PhysicalObject staticObstacle = obstacleFact
                .createStaticObstacle(new GameEnvironment(), new MutablePosition2Dimpl(x, y));
        return new PhysicalObjectSprite(Images.STATIC_OBSTACLE, x, y, staticObstacle, this.mapScene);
    }

    @Override
    public PhysicalObjectSprite generateHealingItemSprite(final int x, final int y) throws IOException{
        final PhysicalObject staticItem = itemFact
                .createHealingItem(new GameEnvironment(), new SpeedVector2DImpl(new MutablePosition2Dimpl(x, y), SPEED));
        return new PhysicalObjectSprite(Images.HEALING_ITEM, x, y, staticItem, this.mapScene);
    }

    @Override
    public PhysicalObjectSprite generateDamagingItemSprite(final int x, final int y) throws IOException{
        final PhysicalObject staticItem = itemFact
                .createDamagingItem(new GameEnvironment(), new SpeedVector2DImpl(new MutablePosition2Dimpl(x, y), SPEED));
        return new PhysicalObjectSprite(Images.DAMAGING_ITEM, x, y, staticItem, this.mapScene);
    }

    @Override
    public PhysicalObjectSprite generatePoisoningItemSprite(final int x, final int y) throws IOException{
        final PhysicalObject staticItem = itemFact
                .createPoisoningItem(new GameEnvironment(), new SpeedVector2DImpl(new MutablePosition2Dimpl(x, y), SPEED));
        return new PhysicalObjectSprite(Images.POISONING_ITEM, x, y, staticItem, this.mapScene);
    }
}
