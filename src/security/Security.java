package security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;
import java.util.HashMap;

public class Security {

	//a map of all the public keys in the network
	private HashMap<String, PublicKey> publicKeys;

	//the private key of this node
	private String ownPrivatekey;

	//the public key of this node
	private String ownPublickey;

	//the key for the global chat, universal
	public static final String globalChatKey = "s";


	public Security (String ownName){

		generateKeyPair();

		publicKeys = new HashMap<>();

		//publicKeys.put(ownName,ownPublickey);
		//publicKeys.put("0",globalChatKey);
	}

	// encrypt a message with the destination's public key
    public static String encrypt(String strClearText,String strKey){
        return  strClearText;
    }

    // decrypt a message with the node's private key,
	// it can only decrypt messages addressed to it and global chart messages
    public static String decrypt(String strEncrypted,String strKey) {
        return strEncrypted ;
    }

//
//    public  getPublicKeyof(String destination){
//		return publicKeys.get(destination);
//	}

	public String getOwnPubickey(){
		return ownPublickey;
	}

	public String getOwnPrivatekey(){
		return ownPrivatekey;
	}


	//this method adds a public key to the map of public keys
//	public void addPubickey(String node, String publickey){
//		publicKeys.put(node,publickey);
//	}

	private void generateKeyPair(){

		try{

			//create key generator
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");

			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

			keyGen.initialize(512, random);

			KeyPair pair = keyGen.generateKeyPair();

			System.out.println(pair.getPrivate());
			System.out.println(pair.getPublic());

			Base64.Encoder encoder = Base64.getEncoder();
			PrivateKey priv = pair.getPrivate();
			PublicKey pub = pair.getPublic();

			System.out.println("privateKey: " + encoder.encodeToString(priv.getEncoded()));
			System.out.println("publicKey: " + encoder.encodeToString(pub.getEncoded()));

			//	this.ownPublickey = pair.getPublic();
		//	this.ownPrivatekey = pair.getPrivate();


		} catch (NoSuchAlgorithmException e){
			System.err.println(e.getMessage());

		} catch (NoSuchProviderException e){
			System.err.println(e.getMessage());
		}

	}









/**public static String encrypt(String strClearText,String strKey) throws Exception{
	String strData="";
	
	try {
		SecretKeySpec skeyspec=new SecretKeySpec(strKey.getBytes(),"Blowfish");
		Cipher cipher=Cipher.getInstance("Blowfish");
		cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
		byte[] encrypted=cipher.doFinal(strClearText.getBytes());
		strData=new String(encrypted);
		
	} catch (Exception e) {
		e.printStackTrace();
		throw new Exception(e);
	}
	return strData;
	}


public static String decrypt(String strEncrypted,String strKey) throws Exception{
	String strData="";
	
	try {
		SecretKeySpec skeyspec=new SecretKeySpec(strKey.getBytes(),"Blowfish");
		Cipher cipher=Cipher.getInstance("Blowfish");
		cipher.init(Cipher.DECRYPT_MODE, skeyspec);
		byte[] decrypted=cipher.doFinal(strEncrypted.getBytes());
		strData=new String(decrypted);
		
	} catch (Exception e) {
		e.printStackTrace();
		throw new Exception(e);
	}
	return strData;
	}

}**/
	

}