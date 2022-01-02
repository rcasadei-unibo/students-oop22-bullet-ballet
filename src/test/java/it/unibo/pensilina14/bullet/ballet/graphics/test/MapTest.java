package it.unibo.pensilina14.bullet.ballet.graphics.test;

import it.unibo.pensilina14.bullet.ballet.graphics.map.CoinSprite;
import it.unibo.pensilina14.bullet.ballet.common.Dimension2Dimpl;
import it.unibo.pensilina14.bullet.ballet.common.MutablePosition2Dimpl;
import it.unibo.pensilina14.bullet.ballet.common.SpeedVector2DImpl;
import it.unibo.pensilina14.bullet.ballet.graphics.map.BackgroundMap;
import it.unibo.pensilina14.bullet.ballet.graphics.map.PlatformSprite;
import it.unibo.pensilina14.bullet.ballet.graphics.sprite.MainEnemy;
import it.unibo.pensilina14.bullet.ballet.model.environment.GameEnvironment;
import it.unibo.pensilina14.bullet.ballet.model.environment.Platform;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class MapTest {

    @Test
    public void platformTest() throws IOException { //TODO: lasciare solo mapTest
        //TODO: platform Test
        PlatformSprite platform = new PlatformSprite(PlatformSprite.Platforms.DESERT_PLATFORM, 
            new MutablePosition2Dimpl(0, 0), new Platform(new SpeedVector2DImpl(new MutablePosition2Dimpl(0, 0), 0.0), new GameEnvironment(), 0.0, new Dimension2Dimpl(0, 0)));

        String path = "res/assets/maps/Tiles/desert_platform2.png";
    }

    @Test
    public void coinTest() throws IOException { //TODO: lasciare solo mapTest
        //TODO: coins test
        CoinSprite coinSprite = new CoinSprite(CoinSprite.Coins.GOLD_COIN, 0,0);

        coinSprite.coinChooser();
    }

    @Test
    public void mainPlayerTest() throws IOException { //TODO: lasciare solo mapTest
        //TODO: mainPlayer test
        //MainPlayer mainPlayer = new MainPlayer();
    }

    @Test
    public void mainEnemyTest() throws IOException { //TODO: lasciare solo mapTest
        //TODO: mainEnemy test
        MainEnemy mainEnemy = new MainEnemy(0,0);
    }

    @Test
    public void mapTest(){
        //TODO: map test
        BackgroundMap map = new BackgroundMap();
        map.setMap(BackgroundMap.Maps.CAVE);

        assertEquals(BackgroundMap.Maps.CAVE.getPath(), map.getMap().getPath());
        assertEquals(PlatformSprite.Platforms.CAVE_PLATFORM, map.getPlatformType());

        final int width = 1280;
        final int height = 720;

        assertEquals(width, map.getMapWidth());
        assertEquals(height, map.getMapHeight());

        //Check whether map, platform and coin are in range of enum.
        // MAP TYPE CHECK
        boolean mapTypeChecker = false;
        for(BackgroundMap.Maps m : BackgroundMap.Maps.values()){
            if (m == map.getMap()) {
                mapTypeChecker = true;
                break;
            }
        }

        assertTrue(mapTypeChecker);

        // PLATFORM TYPE CHECK

        boolean platformTypeChecker = false;
        for(PlatformSprite.Platforms p : PlatformSprite.Platforms.values()){
            if (p == map.getPlatformType()) {
                platformTypeChecker = true;
                break;
            }
        }

        assertTrue(platformTypeChecker);


        // COIN TYPE CHECK

        boolean coinTypeChecker = false;
        for(CoinSprite.Coins c : CoinSprite.Coins.values()){
            if (c == map.getCoinType()) {
                coinTypeChecker = true;
                break;
            }
        }

        assertTrue(coinTypeChecker);
    }

    @Test
    public void mapSceneTest(){
        //TODO: mapScene test
    }
}
