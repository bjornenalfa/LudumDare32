/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
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
    private boolean running, reset, connected, valIn;
    private Thread InThread, OutThread;

    public Client(String ip, int port, String id) {
        srvIP = ip;
        srvPort = port;
        myID = id;
        reset = false;
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
                    System.out.println(getObj());
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
                        System.out.println("\nYou: " + s);
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

    private Object getObj() {
        try {
            DatagramPacket incomingPacket = new DatagramPacket(new byte[1024], new byte[1024].length);
            srvSocket.receive(incomingPacket);
            ByteArrayInputStream in = new ByteArrayInputStream(incomingPacket.getData());
            ObjectInputStream is = new ObjectInputStream(in);
            Object obj = (Object) is.readObject();
            return obj;
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Client("localhost", 9002, "abcdef").run();
    }
}
