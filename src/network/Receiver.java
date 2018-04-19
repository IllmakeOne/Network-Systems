package network;

import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Receiver {


    //the parent MasterMind
    private  MasterMind mind;

    //this is the most recent Sequence Number
    //it is used to send ack for messages that are older by one, to send multiple acks for the same message,
    // in case the first ack failed
    private String mostrecentSeqnr;


    //this keeps in mind each time it receives a pulse from a node so it knows it's in the network
    //it can be modified in multiple threads , so it is a ConcurentHashMap, which is a thread-protected HashMap
    //this is to avoid ConcurrentModification erros
    private ConcurrentHashMap<String, Long> statuses;


    /**
     * Constructor
     * @param mind the parent MasterMind
     */
    public Receiver(MasterMind mind){
        this.mind = mind;
        //initiate statuses
        statuses = new ConcurrentHashMap<String, Long>();
    }


    /**
     * this methods deal with whatever package the node receives.
     * @param message is the message which conforms to the protocol.
     */
    public void dealWithPacket(String message){


        String destination = message.substring(0,1);   //the destination of the message
        String source = message.substring(1,2);     //the source of the message
        String type = message.substring(3,4);       //the type of message (PULSE,MSG,ACK)
        String seq = message.substring(4,5);        // seq br if the message

        //
        if(!source.equals(mind.getOwnName())) {

            if (type.equals(mind.PULSE)) {
                dealwithPulse(message);
            } else if(destination.equals(mind.getOwnName())){
                if(type.equals(mind.ACK)){
                    if(seq.equals(mind.getseqNrssent().get(source))){
                        dealtwithAck(message);
                    }
                } else if(type.equals(mind.MESSAGE)) {
                    if (seq.equals(mind.getseqNrsrcvd().get(source))) {
                        dealwithMessage(message);
                    } else if (seq.equals(mostrecentSeqnr)){
                        dealwithOldMessage(message);
                    }
                }

            } else if(destination.equals("0")){
                if (seq.equals(mind.getseqNrsrcvd().get("0"))) {
                    delawithGlobalMessage(message.substring(5), source);
                }
            }

            //forward pack if it is not destined for itself
            // and it is not from itself, forwarding your own packages is redundant
            if(!destination.equals(mind.getOwnName())) {
                forwardPack(message);
            }
        }
    }



    /**
     * this function checks if the pulse message it got is from a new node in the network
     * and also refreshes the timer on the node the pulse comes from.
     * @param pulse
     */
    public synchronized void dealwithPulse(String pulse){


        if(!statuses.keySet().contains(pulse.substring(0,1))){

            System.out.println(pulse.substring(0,1) + " has come online");

            //if someone new arrives, resent the global message seqnr
            mind.getseqNrsrcvd().put("0","0");
            // initiate seq nrs with the new node
            mind.getseqNrssent().put(pulse.substring(0,1),"0");
            mind.getseqNrsrcvd().put(pulse.substring(0,1),"0");
            mind.getSender().getoutStanding().put(pulse.substring(0,1),false);

        }

        //update the time of last receiving a pulse from this node
        statuses.put(pulse.substring(0,1),System.currentTimeMillis());


    }


    /**
     * this method send a global message to the global chat on the GUI
     * @param message the message itself, encrypted
     * @param fromWho this is always "0" as the function is called only for global messages
     */
    public void delawithGlobalMessage(String message,String fromWho){
        //send message to global chat
        mind.getGui().onGlobalMessageReceived(mind.getSecurity().decrypt(message,"0")
                , Integer.valueOf(fromWho));
        //update seq nr for the global chat
        mind.updateSeqRecvd("0");
    }


    /**
     * this function checks if it has received in the last 3/5 (depending of the value of OUTOFNETWORKTIMEOUT in parent MasterMind)
     * second a pulse from the nodes connected
     * if it has not, it removes it from the list
     */
    public void UpdateStatuses(){
        long now;

        for(String key:statuses.keySet()){
            now = System.currentTimeMillis();
            //if it has not received a pulse from key in mind.OUTOFNETWORKTIMEOUT milliseconds
            //then remove the node from everywhere as it is offline
            if(now - statuses.get(key) > mind.OUTOFNETWORKTIMEOUT){
                System.out.println(key + " has gone offline");
                statuses.remove(key);
                mind.getseqNrsrcvd().remove(key);
                mind.getseqNrssent().remove(key);
            }
        }
    }


    /**
     * @return the map of people online
     */
    public ConcurrentHashMap<String, Long> getStatuses(){
        return statuses;
    }


    /**
     * deal with an ack package, inform the sender side its package arrived
     * @param message
     */
    public void dealtwithAck(String message){

        String source = message.substring(1,2);

        //inform the Sender that an ack for the most recent package towards source has arrived
        mind.getSender().receivedAck(source);

        mind.updateSeqSent(source);

    }


    /**
     * this method deals wiht MESSAGE type packages
     * it sends it to the GUI and also sends an ACK in return
     * @param message
     */
    public void dealwithMessage(String message){
       String mess = mind.getSecurity().decrypt(message.substring(5), message.substring(0,1));
       String destination = message.substring(0,1);
       String source = message.substring(1,2);

       //if it is for the global chat
       if(destination.equals("0")){
           mind.getGui().onGlobalMessageReceived(mess, Integer.valueOf(source));
           mind.updateSeqRecvd("0");
       } else {
           //send to upper layer
           mind.getGui().onMessageReceived(mess, Integer.valueOf(source));
           mostrecentSeqnr = mind.getseqNrsrcvd().get(source);
           mind.updateSeqRecvd(source);

           //send ACK for the message to the sender
           System.out.println("Ack sent for " + message);
           mind.getSender().sendAck(source, message.substring(4,5));
       }

    }

    /**
     * this method sends an acknowledgement for a recent message
     * it is called only if the message's seq nr is smaller only by one
     * This is a safe switch in case the original acknowledgement is lost
     *
     * @param message
     */
    public void dealwithOldMessage(String message){

        String source = message.substring(1,2);

        mind.getSender().sendAck(source, mostrecentSeqnr);
    }



    /**
     * this fucntion takes the message and looks at it's 3 position in the String
     * which is its time to live
     * if it is different than 0, then subtact one and send it in the network
     * @param message
     */
    public void forwardPack(String message){
        if(!message.substring(2,3).equals("0")){
            if(!message.substring(1,2).equals(mind.getOwnName())
                    && !message.substring(0,1).equals((mind.getOwnName()))) {
                if (!message.substring(3, 4).equals(mind.PULSE)) {
                    System.out.println("Forwarding " + message);
                }
                int lowerTTL = Integer.valueOf(message.substring(2, 3)) - 1;
                String lowerTTLmessage = message.substring(0, 2) + lowerTTL + message.substring(3);
                mind.getSender().send(lowerTTLmessage);

                if (!message.substring(3, 4).equals(mind.PULSE)) {
                    System.out.println("------------------");
                }
            }
        }
    }
}