package server;

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
import network.PlayerData;
import network.PlayerDataList;

/**
 * s
 *
 * @author bjodet982
 */
public class Server {

    static public int PACKAGE_SIZE = 4096;
    static public int TICK_RATE = 1;
    static public long TIME_BEFORE_KEEPALIVE = 3000;
    static public long TIME_BEFORE_TIMEOUT = 5000;

    private DatagramSocket srvSocket;
    private int srvPort;
    private ArrayList<InetSocketAddress> usersL;
    private Map<InetSocketAddress, PlayerData> data;
    private Map<InetSocketAddress, Long> lastDataSentTime;
    private Map<InetSocketAddress, Long> lastDataReceivedTime;
    private boolean srvRunning;

    public Server(int port) {
        srvPort = port;
        usersL = new ArrayList<>();
        data = new HashMap();
        lastDataSentTime = new HashMap();
        lastDataReceivedTime = new HashMap();
    }

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
                    ArrayList<InetSocketAddress> plList = (ArrayList<InetSocketAddress>) usersL.clone();

                    for (InetSocketAddress user : plList) {
                        if (System.currentTimeMillis() - lastDataReceivedTime.get(user) >= TIME_BEFORE_TIMEOUT) {
                            System.out.println(user + " disconnected! :(");
                            usersL.remove(user);
                            data.remove(user);
                            usersL.trimToSize();
                        }
                        if (System.currentTimeMillis() - lastDataSentTime.get(user) >= TIME_BEFORE_KEEPALIVE) {
                            sendObj("keepalive-" + System.currentTimeMillis(), user); //ska du skicka keepalive när de inte behövs ska du fan skicka de till alla :P 
                        }
                    }
                    
                    if (plList.size() > 1) {
                        plList = (ArrayList<InetSocketAddress>) usersL.clone();
                        Map<InetSocketAddress, PlayerData> plData = new HashMap<>(data); //fair enough, mah bad
                        
                        PlayerData[] pdl = new PlayerData[plList.size()];
                        PlayerData[] pl = new PlayerData[plList.size() - 1];
                        
                        for (int i = 0; i < pl.length; i++) {
                            pdl[i] = plData.get(plList.get(i));
                            pl[i] = plData.get(plList.get(i + 1));
                        }

                        sendObj(new PlayerDataList(pl, System.currentTimeMillis()), plList.get(0));  //dafuq
                        for (int i = 0; i < pl.length; i++) {
                            pl[i] = pdl[i];
                            sendObj(new PlayerDataList(pl, System.currentTimeMillis()), plList.get(i+1));
                        }

                    }
                    try {
                        Thread.sleep(1000 / TICK_RATE);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        });
        out.start();
    }

    private void sendObj(Object obj, InetSocketAddress receiver) {
        lastDataSentTime.put(receiver, System.currentTimeMillis());
        ObjectOutputStream os = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            os = new ObjectOutputStream(outputStream);
            os.writeObject(obj);
            os.flush();
            byte[] data = outputStream.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(receiver.getHostString()), receiver.getPort());
            if (sendPacket.getLength() >= PACKAGE_SIZE) {
                PACKAGE_SIZE *= 2;
            }

            srvSocket.send(sendPacket);
//            System.out.println("SIZE: " + sendPacket.getLength());
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                os.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void handleObject(Object obj, InetSocketAddress sender) {
        lastDataReceivedTime.put(sender, System.currentTimeMillis());
        if (obj instanceof String) {
            String s = (String) obj;
            if (s.matches("connection request")) {
                if (!usersL.contains(sender)) {
                    usersL.add(sender);
                    data.put(sender, new PlayerData(-1000, -1000, ""));
                    System.out.println("New client connected! Address:" + sender);
                }
                sendObj("connection-" + PACKAGE_SIZE + "-" + TICK_RATE, sender);
                System.out.println("Got connection request from " + sender + " sent connection acknowledgement!");
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
