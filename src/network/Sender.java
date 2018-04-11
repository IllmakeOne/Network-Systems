package network;

import java.net.MulticastSocket;

public class Sender {

    private MulticastSocket sock;

    public Sender(MulticastSocket socket){
        this.sock = socket;
    }
}
