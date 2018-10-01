package game;

import java.io.DataInputStream;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import client.ClientConnectionManager;

public class GameListener implements Runnable{

	private DataInputStream input;
	private JSONParser parser;
	private JSONObject comingMsg;
	public GameListener(){
		input = ClientConnectionManager.getInstance().getInput();
		parser = new JSONParser();
	}
	
	@Override
	public void run() {
		// TODO create listenThread switch case
		String inputStr;
		try {
			while ((inputStr = input.readUTF()) != null) {
				try {
					comingMsg = (JSONObject) parser.parse(inputStr);
				} catch (ParseException e) {
					System.out.println("IOException: Something wrong when parse json command.");
				}
				handleMsg(comingMsg);
			}
		} catch (IOException e) {
			System.out.println("IOException: Something wrong when listening server.");
		}
		
	}
	
	private void handleMsg(JSONObject comingMsg) {
		JSONObject results = new JSONObject();
		switch((String) (comingMsg.get("command"))){
		case "":
		
		}
	
	}

}
