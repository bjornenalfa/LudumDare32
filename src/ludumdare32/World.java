package ludumdare32;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;

public class World {

    static ImageObserver nothing = new ImageObserver() {

        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    static void paint(Graphics2D g) {
        for (int y = 0;y<Tile.verticalTiles;y++) {
            for (int x = 0;x<Tile.horizontalTiles;x++) {
                //g.drawImage(Tile.images[y*Tile.horizontalTiles+x], x*16, y*16, nothing);
                g.drawImage(Tile.images[y*Tile.horizontalTiles+x], x*32, y*32, 32, 32, nothing);
            }
        }
        g.drawImage(Tile.images[0], 0, 0, nothing);
    }
    
}
