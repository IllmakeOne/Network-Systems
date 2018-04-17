package security;

import java.util.Random;

/*
    The Security class is responsible for generating one time pads for every client and for encrypting
    and decrypting outgoing and incoming messages respectively.
 */

public class Security {
    private String id;
    private byte[][] oneTimePads = new byte[5][5000];

    /*
        Constructs a security instances and fills the oneTimePads array with data.
     */
    public Security(String name) {
        id = name;
        Random random = new Random();
        random.setSeed(1);

        for (int i = 0; i < 5; i++) {
            random.nextBytes(oneTimePads[i]);
        }
    }

    /*
        Encrypts an unencrypted and returns it as a bit STRING.
     */
    public String encrypt(String unencryptedMessage, String destination){
        byte[] message = unencryptedMessage.getBytes();
        byte[] key = oneTimePads[Integer.valueOf(destination)];
        StringBuilder result = new StringBuilder();

        // XOR with the key, use strings to represent bytes (string 00000010 being 2).
        for (int i = 0; i < message.length && i < key.length; i++) {
            message[i] = (byte) (message[i] ^ key[i]);

            byte b1 = message[i];
            String s1 = String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0');
            result.append(s1);
        }

        return result.toString();
    }

    /*
        Decrypts a message and returns it as the original string. The decryption is done by
        XOR-ing the key and the bits in each byte.
     */
    public String decrypt(String strEncrypted, String from) {
        byte[] message = new byte[strEncrypted.length() / 8];

        for (int i = 0; i < strEncrypted.length(); i += 8) {
            String substring = strEncrypted.substring(i, i + 8);
            if (substring.length() != 8) {
                break;
            }
            int num = Integer.valueOf(substring, 2);
            Byte b1 = (byte) num;
            message[i / 8] = b1;
        }

        byte[] key;

        // in case of global chat
        if (from.equals("0")) {
            key = oneTimePads[0];
        } else {
            key = oneTimePads[Integer.valueOf(id)];
        }

        for (int i = 0; i < message.length && i < key.length; i++) {
            message[i] = (byte) (message[i] ^ key[i]);
        }

        return new String(message);
    }
}
