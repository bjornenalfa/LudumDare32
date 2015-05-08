package ludumdare32mapeditor;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TileSet {

    BufferedImage[] images;

    int horizontalTiles = 0;
    int verticalTiles = 0;

    static final int INVISIBLE = 285;

    public TileSet(String path, int size, int margin) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(TileSet.class.getResourceAsStream(path));
        } catch (IOException e) {
            System.out.println("Spritesheet not found");
            return;
        }
        int width = img.getWidth();
        int height = img.getHeight();
        horizontalTiles = (int) Math.ceil((double) width / (size + margin));
        verticalTiles = (int) Math.ceil((double) height / (size + margin));

        images = new BufferedImage[horizontalTiles * verticalTiles];
        int iterator = 0;

        for (int y = 0; y < verticalTiles; y++) {
            for (int x = 0; x < horizontalTiles; x++) {
                BufferedImage img2 = img.getSubimage(x * (size + margin), y * (size + margin), size, size);
                images[iterator] = img2;
                iterator++;
            }
        }
    }
    
    public TileSet(String path, int size, int xMargin, int yMargin, int xOffset, int yOffset) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(TileSet.class.getResourceAsStream(path));
        } catch (IOException e) {
            System.out.println("Spritesheet not found");
            return;
        }
        int width = img.getWidth()-xOffset*2;
        int height = img.getHeight()-yOffset*2;
        horizontalTiles = (int) Math.ceil((double) width / (size + xMargin));
        verticalTiles = (int) Math.ceil((double) height / (size + yMargin));

        images = new BufferedImage[horizontalTiles * verticalTiles];
        int iterator = 0;

        for (int y = 0; y < verticalTiles; y++) {
            for (int x = 0; x < horizontalTiles; x++) {
                BufferedImage img2 = img.getSubimage(x * (size + xMargin) + xOffset, y * (size + yMargin) + yOffset, size, size);
                images[iterator] = img2;
                iterator++;
            }
        }
    }
}
