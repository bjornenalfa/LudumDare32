package network;

import java.io.Serializable;

public class PlayerDataList implements Serializable {

    public PlayerData[] list;
    public long timeStamp;

    public PlayerDataList(PlayerData[] dataList, long time) {
        list = dataList;
        timeStamp = time;
    }

    public PlayerDataList() {
        list = new PlayerData[0];
        timeStamp = 0;
    }
    
    public long getTime(){
        return timeStamp;
    }
}
