package virtualFactory;

public class Job {
	String id, plannerId, machineId, type, length, status;
	
	public Job(String id, String plannerId, String machineId, String type, String length, String status) {
		this.id = id;
		this.plannerId = plannerId;
		this.machineId = machineId;
		this.type = type;
		this.length = length;
		this.status = status;
	}

}