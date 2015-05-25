package mapeditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

class WeatherPanel extends JPanel {

    JCheckBox cloudyBox;
    JCheckBox rainyBox;
    JCheckBox sunnyBox;
    JCheckBox snowyBox;
    JCheckBox aboveBox;

    JButton[] buttons = new JButton[4];
    int[] imageIDs = new int[4];
    boolean[] selected = new boolean[4];

    TileSet weatherTiles = new TileSet("img/weatherIcons.png", 221, 16, 66, 100, 82);
    TileSet selectedWeatherTiles = new TileSet("img/weatherIconsBlue.png", 221, 16, 66, 100, 82);

    public WeatherPanel() {
        weatherTiles.changeTileSize(30);
        selectedWeatherTiles.changeTileSize(28);
        
        addButtons();
        setPreferredSize(new Dimension(80, 80));
    }

    private void addButtons() {
        addButton(0);
        addButton(5);
        addButton(1);
        addButton(8);
    }

    int buttonsIterator = 0;

    private void addButton(int imageID) {
        JButton button = new JButton(new ImageIcon(weatherTiles.images[imageID]));
        button.setFocusPainted(false);
        final int ID = buttonsIterator;
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pressed(ID);
            }
        });
        button.setPreferredSize(new Dimension(30, 30));
        add(button);
        buttons[buttonsIterator] = button;
        imageIDs[buttonsIterator] = imageID;
        buttonsIterator++;
    }

    private void pressed(int ID) {
        selected[ID] = !selected[ID];
        if(selected[ID]){
            buttons[ID].setIcon(new ImageIcon(selectedWeatherTiles.images[imageIDs[ID]]));
        } else {
            buttons[ID].setIcon(new ImageIcon(weatherTiles.images[imageIDs[ID]]));
        }
    }
}
