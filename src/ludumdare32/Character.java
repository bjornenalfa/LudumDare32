package ludumdare32;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class Character {

    static ArrayList<Character> characters = new ArrayList();

    private double x, y, vx, vy, hp, r, acceleration;
    private final double maxhp;
    private double targetX, targetY;
    private boolean moveToTarget = false;

    public Character(double x, double y, double hp, double r, double acceleration) {
        this.x = x;
        this.y = y;
        this.hp = maxhp = hp;
        this.r = r;
        this.acceleration = acceleration;

        characters.add(this);
    }

    public static void updateCharacters() {
        for (Character character : characters) {
            character.update();
        }
    }

    public static void paintCharacters(Graphics2D g) {
        for (Character character : characters) {
            character.paint(g);
        }
    }

    public void update() {
        if (moveToTarget) {
            double angle = Math.atan2(targetY - y, targetX - x);
            double distance = Math.sqrt((targetX - x) * (targetX - x) + (targetY - y) * (targetY - y));
            double acc = Math.min(distance / 100, acceleration);
            changeVx(Math.cos(angle) * acc);
            changeVy(Math.sin(angle) * acc);
        }
        move();
        checkCollision();
    }

    public void move() {
        vx *= 0.9;
        vy *= 0.9;
        setX(getX() + getVx());
        setY(getY() + getVy());
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
        this.vx += dv;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public void changeVy(double dv) {
        this.vy += dv;
    }

    public void changeV(double dx, double dy, double acc) {
        if (Math.abs(dx) + Math.abs(dy) > 0) {
            double angle = Math.atan2(dy, dx);
            vx += Math.cos(angle) * acc;
            vy += Math.sin(angle) * acc;
        }
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

    public void delete() {
        characters.remove(this);
    }

    public void setTarget(double x, double y) {
        targetX = x;
        targetY = y;
    }

    public void moveToTarget(boolean yes) {
        moveToTarget = yes;
    }

    public void checkCollision() {
        ArrayList<Point> tiles = new ArrayList();
        int tx = (int) (x + 16) / 32;
        int ty = (int) (y + 16) / 32;
        try {
            for (int x = Math.max(tx - 1, 0); x < Math.min(tx + 2, World.width); x++) {
                for (int y = Math.max(ty - 1, 0); y < Math.min(ty + 2, World.height); y++) {
                    if (World.collisionMap[x][y][Weather.current]) {
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
            if (cx + cy < 32) {
                // Ortogonal projektion av vektor från (0,32) till c på normaliserad vektor (1,-1)
                //cx = cx;
                cy = cy - 32;
                double scalar = (cx / Math.sqrt(2) - cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,-1)
                cx = scalar/Math.sqrt(2); // c = length * normaliserad vektor (1,-1)
                cy = -scalar/Math.sqrt(2);
                cy = cy + 32;
            }
            Vector2D collisionVector = new Vector2D(new Point.Double(x - cx - tile.x, y - cy - tile.y));
            Vector2D velocityVector = new Vector2D(new Point.Double(vx, vy));
            double sqdistance = collisionVector.point.x * collisionVector.point.x + collisionVector.point.y * collisionVector.point.y;
            if (sqdistance < r * r) {
                Vector2D projectionVector = Vector2D.OrthogonalProjection(velocityVector, collisionVector);
                if (Vector2D.scalarProductCoordinates(projectionVector, collisionVector) < 0) {
                    double penetrationDepth = (r-Math.sqrt(sqdistance));
                    collisionVector.normalize();
                    x += collisionVector.point.x * penetrationDepth;
                    y += collisionVector.point.y * penetrationDepth;
                    vx -= projectionVector.point.x * 1;
                    vy -= projectionVector.point.y * 1;
                }
            }
        }
        for (Point tile : tiles) {
            double cx = x - tile.x;
            double cy = y - tile.y;
            cx = Math.max(0, Math.min(32, cx));
            cy = Math.max(0, Math.min(32, cy));
            if (cx + cy < 32) {
                // Ortogonal projektion av vektor från (0,32) till c på normaliserad vektor (1,-1)
                //cx = cx;
                cy = cy - 32;
                double scalar = (cx / Math.sqrt(2) - cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,-1)
                cx = scalar/Math.sqrt(2); // c = length * normaliserad vektor (1,-1)
                cy = -scalar/Math.sqrt(2);
                cy = cy + 32;
            }
            Vector2D collisionVector = new Vector2D(new Point.Double(x - cx - tile.x, y - cy - tile.y));
            Vector2D velocityVector = new Vector2D(new Point.Double(vx, vy));
            double sqdistance = collisionVector.point.x * collisionVector.point.x + collisionVector.point.y * collisionVector.point.y;
            if (sqdistance < r * r) {
                Vector2D projectionVector = Vector2D.OrthogonalProjection(velocityVector, collisionVector);
                if (Vector2D.scalarProductCoordinates(projectionVector, collisionVector) < 0) {
                    double penetrationDepth = (r-Math.sqrt(sqdistance));
                    collisionVector.normalize();
                    x += collisionVector.point.x * penetrationDepth;
                    y += collisionVector.point.y * penetrationDepth;
                    vx -= projectionVector.point.x * 1;
                    vy -= projectionVector.point.y * 1;
                }
            }
        }
    }
}
