package game;

import java.io.Serializable;

public class PlayerDataList implements Serializable {

    public PlayerData[] list;
    public long timeStamp;

    public PlayerDataList(PlayerData[] dataList, long time) {
        list = dataList;
        timeStamp = time;
    }

    public PlayerDataList(long time) {
        list = new PlayerData[0];
        timeStamp = time;
    }
    
    public long getTime(){
        return timeStamp;
    }
}
