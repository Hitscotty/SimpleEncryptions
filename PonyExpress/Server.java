package PonyExpress;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * 
 * @author Jonathan Portorreal aka Scotty 
 * 
 */

public class Server {
	public static final int N = 3;
	public static int n = 0;
	public static int programStart = 0;

	ServerSocket server;

	ClientHandler[] clientHandler = new ClientHandler[N];
	Vector<String> messageList    = new Vector<String>(10);

	public static void main(String[] args) {
		Server server = new Server();

		server.init();

	}

	public void init() {
		try {
			server = new ServerSocket(8080);

			for (int i = 0; i < N; i++) {

				clientHandler[i] = new ClientHandler();

				Thread t         = new Thread(clientHandler[i]);

				t.start();

				n++;

			}

		} catch (IOException x) {
		}
		;

	}

	public class ClientHandler implements Runnable {

		String[] friendsList = new String[N];

		DataInputStream input;
		DataOutputStream output;

		boolean online = true;

		String name;

		public ClientHandler() {

			try {
				Socket socket = server.accept();

				input         = new DataInputStream(socket.getInputStream());
				output        = new DataOutputStream(socket.getOutputStream());

			} catch (IOException x) {}
			
		}

		public void run() {
					System.out.println("-----Encryption-Log------");
			while (true) {
				try {
					String message = input.readUTF();
					System.out.println("------------SERVER-----------");
					System.out.println("Encrypted message: " + message);

					for(int i = 0; i < n; i++){
							clientHandler[i].output.writeUTF(message);
						
					}
					
					}
				catch (IOException x) {
				}
				
			}
		}

		// 										additional methods
		// __________________________________________________________________________________________________


		/**
		 * special characters in message can elicit program protocals
		 * 
		 * @param message
		 * @return
		 */
		public String messageProtocal(String message) {
			String[] packet  = message.split(":");
			String[] names   = packet[0].split(",");
			name             = packet[1];
			
			updateFL(names);
			
			return packet[2];
		}
		

		/**
		 * uses packet information to put each GUIChat's friendList into the 
		 * ClientHandlers friendList
		 * @param names
		 */
		public void updateFL(String[] names) {

			for (int i = 0; i < names.length; i++) {
				friendsList[i] = names[i];
				//messageList.remove(names[i]);
			}
			
		}

		
		/**
		 * takes input message and searches for the sections that holds the name
		 * returns name
		 * 
		 * @param m
		 * @return
		 */
		public String getUser(String m) {
			int size = m.length();
			char end = ':';
			for (int i = 0; i < size; i++) {
				if (m.charAt(i) == end) {
					m = m.substring(0, i);
					break;
				}
			}
			return m;
		}
	}
}

