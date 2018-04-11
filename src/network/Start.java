package network;

public class Start {

    public static void main(String[] args) {
        MasterMind node = new MasterMind(args[0]);
        Thread main = new Thread(node);
        main.start();

    }
}
