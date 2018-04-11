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

    public void dealWithMessage(String message){

        UpdateStatuses();
        System.out.println(message);

        if(message.substring(3,4).equals(mind.ACK)){

            dealtwithAck(message);
        } else if(message.substring(3,4).equals(mind.PULSE)){
            dealwithPulse(message);

        } else if (message.substring(3,4).equals(mind.MESSAGE)){

            dealwithMessage(message);
        } else {
            System.out.println("Unrecognised message");
        }
    }


    /**
     * this function checks if the pulse message it got is from a new node in the network
     * @param message
     */
    public void dealwithPulse(String message){
        if(!statuses.keySet().contains(message.substring(0,1))){
            statuses.put(message.substring(0,1),System.currentTimeMillis());
        }
        forwardPack(message);
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
            }
        }
    }


    public void dealtwithAck(String message){

        forwardPack(message);
    }

    public void dealwithMessage(String message){

        forwardPack(message);
    }


    /**
     * this fucntion takes the message and looks at it's 3 position in the String
     * which is its time to live
     * if it is different than 0, then subtact one and send it in the network
     * @param message
     */
    public void forwardPack(String message){
        String reformatedMessage = message.substring(0,2);
        int stupid = Integer.valueOf(message.substring(2,3)) - 1;
        if(!message.substring(2,3).equals("0")){
            reformatedMessage = reformatedMessage + stupid + message.substring(3);
            mind.getSender().send(reformatedMessage);
        }
    }
}
