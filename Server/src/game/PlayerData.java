package game;

import java.io.Serializable;

public class PlayerData implements Serializable {

    public float x;
    public float y;
    public long time;

    public PlayerData(float x, float y) {
        this.x = x;
        this.y = y;
        time = System.currentTimeMillis();
    }

    public PlayerData(PlayerData data) {
        this.x = data.x;
        this.y = data.y;
        this.time = data.time;
    }
}
