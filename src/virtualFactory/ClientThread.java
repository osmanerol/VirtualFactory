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
		String nextId, request;
		String[] command;
		Machine newMachine;
		do {
			request = input.nextLine();
			command = request.split(" ");
			// client type 1 - machine
			if(command[command.length-1].compareTo("1") == 0) {
				if(command[0].compareTo("CONNECT") == 0) {
					nextId = this.source.lastId;
					this.source.incrementMachineId();
					output.println("200 - Sunucuya baglanildi "+nextId);
					newMachine = new Machine();
					newMachine.id = nextId;
					newMachine.name= command[1];
					newMachine.type = command[2];
					newMachine.speed = Float.parseFloat(command[3]);
					newMachine.status = command[4];
					try {
						this.source.addMachine(newMachine);
						this.source.showMachineList();
					} catch (InterruptedException interruptedException) {
						interruptedException.printStackTrace();
					}
				}
				else if(command[0].compareTo("CLOSE") == 0) {
					this.source.decrementMachineId();
					try {
						// remove closed machine on machine list
						this.source.removeMachine(command[1]);
						this.source.showMachineList();
						output.println("200 - Cikis basarili");
					} catch (InterruptedException interruptedException) {
						interruptedException.printStackTrace();
					}
				}
			}
			// client type 2 - planner
			else {
				
			}
		} while(true);
	}
	
}