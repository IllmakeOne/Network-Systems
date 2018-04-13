package network;

import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class Receiver {


    private MulticastSocket sock;
    private  MasterMind mind;

    private HashMap<String, String> recentMessage;

    //this keeps in mind each time it receives a pulse from a node so it knows it's in the network
    private HashMap<String, Long> statuses;



    public Receiver(MulticastSocket socket, MasterMind mind){
        this.sock = socket;
        this.mind = mind;
        statuses = new HashMap<String, Long>();
        recentMessage = new HashMap<>();
    }


    public void dealWithPacket(String message){


        String destination = message.substring(0,1);
        String type = message.substring(3,4);
        String source = message.substring(1,2);
        String seq = message.substring(4,5);

        if(!source.equals(mind.getOwnName())) {

            if (type.equals(mind.PULSE)) {
                dealwithPulse(message);
            } else if(destination.equals(mind.getOwnName())){
                       if( seq.equals(mind.getSeqNers().get(source))) {
                           System.out.println(message);
                           if (type.equals(mind.ACK)) {
                               if(mind.getSender().getoutStanding().get
                                       (message.substring(1,2))) {
                                   dealtwithAck(message);
                               }
                           } else {
                               dealwithMessage(message);
                           }
                       }

            }

//        if(message.substring(3,4).equals(mind.PULSE)){
//            dealwithPulse(message);
//        } else if(message.substring(0,1).equals(mind.getOwnName())){
//            System.out.println(message);
//            if(message.substring(3,4).equals(mind.ACK)){
//
//                dealtwithAck(message);
//            } else {
//                dealwithMessage(message);
//            }
//        }

            forwardPack(message);
        }
    }



    /**
     * this function checks if the pulse message it got is from a new node in the network
     * @param message
     */
    public void dealwithPulse(String message){


        if(!statuses.keySet().contains(message.substring(0,1))){

            System.out.println(message.substring(0,1) + " has come online");

            statuses.put(message.substring(0,1),System.currentTimeMillis());
            mind.getSeqNers().put(message.substring(0,1),"0");
            mind.getSender().getoutStanding().put(message.substring(0,1),false);
        }

        //System.out.println("updated time for "+ message.substring(0,1));
        statuses.put(message.substring(0,1),System.currentTimeMillis());


    }


    /**
     * this function checks if it has received in the last 3 second a pulse from the nodes conected
     * if it has not, it removes it from the list
     */
    public synchronized void UpdateStatuses(){
        long now;
        for(String key:statuses.keySet()){
            now = System.currentTimeMillis();
            if(now - statuses.get(key) > mind.OUTOFNETWORKTIMEOUT){
                System.out.println(key + " has gone offline");
                statuses.remove(key);
                mind.getSeqNers().remove(key);
            }
        }
    }

    public HashMap<String, Long> getStatuses(){
        return statuses;
    }


    /**
     * deal with an ack packaga, inform the sender side its package arrived
     * @param message
     */
    public void dealtwithAck(String message){

        String source = message.substring(1,2);
        //String seq = message.substring(4,5);
        System.out.println("Received ACk");
        mind.getSender().receivedAck(source);

        mind.updateSeq(source);

    }

    public void dealwithMessage(String message){
       String mess = mind.getSecurity().decrypt(message.substring(5), message.substring(0,1));
       String destination = message.substring(0,1);
       String source = message.substring(1,2);
        //arecentMessage.put(message.substring(1,2), message.g)

       if(destination.equals("0")){
           mind.getGui().onMessageReceived(mess, Integer.valueOf("0"));
       } else {


           //send to upper layer
         //  System.out.println(mess + " " +Integer.valueOf(message.substring(1, 2)));
           mind.getGui().onMessageReceived(mess, Integer.valueOf(message.substring(1, 2)));

           mind.updateSeq(source);
       }

        // send ack package back to sender
        System.out.println("senidng ack for " + mess);
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
            if(!message.substring(1,2).equals(mind.getOwnName())){
                int lowerTTL = Integer.valueOf(message.substring(2,3))-1;
                String lowerTTLmessage = message.substring(0,2) + lowerTTL + message.substring(3);
                mind.getSender().send(lowerTTLmessage);
            }
        }
    }
//        if(!message.substring(0,1).equals(mind.getOwnName())) { //if the message is for itself, do not forward it
//            String reformatedMessage = message.substring(0, 2);
//
//            int stupid = Integer.valueOf(message.substring(2, 3)) - 1;
//          //  System.out.println(stupid+ " in foward");
//
//            if (!message.substring(2, 3).equals("0")) { //if the package's time to live is 0 do not forward it
//                reformatedMessage = reformatedMessage + stupid + message.substring(3);
//                mind.getSender().send(reformatedMessage);
//            }
//        }
//    }
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
