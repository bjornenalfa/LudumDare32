package ludumdare32;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TileHelper extends JFrame {

    JLabel label = new JLabel("");
    MyPanel drawPanel;
    
    TileSet tileSet = new TileSet("img/Spritesheet/cloudy.png", 16, 1);

    public TileHelper() {
        //Tile.loadTileSet("img/Spritesheet/cloudy.png", 16, 1);

        setTitle("TileHelper");

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel inPanel = new JPanel();
        inPanel.setPreferredSize(new Dimension(1824, 20));
        panel.add(inPanel, BorderLayout.NORTH);

        inPanel.setLayout(new BorderLayout());
        inPanel.add(label, BorderLayout.CENTER);
        MyCheckBoxPanel checkBoxPanel = new MyCheckBoxPanel();
        inPanel.add(checkBoxPanel, BorderLayout.EAST);

        drawPanel = new MyPanel();
        panel.add(drawPanel, BorderLayout.CENTER);

        setContentPane(panel);
        getContentPane().setPreferredSize(new Dimension(1824, 1012));
        setResizable(false);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    class MyCheckBoxPanel extends JPanel implements ItemListener {

        JCheckBox cloudyBox;
        JCheckBox rainyBox;
        JCheckBox sunnyBox;
        JCheckBox snowyBox;
        JCheckBox aboveBox;

        public MyCheckBoxPanel() {
            setLayout(new GridLayout(1, 4));
            cloudyBox = new JCheckBox("CloudyCollision", false);
            rainyBox = new JCheckBox("RainyCollision", false);
            sunnyBox = new JCheckBox("SunnyCollision", false);
            snowyBox = new JCheckBox("SnowyCollision", false);
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
                cloudy = 2 - e.getStateChange();
            } else if (source == rainyBox) {
                rainy = 2 - e.getStateChange();
            } else if (source == sunnyBox) {
                sunny = 2 - e.getStateChange();
            } else if (source == snowyBox) {
                snowy = 2 - e.getStateChange();
            } else if (source == aboveBox) {
                renderAbove = 2 - e.getStateChange();
            }
            drawPanel.calcColor();
        }
    }

    int cloudy = 0;
    int rainy = 0;
    int sunny = 0;
    int snowy = 0;
    int renderAbove = 0;

    class MyPanel extends JPanel {

        Point first;
        Point second;

        public MyPanel() {
            addMouseListener(mouseAdapter());
        }

        public void calcColor() {
            if (!Objects.equals(first, null) && !Objects.equals(second, null)) {
                int argbBackwards = (cloudy << 31) | (rainy << 30) | (sunny << 29) | (snowy << 28) | (renderAbove << 25) | (first.x/32 + first.y/32 * 57 << 12) | second.x/32 + second.y/32 * 57;
                int alpha = 0xFF & (argbBackwards >> 24);
                int red = 0xFF & (argbBackwards >> 16);
                int green = 0xFF & (argbBackwards >> 8);
                int blue = 0xFF & (argbBackwards);
                label.setText("Red:" + red + ",Green:" + green + ",Blue:" + blue + ",Alpha:" + alpha);
                StringSelection selection = new StringSelection(Integer.toHexString((first.x/32 + first.y/32 * 57 << 12) | second.x/32 + second.y/32 * 57));
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.fillRect(0, 0, 1824, 992);
            for (int y = 0; y < tileSet.verticalTiles; y++) {
                for (int x = 0; x < tileSet.horizontalTiles; x++) {
                    g.drawImage(tileSet.images[y * tileSet.horizontalTiles + x], x * 32, y * 32, 32, 32, null);
                }
            }
            if (!Objects.equals(first, null)) {
                g.setColor(Color.YELLOW);
                g.drawRect(first.x, first.y, 32, 32);
            }
            if (!Objects.equals(second, null)) {
                g.setColor(Color.MAGENTA);
                g.drawRect(second.x, second.y, 32, 32);
                if (Objects.equals(first, second)) {
                    g.setColor(Color.GREEN);
                    g.drawRect(first.x, first.y, 32, 32);
                }
            }
        }

        private MouseAdapter mouseAdapter() {
            return new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent me) {
                    if (me.getButton() == 1) {
                        System.out.println("left");
                        first = me.getPoint();
                        first.x -= first.x % 32;
                        first.y -= first.y % 32;
                    }
                    if (me.getButton() == 3) {
                        System.out.println("right");
                        second = me.getPoint();
                        second.x -= second.x % 32;
                        second.y -= second.y % 32;
                    }
                    calcColor();
                    repaint();
                }
            };
        }
    }

    public static void main(String[] args) {
        new TileHelper();
    }
}
