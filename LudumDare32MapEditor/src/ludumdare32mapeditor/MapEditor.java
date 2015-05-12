package ludumdare32mapeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MapEditor extends JFrame {

    static TileSet tileSet = new TileSet(".//..//src//ludumdare32/img/Spritesheet/cloudy.png", 16, 1);
    static ArrayList<World> worlds = new ArrayList<>();
    static int selectedWorld = 0;

    MapPanel mapPanel = new MapPanel();
    TilePanel tilePanel = new TilePanel();
    JLabel label = new JLabel("");

    public MapEditor() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MapEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Tile.loadTileSet("img/Spritesheet/cloudy.png", 16, 1);
        //World.setTileSet(tileSet);
        //World.loadFromFile("levels/test.jpg");
        worlds.add(World.loadFromFile(".//..//src//ludumdare32/levels//test.png", "test", tileSet));
        worlds.add(World.loadFromFile(".//..//src//ludumdare32/levels//test2.png", "test2", tileSet));
        worlds.get(1).setOffset(700, 0);

        setTitle("LudumDare32 Map Editor");

        JButton button1 = new JButton("NEW");
        button1.addActionListener(newIMG());
        JButton button2 = new JButton("SAVE");
        button2.addActionListener(save());
        JButton button3 = new JButton("LOAD");
        button3.addActionListener(load());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.add(button1);
        buttonPanel.add(button2);
        buttonPanel.add(button3);

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

        panel.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
        panel.getActionMap().put("exit", exit());

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
                        ImageIO.write(worlds.get(selectedWorld).getImage(), saveFormat, file);
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
                        double x = worlds.get(selectedWorld).xOffset;
                        double y = worlds.get(selectedWorld).yOffset;
                        worlds.set(selectedWorld, World.loadFromImage(ImageIO.read(file), tileSet));
                        worlds.get(selectedWorld).setOffset(x, y);
                        repaint();
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                    System.out.println("Loading: " + file);
                }
            }
        };
    }

    private Action newIMG() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NumberFormat intOnly;
                intOnly = NumberFormat.getIntegerInstance();
                intOnly.setMaximumFractionDigits(0);
                JFormattedTextField width = new JFormattedTextField(intOnly);
                JFormattedTextField height = new JFormattedTextField(intOnly);
                Object[] message = {
                    "Width: ", width,
                    "Height: ", height
                };
                if (JOptionPane.showConfirmDialog(null, message, "Input size", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                    worlds.add(World.loadFromImage(new BufferedImage(Integer.parseInt(width.getText().replaceAll(",", "")), Integer.parseInt(height.getText().replaceAll(",", "")), BufferedImage.TYPE_INT_ARGB), tileSet));
                    worlds.get(worlds.size() - 1).setOffset(700, 700);
                    repaint();
                } else {

                }
            }
        };
    }

    private Action exit() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
    }

    public static void main(String[] args) {
        new MapEditor();
    }
}
