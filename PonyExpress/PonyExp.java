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
import java.util.ArrayList;

//----------------------------------------------------------------------------//

public abstract class PonyExp extends AppletNLO implements ActionListener,
		Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Boolean start       = true;
	Boolean userOnline  = false;
	int listWatcher     = 0;
	TextArea logTA      = new TextArea();
	TextField messageTF = new TextField();
	TextField nameTF    = new TextField();
	
	String name;
	// Button submitBN  = new Button("Submit");

	List onlineLT = new List();
	List friendLT = new List();

 	ArrayList<String> online = new ArrayList<String>();
	char[] key = { '^', '!', '@' };

	DataInputStream input;
	DataOutputStream output;

	// -------------------------------------------------------------------------//

	public PonyExp() {
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
				message = decrypt(message);
				message = messageProtocal(message);
				update(name);
				logTA.append(name + ": " + message + "\n");
			} catch (IOException x) {
			}
			;
		}
				
	}
	
	// -------------------------------------------------------------------------//

	public void actionPerformed(ActionEvent e) 
	{
		oneTimeName();	
		try {
			if (e.getSource().equals(messageTF)) 
			{
				String friends = getFriends();
				String message = friends + ":" + nameTF.getText() + ": " + messageTF.getText();
				message = ponyExpress(message);
				
				output.writeUTF(message);
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
	
	public static String decrypt(String message){
		
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
	
	
	/**
	 * special characters in message can elicit program protocals
	 * 
	 * @param message
	 * @return
	 */
	public String messageProtocal(String message) {
		String[] packet  = message.split(":");

		//int size         = Integer.parseInt(packet[0]);

		//String[] names   = packet[0].split(",");
		//updateFL(names);

		name = packet[1];
		
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
			
			messageTF.setText("##-Has-Entered-The-Chatroom-##");
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

	/**
	 * special characters can be used to initiate commands only read by server
	 * @param message
	 * @throws IOException
	 */
	public void scanKey(String message) throws IOException 
	{
		char slice = ':';
		int start = message.indexOf(slice);
		
			for (int j = 0; j < key.length; j++) 
			{
				if (message.charAt(start+1) == key[j]) 
				{
					char k = key[j];
					switch (k) 
					{
					case '!':
						update(getUser(message));
						output.writeUTF("!");
						break;
					case '@':
						output.writeUTF("@");
						break;
					default:
						return;
					}
				}
			}
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