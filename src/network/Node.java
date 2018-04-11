package network;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class Node {

    //list keeping track of clinets in the network
    public ArrayList<String> nodeOnline;
    //own name
    public String ownName;
    //list to keep track of sequance numbers with other nodes
    public HashMap<String , Integer> seqNrs;
    //the socket used for receding and sending packages
    public MulticastSocket sock;


    public static final int port = 2629;
    public InetAddress group;

    public Node (String name) {
        ownName = name;
        nodeOnline = new ArrayList<>();
        nodeOnline.add(ownName);
        seqNrs = new HashMap<>();
        try {
            sock = new MulticastSocket(port);
            group = InetAddress.getByName("228.0.0.0");
            sock.joinGroup(group);
        } catch (IOException e){
            System.err.println("Could not connect to port or ipgroup");
        }
       // sendFirstPack();
    }

    /**
     * Make a String into a Datagram
     * @param message the message being converted
     * @return the message converted
     */
    public DatagramPacket stringTodatagrampacket(String message){
        DatagramPacket result = new DatagramPacket(message.getBytes(),message.length(), group, port);
        return result;
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

    public void dealWithmessage(String mess){
        if (mess.charAt(0) == "5"){
            //updateNetwork(mess.charAt(1));
        }
    }

//    /**
//     */
//    public void sendFirstPack(){
//        String mess = "54" + ownName;
//        try {
//            sock.send(stringTodatagrampacket(mess));
//        } catch (IOException e) {
//            System.err.println("could not send first pack");
//        }
//    }



    public static void main(String[] args) {
        System.out.println("fake and gay");
        int x;
        x =4;
        x+=4;
        HashMap<String,String> map = new HashMap<>();
        map.put("gay","fale");
        map.put("gy","fale");




    }

    /**
     * when somone connects, add them to the list of people online
     * @param newCommer - the client who connected
     */
    public void someoneNewConnected(String newCommer){
        nodeOnline.add(newCommer);
        seqNrs.put(newCommer, 0);

    }
    /**
     * when somone disconnects, remove them from the list of people online
     * @param leaver 0 the client who left
     */
    public void someoneLeft(String leaver){
        nodeOnline.remove(leaver);
        seqNrs.remove(leaver);
    }
}
