package network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class MasterMind {


    public static final String ACK = "a";
    public static final String MESSAGE = "m";
    public static final String PULSE = "p";
    public static final int TIMEOUTLIMIT = 1000;

    private boolean on;

    //list keeping track of clinets in the network
    private ArrayList<String> nodeOnline;
    //own name
    private String ownName;
    //list to keep track of sequance numbers with other nodes
    private HashMap<String, Integer> seqNrs;
    //the socket used for receding and sending packages
    private MulticastSocket sock;

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

    public void sendMessage(String message){
        new Thread(() -> sender.sendMessage(message)).start();

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
