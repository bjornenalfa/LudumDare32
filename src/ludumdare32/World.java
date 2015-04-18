package ludumdare32;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;

public class World {

    static void paint(Graphics2D g) {
        g.drawImage(Tile.images[0], 0, 0, new ImageObserver() {

            @Override
            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }
    
}
