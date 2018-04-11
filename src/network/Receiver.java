package network;

import java.net.MulticastSocket;
import java.util.HashMap;

public class Receiver {


    private MulticastSocket sock;
    private  MasterMind mind;



    public Receiver(MulticastSocket socket, MasterMind mind){
        this.sock = socket;
        this.mind = mind;
    }

    public void dealWithMessage(String message){
        System.out.println(message);
        if(message.substring(2,3).equals(mind.ACK)){
            dealtwithAck(message);
        } else if(message.substring(2,3).equals(mind.PULSE)){
            dealwithPulse(message);
        } else if (message.substring(2,3).equals(mind.MESSAGE)){
            dealwithMessage(message);
        } else {
            System.out.println("Unrecognised message");
        }
    }

    public void dealwithPulse(String message){


    }


    public void dealtwithAck(String message){

    }

    public void dealwithMessage(String message){

    }

    public void forwardPack(String message){

    }
}
