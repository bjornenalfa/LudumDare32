package game;

import java.io.Serializable;

public class PlayerDataList implements Serializable {

    public PlayerData[] list;

    public PlayerDataList(PlayerData[] dataList) {
        list = dataList;
    }
    
    public PlayerDataList() {
        list = new PlayerData[0];
    }
}
