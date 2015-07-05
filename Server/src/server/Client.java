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
    private final int srvPort;
    private final String myID, srvIP;
    private boolean running, connected;
    private Thread InThread, OutThread;
    private PlayerDataList srvPlayerDataList;

    public Client(String ip, int port, String id) {
        srvIP = ip;
        srvPort = port;
        myID = id;
        connected = false;
    }

    @Override
    public void run() {
        try {
            srvSocket = new DatagramSocket();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        running = true;
        connected = true;
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

    private void sendObj(Object obj) {
        ObjectOutputStream os = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            os = new ObjectOutputStream(outputStream);
            os.writeObject(obj);
            byte[] data = outputStream.toByteArray();
            
            if (data.length > 1024){
                //send smaller package with next package size... (may not be possible?! server doesn't know packet's sender until the package is there...)
            }
            
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
        try {
            DatagramPacket sendPacket = new DatagramPacket(data, dataLength, InetAddress.getByName(srvIP), srvPort);
            srvSocket.send(sendPacket);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private Object getObj(byte[] buf, int bufLength) {
        try {
            DatagramPacket incomingPacket = new DatagramPacket(buf, bufLength);
            srvSocket.receive(incomingPacket);
            ByteArrayInputStream in = new ByteArrayInputStream(incomingPacket.getData());
            ObjectInputStream is = new ObjectInputStream(in);
            Object obj = (Object) is.readObject();
            return obj;
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
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
        new Client("localhost", 9002, "abcdef").run();
    }
}
