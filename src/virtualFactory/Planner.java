package virtualFactory;
import org.json.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Planner extends Thread {
	private String id, username, password;
	private boolean isLoggedIn = false;
	private static InetAddress host;
	private static final int PORT = 5000;
	
	public static void main(String[] args) throws IOException, JSONException {
		try {
			host = InetAddress.getLocalHost();
		} catch(UnknownHostException ex) {
			System.out.println("Host id not found.");
			System.exit(1);
		}
		accessServer();
	}
	
	private static void accessServer() throws IOException, JSONException {
		Scanner plannerInput = new Scanner(System.in);
		String request, line, status;
		// komutlarý dizi olarak tutacak
		String[]  command;
		Planner planner = new Planner();
		Socket socket = new Socket(host, PORT);
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		OutputStreamWriter output = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
		JSONObject readerJson = new JSONObject();
		JSONObject writeJson = new JSONObject();
		do {
			clearJsonObject(readerJson);
			clearJsonObject(writeJson);
			System.out.printf("Planner > ");
			request = plannerInput.nextLine();
			command = request.split(" ");
			writeJson.put("method", command[0]);
			writeJson.put("type", 2);
			if(command[0].compareTo("LOGIN") == 0) {
				if(command.length != 3) {
					System.out.println("Hatali komut girdiniz.");
				}
				else {
					if(!planner.isLoggedIn) {
						planner.username = command[1];
						planner.password = command[2];
						JSONObject payload = new JSONObject();
						payload.put("username", planner.username);
						payload.put("password", planner.password);
						writeJson.put("payload", payload);
						output.write(writeJson.toString() + "\n");
						output.flush();
						line = reader.readLine();
						readerJson = new JSONObject(line);
						status = readerJson.getString("status");
						if(readerJson.getString("status").compareTo("202") == 0) {
							JSONObject responsePayload = readerJson.getJSONObject("payload");
							planner.id = responsePayload.getString("id");
							planner.isLoggedIn = true;
							System.out.println(readerJson.toString());
						}
						else {
							System.out.println(readerJson.toString());
						}
					}
					else {
						System.out.println("Zaten oturum actiniz.");
					}
				}
			}
			else if(command[0].compareTo("LOGIN") != 0) {
				if(planner.isLoggedIn) {
					if(command[0].compareTo("LIST") == 0) {
						if(command.length != 1) {
							System.out.println("Hatali komut girdiniz.");
						}
						else {
							output.write(writeJson.toString() + "\n");
							output.flush();
							line = reader.readLine();
							readerJson = new JSONObject(line);
							JSONObject responsePayload = readerJson.getJSONObject("payload");
							status = readerJson.getString("status");
							JSONArray list = responsePayload.getJSONArray("list");
							System.out.printf("%s\t\t%s\t\t%s\t\t%s\t\t%s\n", "Id", "Ad", "Tur", "Uretim Hizi", "Durum");
							System.out.println("------------------------------------------------------------------------------------------");
							for (int i = 0; i < list.length(); i++) {
								JSONObject machine = (JSONObject) list.get(i);
								System.out.printf("%s\t\t%s\t\t%s\t\t%s\t\t\t%s\n", machine.get("id"), machine.get("name"), machine.get("type"), machine.get("speed"), machine.get("status"));	
							}
						}
					} 
					else if( command[0].compareTo("TYPE") == 0) {
						if(command.length != 2) {
							System.out.println("Hatali komut girdiniz.");
						}
						else {
							JSONObject payload = new JSONObject();
							payload.put("type", command[1]);
							writeJson.put("payload", payload);
							output.write(writeJson.toString() + "\n");
							output.flush();
							line = reader.readLine();
							readerJson = new JSONObject(line);
							JSONObject responsePayload = readerJson.getJSONObject("payload");
							status = readerJson.getString("status");
							JSONArray list = responsePayload.getJSONArray("list");
							System.out.printf("%s\t\t%s\t\t%s\t\t%s\t\t%s\n", "Id", "Ad", "Tur", "Uretim Hizi", "Durum");
							System.out.println("------------------------------------------------------------------------------------------");
							for (int i = 0; i < list.length(); i++) {
								JSONObject machine = (JSONObject) list.get(i);
								System.out.printf("%s\t\t%s\t\t%s\t\t%s\t\t\t%s\n", machine.get("id"), machine.get("name"), machine.get("type"), machine.get("speed"), machine.get("status"));	
							}
						}
					} 
					else if(command[0].compareTo("CLOSE") == 0) {
						if(command.length != 1) {
							System.out.println("Hatali komut girdiniz.");
						}
						else {
							JSONObject payload = new JSONObject();
							payload.put("id", planner.id);
							writeJson.put("payload", payload);
							output.write(writeJson.toString() + "\n");
							output.flush();
							line = reader.readLine();
							readerJson = new JSONObject(line);
							System.out.println(readerJson.toString());
						}
					}
					else if(command[0].compareTo("CREATE") == 0) {
						if(command.length != 3) {
							System.out.println("Hatali komut girdiniz.");
						}
						else {
							JSONObject payload = new JSONObject();
							payload.put("type", command[1]);
							payload.put("length", command[2]);
							payload.put("planner", planner.id);
							writeJson.put("payload", payload);
							output.write(writeJson.toString() + "\n");
							output.flush();
							line = reader.readLine();
							readerJson = new JSONObject(line);
							System.out.println(readerJson.toString());
						}
					}
					else if(command[0].compareTo("STATUS") == 0) {
						if(command.length != 2) {
							System.out.println("Hatali komut girdiniz.");
						}
						else {
							JSONObject payload = new JSONObject();
							payload.put("machineId", command[1]);
							writeJson.put("payload", payload);
							output.write(writeJson.toString() + "\n");
							output.flush();
							line = reader.readLine();
							readerJson = new JSONObject(line);
							JSONObject responsePayload = readerJson.getJSONObject("payload");
							JSONArray list = responsePayload.getJSONArray("list");
							System.out.printf("%s\t\t%s\t\t%s\t\t%s\t\t%s\t\t%s\n", "Id", "Planlamacý Id", "Makine Id", "Tur", "Uzunluk", "Durum");
							System.out.println("-------------------------------------------------------------------------------------------------------------------------");
							for (int i = 0; i < list.length(); i++) {
								JSONObject job = (JSONObject) list.get(i);
								System.out.printf("%s\t\t%s\t\t\t%s\t\t\t%s\t\t%s\t\t%s\n", job.get("id"), job.get("plannerId"), job.get("machineId"), job.get("type"), job.get("length"), job.get("status"));	
							}
						}
					}
					else if(command[0].compareTo("PENDING") == 0) {
						if(command.length != 2) {
							System.out.println("Hatali komut girdiniz.");
						}
						else {
							JSONObject payload = new JSONObject();
							payload.put("type", command[1]);
							writeJson.put("payload", payload);
							output.write(writeJson.toString() + "\n");
							output.flush();
							line = reader.readLine();
							readerJson = new JSONObject(line);
							JSONObject responsePayload = readerJson.getJSONObject("payload");
							JSONArray list = responsePayload.getJSONArray("list");
							System.out.printf("%s\t\t%s\t\t%s\t\t%s\t\t%s\t\t%s\n", "Id", "Planlamacý Id", "Makine Id", "Tur", "Uzunluk", "Durum");
							System.out.println("-------------------------------------------------------------------------------------------------------------------------");
							for (int i = 0; i < list.length(); i++) {
								JSONObject job = (JSONObject) list.get(i);
								System.out.printf("%s\t\t%s\t\t\t%s\t\t\t%s\t\t%s\t\t%s\n", job.get("id"), job.get("plannerId"), job.get("machineId"), job.get("type"), job.get("length"), job.get("status"));	
							}
						}
					}
					else {
						output.write(writeJson.toString() + "\n");
						output.flush();
						line = reader.readLine();
						readerJson = new JSONObject(line);
						System.out.println(readerJson.toString());
					}
				}
				else {
					System.out.println("Once oturum acmalisiniz.");
				}
			}
		} while(command[0].compareTo("CLOSE") != 0);
		socket.close();
		System.exit(1);
	}
	
	private static void clearJsonObject(JSONObject jsonObject) {
		while(jsonObject.length()>0)
			jsonObject.remove((String) jsonObject.keys().next());
	}
	
}