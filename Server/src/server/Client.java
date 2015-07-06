/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PastaPojken
 */
public class Client implements Runnable {

    private DatagramSocket srvSocket;
    private int srvPort;
    private String myID, srvIP;
    private boolean running, connected;
    private Thread InThread, OutThread, heartBeat, heartAttack;
    private PlayerDataList srvPlayerDataList;
    private PlayerData bufferedPlayerData;
    private Long heartTime, lastPacket;
    private int PACKAGE_SIZE, TICK_RATE;

    public Client(String ip, int port, String id) {
        PACKAGE_SIZE = 32;
        srvIP = ip;
        srvPort = port;
        myID = id;
        try {
            srvSocket = new DatagramSocket();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        connected = true;
        System.out.println("Connecting...");
        sendObj("heartbeat");
        heartTime = System.currentTimeMillis();
        running = true;

        heartBeat = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    Object obj = getObj(new byte[PACKAGE_SIZE], PACKAGE_SIZE);
                    if (obj instanceof String) {
                        String str = (String) obj;
                        if (str.contains("heartbeat-")) {
                            connected = true;
                            running = false;
                            System.out.println("Connected!");
                            PACKAGE_SIZE = Integer.parseInt(str.substring(str.indexOf("-") + 1, str.lastIndexOf("-")));
                            TICK_RATE = Integer.parseInt(str.substring(str.lastIndexOf("-") + 1, str.length()));
                            heartAttack.interrupt();
                        } else {
                            connected = false;
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        });
        heartAttack = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    sendObj("heartbeat");
                    if ((System.currentTimeMillis() - heartTime) >= 10000) { //10s
                        System.out.println("Unable to connect to the server!");
                        connected = false;
                        running = false;
                        heartBeat.interrupt();
                        srvSocket.close();
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        });
        heartBeat.start();
        heartAttack.start();
        try {
            heartAttack.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        if (connected) {
            running = true;
            InThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (running) {
                        handleObject(getObj(new byte[PACKAGE_SIZE], PACKAGE_SIZE));
                        try {
                            Thread.sleep(50);
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
                        if (System.currentTimeMillis() - lastPacket >= 10000) {
                            System.out.println("The server is not responding! :(");
                            running = false;
                        }
                        if (bufferedPlayerData != null) {
                            sendObj(new PlayerData(bufferedPlayerData));
                        }
                        try {
                            Thread.sleep((long) (1000 / TICK_RATE));
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    InThread.interrupt();
                }
            });
            OutThread.start();

            while (running) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }

            srvSocket.close();
        }
    }

    private void sendObj(Object obj) {
        ObjectOutputStream os = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            os = new ObjectOutputStream(outputStream);
            os.writeObject(obj);
            byte[] data = outputStream.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(srvIP), srvPort);
            srvSocket.send(sendPacket);
            System.out.println("SIZE: " + sendPacket.getLength());
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
        if (connected) {
            try {
                DatagramPacket incomingPacket = new DatagramPacket(buf, bufLength);
                srvSocket.receive(incomingPacket);
                ByteArrayInputStream in = new ByteArrayInputStream(incomingPacket.getData());
                ObjectInputStream is = new ObjectInputStream(in);
                Object obj = (Object) is.readObject();
                return obj;
            } catch (SocketException ex) {
                System.out.println("The socket has been closed!");
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "";
    }

    private void handleObject(Object obj) {
        lastPacket = System.currentTimeMillis();
        if (obj instanceof String) {
            System.out.println("Received: " + obj);
        } else if (obj instanceof PlayerDataList) {
            srvPlayerDataList = (PlayerDataList) obj;
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
        new Client("127.0.0.1", 9010, "abcdef").run();
    }
}
