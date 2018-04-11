package network;

import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class MasterMind {

    public class Node {

        //list keeping track of clinets in the network
        public ArrayList<String> nodeOnline;
        //own name
        public String ownName;
        //list to keep track of sequance numbers with other nodes
        public HashMap<String, Integer> seqNrs;
        //the socket used for receding and sending packages
        public MulticastSocket sock;


    }
}
