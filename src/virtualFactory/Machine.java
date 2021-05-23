package virtualFactory;
import java.io.*;
import java.net.*;
import java.util.*;

public class Machine extends Thread {
	public static String id, name, type, status;
	public static float speed;
	public static boolean isEmpty = true, isConnected = false;
	private static InetAddress host;
	private static final int PORT = 5000;
	
	public static void main(String[] args) throws IOException {
		try {
			host = InetAddress.getLocalHost();
		} catch(UnknownHostException ex) {
			System.out.println("Host id not found.");
			System.exit(1);
		}
		accessServer();
	}
	
	private static void accessServer() {
		Socket link = null;
		Scanner machineInput = new Scanner(System.in);
		String request, response;
		// komutlarý dizi olarak tutacak
		String[]  command;
		try {
			// ilk komut create islemi mi kontrolu
			do {
				System.out.print("Machine > ");
				request = machineInput.nextLine();
				command = request.split(" ");
				if(command[0].compareTo("CREATE") != 0) {
					System.out.println("400 - Hatalý komut"); 
				}
				else if (command.length != 4) {
					System.out.println("400 - Hatalý komut"); 
				}
				else {
					name = command[1];
					type = command[2];
					speed = Float.parseFloat(command[3]);
				}
			} while(command[0].compareTo("CREATE") != 0);
			// create girildikten sonra socket baglantisi
			do {
				System.out.print("Machine > ");
				request = machineInput.nextLine();
				command = request.split(" ");
				if(command[0].compareTo("CONNECT") != 0) {
					System.out.println("501 - Sunucuya baglanilamadi"); 
				}
				else {
					// connect komutundan sonra server'a baglanma
					link = new Socket(host, PORT);
					Scanner input = new Scanner(link.getInputStream());
					PrintWriter output = new PrintWriter(link.getOutputStream(), true);
					output.println(machineRequest(request));
					response = input.nextLine();
					command = response.split(" ");
					id = command[command.length -1];
					isConnected = true;
					System.out.printf("%s %s %s %f\n", id, name, type, speed);
				}
			} while(command[0].compareTo("CONNECT") != 0);
			// create komutundan sonra diger komutlar
			do {
				System.out.print("Machine > ");
				request = machineInput.nextLine();
				command = request.split(" ");
				if(command[0].compareTo("CLOSE") != 0) {
					System.out.println("400 - Hatalý komut"); 
				}
				else {
					
				}
			} while(command[0].compareTo("CLOSE") != 0);
		} catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	private static String machineRequest(String request) {
		return request +" 1";
	}
	
}