package PonyExpress;
/**
 * @author Jonathan Portorreal
 */
import java.io.IOException;
import java.net.Socket;

public class PonyClient extends OneTimePad{

	private static final long serialVersionUID = 1L;
	
	public PonyClient(){
		super();
	}
		
	public Socket connect() throws IOException{
		 Socket  socket = new Socket("localhost", 8080);
	     return socket;	
	     
	}

}
