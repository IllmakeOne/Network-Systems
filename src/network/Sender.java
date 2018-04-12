package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Sender implements  Runnable{

    private MulticastSocket sock;
    private MasterMind mind;
    // map of the nodes still waiting for a reply
    private HashMap<String, Boolean> outStanding;

    private String mypulse = "";

    //map of timeouts
    private HashMap<String, Long> timeouts;


    public Sender(MulticastSocket socket, MasterMind mind){
        this.sock = socket;
        this.mind = mind;

        outStanding = new HashMap<String, Boolean>();
        timeouts = new HashMap<>();
      //  timeouts = new HashMap<String, Long>();

        mypulse = mind.getOwnName() //the name
                + mind.getOwnName() // it is double so the protocol is respected
                + "2" // time to live
                + mind.PULSE; // type of message

    }

    /**
     * the protocol is:
     * on first index of the string it is the destination, to whome its going
     * the second index is where the package is from
     * on the third index its it's time to live
     * on the 4th index is the type of message, ack synch(pulse) or a message
     * on the 5th index is the seq number 0 or 1
     * and the rest is the encoded message
     * @param mtbs = message to be sent
     *
     * this will run in a new thread and will terminate when it received an ack
     *
     */
    public void sendMessage(String mtbs){

        String detination = mtbs.substring(0,1);
        String mesg = detination //destination
                + mind.getOwnName() // source
                + "2" // time to live
                + mind.MESSAGE // type of message
                + mind.getSeqNers().get(detination) // seq number
                + mind.getSecurity().encrypt(mtbs.substring(1),detination); // encoded message

        System.out.println(mesg + " made in sendMessage in Sender");

        mind.updateCurretnMessage(mesg);
        send(mesg);

        outStanding.put(mesg.substring(0,1),true);//note down its waiting for an ack
        timeouts.put(detination, System.currentTimeMillis());//note down time for the timeout calcualtion

        while(outStanding.get(detination) == true){
            if (System.currentTimeMillis() - timeouts.get(detination) > mind.TIMEOUTLIMIT){

                timeouts.put(detination, System.currentTimeMillis());

                send(mesg);

                mind.updateCurretnMessage(mesg);
            }
        }

        //change the sequance number asociated to a node
        if(mind.getSeqNers().get(detination).equals("9")){
            mind.getSeqNers().put(detination,"0");
        } else {
            mind.getSeqNers().put(detination,
                    String.valueOf(Integer.valueOf(mind.getSeqNers().get(detination)) -1));
        }

    }

    public void sendGlobalMessage(String message){

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

    public void receivedAck(String source){
        outStanding.put(source, false);
    }




    /**
     * this is a pulse, its purpose is to inform the other in the netwrok that they are alive
     * Each pulse is of the form name+time to live + type of msesage
     */
    public void sendPulse(){
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
     * this fucntions takes a message, makes it into a datagrampackage and sends it into the network
     * @param message
     */
    public void send(String message){
        try {
            sock.send(stringTodatagrampacket(message));
        } catch (IOException e){
            System.err.println("Unable to send message in Sender");
        }
    }

    /**
     * Make a String into a Datagram
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
