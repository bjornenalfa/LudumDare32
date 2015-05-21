package mapeditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class Camera {

    private double x = 0, y = 0;
    private double halfWidth = 400, halfHeight = 304;
    int width = 800, height = 608;
    private double scale = 1;

    public Camera(int width, int height) {
        this.width = width;
        this.height = height;
        halfWidth = width / 2d;
        halfHeight = height / 2d;
    }

    public Point.Double windowToWorldCoordinates(Point p) {
        return windowToWorldCoordinates(p.x, p.y);
    }

    public Point.Double windowToWorldCoordinates(Point.Double p) {
        return windowToWorldCoordinates(p.x, p.y);
    }

    public Point.Double windowToWorldCoordinates(double wx, double wy) {
        return new Point.Double((wx + x) / scale, (wy + y) / scale);
    }

    public void changeSize(int width, int height) {
        this.width = width;
        this.height = height;
        halfWidth = width / 2d;
        halfHeight = height / 2d;
    }

    public void zoomOnWindowPoint(double scaleMultiplier, Point point) {
        scale *= scaleMultiplier;
        x = (x * scaleMultiplier + (scaleMultiplier - 1) * point.x);
        y = (y * scaleMultiplier + (scaleMultiplier - 1) * point.y);
    }

    public void zoomCenter(double scaleMultiplier) {
        scale *= scaleMultiplier;
        x = (x * scaleMultiplier + (scaleMultiplier - 1) * halfWidth);
        y = (y * scaleMultiplier + (scaleMultiplier - 1) * halfHeight);
        //x+=halfWidth-halfWidth/scaleMultiplier;
        //y+=halfHeight-halfHeight/scaleMultiplier;
        //x*=scaleMultiplier;
        //y*=scaleMultiplier;
    }

    public void moveWindowPixels(double dx, double dy) {
        x += dx;
        y += dy;
    }

    public void moveWorldPixels(double dx, double dy) {
        x += dx / scale;
        y += dy / scale;
    }

    public void setTranslate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void transformGraphics(Graphics2D g) {
        g.translate(-x, -y);
        g.scale(scale, scale);
    }

    public void resetTransform(Graphics2D g) {
        g.scale(1, 1);
        g.translate(0, 0);
    }

    public void clearScreen(Graphics2D g, Color color) {
        g.setColor(color);
        g.fillRect(0, 0, width, height);
    }

    public void constrainToWorld(World world) {
        x = Math.max(-width + World.squareSize * scale, Math.min(x, world.pixelWidth * scale - World.squareSize * scale));
        y = Math.max(-height + World.squareSize * scale, Math.min(y, world.pixelHeight * scale - World.squareSize * scale));
    }

    public void constrainToTileSet() {
        x = Math.max(-width + World.squareSize * scale, Math.min(x, MapEditor.tileSet.horizontalTiles * World.squareSize * scale - World.squareSize * scale));
        y = Math.max(-height + World.squareSize * scale, Math.min(y, MapEditor.tileSet.verticalTiles * World.squareSize * scale - World.squareSize * scale));
    }

//    public void update(Player player, boolean centered) {
//        if (centered) {
//            translateX = player.getX() - halfWidth;
//            translateY = player.getY() - halfHeight;
//            translateX = -Math.max(0, Math.min(World.pixelWidth - width, translateX));
//            translateY = -Math.max(0, Math.min(World.pixelHeight - height, translateY));
//        }
//    }
}
