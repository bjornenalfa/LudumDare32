package game;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayerDataList implements Serializable {

    PlayerData[] list;

    public PlayerDataList(PlayerData[] dataList) {
        list = dataList;
    }
    
    public PlayerDataList() {
        list = new PlayerData[0];
    }

//    public PlayerDataList(int size) {
//        list = new PlayerData[size];
//    }

//    public void add(PlayerData data) {
//        list.add(data);
//    }

//    public void clear() {
//        list.clear();
//    }

//    public ArrayList<PlayerData> getL(){
//        return list;
//    }
}
