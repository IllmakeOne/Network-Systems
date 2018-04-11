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

    /**
     * the protocol is:
     * on first index of the string it is the destination, to whome its going
     * on the second index its it's time to live
     * on the third index is the type of message, ack synch(pulse) or a message
     * on the 4th index is the seq number
     * and the rest is the encoded message
     * @param mtbs = message to be sent
     */
    public void sendMessage(String mtbs){
        String mesg = mtbs.substring(0, 1) //destination
                + "4" // time to live
                + mind.MESSAGE // type of message
                + mind.getSeqNers().get(mtbs.substring(0, 1)) // seq number
                + mind.getSecurity().encode(mtbs.charAt(0),mtbs.substring(1)); // encoded message

        //change the sequance number asociated to a node
        if(mind.getSeqNers().get(mtbs.substring(0, 1)) == 9){
            mind.getSeqNers().put(mtbs.substring(0, 1),0);
        } else {
            // increse the seq number fo a node by one
            mind.getSeqNers().put(mtbs.substring(0, 1),
                    mind.getSeqNers().get(mtbs.substring(0, 1)));

        }

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
