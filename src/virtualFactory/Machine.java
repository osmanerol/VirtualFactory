package virtualFactory;
import org.json.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Machine extends Thread {
	public String id, name, type, status = "EMPTY";
	public float speed;
	public boolean isEmpty = true, isConnected = false;
	private static InetAddress host;
	private static final int PORT = 5000;
	
	public static void main(String[] args) throws IOException, JSONException, NumberFormatException, InterruptedException {
		try {
			host = InetAddress.getLocalHost();
		} catch(UnknownHostException ex) {
			System.out.println("Host id not found.");
			System.exit(1);
		}
		accessServer();
	}
	
	private static void accessServer() throws IOException, JSONException, NumberFormatException, InterruptedException {
		Scanner machineInput = new Scanner(System.in);
		String request, line;
		String[]  command;
		Machine machine = new Machine();
		Socket socket = new Socket(host, PORT);
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		OutputStreamWriter output = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
		JSONObject readerJson = new JSONObject();
		JSONObject writeJson = new JSONObject();
		do {
			clearJsonObject(readerJson);
			clearJsonObject(writeJson);
			System.out.printf("Machine > ");
			request = machineInput.nextLine();
			command = request.split(" ");
			writeJson.put("method", command[0]);
			writeJson.put("type", 1);
			if(command[0].compareTo("CREATE") == 0) {
				if(command.length != 4) {
					//System.out.println("{\"message\":\"Girilen komut hatali\",\"status\":\"400\"}");
					System.out.println("Hatali komut girdiniz.");
				}
				else {
					machine.name = command[1];
					machine.type = command[2];
					machine.speed = Float.parseFloat(command[3]);
					System.out.println("Makine olusturuldu.");
				}
			}
			else if(command[0].compareTo("CONNECT") == 0) {
				JSONObject payload = new JSONObject();
				payload.put("name", machine.name);
				payload.put("type", machine.type);
				payload.put("speed", String.valueOf(machine.speed));
				payload.put("status", machine.status);
				writeJson.put("payload", payload);
				output.write(writeJson.toString() + "\n");
				output.flush();
				line = reader.readLine();
				readerJson = new JSONObject(line);
				JSONObject responsePayload = readerJson.getJSONObject("payload");
				machine.id = responsePayload.getString("id");
				machine.isConnected = true;
				System.out.println(readerJson.toString());
				// after connect request , wait server work message
				do {
					line = reader.readLine();
					readerJson = new JSONObject(line);
					System.out.println(readerJson.toString());
					responsePayload = readerJson.getJSONObject("payload");
					String length = responsePayload.getString("length");
					String jobId = responsePayload.getString("jobId");
					machine.status = "BUSY";
					sleep((long) (Float.parseFloat(length) * machine.speed * 60000));
					machine.status = "EMPTY";
					clearJsonObject(writeJson);
					writeJson.put("method", "FINISH");
					writeJson.put("type", 1);
					payload = new JSONObject();
					payload.put("machineId", machine.id);
					payload.put("jobId", jobId);
					writeJson.put("payload", payload);
					output.write(writeJson.toString() + "\n");
					output.flush();
					line = reader.readLine();
					readerJson = new JSONObject(line);
					System.out.println(readerJson.toString());
				} while(true);
			}
			else {
				output.write(writeJson.toString() + "\n");
				output.flush();
				line = reader.readLine();
				readerJson = new JSONObject(line);
				System.out.println(readerJson.toString());
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