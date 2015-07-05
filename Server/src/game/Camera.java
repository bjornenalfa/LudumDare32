package game;

import game.LudumDare32.MyPanel;

public class Camera {

    double translateX, translateY;
    int halfWidth = 400, halfHeight = 304;
    int width = 800, height = 608;

    MyPanel panel;

    public Camera() {
//        halfWidth = panel.getWidth() / 2;
//        halfHeight = panel.getHeight() / 2;
    }

    public void update(Player player, boolean centered) {
        if (centered) {
            translateX = player.getX() - halfWidth;
            translateY = player.getY() - halfHeight;
            translateX = -Math.max(0, Math.min(World.pixelWidth - width, translateX));
            translateY = -Math.max(0, Math.min(World.pixelHeight - height, translateY));
        }
    }
}
