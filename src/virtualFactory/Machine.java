package virtualFactory;
import java.io.*;
import java.net.*;
import java.util.*;

public class Machine extends Thread {
	public String id, name, type, status = "EMPTY";
	public float speed;
	public boolean isEmpty = true, isConnected = false;
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
		Machine machine = new Machine();
		Scanner input = null;
		PrintWriter output = null;
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
					machine.name = command[1];
					machine.type = command[2];
					machine.speed = Float.parseFloat(command[3]);
				}
			} while(command[0].compareTo("CREATE") != 0 || command.length != 4);
			// create girildikten sonra socket baglantisi
			do {
				System.out.print("Machine > ");
				request = machineInput.nextLine();
				command = request.split(" ");
				if(command[0].compareTo("CONNECT") != 0) {
					System.out.println("501 - Sunucuya baglanilamadi"); 
				}
				else if(command[0].compareTo("CONNECT") != 0 && command.length != 1) {
					System.out.println("400 - Hatalý komut");
				}
				else {
					// connect komutundan sonra server'a baglanma
					link = new Socket(host, PORT);
					input = new Scanner(link.getInputStream());
					output = new PrintWriter(link.getOutputStream(), true);
					request = "CONNECT " + machine.name + " " +  machine.type + " " + machine.speed + " " + machine.status; 
					output.println(machineRequest(request));
					response = input.nextLine();
					System.out.println(response);
					command = response.split(" ");
					machine.id = command[command.length -1];
					machine.isConnected = true;
				}
			} while(command[0].compareTo("CONNECT") != 0 && !machine.isConnected);
			// create komutundan sonra diger komutlar
			do {
				System.out.print("Machine > ");
				request = machineInput.nextLine();
				command = request.split(" ");
				if(command[0].compareTo("CLOSE") != 0) {
					System.out.println("400 - Hatalý komut"); 
				}
				else {
					request = "" + request + " " + machine.id;
					output.println(machineRequest(request));
					response = input.nextLine();
					System.out.println(response);
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