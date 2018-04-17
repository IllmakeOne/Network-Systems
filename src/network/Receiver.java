package network;

import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Receiver {



    private  MasterMind mind;


    //this keeps in mind each time it receives a pulse from a node so it knows it's in the network
    private ConcurrentHashMap<String, Long> statuses;



    public Receiver(MasterMind mind){
        this.mind = mind;
        statuses = new ConcurrentHashMap<String, Long>();
    }


    /**
     * this methods deal with whatever package the node receives.
     */
    public void dealWithPacket(String message){


        String destination = message.substring(0,1);
        String source = message.substring(1,2);
        String type = message.substring(3,4);
        String seq = message.substring(4,5);

        if(!source.equals(mind.getOwnName())) {

            if (type.equals(mind.PULSE)) {
                dealwithPulse(message);
            } else if(destination.equals(mind.getOwnName())){
                if(type.equals(mind.ACK)){
                    if(seq.equals(mind.getseqNrssent().get(source))){
                        dealtwithAck(message);
                    }
                } else if(type.equals(mind.MESSAGE)){
                    if(seq.equals(mind.getseqNrsrcvd().get(source))){
                        dealwithMessage(message);
                    }
                }
//                       if( seq.equals(mind.getseqNrssent().get(source))) {
//                           //System.out.println(message);
//                           if (type.equals(mind.ACK)) {
//                               if(mind.getSender().getoutStanding().get
//                                       (message.substring(1,2))) {
//                                   dealtwithAck(message);
//                               }
//                           } else {
//                               dealwithMessage(message);
//                           }
//                       }

            } else if(destination.equals("0")){
               // System.out.println(message);
                if (seq.equals(mind.getseqNrsrcvd().get("0"))) {
                    delawithGlobalMessage(message.substring(5), source);
                }
            }



            forwardPack(message);
        }
    }



    /**
     * this function checks if the pulse message it got is from a new node in the network
     * and also refreshes the timer on the node the pulse comes from.
     * @param message
     */
    public synchronized void dealwithPulse(String message){


        if(!statuses.keySet().contains(message.substring(0,1))){

            System.out.println(message.substring(0,1) + " has come online");

            //
            mind.getseqNrsrcvd().put("0","0");
            mind.getseqNrssent().put(message.substring(0,1),"0");
           // statuses.put(message.substring(0,1),System.currentTimeMillis());
            mind.getseqNrsrcvd().put(message.substring(0,1),"0");
            mind.getSender().getoutStanding().put(message.substring(0,1),false);

        }


        statuses.put(message.substring(0,1),System.currentTimeMillis());


    }


    /**
     * this method send a global message to the global chat on the GUI
     * @param message the message itself, encrypted
     * @param fromWho this is always "0" as the function is called only for global messages
     */
    public void delawithGlobalMessage(String message,String fromWho){

        mind.getGui().onGlobalMessageReceived(mind.getSecurity().decrypt(message,"0")
                , Integer.valueOf(fromWho));
        mind.updateSeqRecvd("0");
    }


    /**
     * this function checks if it has received in the last 3 second a pulse from the nodes conected
     * if it has not, it removes it from the list
     */
    public void UpdateStatuses(){
        long now;

        for(String key:statuses.keySet()){
            now = System.currentTimeMillis();
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
     * deal with an ack packaga, inform the sender side its package arrived
     * @param message
     */
    public void dealtwithAck(String message){

        String source = message.substring(1,2);
        //String seq = message.substring(4,5);
        //System.out.println("Received ACk");
        mind.getSender().receivedAck(source);

        mind.updateSeqSent(source);

    }

    public void dealwithMessage(String message){
       String mess = mind.getSecurity().decrypt(message.substring(5), message.substring(0,1));
       String destination = message.substring(0,1);
       String source = message.substring(1,2);
        //arecentMessage.put(message.substring(1,2), message.g)

       if(destination.equals("0")){
           mind.getGui().onGlobalMessageReceived(mess, Integer.valueOf(source));
           mind.updateSeqRecvd("0");
       } else {


           //send to upper layer
         //  System.out.println(mess + " " +Integer.valueOf(message.substring(1, 2)));
           mind.getGui().onMessageReceived(mess, Integer.valueOf(message.substring(1, 2)));

           mind.updateSeqRecvd(source);
       }

        // send ack package back to sender
        System.out.println("Ack sent for " + message);
        mind.getSender().sendAck(message.substring(1,2), message.substring(4,5));
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











//
//    public void dealWithPacket(String message){
//
//        if(!message.substring(1,2).equals(mind.getOwnName())) {
//
//            if (message.substring(3, 4).equals(mind.PULSE)) {
//                dealwithPulse(message);
//
//            } else if (message.substring(0, 1).equals(mind.getOwnName()) &&
//                    mind.getSeqNers().get(message.substring(1,2)).equals(message.substring(4,5))) {
//                System.out.println(message);
//                if (message.substring(3, 4).equals(mind.ACK)) {
//
//                    dealtwithAck(message);
//                } else {
//                    dealwithMessage(message);
//                }
//            }
//
//            forwardPack(message);
//        }
//    }
