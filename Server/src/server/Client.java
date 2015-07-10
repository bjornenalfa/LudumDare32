/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import network.PlayerData;
import network.PlayerDataList;

/**
 *
 * @author PastaPojken
 */
public class Client {
    static public long TIME_BEFORE_KEEPALIVE = 3000;
    static public long TIME_BEFORE_TIMEOUT = 5000;
    static public long TIME_BEFORE_CONNECTIONFAIL = 10000;

    private DatagramSocket srvSocket;
    private int srvPort;
    private String myID, srvIP;
    private boolean running, connected;
    private boolean connecting = false;
    private Thread InThread, OutThread, connectionFinder, connectionAttemptTimer;
    private PlayerDataList srvPlayerDataList;
    private PlayerData bufferedPlayerData;
    private Long connectionAttemptStartTime, lastPacket, lastPacketTimeStamp;
    private Long timeWhenLastDataSent = Long.MIN_VALUE;
    private int PACKAGE_SIZE, TICK_RATE;

    public Client(String ip, int port, String id) {
        PACKAGE_SIZE = 32;
        srvIP = ip;
        srvPort = port;
        myID = id;
        lastPacket = (long) 0;
        lastPacketTimeStamp = (long) 0;
        try {
            srvSocket = new DatagramSocket();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        connected = false;
        connecting = true;
        System.out.println("Connecting...");
        sendObj("connection request");
        connectionAttemptStartTime = System.currentTimeMillis();
        running = false;

        connectionFinder = new Thread(new Runnable() {
            @Override
            public void run() {
                while (connecting) {
                    Object obj = getObj(new byte[PACKAGE_SIZE], PACKAGE_SIZE);
                    lastPacket = System.currentTimeMillis();
                    if (obj instanceof String) {
                        String str = (String) obj;
                        if (str.contains("connection-")) {
                            System.out.println("Received connection:" + str);
                            connected = true;
                            connecting = false;
                            System.out.println("Connected!");
                            PACKAGE_SIZE = Integer.parseInt(str.substring(str.indexOf("-") + 1, str.lastIndexOf("-")));
                            TICK_RATE = Integer.parseInt(str.substring(str.lastIndexOf("-") + 1, str.length()));
                            connectionAttemptTimer.interrupt();
                            startCommunications();
                        }
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        });
        connectionAttemptTimer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (connecting) {
                    sendObj("connection request"); //udp, cant know if it arrives, best to send more than one....
                    if ((System.currentTimeMillis() - connectionAttemptStartTime) >= TIME_BEFORE_CONNECTIONFAIL) { //10s
                        System.out.println("Unable to connect to the server!");
                        connected = false;
                        connecting = false;
                        running = false;
                        connectionFinder.interrupt();
                        disconnect();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        });
        connectionFinder.start();
        connectionAttemptTimer.start();
        try {
            connectionAttemptTimer.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startCommunications() {
        if (connected) {
            running = true;
            InThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (running) {
                        handleObject(getObj(new byte[PACKAGE_SIZE], PACKAGE_SIZE));
                        try {
                            Thread.sleep(1); //need to sleep to allow boolean check...
                        } catch (InterruptedException ex) {
                        }
                    }
                    OutThread.interrupt();
                }
            });
            InThread.start();

            OutThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (running) {
                        //System.out.println("PING: " + (lastPacket - lastPacketTimeStamp)); Prints waaay too much
                        if (bufferedPlayerData != null) {
                            sendObj(new PlayerData(bufferedPlayerData));
                            bufferedPlayerData = null;
                        }
                        if (System.currentTimeMillis() - lastPacket >= TIME_BEFORE_TIMEOUT) {
                            System.out.println("The server is not responding! :(");
                            running = false;
                            disconnect();
                        }
                        if (System.currentTimeMillis() - timeWhenLastDataSent >= TIME_BEFORE_KEEPALIVE) { //Make sure that the server don't think we disconnected!
                            sendObj("keepAlive");
                        }
                        try {
                            Thread.sleep((long) (1000 / TICK_RATE));
                        } catch (InterruptedException ex) {
                        }
                    }
                    InThread.interrupt();
                }
            });
            OutThread.start();
        }
    }

    private void disconnect() {
        srvSocket.disconnect();
        srvSocket.close();
    }

    private void sendObj(Object obj) {
        timeWhenLastDataSent = System.currentTimeMillis();
//        System.out.println("Sending: " + obj);
        ObjectOutputStream os = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            os = new ObjectOutputStream(outputStream);
            os.writeObject(obj);
            os.flush();
            byte[] data = outputStream.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(srvIP), srvPort);
            srvSocket.send(sendPacket);
//            System.out.println("SIZE: " + sendPacket.getLength());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                os.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Object getObj(byte[] buf, int bufLength) {
        if (connected || connecting) {
            try {
                DatagramPacket incomingPacket = new DatagramPacket(buf, bufLength);
                srvSocket.receive(incomingPacket);
                ByteArrayInputStream in = new ByteArrayInputStream(incomingPacket.getData());
                ObjectInputStream is = new ObjectInputStream(in);
                Object obj = (Object) is.readObject();
                return obj;
            } catch (SocketException ex) {
                System.out.println("The socket has been closed!");
            } catch (EOFException ex) {
                System.out.println("Package bigger than expected...\nPACKAGE_SIZE is being expanded");
                PACKAGE_SIZE *= 2;
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private void handleObject(Object obj) {
        lastPacket = System.currentTimeMillis();
        if (obj instanceof String) {
            String str = (String) obj;
            if (str.contains("keepalive-")) {
                lastPacketTimeStamp = Long.parseLong(str.substring(str.indexOf("-") + 1, str.length()));
                srvPlayerDataList = new PlayerDataList();
            } else {
                System.out.println("Received: " + str);
            }
        } else if (obj instanceof PlayerDataList) {
            srvPlayerDataList = (PlayerDataList) obj;
            lastPacketTimeStamp = srvPlayerDataList.getTime();
//            System.out.println("Received new player data list");
        } else {
            System.out.println("Received unknown object: " + obj);
        }

    }

    public PlayerDataList getPlayerDataList() {
        return srvPlayerDataList;
    }

    public void sendPlayerData(PlayerData data) {
        bufferedPlayerData = data;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Client("127.0.0.1", 9010, "abcdef");
    }
}
