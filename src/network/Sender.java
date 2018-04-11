package network;

import java.net.DatagramPacket;
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
        String mesg = message.charAt(0) +
                message.substring(1);

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
}
