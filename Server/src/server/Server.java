package server;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author bjodet982
 */
public class Server implements Runnable {

    private DatagramSocket srvSocket;
    private int srvPort;
    private JFrame startFr;
    private JTextArea inA;
    private final ArrayList<InetSocketAddress> usersL;
    private final Map<InetSocketAddress, Object> toSend;
    private Long srvTime;
    private boolean srvRunning;

    public Server(int port) {
        srvPort = port;
        startFr = new JFrame();
        startFr.setResizable(true);
        startFr.setLocationRelativeTo(null);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startFr.dispatchEvent(new WindowEvent(startFr, WindowEvent.WINDOW_CLOSING));
            }
        };
        startFr.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        startFr.getRootPane().getActionMap().put("ESCAPE", escapeAction);
        startFr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFr.setSize(640, 480);
        startFr.setLayout(new GridLayout());
        inA = new JTextArea();
        inA.setEditable(false);
        inA.setLineWrap(true);
        inA.setWrapStyleWord(true);
        DefaultCaret caret1 = (DefaultCaret) inA.getCaret();
        caret1.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollP = new JScrollPane(inA);
        startFr.add(scrollP);
        startFr.setVisible(true);
        usersL = new ArrayList();
        toSend = new HashMap();
    }

    @Override
    public void run() {
        boolean portErr = false;
        do {
            try {
                srvSocket = new DatagramSocket(srvPort);
                portErr = false;
            } catch (SocketException ex) {
                portErr = true;
                final JDialog d = new JDialog(startFr, "", true);
                d.setLayout(new BorderLayout());
                JButton but1 = new JButton("OK");
                JButton but2 = new JButton("EXIT");
                d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                JLabel str = new JLabel("Port already in use! Input a new port: ", SwingConstants.CENTER);
                final JTextField srvP = new JTextField(Integer.toString(srvPort), 10);
                but1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e1) {
                        String input = srvP.getText();
                        if (input.matches("[0-9]+")) {
                            srvPort = Integer.parseInt(input);
                            d.dispose();
                        }
                    }
                });
                but2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e1) {
                        d.dispose();
                        startFr.dispatchEvent(new WindowEvent(startFr, WindowEvent.WINDOW_CLOSING));
                    }
                });
                JPanel p1 = new JPanel(new FlowLayout());
                JPanel p2 = new JPanel(new FlowLayout());
                JPanel p3 = new JPanel(new FlowLayout());
                d.setLocationRelativeTo(startFr);
                d.setResizable(false);
                p1.add(str);
                p2.add(srvP);
                p3.add(but1);
                p3.add(but2);
                d.add(p1, BorderLayout.NORTH);
                d.add(p2, BorderLayout.CENTER);
                d.add(p3, BorderLayout.SOUTH);
                d.pack();
                d.setVisible(true);
            }
        } while (portErr);
        srvTime = (System.currentTimeMillis() / 1000L);
        srvRunning = true;
        addOut("Server starting up on UDP port: " + srvPort);

        Thread in = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    DatagramPacket incomingPacket = new DatagramPacket(new byte[1024], new byte[1024].length);
                    srvSocket.receive(incomingPacket);
                    ByteArrayInputStream in = new ByteArrayInputStream(incomingPacket.getData());
                    ObjectInputStream is = new ObjectInputStream(in);
                    Object obj = (Object) is.readObject();
                    toSend.put((InetSocketAddress) incomingPacket.getSocketAddress(), obj);

                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
        in.start();

        Thread out = new Thread(new Runnable() {

            @Override
            public void run() {
                for (InetSocketAddress s : usersL) {
                    Iterator it = toSend.entrySet().iterator();
                    while (it.hasNext()) {
                        Entry ent = (Entry) it.next();
                        if (ent.getKey() != s) {
                            sendObj(ent.getValue(), s.getHostString(), s.getPort());
                        }
                    }
                }
            }
        });
        out.start();
    }

    private void addOut(String str) {
        if (inA.getText().isEmpty()) {
            inA.setText(str);
        } else {
            inA.append("\n" + str);
        }
    }

    private void sendObj(Object obj, String ipaddr, int port) {
        try {
            DatagramSocket socket = new DatagramSocket();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(obj);
            byte[] data = outputStream.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(ipaddr), port);
            socket.send(sendPacket);
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Server(9002).run();
    }
}
