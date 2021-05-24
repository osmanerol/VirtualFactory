package virtualFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashMap;

public class Resource implements Buffer {
	
	private final Lock accessLock = new ReentrantLock();
	private final Condition canWrite = this.accessLock.newCondition();
	private final Condition canRead = this.accessLock.newCondition();
	private static ArrayList<Machine> machineList = new ArrayList<Machine>();
	private User[] plannerList = {new User("1", "admin1", "password1", false), new User("2", "admin2", "password2", false), new User("3", "admin3", "password3", false)};
	private static ArrayList<Job> jobList = new ArrayList<Job>();
	public String lastId = "1", nextJobId = "1";
	public HashMap<String, Socket> planners = new HashMap<String, Socket>();
	public HashMap<String, Socket> machines = new HashMap<String, Socket>();
	
	public synchronized void addPlanner(String id, Socket planner) throws InterruptedException {
		this.planners.put(id, planner);
		notify();
	}

	public synchronized void addMachine(String id, Socket machine) throws InterruptedException {
		this.machines.put(id, machine);
		notify();
	}
	
	public Socket getPlanner(String id) throws InterruptedException {
		return this.planners.get(id);
	}

	public Socket getMachine(String id) throws InterruptedException {
		return this.machines.get(id);
	}
	
	public synchronized void addMachine(Machine newMachine) throws InterruptedException {
		this.machineList.add(newMachine);
		notify();
	}
	
	public synchronized void removeMachine(String id) throws InterruptedException {
		System.out.println(id);
		int deletedIndex = 0;
		for (int i = 0; i < this.machineList.size(); i++) {
			if(this.machineList.get(i).id == id) {
				deletedIndex = i;
			}
		}
		this.machineList.remove(deletedIndex);
		notify();
	}
	
	public Machine[] getMachineList() throws InterruptedException {
		Machine[] list = this.machineList.toArray(new Machine[this.machineList.size()]);
		return list;
	}
	
	public Machine[] getMachineListWithType(String type) throws InterruptedException {
		ArrayList<Machine> tempMachineList = new ArrayList<Machine>();
		for (int i = 0; i < this.machineList.size(); i++) {
			if(this.machineList.get(i).type.equals(type)) {
				tempMachineList.add(this.machineList.get(i));
			}
		}
		Machine[] list = tempMachineList.toArray(new Machine[tempMachineList.size()]);
		return list;
	}
	
	public synchronized void incrementMachineId() {
		int id = Integer.parseInt(lastId);
		this.lastId = Integer.toString(++id);
		notify();
	}
	
	public synchronized void decrementMachineId() {
		int id = Integer.parseInt(lastId);
		this.lastId = Integer.toString(--id);
		notify();
	}

	public synchronized void incrementJobId() {
		int id = Integer.parseInt(this.nextJobId);
		this.nextJobId = Integer.toString(++id);
		notify();
	}
	
	public String isUserExists(String username, String password) throws InterruptedException {
		for (int i = 0; i < plannerList.length; i++) {
			if(plannerList[i].username.equals(username) && plannerList[i].password.equals(password)) {
				if(plannerList[i].isLoggedIn) {
					return "-1";
				}
				else {
					plannerList[i].isLoggedIn = true;
					return plannerList[i].id;
				}
			}
		}
		return "0";
	}
	
	public synchronized void addJob(Job newJob) throws InterruptedException {
		this.jobList.add(newJob);
		notify();
	}
	
	public String detectMachine(String type) throws InterruptedException {
		boolean isMachineExists = false;
		for (int i = 0; i < this.machineList.size(); i++) {
			if(this.machineList.get(i).type.equals(type) && this.machineList.get(i).status.equals("EMPTY")) {
				this.machineList.get(i).status = "BUSY";
				return String.valueOf(this.machineList.get(i).id);
			}
			if(this.machineList.get(i).type.equals(type)) {
				isMachineExists = true;
			}
		}
		if(isMachineExists) {
			return "0";
		}
		return "-1";
	}

	public Job[] getMachineJobList(String id) throws InterruptedException {
		ArrayList<Job> tempMachineJobList = new ArrayList<Job>();
		for (int i = 0; i < this.jobList.size(); i++) {
			if(this.jobList.get(i).machineId.equals(id)) {
				tempMachineJobList.add(this.jobList.get(i));
			}
		}
		Job[] list = tempMachineJobList.toArray(new Job[tempMachineJobList.size()]);
		return list;
	}

	public Job[] getPendingAllMachineJobList() throws InterruptedException {
		ArrayList<Job> tempMachineJobList = new ArrayList<Job>();
		for (int i = 0; i < this.jobList.size(); i++) {
			if(this.jobList.get(i).status.equals("PENDING")) {
				tempMachineJobList.add(this.jobList.get(i));
			}
		}
		Job[] list = tempMachineJobList.toArray(new Job[tempMachineJobList.size()]);
		return list;
	}
	
	public Job[] getPendingMachineJobList(String type) throws InterruptedException {
		ArrayList<Job> tempMachineJobList = new ArrayList<Job>();
		for (int i = 0; i < this.jobList.size(); i++) {
			if(this.jobList.get(i).type.equals(type) && this.jobList.get(i).status.equals("PENDING")) {
				tempMachineJobList.add(this.jobList.get(i));
			}
		}
		Job[] list = tempMachineJobList.toArray(new Job[tempMachineJobList.size()]);
		return list;
	}
	
	public void logout(String id) throws InterruptedException {
		for (int i = 0; i < plannerList.length; i++) {
			if(plannerList[i].id.equals(id)) {
				plannerList[i].isLoggedIn = false;
			}
		}
	}


}