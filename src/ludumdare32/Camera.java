package ludumdare32;

import ludumdare32.LudumDare32.MyPanel;

public class Camera {

    double translateX, translateY;
    int halfWidth = 400, halfHeight = 300;

    MyPanel panel;

    public Camera() {
//        halfWidth = panel.getWidth() / 2;
//        halfHeight = panel.getHeight() / 2;
    }

    public void update(Player player, boolean centered) {
        if (centered) {
            translateX = player.getX() - halfWidth;
            translateY = player.getY() - halfHeight;
            translateX = -Math.max(0, Math.min(World.width * 32 - 2*halfWidth, translateX));
            translateY = -Math.max(0, Math.min(World.height * 32 - 2*halfHeight, translateY));
        }
    }
}
