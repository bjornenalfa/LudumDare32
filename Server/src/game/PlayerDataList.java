package game;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayerDataList implements Serializable {

    ArrayList<PlayerData> list;

    public PlayerDataList(ArrayList dataList) {
        list = dataList;
    }

    public PlayerDataList() {
        list = new ArrayList();
    }

    public void add(PlayerData data) {
        list.add(data);
    }

    public void clear() {
        list.clear();
    }
}
