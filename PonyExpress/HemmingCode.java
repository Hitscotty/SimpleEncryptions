package PonyExpress;

/**
 * @author Jonathan Portorreal
 */

import java.applet.Applet;
import java.awt.Button;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public abstract class HemmingCode extends Applet implements ActionListener,
		Runnable {

	/**
	 * PonyExpress OneTimePad HemmingCode
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TextField messageTF = new TextField();
	TextField encodedTF = new TextField();
	Label op = new Label("Message");
	Label ip = new Label("Encrypted");
	Button b = new Button("Enter");

	DataInputStream input;
	DataOutputStream output;

	public HemmingCode() {
		super();
	}

	public void init() {
		setupGUI();

		setupConnection();

		setupInputThread();
	}

	// -------------------------------------------------------------------------//

	public void setupGUI() {
		setLayout(null);
		setSize(350, 280);
		add(messageTF);
		add(encodedTF);

		messageTF.setBounds(110,50, 100, 30);
		encodedTF.setBounds(110,150, 100, 30);
		
		b.setBounds(120, 200, 80, 60);
		op.setBounds(135, 10, 80, 80);
		ip.setBounds(130, 120, 80, 80);
		add(b);
		add(op);
		add(ip);

		b.addActionListener(this);
	};

	// -------------------------------------------------------------------------//

	public void setupConnection() {
		try {
			Socket socket = connect();

			input         = new DataInputStream(socket.getInputStream());
			output        = new DataOutputStream(socket.getOutputStream());
		} catch (IOException x) {}
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
				messageTF.setText(message);

			} catch (IOException x) {}
		}
	}

	// -------------------------------------------------------------------------//

	public void actionPerformed(ActionEvent e) {
		String message;
		int [] code;
		try {
			if (e.getSource() == b) {

				message = messageTF.getText();
				code    = HammingCode(convertArray(message));
				message = convertString(code);
				encodedTF.setText(message);
				output.writeUTF(message);
			}
		} catch (IOException x) {}
	}
	
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
	
	
	public String convertString(int[] code){
		StringBuilder convert = new StringBuilder();
		for(int i = 0; i < code.length; i++){
			convert.append(code[i]);

		}
		String num = convert.toString();
		
		return num;
	}
	
	public static int [] convertArray(String number){
		int size        = number.length();
		String [] temp  = new String[size];
		int    [] code  = new int   [size]; 
		int row         = 0;
		int col         = 1;
	

		for(int i = 0; i < size; i++){
			temp[i] = number.substring(row++,col++);
		}
		
		for(int j = 0; j < size; j++){
			code[j] = Integer.parseInt(temp[j]);
		}
		return code;
	}

}
