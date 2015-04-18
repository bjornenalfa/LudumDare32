package ludumdare32;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

public class Vector2D {

    static final Vector2D zero = new Vector2D(0, 0);

    Point.Double point;
    Double length;
    Double angle;

    public Vector2D(Point.Double point) {
        this.point = point;
    }

    public Vector2D(int x, int y) {
        point = new Point.Double(x, y);
    }
    
    public Vector2D(double x1, double y1, double x2, double y2) {
        point = new Point.Double(x1-x2, y1-y2);
    }

    public Vector2D(Point.Double point1, Point.Double point2) {
        point = new Point.Double(point2.x - point1.x, point2.y - point1.y);
    }

    public Vector2D(double len, double ang) {
        length = len;
        angle = ang;
    }

    public Vector2D(Vector2D base) {
        length = base.length;
        angle = base.angle;
        point = (Point2D.Double) base.point.clone();
    }

    public Vector2D rotate(double deltaAngle) {
        if (angle == null) {
            angle = calculateAngle(point);
        }
        angle += deltaAngle;
        readyLength();
        point = null;
        return this;
    }

    public Point.Double getPoint() {
        if (point == null) {
            point = calculatePoint(length, angle);
        }
        return point;
    }

    public double getAngle() {
        if (angle == null) {
            angle = calculateAngle(point);
        }
        return angle;
    }

    public double getLength() {
        if (length == null) {
            length = calculateLength(point);
        }
        return length;
    }

    public void readyPoint() {
        if (point == null) {
            point = calculatePoint(length, angle);
        }
    }

    public void readyLength() {
        if (length == null) {
            calculateLength();
        }
    }

    public Vector2D add(Vector2D secondVector) {
        readyPoint();
        point.x += secondVector.getPoint().x;
        point.y += secondVector.getPoint().y;
        return this;
    }

    public Vector2D subtract(Vector2D secondVector) {
        readyPoint();
        point.x -= secondVector.getPoint().x;
        point.y -= secondVector.getPoint().y;
        return this;
    }

    public Vector2D multiply(double num) {
        if (length != null) {
            length *= num;
        }
        if (point != null) {
            point.x *= num;
            point.y *= num;
        }
        return this;
    }

    public Vector2D print() {
        System.out.println(this.show());
        return this;
    }

    public String show() {
        return "x:" + point.x + " y:" + point.y;

    }

    public void calculateLength() {
        length = calculateLength(point);
    }

    public static double calculateAngle(Point.Double point) {
        return Math.atan2(point.y, point.x);
    }

    public static double calculateLength(Point.Double point) {
        return Math.sqrt(point.x * point.x + point.y * point.y);
    }

    public static Point.Double calculatePoint(double length, double angle) {
        return new Point.Double(Math.cos(angle) * length, Math.sin(angle) * length);
    }

    public static Vector2D add(Vector2D firstVector, Vector2D secondVector) {
        firstVector.point.x += secondVector.getPoint().x;
        firstVector.point.y += secondVector.getPoint().y;
        return firstVector;
    }

    public static Vector2D subtract(Vector2D firstVector, Vector2D secondVector) {
        firstVector.point.x -= secondVector.getPoint().x;
        firstVector.point.y -= secondVector.getPoint().y;
        return firstVector;
    }

    public static Vector2D multiply(Vector2D vector, double num) {
        if (vector.length != null) {
            vector.length *= num;
        }
        if (vector.point != null) {
            vector.point.x *= num;
            vector.point.y *= num;
        }
        return vector;
    }

    public static double scalarProductCoordinates(Vector2D vector1, Vector2D vector2) {
        return (vector1.point.x * vector2.point.x + vector1.point.y * vector2.point.y);
    }

    public static double crossProduct(Vector2D vector1, Vector2D vector2) {
        return vector1.point.x * vector2.point.y - vector1.point.y * vector2.point.x;
    }

    public static Vector2D crossProduct(Vector2D vector, double k) {
        return new Vector2D(new Point.Double(k * vector.point.y, -k * vector.point.x));
    }

    public static Vector2D crossProduct(double k, Vector2D vector) {
        return new Vector2D(new Point.Double(-k * vector.point.y, k * vector.point.x));
    }

    public static Vector2D OrthogonalProjection(Vector2D vector, Vector2D base) {
        if (base.getLength() == 0) {
            return zero;
        }
        return multiply(new Vector2D(base), scalarProductCoordinates(vector, base) / (Math.pow(base.getLength(), 2)));
    }

    public static Vector2D getNormalComponent(Vector2D vector, Vector2D base) {
        return subtract(new Vector2D(vector), OrthogonalProjection(vector, base));
    }

    public Vector2D getCounterClockwiseNormal() {
        if (point == null) {
            readyPoint();
        }
        return new Vector2D(new Point.Double(point.y, -point.x));
    }

    public Vector2D getClockwiseNormal() {
        if (point == null) {
            readyPoint();
        }
        return new Vector2D(new Point.Double(-point.y, point.x));
    }

    public Vector2D normalize() {
        if (point == null) {
            length = 1.0;
        } else {
            calculateLength();
            if (length == 0) {
                return this;
            }
            point = new Point.Double(point.x / length, point.y / length);
        }
        return this;
    }

    public void paint(Graphics2D g, double x, double y, double scale, Color c) {
        g.setColor(c);
        g.drawLine((int) x, (int) y, (int) (x + getPoint().x * scale), (int) (y + getPoint().y * scale));
    }

    @Override
    public String toString() {
        return "A:" + getAngle() + " L:" + getLength() + " X:" + getPoint().x + " Y:" + point.y;
    }
}
