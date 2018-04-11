package network;

import java.net.MulticastSocket;

public class Receiver {


    private MulticastSocket sock;

    public Receiver(MulticastSocket socket){
        this.sock = socket;
    }
}
