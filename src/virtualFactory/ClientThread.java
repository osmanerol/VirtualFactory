package virtualFactory;
import java.io.*;
import java.net.*;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClientThread extends Thread {
	
	private Socket client;
	private Resource source;
	private BufferedReader reader;
	private OutputStreamWriter output;
	
	public ClientThread(Socket client, Resource source) {
		this.client = client;
		this.source = source;
		try {
			reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
			output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
		} catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void run() {
		String nextId, line, machineType, length, plannerId, machineId;
		String[] command;
		Machine newMachine;
		String method;
		int type;
		JSONObject readerJson = new JSONObject();
		JSONObject writeJson = new JSONObject();
		Machine[] machineList;
		Job[] jobList;
		JSONObject payload;
		JSONArray list;
		try {
			do {
				clearJsonObject(readerJson);
				clearJsonObject(writeJson);
				line = reader.readLine();
				readerJson = new JSONObject(line);
				method = readerJson.getString("method");
				type = readerJson.getInt("type");
				// machine request
				if(type == 1) {
					switch(method) {
						case "CONNECT" :
							nextId = this.source.lastId;
							this.source.incrementMachineId();
							payload = readerJson.getJSONObject("payload");
							newMachine = new Machine();
							newMachine.id = nextId;
							newMachine.name= payload.getString("name");
							newMachine.type = payload.getString("type");
							newMachine.speed = Float.parseFloat(payload.getString("speed"));
							newMachine.status = payload.getString("status");
							this.source.addMachine(newMachine);
							writeJson.put("status", "203");
							writeJson.put("message", "Makine eklendi");
							JSONObject writePayload = new JSONObject();
							writePayload.put("id", String.valueOf(nextId));
							writeJson.put("payload", writePayload);
							output.write(writeJson.toString() + "\n");
							output.flush();
							// add machineList
							this.source.addMachine(nextId, this.client);
							break;
						case "FINISH" :
							payload = readerJson.getJSONObject("payload");
							machineId = payload.getString("machineId");
							String jobId = payload.getString("jobId");
							this.source.updateJob(jobId);
							this.source.updateMachineList(machineId);
							Socket client = this.source.getMachine(machineId);
							writeJson.put("status", "200");
							writeJson.put("message", "Ýs tamamlandi. Durum güncellendi");
							OutputStreamWriter finishOutput = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
							finishOutput.write(writeJson.toString() + "\n");
							finishOutput.flush();
							// is any pending job with same type
							String[] job = this.source.getPendingJob(machineId);
							if(job != null) {
								writeJson.put("status", "200");
								writeJson.put("message", "Ýs baslatildi");
								writePayload = new JSONObject();
								writePayload.put("jobId", job[0]);
								writePayload.put("length", job[1]);
								writeJson.put("payload", writePayload);
								finishOutput.write(writeJson.toString() + "\n");
								finishOutput.flush();
							}
							break;
						case "CLOSE" :
							this.source.decrementMachineId();
							writeJson.put("status", "200");
							writeJson.put("message", "Ýstek basarili");
							output.write(writeJson.toString() + "\n");
							output.flush();
							break;
						default:
							writeJson.put("status", "400");
							writeJson.put("message", "Girilen istek bulunamadi");
							output.write(writeJson.toString() + "\n");
							output.flush();
							break;
					}
				}
				// planner request
				else {
					switch(method) {
						case "LOGIN" :
							JSONObject readPayload = readerJson.getJSONObject("payload");
							String username = readPayload.getString("username");
							String password = readPayload.getString("password");
							String id = this.source.isUserExists(username, password);
							if(id.equals("0")) {
								writeJson.put("status", "401");
								writeJson.put("message", "Kullanici adi veya sifre hatali");
							}
							else if(id.equals("-1")){
								writeJson.put("status", "401");
								writeJson.put("message", "Bu bilgiler ile oturum acilmis");
							}
							else {
								writeJson.put("status", "202");
								writeJson.put("message", "Giris basarili");
								payload = new JSONObject();
								payload.put("id", id);
								writeJson.put("payload", payload);
								// add planerList
								this.source.addPlanner(id, this.client);
							}
							output.write(writeJson.toString() + "\n");
							output.flush();
							break;
						case "LIST" :
							machineList = this.source.getMachineList();
							writeJson.put("status", "200");
							writeJson.put("message", "Ýstek basarili");
							payload = new JSONObject();
							list = new JSONArray();
							for (int i = 0; i < machineList.length; i++) {
								JSONObject machine = new JSONObject();
								machine.put("id", machineList[i].id);
								machine.put("name", machineList[i].name);
								machine.put("type", machineList[i].type);
								machine.put("speed", machineList[i].speed);
								machine.put("status", machineList[i].status);
								list.put(machine);
							}
							payload.put("list", list);
							writeJson.put("payload", payload);
							output.write(writeJson.toString() + "\n");
							output.flush();
							break;
						case "TYPE" :
							readPayload = readerJson.getJSONObject("payload");
							machineType = readPayload.getString("type");
							machineList = this.source.getMachineListWithType(machineType);
							writeJson.put("status", "200");
							writeJson.put("message", "Ýstek basarili");
							payload = new JSONObject();
							list = new JSONArray();
							for (int i = 0; i < machineList.length; i++) {
								JSONObject machine = new JSONObject();
								machine.put("id", machineList[i].id);
								machine.put("name", machineList[i].name);
								machine.put("type", machineList[i].type);
								machine.put("speed", machineList[i].speed);
								machine.put("status", machineList[i].status);
								list.put(machine);
							}
							payload.put("list", list);
							writeJson.put("payload", payload);
							output.write(writeJson.toString() + "\n");
							output.flush();
							break;
						case "CREATE" : 
							readPayload = readerJson.getJSONObject("payload");
							machineType = readPayload.getString("type");
							length = readPayload.getString("length");
							machineType = readPayload.getString("type");
							plannerId =  readPayload.getString("planner");
							machineId = this.source.detectMachine(machineType);
							if(machineId.equals("0")) {
								writeJson.put("status", "200");
								writeJson.put("message", "Bosta makine yok. Emir listeye eklendi");
								Job job = new Job(this.source.nextJobId, plannerId, machineId, machineType, length, new String("PENDING"));
								this.source.addJob(job);
								output.write(writeJson.toString() + "\n");
								output.flush();
							}
							else if(machineId.equals("-1")) {
								writeJson.put("status", "405");
								writeJson.put("message", "Girilen tipte makine bulunamadi");
								output.write(writeJson.toString() + "\n");
								output.flush();
							}
							else {
								Job job = new Job(this.source.nextJobId, plannerId, machineId, machineType, length, new String("STARTED"));
								this.source.addJob(job);
								writeJson.put("status", "200");
								writeJson.put("message", "Ýs baslatildi");
								output.write(writeJson.toString() + "\n");
								output.flush();
								Socket client = this.source.getMachine(machineId);
								OutputStreamWriter finishOutput = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
								writeJson.put("status", "200");
								writeJson.put("message", "Ýs baslatildi");
								JSONObject writePayload = new JSONObject();
								writePayload.put("length", String.valueOf(length));
								writePayload.put("jobId", this.source.nextJobId);
								writeJson.put("payload", writePayload);
								finishOutput.write(writeJson.toString() + "\n");
								finishOutput.flush();
							}
							this.source.incrementJobId();
							break;
						case "STATUS":
							readPayload = readerJson.getJSONObject("payload");
							machineId = readPayload.getString("machineId");
							jobList = this.source.getMachineJobList(machineId);
							payload = new JSONObject();
							list = new JSONArray();
							for (int i = 0; i < jobList.length; i++) {
								JSONObject job = new JSONObject();
								job.put("id", jobList[i].id);
								job.put("plannerId", jobList[i].plannerId);
								job.put("machineId", jobList[i].machineId);
								job.put("type", jobList[i].type);
								job.put("length", jobList[i].length);
								job.put("status", jobList[i].status);
								list.put(job);
							}
							payload.put("list", list);
							writeJson.put("payload", payload);
							output.write(writeJson.toString() + "\n");
							output.flush();
							break;
						case "PENDING":
							readPayload = readerJson.getJSONObject("payload");
							machineType = readPayload.getString("type");
							if(machineType.equals("all")) {
								jobList = this.source.getPendingAllMachineJobList();
								payload = new JSONObject();
								list = new JSONArray();
								for (int i = 0; i < jobList.length; i++) {
									JSONObject job = new JSONObject();
									job.put("id", jobList[i].id);
									job.put("plannerId", jobList[i].plannerId);
									job.put("machineId", jobList[i].machineId);
									job.put("type", jobList[i].type);
									job.put("length", jobList[i].length);
									job.put("status", jobList[i].status);
									list.put(job);
								}
								payload.put("list", list);
								writeJson.put("payload", payload);
								output.write(writeJson.toString() + "\n");
								output.flush();
							}
							else {
								jobList = this.source.getPendingMachineJobList(machineType);
								payload = new JSONObject();
								list = new JSONArray();
								for (int i = 0; i < jobList.length; i++) {
									JSONObject job = new JSONObject();
									job.put("id", jobList[i].id);
									job.put("plannerId", jobList[i].plannerId);
									job.put("machineId", jobList[i].machineId);
									job.put("type", jobList[i].type);
									job.put("length", jobList[i].length);
									job.put("status", jobList[i].status);
									list.put(job);
								}
								payload.put("list", list);
								writeJson.put("payload", payload);
								output.write(writeJson.toString() + "\n");
								output.flush();
							}
							break;
						case "CLOSE" :
							readPayload = readerJson.getJSONObject("payload");
							id = readPayload.getString("id");
							this.source.logout(id);
							writeJson.put("status", "200");
							writeJson.put("message", "Ýstek basarili");
							output.write(writeJson.toString() + "\n");
							output.flush();
							break;
						default:
							writeJson.put("status", "400");
							writeJson.put("message", "Girilen istek bulunamadi");
							output.write(writeJson.toString() + "\n");
							output.flush();
							break;
					}
				}
			} while(method.compareTo("CLOSE") != 0);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (JSONException jsonException) {
			jsonException.printStackTrace();
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}
	}

	private static void clearJsonObject(JSONObject jsonObject) {
		while(jsonObject.length()>0)
			jsonObject.remove((String) jsonObject.keys().next());
	}
	
}