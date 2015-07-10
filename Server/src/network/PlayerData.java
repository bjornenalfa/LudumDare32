package network;

import java.io.Serializable;

public class PlayerData implements Serializable {

    public float x;
    public float y;
    public long time;
    public String ID;

    public PlayerData(float x, float y, String name) {
        this.x = x;
        this.y = y;
        ID = name;
        time = System.currentTimeMillis();
    }

    public PlayerData(PlayerData data) {
        this.x = data.x;
        this.y = data.y;
        this.time = data.time;
        this.ID = data.ID;
    }
}
