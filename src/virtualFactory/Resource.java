package virtualFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.ArrayList;

public class Resource implements Buffer {
	
	private final Lock accessLock = new ReentrantLock();
	private final Condition canWrite = this.accessLock.newCondition();
	private final Condition canRead = this.accessLock.newCondition();
	private ArrayList<Machine> machineList = new ArrayList<Machine>();
	public String lastId = "1";
	
	public void addMachine(Machine newMachine) throws InterruptedException {
		this.machineList.add(newMachine);
		System.out.println("200 - Makine listeye eklendi");
	}
	
	public void removeMachine(String id) throws InterruptedException {
		System.out.println(id);
		int deletedIndex = 0;
		for (int i = 0; i < this.machineList.size(); i++) {
			if(this.machineList.get(i).id == id) {
				deletedIndex = i;
			}
		}
		this.machineList.remove(deletedIndex);
		System.out.println("200 - Makine kaldirildi");
	}
	
	public void showMachineList() throws InterruptedException {
		System.out.printf("%s\t\t%s\t\t%s\t\t%s\t\t%s\n", "Id", "Ad", "Tur", "Uretim Hizi", "Durum");
		System.out.println("------------------------------------------------------------------------------------------");
		for(int i = 0; i < this.machineList.size(); i++) {
			Machine tempMachine = this.machineList.get(i);
			System.out.printf("%s\t\t%s\t\t%s\t\t%f\t\t%s\n", tempMachine.id, tempMachine.name, tempMachine.type, tempMachine.speed, tempMachine.status);
		}
	}
	
	public void incrementMachineId() {
		int id = Integer.parseInt(lastId);
		this.lastId = Integer.toString(++id);
	}
	
	public void decrementMachineId() {
		int id = Integer.parseInt(lastId);
		this.lastId = Integer.toString(--id);
	}
	
}