package game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import client.ClientConnectionManager;
import clientGUI.GameHall;
import clientGUI.GameRoom;
import clientGUI.MainFrame;

public class GameListener implements Runnable {

	private DataOutputStream output;
	private DataInputStream input;
	private JSONParser parser;
	private JSONObject comingMsg;
	private String inputStr;
	private String playerName;
	private boolean endListener;

	public GameListener(DataInputStream input, DataOutputStream output) {
		this.input = input;
		this.output = output;
		parser = new JSONParser();
		playerName = "";
		endListener = false;
	}

	@Override
	public void run() {

		try {
			while (!endListener && (inputStr = input.readUTF()) != null) {
				comingMsg = (JSONObject) parser.parse(inputStr);
				System.out.println("MESSAGE RECEIVED: " + comingMsg.toJSONString());
				endListener = handleMsg(comingMsg);
			}
		} catch (ParseException e) {
			System.out.println("ParseException: Something wrong when Listener parse json command.");
		} catch (IOException e) {
			System.out.println("Oops, Server shutdown.");
		}
		System.exit(0);

	}

	// TODO create listenThread switch case
	private boolean handleMsg(JSONObject comingMsg) {
		switch ((String) (comingMsg.get("command"))) {
		case "REFRESH_PLAYER_LIST":
			GameHall.getInstance().refreshArea((JSONArray) comingMsg.get("content"));
			break;
		case "SET_NAME":
			if (((String) comingMsg.get("content")).equals("SUCCESS")) {
				// username is available
				playerName = (String) comingMsg.get("username");
				ClientConnectionManager.getInstance().setUsername(playerName);
				JOptionPane.showMessageDialog(MainFrame.getInstance(),
						"Hi, " + playerName + ", Welcome to the Game Hall!");
				MainFrame.getInstance().gameHallStartUp();
				JSONObject request = new JSONObject();
				request.put("command", "LOGIN");
				request.put("content", playerName);
				try {

					output.writeUTF(request.toJSONString());
					output.flush();
					System.out.println("SET_NAME: " + request.toJSONString());
				} catch (IOException ex) {
					System.out.println("Fail to send LOGIN command to server.");
				}
			} else {

				JOptionPane.showMessageDialog(MainFrame.getInstance(), "Sorry, Username is taken. Try another.");

			}
			break;
		case "ENTER_ROOM":
			String roomID = (String) comingMsg.get("content");
			
			break;
		case "QUIT":
			JOptionPane.showMessageDialog(GameRoom.getInstance(), "Going back to Game Hall!");
			GameRoom.getInstance().dispose();
			GameHall.getInstance().backToHall();
			GameRoom.getInstance().delete();
			break;
		case "EXIT":
			JOptionPane.showMessageDialog(MainFrame.getInstance(), "Goodbye!");
			// pass STOP message to listener
			return true;
			// exit program successfully
		}
		return false;

	}

}
