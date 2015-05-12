package ludumdare32mapeditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ToolPanel extends JPanel {

    final static int PENCIL = 0;
    final static int BRUSH = 1;
    final static int BUCKET = 2;
    final static int PICKTILE = 3;
    final static int RECTANGLE = 4;
    
    static Tile tile = new Tile();
    final static int amountOfButtons = 5;
    JButton[] buttons = new JButton[amountOfButtons];
    int[] imageIDs = new int[amountOfButtons];
    
    static int currentTool = 0;
    
    TileSet toolTiles = new TileSet("img/Art-Icons-Transparent.png", 64, 23, 34, 77, 74);
    TileSet selectedToolTiles = new TileSet("img/Art-Icons-Blue-Transparent.png", 64, 23, 34, 77, 74);

    public ToolPanel() {
        setPreferredSize(new Dimension(80, 608));
        toolTiles.changeTileSize(30);
        selectedToolTiles.changeTileSize(28);
        
        addToolButtons();
        addLabels();
    }

    private void addToolButtons() {
        addButton(1);
        addButton(2);
        addButton(14);
        addButton(16);
        addButton(46);
    }

    int buttonsIterator = 0;

    private void addButton(int imageID) {
        JButton button = new JButton(new ImageIcon(toolTiles.images[imageID]));
        button.setFocusPainted(false);
        final int ID = buttonsIterator;
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeTool(ID);
            }
        });
        button.setPreferredSize(new Dimension(30, 30));
        add(button);
        buttons[buttonsIterator] = button;
        imageIDs[buttonsIterator] = imageID;
        buttonsIterator++;
    }

    private void changeTool(int toolID) {
        currentTool = toolID;
        for (int i = 0; i < amountOfButtons; i++) {
            buttons[i].setIcon(new ImageIcon(toolTiles.images[imageIDs[i]]));
        }
        buttons[toolID].setIcon(new ImageIcon(selectedToolTiles.images[imageIDs[toolID]]));
    }

    public void addLabels() {

    }

    
}
