package ludumdare32mapeditor;

public class Tile {
    int texture1 = TileSet.INVISIBLE;
    int texture2 = TileSet.INVISIBLE;
    boolean renderAbove = false;
    boolean collisionMap[] = new boolean[4];
    
    public Tile(int t1, int t2, boolean ra, boolean[] cm) {
        texture1 = t1;
        texture2 = t2;
        renderAbove = ra;
        collisionMap = cm;
    }
}
