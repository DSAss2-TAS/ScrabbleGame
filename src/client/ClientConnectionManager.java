package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ClientConnectionManager {

	private static ClientConnectionManager instance;
	private Socket clientSocket;
	private String username;
	private DataInputStream input;
    private DataOutputStream output;
    private String inputStr;
	
	public static ClientConnectionManager getInstance() {
		return instance;
	}

	public static ClientConnectionManager getInstance(Socket clientSocket) {
		instance = new ClientConnectionManager(clientSocket);
		return instance;
	}

	private ClientConnectionManager(Socket clientSocket) {
		this.clientSocket = clientSocket;
		username = "";
		try {
			output = new DataOutputStream(clientSocket.getOutputStream());
	        input = new DataInputStream(clientSocket.getInputStream());
//			inputStr = input.readUTF();
	        
	        Runnable listener = new Runnable() {
				@Override
				public void run() {
					JSONObject comingCommand;
					JSONParser parser = new JSONParser();
					input = ClientConnectionManager.getInstance().getInput();
					try {
						while ((inputStr = input.readUTF()) != null) {
							comingCommand = (JSONObject) parser.parse(inputStr);
							switch ((String) (comingCommand.get("command"))) {
							case "REFRESH_PLAYER_LIST":
								System.out.println("going to refresh player list");
								break;
								
							case "ENTER_ROOM":
								System.out.println("going to obtain room ID");
								
								break;
//							case "REFRESH_PLAYER_LIST":
//								break;
//							case "REFRESH_PLAYER_LIST":
//								break;
//							case "REFRESH_PLAYER_LIST":
//								break;
//								
							}
							
							
							
							
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			Thread t = new Thread(listener);
			t.start();
		} catch (IOException ex) {
			System.out.println("IOException: Something wrong when define Reader and Writer in Login Window!");
		}
	}

	public Socket getClientSocket() {
		return clientSocket;

	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername(){
		return username;
	}
	public DataInputStream getInput(){
		return input;
	}
	
	public DataOutputStream getOutput(){
		return output;
	}
}
