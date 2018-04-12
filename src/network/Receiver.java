package network;

import java.net.MulticastSocket;
import java.util.HashMap;

public class Receiver {


    private MulticastSocket sock;
    private  MasterMind mind;

    //this keeps in mind each time it receives a pulse from a node so it knows it's in the network
    private HashMap<String, Long> statuses;



    public Receiver(MulticastSocket socket, MasterMind mind){
        this.sock = socket;
        this.mind = mind;
        statuses = new HashMap<String, Long>();
    }

    public void dealWithPacket(String message){

        UpdateStatuses();

        if(message.substring(3,4).equals(mind.PULSE)){
            dealwithPulse(message);
        } else if(message.substring(0,1).equals(mind.getOwnName())){
            System.out.println(message);
            if(message.substring(3,4).equals(mind.ACK)){

                dealtwithAck(message);
            } else {
                dealwithMessage(message);
            }
        }

        forwardPack(message);
//        if(message.substring(3,4).equals(mind.ACK)){
//
//            dealtwithAck(message);
//        } else if(message.substring(3,4).equals(mind.PULSE)){
//            dealwithPulse(message);
//
//        } else if (message.substring(3,4).equals(mind.MESSAGE)){
//
//            dealwithMessage(message);
//        } else {
//            System.out.println("Unrecognised message");
//        }
    }


    /**
     * this function checks if the pulse message it got is from a new node in the network
     * @param message
     */
    public void dealwithPulse(String message){
        if(!statuses.keySet().contains(message.substring(0,1))){
            statuses.put(message.substring(0,1),System.currentTimeMillis());
            mind.getSeqNers().put(message.substring(0,1),"0");
        }
    }


    /**
     * this function checks if it has received in the last 3 second a pulse from the nodes conected
     * if it has not, it removes it from the list
     */
    public void UpdateStatuses(){
        long now = System.currentTimeMillis();
        for(String key:statuses.keySet()){
            if(now - statuses.get(key) > mind.OUTOFNETWORKTIMEOUT){
                statuses.remove(key);
                mind.getSeqNers().remove(key);
            }
        }
    }


    public void dealtwithAck(String message){
        String source = message.substring(0,1);
        String seq = message.substring(4,5);
        if(seq.equals(mind.getSeqNers().get(source))){
            mind.getSender().receivedAck(source);
        }

    }

    public void dealwithMessage(String message){
       String mess = mind.getSecurity().decrypt(message.substring(5), message.substring(0,1));


       // ADD FOR GROUP CHAT
        //send to upper layer
        mind.getGui().onMessageReceived(mess, Integer.valueOf(message.substring(1,2)));


        // send ack package back to sender
        mind.getSender().sendAck(message.substring(1,2), message.substring(4,5));
    }


    /**
     * this fucntion takes the message and looks at it's 3 position in the String
     * which is its time to live
     * if it is different than 0, then subtact one and send it in the network
     * @param message
     */
    public void forwardPack(String message){
        if(!message.substring(0,1).equals(mind.getOwnName())) { //if the message is for itself, do not forward it
            String reformatedMessage = message.substring(0, 2);
            int stupid = Integer.valueOf(message.substring(2, 3)) - 1;
            if (!message.substring(2, 3).equals("0")) { //if the package's time to live is 0 do not forward it
                reformatedMessage = reformatedMessage + stupid + message.substring(3);
                mind.getSender().send(reformatedMessage);
            }
        }
    }
}
