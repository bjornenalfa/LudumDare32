package ludumdare32;

import ludumdare32.LudumDare32.MyPanel;

public class Camera {

    double translateX, translateY;
    int halfWidth, halfHeight;

    MyPanel panel;

    public Camera(MyPanel panel) {
        halfWidth = panel.getWidth() / 2;
        halfHeight = panel.getHeight() / 2;
    }

    public void update(Player player, boolean centered) {
        if (centered) {
            translateX = -player.getX() - halfWidth;
            if (translateX < halfWidth) {
                translateX = halfWidth;
            }
            if (translateX > World.width - halfWidth) {
                translateX = World.width - halfWidth;
            }
            translateY = -player.getY() - halfHeight;
            if (translateY < halfHeight) {
                translateY = halfHeight;
            }
            if (translateY > World.height - halfHeight) {
                translateY = World.height - halfHeight;
            }
        }
        
    }
}
