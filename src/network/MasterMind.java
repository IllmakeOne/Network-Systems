package network;

import com.sun.xml.internal.fastinfoset.util.StringArray;
import gui.SceneSwitch;
import javafx.application.Application;
import security.Security;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MasterMind implements Runnable {


    //Some protocol finals
    public static final String ACK = "a";
    public static final String MESSAGE = "m";
    public static final String PULSE = "p";

    public static final int TIMEOUTLIMIT = 2000;

    public static final int OUTOFNETWORKTIMEOUT = 5000;

    private boolean on;


    //own name
    private String ownName;

    //the gui
    private SceneSwitch gui;


    //list to keep track of sequance numbers with other nodes, on the receving of messages from them
    private HashMap<String, String> seqNrsrcvd;

    //list to keep track of sequance numbers with other nodes, on the for the sending of messages
    private HashMap<String, String> seqNrssent;

    //the socket used for receding and sending packages
    private MulticastSocket sock;


    // class sender which is used to send all types of messages using the Multicastsocket
    private Sender sender;

    //class Receiver used for dealing with all types of messages
    private Receiver receiver;

    //port
    private static final int port = 2629;
    //internet address of our group
    private InetAddress group;

    //the security class which encrypt and decrypt
    private Security keys;

    /**
     * An instance of the class Mastermind is made in the GUI and is ran in a separate thread
     *
     * Constructor of the Mastermind
     * @param name is the name of this node
     * @param gui  is the GUI connected to this node, messages will be send to it
     *
     */
    public MasterMind(String name, SceneSwitch gui){

        //initiate name
        ownName = name;
        //initiate gui
        this.gui = gui;
        //initiate the seqace numbers map
        seqNrsrcvd = new HashMap<>();
        seqNrssent = new HashMap<>();

        //initualize the sequance number fo global chat
        seqNrsrcvd.put("0","0");

        keys = new Security(ownName);

        on = true;

        try {
            sock = new MulticastSocket(port);
            group = InetAddress.getByName("228.0.0.0");
            sock.joinGroup(group);
            sender = new Sender(sock, this);
            receiver = new Receiver(this);
        } catch (IOException e){
            System.err.println("Could not connect to port or IPgroup");
        }
    }

    /**
     * cut off all empty bytes
     */
    static byte[] CutEmptyBytes(byte[] msg)
    {
        int index = msg.length - 1;
        while (index >= 0 && msg[index] == 0)
        {
            --index;
        }

        return Arrays.copyOf(msg, index + 1);
    }

    @Override
    public void run() {

        //start a new thread which will send a pulse each second
        Thread pulsing = new Thread(sender);
        pulsing.start();

        //while is on, keeping looking for packs to receive
        while(on){
//
            byte[] buf = new byte[500];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);

            try {
                sock.receive(recv);
            } catch (IOException e) {
                System.err.println("Could not receive window");
            }


            String stringmess = datagrampacketTostring(recv);



            receiver.dealWithPacket(stringmess);


        }
    }


    /**
     * take a datagram packet and convert it into a string
     * @param pack, the datagram packet being decoded
     * @return the message in the datagram
     */
    public String datagrampacketTostring ( DatagramPacket pack){
        byte[] buf = new byte[500];
        buf = CutEmptyBytes(pack.getData());
        String result = new String(buf);
        return  result;
    }

    /**
     * this will start a new thread for sending a message
     * the thread will end after the node receives and ack for that sent message
     */
    public void sendMessage(String message){

      //  System.out.println(message + " In send msessage MAstermind");

        //wait untill it is done with the previous message
        if(receiver.getStatuses().keySet().contains(message.substring(0,1))){
            while (sender.getoutStanding().get(message.substring(0, 1))) {
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                    //System.out.println("waiting to finish up his messageing");
                } catch (InterruptedException e) {
                    System.err.println("could not wait int Mastermind SendMessage,for some reason");
                }
            }

            //send a message in a new thread
            new Thread(() -> sender.sendMessage(message.substring(0,1),//destination
                             ownName,   // source
                            "2",    // time to live
                            MESSAGE, //type of message
                            seqNrssent.get(message.substring(0,1)), // seqnr
                            message.substring(1))       //message
                            ).start();
        } else {
            //if someone tried to send a message to an offlien node,
            // then it will tell the guy that it is not online
            if(!message.substring(0,1).equals("0")) {
                gui.onMessageReceived(gui.OFFLINE,
                        Integer.valueOf(message.substring(0, 1)));
            }
            //System.out.println("node not online");
        }

        //for all chat
        if(message.substring(0,1).equals("0")){
           // System.out.println(getSeqNers().get("0") + " Trying to send message in mastermind " );
            new Thread(() -> sender.sendGlobalMessage(message)).start();
        }

    }


    /**
     * increases the sequance number with a node by one
     * @param source
     */
    public synchronized void updateSeqRecvd(String source){
      //  System.out.println("Changed Seq nr of " + source + " from " + getSeqNers().get(source));
        if(getseqNrsrcvd().get(source).equals("9")){
            getseqNrsrcvd().put(source,"0");
        } else {
            getseqNrsrcvd().put(source,
                    String.valueOf(Integer.valueOf(getseqNrsrcvd().get(source)) + 1));
        }
      //  System.out.println("to " + getSeqNers().get(source));
    }

    /**
     * increases the sequance number with a node by one
     * @param source
     */
    public synchronized void updateSeqSent(String source){
        //  System.out.println("Changed Seq nr of " + source + " from " + getSeqNers().get(source));
        if(getseqNrssent().get(source).equals("9")){
            getseqNrssent().put(source,"0");
        } else {
            getseqNrssent().put(source,
                    String.valueOf(Integer.valueOf(getseqNrssent().get(source)) + 1));
        }
        //  System.out.println("to " + getSeqNers().get(source));
    }


    public void turnOff(){
        this.on = false;
    }

    public HashMap<String, String> getseqNrsrcvd(){
        return seqNrsrcvd;
    }

    public HashMap<String, String> getseqNrssent(){ return seqNrssent;  }

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

    public SceneSwitch getGui(){
        return  gui;
    }





}
