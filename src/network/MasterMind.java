package network;

import security.Security;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class MasterMind implements Runnable {


    public static final String ACK = "a";
    public static final String MESSAGE = "m";
    public static final String PULSE = "p";
    public static final int TIMEOUTLIMIT = 1000;
    public static final int OUTOFNETWORKTIMEOUT = 3000;

    private boolean on;

    //list keeping track of clinets in the network
    private ArrayList<String> nodeOnline;
    //own name
    private String ownName;
    //list to keep track of sequance numbers with other nodes
    private HashMap<String, Integer> seqNrs;
    //the socket used for receding and sending packages
    private MulticastSocket sock;

    //this keeps in mind each time it receives a pulse from a node so it knows it's in the network
    private HashMap<String, Long> statuses;

    private Sender sender;
    private Receiver receiver;

    private static final int port = 2629;
    private InetAddress group;

    private Security keys;


    public MasterMind(String name){
        ownName = name;
        seqNrs = new HashMap<>();
        keys = new Security();
        on = true;
        try {
            sock = new MulticastSocket(port);
            group = InetAddress.getByName("228.0.0.0");
            sock.joinGroup(group);
            sender = new Sender(sock, this);
            receiver = new Receiver(sock, this);
        } catch (IOException e){
            System.err.println("Could not connect to port or IPgroup");
        }
    }

    @Override
    public void run() {

        //start a new thread which will pulse each second
        Thread pulsing = new Thread(sender);
        pulsing.start();

        //while is on, keeping looking for packs to receive
        while(on){
            byte[] buf = new byte[1000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);

            try {
                sock.receive(recv);
            } catch (IOException e) {
                System.err.println("Could not receive window");
            }

            String stringmess = datagrampacketTostring(recv);
            //if its a pulse from itself , just ignore it
            if (!stringmess.equals(sender.getMyPulse())) {
                receiver.dealWithMessage(stringmess);
        }
        }
    }


    /**
     * take a datagram packet and convert it into a string
     * @param pack, the datagram packet being decoded
     * @return the message in the datagram
     */
    public String datagrampacketTostring ( DatagramPacket pack){
        String result = new String(pack.getData());
        return  result;
    }

    /**
     * this will start a new thread for sending a message
     * the thread will end after the node receives and ack for that sent message
     */
    public void sendMessage(String message){
        new Thread(() -> sender.sendMessage(message)).start();

    }

    public void turnOff(){
        this.on = false;
    }

    public HashMap<String, Integer> getSeqNers(){
        return seqNrs;
    }

    public ArrayList<String> getNodeOnline(){
        return nodeOnline;
    }

    public String getOwnName(){
        return  ownName;
    }

    public InetAddress getGroup(){
        return group;
    }

    public int getPort(){
        return port;
    }

    public Security getSecurity(){
        return keys;
    }

    public boolean getStatus(){
        return on;
    }

    public Sender getSender(){
        return this.sender;
    }

    public Receiver getReceiver() {
        return receiver;
    }





}
