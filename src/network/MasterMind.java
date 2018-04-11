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


    public MasterMind(String name){
        ownName = name;
        seqNrs = new HashMap<>();
        try {
            sock = new MulticastSocket(port);
            group = InetAddress.getByName("228.0.0.0");
            sock.joinGroup(group);
            sender = new Sender(sock);
            receiver = new Receiver(sock);
        } catch (IOException e){
            System.err.println("Could not connect to port or IPgroup");
        }
    }


}
