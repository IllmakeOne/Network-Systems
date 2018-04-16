package security;

import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;

public class Security {

	//a map of all the public keys in the network
	private HashMap<String, Key> publicKeys;

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
    public static String encrypt(String unencrypteMessage, String destination){
/*
		try {
			PublicKey key = publicKeys.get(destination);
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.PUBLIC_KEY, publicKeys.get(destination));

		} catch (NoSuchAlgorithmException| NoSuchPaddingException e){
			System.err.println("coult not encrypt");
		}
		*/


		return  unencrypteMessage;
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

	/**
	 * convert a string into a publickey
	 * @param node
	 * @param publickeystring
	 */
	public void addPubickey(String node, String publickeystring){

		try {
			BASE64Decoder decoder = new BASE64Decoder();

			byte[] sigBytes2 = decoder.decodeBuffer(publickeystring);

			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes2);

			KeyFactory keyFact = KeyFactory.getInstance("RSA", "BC");

			PublicKey key = keyFact.generatePublic(x509KeySpec);


			publicKeys.put(node, key);
		} catch ( IOException e) {
			System.err.println("coulnd not convert keystring to key");
		} catch ( NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e){

			System.err.println("coulnd not convert keystring to key");

		}
	}

	private void generateKeyPair(){

		try{

			//create key generator
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");

			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

			keyGen.initialize(512, random);

			KeyPair pair = keyGen.generateKeyPair();

			//System.out.println(pair.getPrivate());
			//System.out.println(pair.getPublic());

			Base64.Encoder encoder = Base64.getEncoder();
			PrivateKey priv = pair.getPrivate();
			PublicKey pub = pair.getPublic();


			ownPublickey = encoder.encodeToString(pub.getEncoded());

			ownPrivatekey = encoder.encodeToString(priv.getEncoded());

			System.out.println("ownPublickey  " + ownPublickey);
			System.out.println("ownPrivatekey " + ownPrivatekey);



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