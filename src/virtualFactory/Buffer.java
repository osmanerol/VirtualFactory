package virtualFactory;

import java.net.Socket;

public interface Buffer {
	
	public void addMachine(Machine machine) throws InterruptedException;
	public void removeMachine(String id) throws InterruptedException;
	public Machine[] getMachineList() throws InterruptedException;
	public Machine[] getMachineListWithType(String type) throws InterruptedException;
	public void logout(String id) throws InterruptedException;
	public String isUserExists(String username, String password) throws InterruptedException;
	public void addJob(Job newJob) throws InterruptedException;
	public String detectMachine(String type) throws InterruptedException;
	public Job[] getMachineJobList(String id) throws InterruptedException;
	public Job[] getPendingMachineJobList(String type) throws InterruptedException;
	public Job[] getPendingAllMachineJobList() throws InterruptedException;
	public void addPlanner(String id, Socket planner) throws InterruptedException;
	public void addMachine(String id, Socket machine) throws InterruptedException;
	public Socket getPlanner(String id) throws InterruptedException;
	public Socket getMachine(String id) throws InterruptedException;
	
}