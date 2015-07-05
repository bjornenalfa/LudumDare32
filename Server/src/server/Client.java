/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author PastaPojken
 */
public class Client implements Runnable {

    private DatagramSocket srvSocket;
    private int srvPort;
    private String myID, srvIP;
    private boolean running, reset, connected, valIn;
    private Thread InThread, OutThread;
    private JFrame startFr, conFr, mainFr;
    private JPanel startP;
    private JTextArea outF, inArea;
    private JButton sendMsg;

    public Client() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        conFr = new JFrame();
        srvPort = 9002;
        inJFrame();
        reset = false;
        connected = false;
    }

    private void start() {
        boolean disconnect = true;
        while (disconnect) {
            try {
                srvSocket = new DatagramSocket();
                disconnect = false;
            } catch (IllegalArgumentException ex) {
                disconnect = true;
                startFr.dispose();
                conFr.dispose();
                final JDialog d = new JDialog(startFr, "", true);
                d.setLayout(new FlowLayout());
                JButton but = new JButton("OK");
                but.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e1) {
                        d.dispose();
                    }
                });
                JLabel str = new JLabel("Can not reach the server, please enter another ip!", SwingConstants.CENTER);
                d.setLocationRelativeTo(null);
                d.setResizable(false);
                d.add(str);
                d.add(but);
                d.pack();
                d.setVisible(true);
                inJFrame();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void inJFrame() {
        valIn = false;
        startFr = new JFrame();
        startFr.setResizable(false);
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
        startP = new JPanel();
        JPanel pan1 = new JPanel();
        JPanel pan2 = new JPanel();
        JPanel pan3 = new JPanel();
        startFr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFr.setSize(325, 200);
        startFr.setLayout(new GridLayout());
        final JTextField srvIPF = new JTextField(srvIP, 25);
        JLabel str1 = new JLabel("Server ip:");
        final JTextField srvIPp = new JTextField(Integer.toString(srvPort), 25);
        JLabel str2 = new JLabel("Server port:");
        final JTextField usrN = new JTextField(myID, 25);
        JLabel str3 = new JLabel("Username:");
        JButton button1 = new JButton("Connect");
        JButton button2 = new JButton("Exit");

        pan1.add(str1);
        pan1.add(srvIPF);
        pan2.add(str2);
        pan2.add(srvIPp);
        pan3.add(str3);
        pan3.add(usrN);
        startP.add(pan1);
        startP.add(pan2);
        startP.add(pan3);
        startP.add(button1);
        startP.add(button2);
        startFr.add(startP);
        startFr.setVisible(true);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                srvIP = srvIPF.getText();
                String input = srvIPp.getText();
                if (input.matches("[0-9]+")) {
                    srvPort = Integer.parseInt(input);
                    valIn = true;
                    myID = usrN.getText();
                    if (myID.isEmpty()) {
                        valIn = false;
                        final JDialog d = new JDialog(startFr, "", true);
                        d.setLayout(new BorderLayout());
                        JButton but = new JButton("OK");
                        but.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e1) {
                                d.dispose();
                            }
                        });
                        JLabel str = new JLabel("Username can not be empty!", SwingConstants.CENTER);
                        d.setLocationRelativeTo(startFr);
                        d.setResizable(false);
                        d.add(str, BorderLayout.NORTH);
                        d.add(but, BorderLayout.SOUTH);
                        d.pack();
                        d.setVisible(true);
                    } else {
                        valIn = true;
                    }
                } else {
                    valIn = false;
                    final JDialog d = new JDialog(startFr, "", true);
                    d.setLayout(new BorderLayout());
                    JButton but = new JButton("OK");
                    but.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e1) {
                            d.dispose();
                        }
                    });
                    JLabel str = new JLabel("Invalid port!", SwingConstants.CENTER);
                    d.setLocationRelativeTo(startFr);
                    d.setResizable(false);
                    d.add(str, BorderLayout.NORTH);
                    d.add(but, BorderLayout.SOUTH);
                    d.pack();
                    d.setVisible(true);
                }
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startFr.dispatchEvent(new WindowEvent(startFr, WindowEvent.WINDOW_CLOSING));
            }
        });
        while (!valIn) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }
        }
    }

    private void reset() {
        if (!InThread.isInterrupted()) {
            InThread.interrupt();
            try {
                InThread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (!OutThread.isInterrupted()) {
            OutThread.interrupt();
            try {
                OutThread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        reset = false;
        connected = false;
        mainFr.dispose();
        inJFrame();
        this.run();
    }

    @Override
    public void run() {
        startFr.dispose();
        conFr.setLocationRelativeTo(null);
        conFr.setResizable(false);
        conFr.add(new JLabel("Connecting...", SwingConstants.CENTER));
        conFr.setSize(125, 75);
        conFr.setVisible(true);
        start();
        conFr.dispose();
        mainFr = new JFrame();
        mainFr.setSize(800, 600);
        mainFr.setLayout(new BorderLayout());
        outF = new JTextArea();
        outF.setEditable(false);
        outF.setLineWrap(true);
        outF.setWrapStyleWord(true);
        DefaultCaret caret1 = (DefaultCaret) outF.getCaret();
        caret1.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollP1 = new JScrollPane(outF);
        scrollP1.setBounds(0, 0, mainFr.getWidth(), mainFr.getHeight());
        scrollP1.setPreferredSize(new Dimension(mainFr.getWidth(), 500));
        mainFr.add(scrollP1, BorderLayout.CENTER);
        JPanel p = new JPanel(new FlowLayout());
        inArea = new JTextArea();
        inArea.setLineWrap(true);
        inArea.setWrapStyleWord(true);
        inArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        inArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = inArea.getText();

                if (s.trim().length() > 0) {
                    sendStr(s);
                    outF.append("\nYou: " + s);
                    inArea.setText(null);
                }
            }
        });
        inArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inArea.append("\n");
            }
        });
        DefaultCaret caret2 = (DefaultCaret) inArea.getCaret();
        caret2.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        final JScrollPane scrollP2 = new JScrollPane(inArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollP2.setBounds(p.getX(), p.getY(), p.getWidth(), p.getHeight());
        sendMsg = new JButton("send");
        sendMsg.setEnabled(false);
        p.add(scrollP2, FlowLayout.LEFT);
        p.add(sendMsg);
        mainFr.add(p, BorderLayout.SOUTH);
        mainFr.setResizable(true);
        mainFr.setLocationRelativeTo(null);
        mainFr.setVisible(true);
        scrollP2.setPreferredSize(new Dimension(mainFr.getWidth() - (sendMsg.getWidth() * 2), inArea.getFont().getSize() * 2));
        mainFr.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                scrollP2.setPreferredSize(new Dimension(mainFr.getWidth() - (sendMsg.getWidth() * 2), inArea.getFont().getSize() * 2));
            }
        });
        mainFr.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                if (e.getNewState() == JFrame.MAXIMIZED_BOTH || e.getNewState() == JFrame.NORMAL) {
                    scrollP2.setPreferredSize(new Dimension(mainFr.getWidth() - (sendMsg.getWidth() * 2), inArea.getFont().getSize() * 2));
                }
            }
        });

        mainFr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFr.dispatchEvent(new WindowEvent(mainFr, WindowEvent.WINDOW_CLOSING));
            }
        };
        mainFr.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        mainFr.getRootPane().getActionMap().put("ESCAPE", escapeAction);
        running = true;
        connected = true;
        OutThread = new Thread();
        OutThread.start();
        InThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    addOut(getStr());
                }
                OutThread.interrupt();
            }
        });
        OutThread = new Thread(new Runnable() {
            @Override
            public void run() {
                sendMsg.setEnabled(true);
                sendMsg.setAction(new AbstractAction() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String s = inArea.getText();
                        if (s.trim().length() > 0) {
                            sendStr(s);
                            outF.append("\nYou: " + s);
                            inArea.setText(null);
                        }
                    }
                });
                sendMsg.setText("send");
                while (running) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException ex) {
                    }
                }
                InThread.interrupt();
            }
        });
        InThread.start();
        while (running) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }
        }

        srvSocket.close();
        myID = "";
        srvIP = "";
        srvPort = 9002;
        if (reset) {
            reset();
        }
    }

    private void sendObj(Object obj) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(obj);
            byte[] data = outputStream.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(srvIP), srvPort);
            srvSocket.send(sendPacket);
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendStr(String str) {
        if (str.matches("!quit") || str.matches("!q")) {
            sendStr("(STX)" + "close" + "(ETX)");
            addOut("You have disconnected!");
            reset = false;
            running = false;
        } else if (str.matches("!disconnect") || str.matches("!dc")) {
            sendStr("(STX)" + "close" + "(ETX)");
            addOut("You have disconnected!");
            running = false;
            reset = true;
        } else if (str.matches("!l") || str.matches("!list")) {
            sendStr("(STX)" + "listusers" + "(ETX)");
        } else if (!str.isEmpty() && running) {
            str += "(STX)" + (System.currentTimeMillis() / 1000L) + "(ETX)";
            sendObj(str);
        }
    }

    private Object getObj() {
        try {
            DatagramPacket incomingPacket = new DatagramPacket(new byte[1024], new byte[1024].length);
            srvSocket.receive(incomingPacket);
            ByteArrayInputStream in = new ByteArrayInputStream(incomingPacket.getData());
            ObjectInputStream is = new ObjectInputStream(in);
            Object obj = (Object) is.readObject();
            return obj;
        } catch (SocketException ex) {
            if (running) {
                addOut("You have been disconnected from the server!");
                running = false;
            }
            reset = true;
        } catch (EOFException ex) {
            if (running) {
                addOut("You have been disconnected from the server!");
                running = false;
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    private String getStr() {
        String str = (String) getObj();
        System.out.println(str);
        return new String(str.getBytes(Charset.defaultCharset()));
    }

    private static String rmTime(String str) {
        String ogStr = str;
        try {
            Matcher matcher = Pattern.compile("\\(STX\\)(.+?)\\(ETX\\)").matcher(str);
            matcher.find();
            return (String) str.subSequence(0, matcher.start(0));
        } catch (IllegalStateException ex) {
        }
        return str;
    }

    private void addOut(String str) {
        if (outF.getText().isEmpty()) {
            outF.setText(str);
        } else {
            outF.append("\n" + str);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Client().run();
    }
}
