package server;

import game.PlayerData;
import game.PlayerDataList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * s
 *
 * @author bjodet982
 */
public class Server implements Runnable {

    public static final int PACKAGE_SIZE = 256;
    public static int TICK_RATE = 64;

    private DatagramSocket srvSocket;
    private int srvPort;
    private ArrayList<InetSocketAddress> usersL;
    private Map<InetSocketAddress, PlayerData> data;
    private boolean srvRunning;

    public Server(int port) {
        srvPort = port;
        usersL = new ArrayList();
        data = new HashMap();
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
                System.out.print("Port already in use! Input a new port: ");
                srvPort = new Scanner(System.in).nextInt();
            }
        } while (portErr);
        srvRunning = true;
        System.out.println("Server starting up on UDP port: " + srvPort);

        Thread in = new Thread(new Runnable() {
            @Override
            public void run() {
                while (srvRunning) {
                    try {
                        DatagramPacket incomingPacket = new DatagramPacket(new byte[PACKAGE_SIZE], new byte[PACKAGE_SIZE].length);
                        srvSocket.receive(incomingPacket);
                        ByteArrayInputStream in = new ByteArrayInputStream(incomingPacket.getData());
                        ObjectInputStream is = new ObjectInputStream(in);

                        handleObject((Object) is.readObject(), (InetSocketAddress) incomingPacket.getSocketAddress());
                    } catch (IOException | ClassNotFoundException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        });
        in.start();

        Thread out = new Thread(new Runnable() {
            @Override
            public void run() {
                while (srvRunning) {
                    for (InetSocketAddress user : (ArrayList<InetSocketAddress>) usersL.clone()) {
                        if (System.currentTimeMillis() - data.get(user).time >= 5000) {
                            System.out.println(user + " disconnected! :(");
                            usersL.remove(user);
                            data.remove(user);
                        }
                    }
                    if (usersL.size() > 1) {
                        PlayerData[] pdl = new PlayerData[usersL.size()];

                        int i = 0;
                        for (InetSocketAddress user : usersL) {
                            pdl[i] = data.get(user);
                            i++;
                        }

                        PlayerData[] pl = new PlayerData[usersL.size() - 1];
                        for (i = 1; i < usersL.size(); i++) {
                            pl[i - 1] = pdl[i];
                        }

                        i = 0;
                        for (InetSocketAddress s : usersL) {
                            sendObj(new PlayerDataList(pl), s);
                            if (i < usersL.size() - 1) {
                                pl[i] = pdl[i];
                                i++;
                            }
                        }

                        try {
                            Thread.sleep(1000 / TICK_RATE);
                        } catch (InterruptedException ex) {
                        }
                    } else if (usersL.size() == 1) {
                        sendObj("keepalive", usersL.get(0));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                        }
                    }
                }
            }
        });
        out.start();
    }

    private void sendObj(Object obj, InetSocketAddress s) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(obj);
            byte[] data = outputStream.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(s.getHostString()), s.getPort());
            srvSocket.send(sendPacket);
//            System.out.println("SIZE: " + sendPacket.getLength());
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleObject(Object obj, InetSocketAddress sender) {
        if (obj instanceof String) {
            String s = (String) obj;
            if (s.matches("heartbeat")) {
                if (!usersL.contains(sender)) {
                    usersL.add(sender);
                    data.put(sender, new PlayerData(-1000, -1000));
                    System.out.println("New client connected! Address:" + sender);
                }
                sendObj("heartbeat-" + PACKAGE_SIZE + "-" + TICK_RATE, sender);
                System.out.println("Got heartbeat from " + sender + " sent one back <3");
            }
        } else if (obj instanceof PlayerData) {
            PlayerData rpd = (PlayerData) obj;
            data.put(sender, rpd);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Server(9010).run();
    }
}
