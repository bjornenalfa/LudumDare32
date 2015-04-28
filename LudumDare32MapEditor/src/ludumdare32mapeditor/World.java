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

    static int worldNumber = 0;
    static String[] worlds = {"test", "test2"};

    static TileSet currentTileSet;

    static final int squareSize = 16;

    static boolean[][][] collisionMap;
    static boolean[][] renderAbove;
    static int[][] textureMap;
    static int[][] textureMap2;
    //sstatic int[][] textureMap3;
    static int width = 0;
    static int height = 0;
    static int pixelWidth = 0;
    static int pixelHeight = 0;

    static BufferedImage layer1;
    static BufferedImage layer2;
    //static BufferedImage layer3;

    static AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
    static ImageObserver nothing = new ImageObserver() {
        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    static void setTileSet(TileSet tileSet) {
        currentTileSet = tileSet;
    }

    static void changeTileSet(TileSet tileSet) {
        setTileSet(tileSet);
        renderMap();
    }
    
    static BufferedImage getImage() {
        throw new UnsupportedOperationException("Dario pls call this!");
    }

    static void changeTile(int x, int y, int tile1, int tile2, boolean cloudy, boolean sunny, boolean rainy, boolean snowy, boolean renderAbov) {
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

    static void renderMap() {
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

    static void paint(Graphics2D g) {
        g.drawImage(layer1, 0, 0, nothing);
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                g.drawImage(Tile.images[textureMap[x][y]], x * 32, y * 32, 32, 32, nothing);
//                g.drawImage(Tile.images[textureMap2[x][y]], x * 32, y * 32, 32, 32, nothing);
//            }
//        }
    }

    static void paint2(Graphics2D g) {
        g.drawImage(layer2, 0, 0, nothing);
    }

    public static void loadWorld(int ID) {
        try {
            loadFromFile("levels/" + worlds[ID] + ".png");
            worldNumber = ID;
        } catch (Exception e) {
            System.out.println("Unknown world");
            System.out.println(e);
            return;
        }
    }

    static BufferedImage img = null;

    static void changeImg(int x, int y, int argb) {
        img.setRGB(x, y, argb);
    }

    public static void loadFromFile(String path) {
        try {
            img = ImageIO.read(World.class.getResourceAsStream(path));
        } catch (IOException e) {
            System.out.println("Level not found");
            return;
        }
        width = img.getWidth();
        height = img.getHeight();
        pixelWidth = width * squareSize;
        pixelHeight = height * squareSize;
        textureMap = new int[width][height];
        textureMap2 = new int[width][height];
        renderAbove = new boolean[width][height];
        collisionMap = new boolean[width][height][4];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = img.getRGB(x, y);
                boolean cloudy = 1 == (1 & (argb >> 31));
                boolean rainy = 1 == (1 & (argb >> 30));
                boolean sunny = 1 == (1 & (argb >> 29));
                boolean snowy = 1 == (1 & (argb >> 28));
                boolean aboveCharacters = 1 == (1 & (argb >> 25));

                int texture1 = 4095 & (argb >> 12);
                int texture2 = 4095 & argb;
                textureMap[x][y] = texture1;
//                if (aboveCharacters) {
//                    textureMap3[x][y] = texture2;
//                    textureMap2[x][y] = TileSet.INVISIBLE;
//                } else {
                textureMap2[x][y] = texture2;
                renderAbove[x][y] = aboveCharacters;
//                    textureMap3[x][y] = TileSet.INVISIBLE;
//                }
                collisionMap[x][y][0] = cloudy;
                collisionMap[x][y][1] = sunny;
                collisionMap[x][y][2] = rainy;
                collisionMap[x][y][3] = snowy;

                /*int argbBackwards = (cloudy << 31) | (rainy << 30) | (sunny << 29) | (snowy << 28) | (texture1 << 12 ) | texture2;
                
                 int alpha = 255 & (argb >> 24);
                 int red = 255 & (argb >> 16);
                 int green = 255 & (argb >> 8);
                 int blue = 255 & (argb >> 0);
                 textureMap[x][y] = red + green * 256;*/
            }
        }
        renderMap();
    }
}
