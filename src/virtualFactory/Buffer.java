package virtualFactory;

public interface Buffer {
	
	public void addMachine(Machine machine) throws InterruptedException;
	public void removeMachine(String id) throws InterruptedException;
	public void showMachineList() throws InterruptedException;
	
}