package network;

import java.net.MulticastSocket;
import java.util.HashMap;

public class Sender {

    private MulticastSocket sock;
    private MasterMind mind;

    public Sender(MulticastSocket socket, MasterMind mind){
        this.sock = socket;
        this.mind = mind;
    }


    public void sendMessage(String message){


    }
}
