package PonyExpress;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Implementation of One Time Pad, Pony Express, and One bit correcting HammingCode
 * @author Jonathan Portorreal
 *
 * @param <T>
 */

public class Crypter<T> implements Comparable<T>{

	
	//										One Time Pad
	//-------------------------------------------------------------------------------------------------

	private static String decMes;

	/**
	 * Takes a String and converts to bytes.
	 * Uses array of bytes to encode message manipulating numbers with a secret key
	 * using '^' operator to serve as an XOR.
	 * operation is whether to "encrypt" or "decrypt"
	 * @param message
	 * @return
	 */	
	public static String oneTimePad(String message)
	{
		final byte [] messageToEncrypt = message.getBytes();
	    final byte [] key              = new byte[message.length()];
		final byte [] encodedMessage   = new byte[message.length()];
	    final byte [] decodedMessage   = new byte[message.length()];


		//puts every random byte into array 'key'
	    new SecureRandom().nextBytes(key);
	      
	    //encrpt into the encodedMessage array
	    for(int digit = 0; digit < key.length; digit++)
	    {
	    	encodedMessage[digit] = (byte) (messageToEncrypt[digit] ^ key[digit]);
	    }
	    
	    // Decrypt
	    for (int i = 0; i < encodedMessage.length; i++) {
	        decodedMessage[i] = (byte) (encodedMessage[i] ^ key[i]);
	    }
	    
	    decMes = convertString(decodedMessage);
	
		return convertString(encodedMessage);
		
	}
	
	//									Pony Express
	//-------------------------------------------------------------------------------------------------
	public static String ponyExpress(String message)
	{
		char [] messageToEncode = message.toCharArray();
		char [] encodedMessage  = new char[messageToEncode.length];
	
		//key for ponyExpress
		for(int letter = 0; letter < encodedMessage.length; letter++)
		{
			encodedMessage[letter] = (char) (messageToEncode[letter] + 3);
		}
		
		message = new String(encodedMessage);
		
		return message;
	}
	
	public static String decryptPony(String message){
		
		char [] messageToDecode = message.toCharArray();
		char [] decodedMessage  = new char[messageToDecode.length];
	
		//key for ponyExpress
		for(int letter = 0; letter < decodedMessage.length; letter++)
		{
			decodedMessage[letter] = (char) (messageToDecode[letter] - 3);
		}
		
		message = new String(decodedMessage);
		
		return message;
	}
	
//										HammingCode
//-------------------------------------------------------------------------------------------------
	public static int [] HammingCode(int [] data){
		int [] parityBit    = new int [3];
		int [] codedMessage = new int [7];
			
		//parityBits represent p1,p2,p4,p8 
		parityBit[0] = data[0] ^ data[1] ^ data[3];
		parityBit[1] = data[0] ^ data[2] ^ data[3];
		parityBit[2] = data[1] ^ data[2] ^ data[3];

		//Combine parityBits and data
		codedMessage[0] = parityBit[0];
		codedMessage[1] = parityBit[1];
		codedMessage[2] = data[0];
		codedMessage[3] = parityBit[2];
		codedMessage[4] = data[1];
		codedMessage[5] = data[2];
		codedMessage[6] = data[3];
		
		return codedMessage;
	}
	public static int [] decryptHam(int [] data){
		int [] parityBit      = new int [3];
		int [] decodedMessage = new int [4];
		int [] errorBits      = {1,2,4};
		int bitCounter        = 0;
			
		//Check for errors 
		parityBit[0] = data[0] ^ data[2] ^ data[4] ^ data[6];
		parityBit[1] = data[1] ^ data[2] ^ data[5] ^ data[6];
		parityBit[2] = data[3] ^ data[4] ^ data[5] ^ data[6];
		
		for(int i = 0; i < parityBit.length; i++){
			if(parityBit[i]%2 != 0){
				bitCounter+=errorBits[i];
			}
		}
		
		//Create correct data message if incorrect
		if(bitCounter!=0){
			if(data[bitCounter] == 1){
				data[bitCounter] = 0;
			}else{
				data[bitCounter] = 1;
			}	
		}
		
		//remove message from data
		decodedMessage[0] = data[2];
		decodedMessage[1] = data[4];
		decodedMessage[2] = data[5];
		decodedMessage[3] = data[6];
		
		return decodedMessage;
	}
	
	
	//									Over All Useful Methods
	//-------------------------------------------------------------------------------------------------
	/**
	 * Messages are all in bytes and so this method converts from byte to String 
	 * so it can be a readable message once again
	 * @param encodedMessage
	 * @return
	 */
	public static String convertString(byte [] encodedMessage)
	{
		String decodedMessage = new String(encodedMessage);
		return decodedMessage;
	}
	
	/**
	 * checks an array of bits and adds it up, if the result is even returns true
	 * if result is odd return false
	 * @param code
	 */
	public static boolean isEven(int [] code){
		int size   = code.length;
		int oddity = 0;
		
		for(int i = 0; i < size; i++){
			oddity += code[i];
		}
		
		if((oddity%2) == 0){
			return true;
		}
		
		return false;
	}
	
	@Override
	public int compareTo(T o) {
		return 0;
	}
	
	public String toString(){
		return null;
		
	}
		
	//									Debugging SECTION
	//-------------------------------------------------------------------------------------------------
	
	/**
	 * Main method to debug/test encryption implementations to be used 
	 * with a GUI interface 
	 * @param args
	 */
	public static void main(String[] args) 
	{
		
		//samples to use for debugging
		int [] odd     = {1,0,1,0};
		int [] oddCrypt;
		int [] oddDecrypt;
		String test    = "This is a test";
		String crypt   = "";
		String decrypt = "";

		System.out.println("---------One-Time-Pad-----------");
		
		//Test for One Time Pad
		crypt   = oneTimePad(test);
		System.out.println("Before Encryption: "    + test);
		System.out.println("After OTP Encryption: " + crypt);
		System.out.println("Decryption: "           + decMes);

		System.out.println("\n---------Pony-Express-----------");

		//Test for Pony Express
		crypt   = ponyExpress(test);
		decrypt = decryptPony(crypt);
		System.out.println("Before Encryption: "     + test);
		System.out.println("After Pony Encryption: " + crypt);
		System.out.println("Decryption: "            + decrypt);
		
		System.out.println("\n---------Haming-Code------------");
		
		//Test for Hamming Code
		
	    oddCrypt   = HammingCode(odd);
	    oddDecrypt = decryptHam(oddCrypt); 
		System.out.println("Before Encryption: "        + Arrays.toString(odd));
		System.out.println("After Hamming Encryption: " + Arrays.toString(oddCrypt));
		System.out.println("Decryption: "               + Arrays.toString(oddDecrypt));
		
			
	}
}
