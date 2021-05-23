package virtualFactory;
import java.io.*;
import java.net.*;

public class Server {

	private static ServerSocket serverSocket;
	private static final int PORT = 5000;
	
	public static void main(String[] args) throws IOException {
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("Server olusturuldu");
		} catch(IOException ioException) {
			System.out.println("Baglanti kurulamadi");
			System.exit(1);
		}
		// ortak kaynak
		Resource item = new Resource();
		do {
			// yeni socket baglantisini dinle
			Socket client = serverSocket.accept();
			System.out.println("Yeni kullanici baglandi");
			ClientThread clientThread = new ClientThread(client, item);
			clientThread.start();
		} while(true);
	}

}