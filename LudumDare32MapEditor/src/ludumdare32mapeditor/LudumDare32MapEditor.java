package ludumdare32mapeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class LudumDare32MapEditor extends JFrame {

    static TileSet tileSet = new TileSet("img/Spritesheet/cloudy.png", 16, 1);
    World[] worlds = {World.loadFromFile(".//..//src//ludumdare32/levels//test.png", tileSet), World.loadFromFile(".//..//src//ludumdare32/levels//test2.png", tileSet)};
    int selectedWorld = 0;

    MapPanel mapPanel = new MapPanel(worlds, selectedWorld);
    TilePanel tilePanel = new TilePanel();
    JLabel label = new JLabel("");

    public LudumDare32MapEditor() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(LudumDare32MapEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Tile.loadTileSet("img/Spritesheet/cloudy.png", 16, 1);
        //World.setTileSet(tileSet);
        //World.loadFromFile("levels/test.jpg");
        worlds[1].setOffset(700, 0);

        setTitle("LudumDare32 Map Editor");

        JButton button1 = new JButton("SAVE");
        button1.addActionListener(save());
        JButton button2 = new JButton("LOAD");
        button2.addActionListener(load());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.add(button1);
        buttonPanel.add(button2);

        CheckBoxPanel checkBoxPanel = new CheckBoxPanel();

        JPanel inPanel = new JPanel(new BorderLayout());
        inPanel.setPreferredSize(new Dimension(1600, 20));
        inPanel.add(buttonPanel, BorderLayout.WEST);
        inPanel.add(label, BorderLayout.CENTER);
        inPanel.add(checkBoxPanel, BorderLayout.EAST);

        //JSplitPane tileCollisionPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tilePanel, );
        //tileCollisionPane.setResizeWeight(0.75);
        //tileCollisionPane.setOneTouchExpandable(true);
        //tileCollisionPane.setContinuousLayout(true);
        //tileCollisionPane.setDividerSize(6);

        //JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapPanel, tileCollisionPane);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapPanel, tilePanel);
        splitPane.setResizeWeight(0.75);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(6);

        JPanel toolPanel = new ToolPanel();

        JPanel anotherPanel = new JPanel(new BorderLayout());
        anotherPanel.add(toolPanel, BorderLayout.WEST);
        anotherPanel.add(splitPane, BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(anotherPanel, BorderLayout.CENTER);
        panel.add(inPanel, BorderLayout.NORTH);

        setContentPane(panel);
        getContentPane().setPreferredSize(new Dimension(1600, 608));
        setResizable(true);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Action save() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG", "png");
                FileNameExtensionFilter jpegFilter = new FileNameExtensionFilter("JPEG", "jpeg");
                chooser.setFileFilter(jpegFilter);
                chooser.setFileFilter(pngFilter);
                chooser.setCurrentDirectory(new File(".//..//src//ludumdare32/levels//"));
                if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    String saveFormat = "";
                    File file = chooser.getSelectedFile();
                    if (chooser.getFileFilter() == pngFilter) {
                        if (!file.getAbsolutePath().toLowerCase().endsWith(".png")) {
                            file = new File(file.getAbsolutePath() + ".png");
                        }
                        saveFormat = "png";
                    } else if (chooser.getFileFilter() == jpegFilter) {
                        if (!file.getAbsolutePath().toLowerCase().endsWith(".jpeg")) {
                            file = new File(file.getAbsolutePath() + ".jpeg");
                        }
                        saveFormat = "jpeg";
                    }

                    try {
                        ImageIO.write(worlds[selectedWorld].getImage(), saveFormat, file);
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                    System.out.println("Saving: " + file);
                }
            }
        };
    }

    private Action load() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File(".//..//src//ludumdare32/levels//"));
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try {
                        double x = worlds[selectedWorld].xOffset;
                        double y = worlds[selectedWorld].yOffset;
                        worlds[selectedWorld] = World.loadFromImage(ImageIO.read(file), tileSet);
                        worlds[selectedWorld].setOffset(x, y);
                        repaint();
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                    System.out.println("Loading: " + file);
                }
            }
        };
    }

    public static void main(String[] args) {
        new LudumDare32MapEditor();
    }
}
