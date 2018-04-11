package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Sender implements  Runnable{

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

        try {
            sock.send(stringTodatagrampacket(mesg));
        } catch (IOException e){
            System.err.println("Unable to send message in Sender");
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


    /**
     * this is a pulse, its purpose is to inform the other in the netwrok that they are alive
     * Each pulse is of the form name+time to live + type of msesage
     */
    public void sendPulse(){
        String pulse = mind.getOwnName() //the name
                + "4" // time to live
                + mind.PULSE; // type of message

        try {
            sock.send(stringTodatagrampacket(pulse));
        } catch (IOException e){
            System.err.println("Unable to send message in Sender");
        }

    }

    /**
     * start sending a pulse each second
     */
    @Override
    public void run() {
        while(mind.getStatus()){
            sendPulse();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.err.println("could not wait,for soem reason");
            }
        }
    }
}
