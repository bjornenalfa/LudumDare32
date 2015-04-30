package ludumdare32mapeditor;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import javax.imageio.ImageIO;

public class World {

//    static int worldNumber = 0;
    static String[] worlds = {"test", "test2"};

    TileSet currentTileSet;

    static final int squareSize = 16;

    boolean[][][] collisionMap;
    boolean[][] renderAbove;
    int[][] textureMap;
    int[][] textureMap2;
    //static int[][] textureMap3;
    int width = 0;
    int height = 0;
    int pixelWidth = 0;
    int pixelHeight = 0;

    double xOffset = 0;
    double yOffset = 0;

    BufferedImage layer1;
    BufferedImage layer2;
    //static BufferedImage layer3;

    static AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
    static ImageObserver nothing = new ImageObserver() {
        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    public World(BufferedImage image, TileSet tileSet) {
        currentTileSet = tileSet;
        width = image.getWidth();
        height = image.getHeight();
        pixelWidth = width * squareSize;
        pixelHeight = height * squareSize;
        textureMap = new int[width][height];
        textureMap2 = new int[width][height];
        renderAbove = new boolean[width][height];
        collisionMap = new boolean[width][height][4];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = image.getRGB(x, y);
                boolean cloudy = 1 == (1 & (argb >> 31));
                boolean rainy = 1 == (1 & (argb >> 30));
                boolean sunny = 1 == (1 & (argb >> 29));
                boolean snowy = 1 == (1 & (argb >> 28));
                boolean aboveCharacters = 1 == (1 & (argb >> 25));

                int texture1 = 4095 & (argb >> 12);
                int texture2 = 4095 & argb;
                textureMap[x][y] = texture1;
                textureMap2[x][y] = texture2;
                renderAbove[x][y] = aboveCharacters;
                collisionMap[x][y][0] = cloudy;
                collisionMap[x][y][1] = sunny;
                collisionMap[x][y][2] = rainy;
                collisionMap[x][y][3] = snowy;
            }
        }
        renderMap();
    }

    public World(int width, int height, TileSet tileSet) {
        currentTileSet = tileSet;
        pixelWidth = width * squareSize;
        pixelHeight = height * squareSize;
        textureMap = new int[width][height];
        textureMap2 = new int[width][height];
        renderAbove = new boolean[width][height];
        collisionMap = new boolean[width][height][4];
        renderMap();
    }

    public void move(double dx, double dy) {
        xOffset += dx;
        yOffset += dy;
    }

    public void setOffset(double x, double y) {
        xOffset = x;
        yOffset = y;
    }

    public void setTileSet(TileSet tileSet) {
        currentTileSet = tileSet;
    }

    public void changeTileSet(TileSet tileSet) {
        setTileSet(tileSet);
        renderMap();
    }

    public BufferedImage getImage() {
        BufferedImage image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //int data = (collisionMap[x][y][0] ? 1 : 0) << 31 | (collisionMap[x][y][1] ? 1 : 0) << 30 | (collisionMap[x][y][2] ? 1 : 0) << 29 | (collisionMap[x][y][3] ? 1 : 0) << 28 | (renderAbove[x][y] ? 1 : 0) << 25 | textureMap[x][y] << 12 | textureMap2[x][y];
                image.setRGB(x, y, (collisionMap[x][y][0] ? 1 : 0) << 31 | (collisionMap[x][y][1] ? 1 : 0) << 30 | (collisionMap[x][y][2] ? 1 : 0) << 29 | (collisionMap[x][y][3] ? 1 : 0) << 28 | (renderAbove[x][y] ? 1 : 0) << 25 | textureMap[x][y] << 12 | textureMap2[x][y]);
            }
        }
        return image;
    }

    public void changeTile(int x, int y, int tile1, int tile2, boolean cloudy, boolean sunny, boolean rainy, boolean snowy, boolean renderAbov) {
        if (x < 0 | x > width | y < 0 | y > height) {
            return;
        }
        collisionMap[x][y][0] = cloudy;
        collisionMap[x][y][1] = sunny;
        collisionMap[x][y][2] = rainy;
        collisionMap[x][y][3] = snowy;
        textureMap[x][y] = tile1;
        //if (renderAbove) {
        //    textureMap3[x][y] = tile2;
        //    textureMap2[x][y] = TileSet.INVISIBLE;
        //} else {
        textureMap2[x][y] = tile2;
        renderAbove[x][y] = renderAbov;
        //    textureMap3[x][y] = TileSet.INVISIBLE;
        //}
        Graphics2D g2d = (Graphics2D) layer1.getGraphics();
        g2d.setComposite(composite);
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(x * squareSize, y * squareSize, squareSize, squareSize);
        layer1.createGraphics().drawImage(currentTileSet.images[textureMap[x][y]], x * squareSize, y * squareSize, nothing);

        g2d = (Graphics2D) layer2.getGraphics();
        g2d.setComposite(composite);
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(x * squareSize, y * squareSize, squareSize, squareSize);
        layer2.createGraphics().drawImage(currentTileSet.images[textureMap2[x][y]], x * squareSize, y * squareSize, nothing);

//        g2d = (Graphics2D) layer3.getGraphics();
//        g2d.setComposite(composite);
//        g2d.setColor(new Color(0, 0, 0, 0));
//        g2d.fillRect(x*squareSize, y*squareSize, squareSize, squareSize);
//        layer3.createGraphics().drawImage(currentTileSet.images[textureMap3[x][y]], x * squareSize, y * squareSize, nothing);
    }

    public void renderMap() {
        layer1 = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) layer1.getGraphics();
        g2d.setComposite(composite);
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, pixelWidth, pixelHeight);
        g2d = layer1.createGraphics();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                try {
                    g2d.drawImage(currentTileSet.images[textureMap[x][y]], x * squareSize, y * squareSize, nothing);
                } catch (Exception e) {
                    g2d.drawImage(currentTileSet.images[0], x * squareSize, y * squareSize, nothing);
                }
            }
        }

        layer2 = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        g2d = (Graphics2D) layer2.getGraphics();
        g2d.setComposite(composite);
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, pixelWidth, pixelHeight);
        g2d = layer2.createGraphics();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                try {
                    g2d.drawImage(currentTileSet.images[textureMap2[x][y]], x * squareSize, y * squareSize, nothing);
                } catch (Exception e) {
                    g2d.drawImage(currentTileSet.images[0], x * squareSize, y * squareSize, nothing);
                }
            }
        }

//        layer3 = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
//        g2d = (Graphics2D) layer3.getGraphics();
//        g2d.setComposite(composite);
//        g2d.setColor(new Color(0, 0, 0, 0));
//        g2d.fillRect(0, 0, pixelWidth, pixelHeight);
//        g2d = layer3.createGraphics();
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                try {
//                    g2d.drawImage(currentTileSet.images[textureMap3[x][y]], x * squareSize, y * squareSize, nothing);
//                } catch (Exception e) {
//                    g2d.drawImage(currentTileSet.images[0], x * squareSize, y * squareSize, nothing);
//                }
//            }
//        }
    }

    public void paintLayer1(Graphics2D g) {
        g.drawImage(layer1, (int) xOffset, (int) yOffset, nothing);
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                g.drawImage(Tile.images[textureMap[x][y]], x * 32, y * 32, 32, 32, nothing);
//                g.drawImage(Tile.images[textureMap2[x][y]], x * 32, y * 32, 32, 32, nothing);
//            }
//        }
    }

    public void paintLayer2(Graphics2D g) {
        g.drawImage(layer2, (int) xOffset, (int) yOffset, nothing);
    }

//    public static World loadWorld(int ID) {
//        try {
//            loadFromFile("levels/" + worlds[ID] + ".png");
//            worldNumber = ID;
//        } catch (Exception e) {
//            System.out.println("Unknown world");
//            System.out.println(e);
//            return;
//        }
//    }
    public static World loadFromFile(String path, TileSet tileSet) {
        try {
            return loadFromImage(ImageIO.read(World.class.getResourceAsStream(path)), tileSet);
        } catch (IOException e) {
            System.out.println("Level not found");
            return null;
        }
    }

    public static World loadFromImage(BufferedImage image, TileSet tileSet) {
        return new World(image, tileSet);
    }
}
