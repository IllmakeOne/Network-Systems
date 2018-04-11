package network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class MasterMind {


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




}
