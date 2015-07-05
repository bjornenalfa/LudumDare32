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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bjodet982
 */
public class Server implements Runnable {

    private DatagramSocket srvSocket;
    private int srvPort;
    private final ArrayList<InetSocketAddress> usersL;
    private final Map<InetSocketAddress, Object> toSend;
    private Long srvTime;
    private boolean srvRunning;
    private PlayerDataList mainPlayerDataList;

    public Server(int port) {
        srvPort = port;
        usersL = new ArrayList();
        toSend = new HashMap();
        mainPlayerDataList = new PlayerDataList();
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
                        DatagramPacket incomingPacket = new DatagramPacket(new byte[1024], new byte[1024].length);
                        srvSocket.receive(incomingPacket);
                        ByteArrayInputStream in = new ByteArrayInputStream(incomingPacket.getData());
                        ObjectInputStream is = new ObjectInputStream(in);

                        handleObject((Object) is.readObject(), (InetSocketAddress) incomingPacket.getSocketAddress());
                    } catch (IOException | ClassNotFoundException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
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
                    Iterator it = toSend.entrySet().iterator();
                    Entry ent;
                    while (it.hasNext()) {
                        for (InetSocketAddress s : usersL) {
                            ent = (Entry) it.next();
                            if (ent.getKey() != s) {
                                sendObj(ent.getValue(), s.getHostString(), s.getPort());
                            }
                        }
                        it.remove();
                    }
                    for (InetSocketAddress s : usersL) {
                        sendObj(mainPlayerDataList, s.getHostString(), s.getPort());
                    }

                    try {
                        Thread.sleep(100);
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

            DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(ipaddr), port);
            srvSocket.send(sendPacket);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleObject(Object obj, InetSocketAddress sender) {
        System.out.println("RECEIVED PACKET FROM: " + sender);
        if (obj instanceof String) {
            String s = (String) obj;
            if (s.matches("heartbeat")) {
                sendObj("heartbeat", sender.getHostString(), sender.getPort());
                if (!usersL.contains(sender)) {
                    usersL.add(sender);
                }
            } else {
                toSend.put(sender, obj);
                System.out.println("Received: " + obj);
            }
        } else if (obj instanceof PlayerData) {
            mainPlayerDataList.add((PlayerData) obj);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Server(9010).run();
    }
}
