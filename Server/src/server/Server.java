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
 *
 * @author bjodet982
 */
public class Server implements Runnable {

    public static final int PACKAGE_SIZE = 256;
    public static final int TICK_RATE = 32;

    private DatagramSocket srvSocket;
    private int srvPort;
//    private ArrayList<PlayerData> data;
    private ArrayList<InetSocketAddress> usersL;
    private Map<InetSocketAddress, PlayerData> data;
    private Map<InetSocketAddress, Object> toSend;
    private Long srvTime;
    private boolean srvRunning;
    private PlayerDataList mainPlayerDataList;

    public Server(int port) {
        srvPort = port;
        usersL = new ArrayList();
        toSend = new HashMap();
        mainPlayerDataList = new PlayerDataList();
//        data = new ArrayList();
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
        srvTime = (System.currentTimeMillis() / 1000L);
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
//                    if (!mainPlayerDataList.getL().isEmpty()) {
                    if (usersL.size() > 1) {
//                        System.out.println("Data.size() = "+data.size());
//                        PlayerData[] pl = new PlayerData[usersL.size()];
//                        for (int i = 0; i < data.size(); i++) {
//                            pl[i] = data.get(i);
//                        }
//                        int i = 0;
//                        for (InetSocketAddress user : usersL) {
//                            pl[i] = data.get(user);
//                            i++;
//                        }
//                        
//                        mainPlayerDataList = new PlayerDataList(pl);
//                        
//                        for (InetSocketAddress s : usersL) {
//                            sendObj(mainPlayerDataList, s.getHostString(), s.getPort());
//                        }

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
                            sendObj(new PlayerDataList(pl), s.getHostString(), s.getPort());
                            pl[i] = pdl[i];
                            i++;
                        }

//                      System.out.println("Sent data containing "+pl.length+" playerData to "+usersL.size()+" users.");
                    }
//                    data.clear();
//                    mainPlayerDataList.clear();

                    try {
                        Thread.sleep(1000 / TICK_RATE);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        out.start();
    }

    private void sendObj(Object obj, String ipaddr, int port) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(obj);
            byte[] data = outputStream.toByteArray();
//            System.out.println("SIZE: " + data.length);

            DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(ipaddr), port);
            srvSocket.send(sendPacket);

        } catch (IOException ex) {
            Logger.getLogger(Server.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleObject(Object obj, InetSocketAddress sender) {
        //System.out.println("RECEIVED PACKET FROM: " + sender);
        if (!usersL.contains(sender)) {
            usersL.add(sender);
            data.put(sender, new PlayerData(-1000, -1000));
            System.out.println("New client connected! Address:" + sender);
        }

        if (obj instanceof String) {
            String s = (String) obj;
            if (s.matches("heartbeat")) {
                sendObj("heartbeat", sender.getHostString(), sender.getPort());
                System.out.println("Got heartbeat from " + sender + " sent one back <3");
            }
        } else if (obj instanceof PlayerData) {
//            mainPlayerDataList.add((PlayerData) obj);
            PlayerData rpd = (PlayerData) obj;
            data.put(sender, rpd);
//            System.out.println("Got new player data from:"+sender+" x:"+rpd.x+" y:"+rpd.y);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Server(9010).run();
    }
}
