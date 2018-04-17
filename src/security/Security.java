package security;
import java.security.SecureRandom;

public class Security {
    public static final String globalChatKey = "s";
    private String id;
    private byte[][] oneTimePads = new byte[5][5000];

    public Security(String name) {
        id = name;
        SecureRandom random = new SecureRandom();
        random.setSeed(1);

        for (int i = 0; i < 5; i++) {
            random.nextBytes(oneTimePads[i]);
        }
    }

    // encrypt a message with the destination's public key
    public String encrypt(String unencrypteMessage, String destination){
        byte[] message = unencrypteMessage.getBytes();
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

    // decrypt a message with the node's private key,
    // it can only decrypt messages addressed to it and global chart messages
    public String decrypt(String strEncrypted, String from) {
        byte[] message = new byte[strEncrypted.length() / 8];

        for (int i = 0; i < strEncrypted.length(); i += 8) {
        	try {
				String substring = strEncrypted.substring(i, i + 8);
				if (substring.length() != 8) {
					break;
				}
				Integer substringAsInteger = Integer.valueOf(substring, 2);
				int num = (int) substringAsInteger;
				Byte b1 = (byte) num;
				message[i / 8] = b1;
			} catch (NumberFormatException e){

			}
        }

        byte[] key = oneTimePads[Integer.valueOf(id)];

        for (int i = 0; i < message.length && i < key.length; i++) {
            message[i] = (byte) (message[i] ^ key[i]);
        }

        return new String(message);
    }

    public static void main(String[] args) {
        Security security = new Security("1");
        String encrypted = security.encrypt("asdgkadsgljk!~!!!!!sadjgasgdkljgsadj", "1");
        String decrypted = security.decrypt(encrypted, "1");
    }
}
