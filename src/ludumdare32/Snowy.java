package ludumdare32;

import java.awt.geom.Point2D;

public class Snowy extends Weather {

    static TileSet tileSet = new TileSet("img/Spritesheet/snowy.png", 16, 1);
    
    public static void activate(Point2D.Double point) {
        Weather.startTransition(point);
        //Tile.loadTileSet("img/Spritesheet/snowy.png", 16, 1);
        World.changeTileSet(tileSet);
        World.renderMap();
        Weather.current = Weather.SNOWY;
    }
    
    public static void makeParticles() {
        if (transitioning && old == SNOWY) {
            for (int i = 0;i<3;i++) {
                SnowParticle particle = new SnowParticle(-LudumDare32.camera.translateX+Math.random()*1600-400,-LudumDare32.camera.translateY-50-Math.random()*100);
            }
        } else {
            for (int i = 0;i<3;i++) {
                SnowParticle particle = new SnowParticle(-LudumDare32.camera.translateX+Math.random()*1600-400,-LudumDare32.camera.translateY-50-Math.random()*100);
            }
        }
    }
    
    public static void update() {
        makeParticles();
    }
    
}
