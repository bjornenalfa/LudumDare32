package game;

import java.io.Serializable;

public class PlayerData implements Serializable{
    public float x;
    public float y;
    
    public PlayerData(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
