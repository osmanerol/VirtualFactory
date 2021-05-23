package virtualFactory;
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientThread extends Thread {
	
	private Socket client;
	private Resource source;
	private Scanner input;
	private PrintWriter output;
	
	public ClientThread(Socket client, Resource source) {
		this.client = client;
		this.source = source;
		try {
			input = new Scanner(this.client.getInputStream());	
			output = new PrintWriter(this.client.getOutputStream(), true);
		} catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void run() {
		String nextId;
		String request;
		String[] command;
		do {
			try {
				this.source.removeMachine("1");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			request = input.nextLine();
			command = request.split(" ");
			// client type 1 - machine
			if(command[command.length-1].compareTo("1") == 0) {
				if(command[0].compareTo("CONNECT") == 0) {
					nextId = this.source.lastId;
					this.source.incrementMachineId();
					output.println("200 - Sunucuya baglanildi "+nextId);
					Machine newMachine = new Machine();
				}
			}
			// client type 2 - planner
			else {
				
			}
		} while(true);
	}
	
}