package ludumdare32;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class Character {

    static ArrayList<Character> characters = new ArrayList();

    private double x, y, vx, vy, hp, r;
    private final double maxhp;

    public Character(double x, double y, double hp, double r) {
        this.x = x;
        this.y = y;
        this.hp = maxhp = hp;
        this.r = r;
    }

    public void move() {
        setX(getX() + getVx());
        setY(getY() + getVy());
        vx *= 0.9;
        vy *= 0.9;
    }

    public void paint(Graphics2D g) {
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }
    
    public void changeVx(double dv) {
        this.vx+=dv;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }
    
    public void changeVy(double dv) {
        this.vy+=dv;
    }

    public double getHp() {
        return hp;
    }

    public void setHp(double hp) {
        this.hp = hp;
        if (this.hp > maxhp) {
            this.hp = maxhp;
        }
        if (this.hp < 0) {
            //die
        }
    }

    public double getMaxHp() {
        return maxhp;
    }

    public double getR() {
        return r;
    }

    public void checkCollision() {
        ArrayList<Point> tiles = new ArrayList();
        int tx = (int) (x + 16) / 32;
        int ty = (int) (y + 16) / 32;
        try {
            for (int x = tx - 1; x < tx + 2; x++) {
                for (int y = ty - 1; y < ty + 2; y++) {
                    if (World.collisionMap[x][y]) {
                        tiles.add(new Point(x * 32, y * 32));
                    }
                }
            }
        } catch (Exception e) {
        }
        for (Point tile : tiles) {
            double cx = x - tile.x;
            double cy = y - tile.y;
            cx = Math.max(0, Math.min(32, cx));
            cy = Math.max(0, Math.min(32, cy));
            Vector2D collisionVector = new Vector2D(new Point.Double(x - cx - tile.x, y - cy - tile.y));
            Vector2D velocityVector = new Vector2D(new Point.Double(vx, vy));
            if (collisionVector.point.x * collisionVector.point.x + collisionVector.point.y * collisionVector.point.y < r * r) {
                Vector2D projectionVector = Vector2D.OrthogonalProjection(velocityVector, collisionVector);
                vx -= projectionVector.point.x * 1.1;
                vy -= projectionVector.point.y * 1.1;
            }
        }
    }
}
