package ludumdare32;

import java.awt.Point;

public class Sunny extends Weather{
    
    public static void activate(Point.Double point) {
        Weather.startTransition(point);
        Tile.loadTileSet("img/Spritesheet/sunny.png", 16, 1);
        World.renderMap();
        Weather.current = Weather.SUNNY;
    }
}
