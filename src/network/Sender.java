package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Sender implements  Runnable{

    //the socket with which it will broadcast messages
    private MulticastSocket sock;

    //parent MasterMind
    private MasterMind mind;

    // map of the nodes still waiting for a reply
    private HashMap<String, Boolean> outStanding;

    //the pusle of this shaped in the constructor
    private String mypulse = "";

    //map of timeouts
    private HashMap<String, Long> timeouts;


    public Sender(MulticastSocket socket, MasterMind mind){

        this.sock = socket;
        this.mind = mind;

        outStanding = new HashMap<>();
        timeouts = new HashMap<>();


        mypulse = mind.getOwnName() //the name of this node
                + mind.getOwnName() // it is double so the protocol is respected
                + "2"               // time to live
                + mind.PULSE;       // type of message

    }

    /**
     * the protocol is:
     * on first index of the string it is the destination, to whome its going
     * the second index is where the package is from
     * on the third index its it's time to live
     * on the 4th index is the type of message, ack synch(pulse) or a message
     * on the 5th index is the seq number 0 or 1
     * and the rest is the encoded message
     *
     *
     * this will run in a new thread and will terminate when it received an ack
     *
     */
    public synchronized void sendMessage(String destination,String source, String tty, String messageType,
                                         String seqNr, String payload){
        int count = 0;
        String mesg = destination ///*
                + source // source
                + tty // time to live
                + messageType // type of message
                + seqNr // seq number
                + mind.getSecurity().encrypt(payload,destination); // encoded message

        //send the message
        send(mesg);
        //note down that its waiting for an ack from the destination of the message
        outStanding.put(mesg.substring(0,1),true);
        //note down time of the sending of the message for calculation of timeout
        timeouts.put(destination, System.currentTimeMillis());

        while(outStanding.get(destination) == true){
            //if it times out, resend message
            if (System.currentTimeMillis() - timeouts.get(destination) > mind.TIMEOUTLIMIT){
                count++;
                timeouts.put(destination, System.currentTimeMillis());

                send(mesg);

                System.err.println("Retransmitting  message");
            }
            //if it retransmitted the message 5 time already, the node probably went offline, so give up
            if(count >5 ){
                break;
            }
        }

        if(count == 6){
            System.out.println(payload + " has not recieved ack");
            outStanding.put(destination,false);
        } else {
            System.out.println("got Ack for " + payload);
        }

    }

    /**
     * send message on all chat
     * @param message
     */
    public void sendGlobalMessage(String message){
        String mesg = "0"       //destination group chat
                    + mind.getOwnName()  //from itself
                     + "2"               // time to live
                    + mind.MESSAGE       // type of package, message
                    + mind.getseqNrsrcvd().get("0")        //global chat seq nr
                    + mind.getSecurity().encrypt(message.substring(1),"0"); // encode the message

        mind.updateSeqRecvd("0");

        send(mesg);

    }



    /**
     * this sends an Acknowledgement package into the network
     * @param destination of the ack
     * @param seqaceNr the seq nr it is acknowledging
     */
    public void sendAck(String destination, String seqaceNr){
        String ack = destination
                    + mind.getOwnName()
                    + "2"
                    + mind.ACK
                    + seqaceNr;

        send(ack);
    }


    /**
     * this method notifies the sender that it got an ack for it most recent package sent towards the source
     * @param source
     */
    public void receivedAck(String source){
        outStanding.put(source, false);
    }




    /**
     * this is a pulse, its purpose is to inform the other in the netwrok that they are alive
     * Each pulse is of the form name+time to live + type of msesage
     * and after all that its public key,
     * in this way anyone who connects to a network will receive the public keys of nodes conected
     */
    public void sendPulse(){
       // String puseWithKey = mypulse + mind.getSecurity().getOwnPubickey();
        send(mypulse);

    }

    /**
     * start sending a pulse each second
     */
    @Override
    public void run() {
        while(mind.getStatus()){
            sendPulse();
            mind.getReceiver().UpdateStatuses();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.err.println("could not wait in Sender -> run,for some reason");
            }
        }
    }

    /**
     * this method takes a String message, transforms it into a DatagramPacket and sends it in the network
     * @param message
     */
    public void send(String message){
        try {
            if(!message.substring(3,4).equals(mind.PULSE)) {
                System.out.println("Sending " + message);
            }
            sock.send(stringTodatagrampacket(message));
        } catch (IOException e){
            System.err.println("Unable to send message in Sender");
        }
    }

    /**
     * Make a String into a DatagramPacket
     * @param message the message being converted
     * @return the message converted
     */
    public DatagramPacket stringTodatagrampacket(String message){
        DatagramPacket result = new DatagramPacket(message.getBytes(),message.length(),
                mind.getGroup(), mind.getPort());
        return result;
    }


    public String getMyPulse(){
        return mypulse;
    }


    public HashMap<String, Boolean> getoutStanding(){
        return outStanding;
    }

}
