package ludumdare32mapeditor;

public class Tile {

    int texture1 = TileSet.INVISIBLE;
    int texture2 = TileSet.INVISIBLE;
    boolean renderAbove = false;
    boolean collisionMap[] = {false, false, false, false};
    int layer = 0;

    public Tile() {
    }

    public Tile(int t1, int t2, boolean ra, boolean[] cm) {
        texture1 = t1;
        texture2 = t2;
        renderAbove = ra;
        collisionMap = cm;
        layer = 0;
    }

    public Tile(int t, boolean ra, boolean[] cm, int layer) {
        texture1 = t;
        renderAbove = ra;
        collisionMap = cm;
        this.layer = layer;
    }
}
