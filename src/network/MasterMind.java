package network;

import gui.SceneSwitch;
import security.Security;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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

    private SceneSwitch gui;

    public String currentMessage = "";

    //list to keep track of sequance numbers with other nodes
    private HashMap<String, String> seqNrs;

    //the socket used for receding and sending packages
    private MulticastSocket sock;



    private Sender sender;
    private Receiver receiver;

    private static final int port = 2629;
    private InetAddress group;

    private Security keys;


    public MasterMind(String name){
        gui = new SceneSwitch(name, this);
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

        int run = 0;
        //start a new thread which will pulse each second
        Thread pulsing = new Thread(sender);
        pulsing.start();

        //while is on, keeping looking for packs to receive
        while(on){
            run++;
            if(run == 10){
                this.sendMessage("1boi");
            }
            byte[] buf = new byte[1000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);

            try {
                sock.receive(recv);
            } catch (IOException e) {
                System.err.println("Could not receive window");
            }

            String stringmess = datagrampacketTostring(recv);
            //if its a pulse from itself , just ignore it
//            if (!stringmess.substring(0,2).equals(sender.getMyPulse().substring(0,2)) &&
//                    !currentMessage.equals(stringmess)) {
            if(!stringmess.substring(1,2).equals(ownName)){
                receiver.dealWithPacket(stringmess);
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

        while(sender.getoutStanding().get(message.substring(0,1))){
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                System.err.println("could not wait int Mastermind SendMessage,for some reason");
            }
        }

        new Thread(() -> sender.sendMessage(message)).start();

    }

    public void updateCurretnMessage(String msg){
        this.currentMessage = msg;
    }

    public void turnOff(){
        this.on = false;
    }

    public HashMap<String, String> getSeqNers(){
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

    public SceneSwitch getGui(){
        return  gui;
    }





}
