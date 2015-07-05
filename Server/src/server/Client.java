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
import java.net.UnknownHostException;
import java.util.Scanner;
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
    private Thread InThread, OutThread;
    private PlayerDataList srvPlayerDataList;

    public Client(String ip, int port, String id) {
        srvIP = ip;
        srvPort = port;
        myID = id;
        try {
            srvSocket = new DatagramSocket();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        connected = true;
        sendObj("heartbeat");
        running = true;

        Thread heartbeat = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    String str = (String) getObj(new byte[1024], 1024);
                    if (str.matches("heartbeat")) {
                        connected = true;
                        running = false;
                        break;
                    } else {
                        connected = false;
                        running = false;
                        break;
                    }
                }
            }
        });
        heartbeat.start();
        Long time = System.currentTimeMillis();
        while (running) {
            if ((System.currentTimeMillis() - time) >= 10000) { //10s
                System.out.println("Unable to connect to the server!");
                connected = false;
                running = false;
                srvSocket.close();
                heartbeat.interrupt();
                break;
            }
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
                        handleObject(getObj(new byte[1024], 1024));
                    }
                    OutThread.interrupt();
                }
            });
            OutThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (running) {
                        String s = new Scanner(System.in).nextLine();
                        if (s.trim().length() > 0) {
                            sendObj(s);
                            System.out.println("Sent: " + s);
                        }
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException ex) {
                        }
                    }
                    InThread.interrupt();
                }
            });
            OutThread.start();
            InThread.start();
            while (running) {
                try {
                    Thread.sleep(500);
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

            sendObj(data, data.length);
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

    private void sendObj(byte[] data, int dataLength) {
        if (connected) {
            try {
                DatagramPacket sendPacket = new DatagramPacket(data, dataLength, InetAddress.getByName(srvIP), srvPort);
                srvSocket.send(sendPacket);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
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
        sendObj(data);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Client("dariorostirolla.se", 9010, "abcdef").run();
    }
}
