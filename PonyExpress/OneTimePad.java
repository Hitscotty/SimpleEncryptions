package PonyExpress;

/**
 * @author Jonathan Portorreal
 */
//----------------------------------------------------------------------------//

import java.awt.List;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;

//----------------------------------------------------------------------------//

public abstract class OneTimePad extends AppletNLO implements ActionListener,
		Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String secret;
	Boolean start       = true;
	Boolean userOnline  = false;
	TextArea logTA      = new TextArea();
	TextField messageTF = new TextField();
	TextField nameTF    = new TextField();
	
 	ArrayList<String> online = new ArrayList<String>();	
	String name;

	List onlineLT = new List();
	List friendLT = new List();
	
	DataInputStream input;
	DataOutputStream output;

	// -------------------------------------------------------------------------//

	public OneTimePad() {
		super();
	}

	// -------------------------------------------------------------------------//

	public void init() {
		setupGUI();

		setupConnection();

		setupInputThread();
	}

	// -------------------------------------------------------------------------//

	public void setupGUI() {
		setSize(500, 300);
		add(logTA, 10, 10, 300, 200);
		add(messageTF, 10, 220, 240, 20, this);
		add(nameTF, 250, 220, 70, 20);

		messageTF.setText("--------UserName Here--------");
		nameTF.setText("UserName");

		add(onlineLT, 320, 10, 100, 100);
		add(friendLT, 320, 120, 100, 100);

		onlineLT.addItemListener(new OnlineListener());
		friendLT.addItemListener(new FriendListener());

	}

	// -------------------------------------------------------------------------//

	public void setupConnection() {
		try {
			Socket socket = connect();

			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException x) {
		}
		;
	}

	// -------------------------------------------------------------------------//

	public abstract Socket connect() throws IOException;

	// -------------------------------------------------------------------------//

	public void setupInputThread() {
		Thread t = new Thread(this);

		t.start();
	}

	// -------------------------------------------------------------------------//

	public void run() {
		String message;

		while (true) {
			
			try {
				message = input.readUTF();
				message = messageProtocal(secret);
				update(name);
				
				logTA.append(name + ": " + message + "\n");
				
			} catch (IOException x) {
			}
			
		}
				
	}
	
	// -------------------------------------------------------------------------//

	public void actionPerformed(ActionEvent e) 
	{
		oneTimeName();	
		try {
			if (e.getSource().equals(messageTF)) 
			{
				String friends    = getFriends();
				String message    = friends + ":" + nameTF.getText() + ": " + messageTF.getText();
				String [] packet  = oneTimePad(message);
				update(name);
				secret            = packet[0];
				//logTA.append(name + ": " + messageTF.getText() + "\n");

				output.writeUTF(packet[1]);
				messageTF.setText("");
			}
		} catch (IOException x) {}
	}

	// -------------------------------------------------------------------------//

	public class OnlineListener implements ItemListener {

		public void itemStateChanged(ItemEvent e) {

			if(!itemExists())
			{
			friendLT.add(onlineLT.getSelectedItem());
			}
		}
	}

	public class FriendListener implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			friendLT.remove(friendLT.getSelectedIndex());
		}
	}

	//                         additional methods
	// -------------------------------------------------------------------------//

	/**
	 * Takes a String and converts to bytes.
	 * Uses array of bytes to encode message manipulating numbers with a secret key
	 * using '^' operator to serve as an XOR.
	 * operation is whether to "encrypt" or "decrypt"
	 * @param message
	 * @return
	 */	
	public static String [] oneTimePad(String message)
	{
	    final byte [] key = new byte[message.length()];
		final byte [] messageToEncrypt = message.getBytes();
		final byte [] encodedMessage   = new byte[message.length()];
	    final byte [] decodedMessage   = new byte[message.length()];
	    final String [] packet           = new String [2];


		//puts every random byte into array 'key'
	    new SecureRandom().nextBytes(key);
	      
	    //encrpt into the encodedMessage array
	    for(int digit = 0; digit < key.length; digit++)
	    {
	    	encodedMessage[digit] = (byte) (messageToEncrypt[digit] ^ key[digit]);
	    }
	    
	    System.out.println("--------decrypt-------------");
	    // Decrypt
	    
	    for (int i = 0; i < encodedMessage.length; i++) {
	        decodedMessage[i] = (byte) (encodedMessage[i] ^ key[i]);

	    }
	    packet [0] = convertString(decodedMessage);
	    packet [1] = convertString(encodedMessage);

	    
		return packet;
		
	}
	
	/**
	 * Messages are all in bytes and so this method converts from byte to String 
	 * so it can be a readable message once again
	 * @param encodedMessage
	 * @return
	 */
	public static String convertString(byte [] encodedMessage)
	{
		return new String(encodedMessage);
	}
	
	
	/**
	 * special characters in message can elicit program protocals
	 * 
	 * @param message
	 * @return
	 */
	public String messageProtocal(String message) {
		System.out.println("the message is: " + message);
		String[] packet  = message.split(":");

		//int size         = Integer.parseInt(packet[0]);

		//String[] names   = packet[0].split(",");
		//update(names);
		
		name = packet[1];
		update(name);
		
		
		return packet[2];

	}
	
	/**
	 * makes sure that name cant be changed once entering chat
	 */
	public void oneTimeName(){
		if (start) {
			
			nameTF.setText(messageTF.getText());
			name = nameTF.getText();
			nameTF.setEditable(false);
			
			//messageTF.setText("##-Has-Entered-The-Chatroom-##");
			start = false;
		}
	}
	
	/**
	 * fixes bug where multiple of the same friend added into friendsList
	 * so that only one of the same name can be in friendsList.
	 * @return
	 */
	public boolean itemExists()
	{
		boolean exists = false;
		for(int i = 0; i < friendLT.getItemCount(); i++)
		{
			if(onlineLT.getSelectedItem() == friendLT.getItem(i))
			{
				exists = true;
			}
		}
		return exists;
	}
	
	
	public String getFriends()
	{
		StringBuilder friends = new StringBuilder();
		for(int i = 0; i < friendLT.getItemCount(); i ++)
		{
			friends.append(friendLT.getItem(i));
			if(i < friendLT.getItemCount()-1) friends.append(",");
		}
				
		return friends.toString();
	}

	public String getUser(String m) 
	{
		int size = m.length();
		char end = ':';
		for (int i = 0; i < size; i++)
		{
			if (m.charAt(i) == end)
			{
				m = m.substring(0, i);
				break;
			}
		}
		return m;
	}
	

	/**
	 * where list of names is held. 
	 * Method to update the online List box in gui 
	 * @param n
	 */
	public void update(String n) 
	{
	if(n == nameTF.getText())
	{
		System.out.println("WTF!!");
	}
	if (!online.contains(n)) 
	{
		online.add(n);
		onlineLT.add(n);
	}
	
	}
}