package it.unibo.pensilina14.bullet.ballet.environment.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import it.unibo.pensilina14.bullet.ballet.environment.Dimension2Dimpl;
import it.unibo.pensilina14.bullet.ballet.environment.Environment;
import it.unibo.pensilina14.bullet.ballet.environment.GameEnvironment;
import it.unibo.pensilina14.bullet.ballet.environment.ImmutablePosition2D;
import it.unibo.pensilina14.bullet.ballet.environment.ImmutablePosition2Dimpl;
import it.unibo.pensilina14.bullet.ballet.environment.PhysicalObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.Test;

public class GameEnvironmentTest {
  private static final double EARTH_GRAVITY = 9.81;
  private static final int DEFAULT_DIM = 500;

  @Test
  public void testGameEnvironment() {
    final Environment gameEnv = new GameEnvironment();
    
    assertEquals(gameEnv.getGravity(), EARTH_GRAVITY);
    assertSame(gameEnv.getDimension(), new Dimension2Dimpl(DEFAULT_DIM, DEFAULT_DIM));
    
    final Map<ImmutablePosition2D, Optional<PhysicalObject>> expectedMap = new HashMap<>();
    for (int x = 0; x < gameEnv.getDimension().getWidth(); x++) {
      for (int y = 0; y < gameEnv.getDimension().getHeight(); y++) {
        expectedMap.put(new ImmutablePosition2Dimpl(x, y), Optional.empty());
      }
    }
    
    assertSame(gameEnv.getMap(), expectedMap);
  }
}
