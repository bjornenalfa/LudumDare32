package mapeditor;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

class CheckBoxPanel extends JPanel implements ItemListener {

    JCheckBox cloudyBox;
    JCheckBox rainyBox;
    JCheckBox sunnyBox;
    JCheckBox snowyBox;
    JCheckBox aboveBox;

    public CheckBoxPanel() {
        TileSet weatherSet = new TileSet("img/weatherIconsBlue.png", 221, 16, 66, 100, 82);
        weatherSet.changeTileSize(30);
        
        setLayout(new GridLayout(1, 0));
        cloudyBox = new JCheckBox(new ImageIcon(weatherSet.images[0]), false);
        rainyBox = new JCheckBox(new ImageIcon(weatherSet.images[5]), false);
        sunnyBox = new JCheckBox(new ImageIcon(weatherSet.images[1]), false);
        snowyBox = new JCheckBox(new ImageIcon(weatherSet.images[8]), false);
        aboveBox = new JCheckBox("RenderAboveCharacters", false);

        cloudyBox.addItemListener(this);
        rainyBox.addItemListener(this);
        sunnyBox.addItemListener(this);
        snowyBox.addItemListener(this);
        aboveBox.addItemListener(this);

        add(cloudyBox);
        add(rainyBox);
        add(sunnyBox);
        add(snowyBox);
        add(aboveBox);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();

        if (source == cloudyBox) {
            ToolPanel.tile.collisionMap[0] = e.getStateChange() == ItemEvent.SELECTED;
        } else if (source == sunnyBox) {
            ToolPanel.tile.collisionMap[1] = e.getStateChange() == ItemEvent.SELECTED;
        } else if (source == rainyBox) {
            ToolPanel.tile.collisionMap[2] = e.getStateChange() == ItemEvent.SELECTED;
        } else if (source == snowyBox) {
            ToolPanel.tile.collisionMap[3] = e.getStateChange() == ItemEvent.SELECTED;
        } else if (source == aboveBox) {
            ToolPanel.tile.renderAbove = e.getStateChange() == ItemEvent.SELECTED;
        }
    }
}
