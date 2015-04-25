package ludumdare32;

import java.awt.Point;

public class Cloudy extends Weather{
    
    static TileSet tileSet = new TileSet("img/Spritesheet/cloudy.png", 16, 1);
    
    public static void activate(Point.Double point) {
        Weather.startTransition(point);
        //Tile.loadTileSet("img/Spritesheet/cloudy.png", 16, 1);
        World.changeTileSet(tileSet);
        World.renderMap();
        Weather.current = Weather.CLOUDY;
    }
}
