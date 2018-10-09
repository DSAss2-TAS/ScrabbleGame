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
		JSONObject request = new JSONObject();
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
				
				request.put("command", "LOGIN");
				request.put("content", playerName);
				
			} else {

				JOptionPane.showMessageDialog(MainFrame.getInstance(), "Sorry, Username is taken. Try another.");

			}
			break;
		case "INSERTING":
			// TODO get direction, X, and Y, and highlight the word line.
			int voteResult=JOptionPane.showConfirmDialog(GameRoom.getInstance(), "Do you agree to give score for this word?", "Vote", JOptionPane.YES_NO_OPTION);
			request.put("command", "VOTE");
			if(voteResult== JOptionPane.YES_OPTION){
				System.out.println("vote yes");
				request.put("content", true);
			}
			else if(voteResult==JOptionPane.NO_OPTION){
				System.out.println("vote no");
				request.put("content", false);
			}
			else {	// voteResult==JOptionPane.CLOSED_OPTION
				JOptionPane.showMessageDialog(GameRoom.getInstance(), "The result would be Horizontal if you close it without selection.");
				request.put("content", true);
			}
			
			break;
		case "ENTER_ROOM":
			String roomID = (String) comingMsg.get("content");
			GameRoom.getInstance(roomID);
			GameRoom.getInstance().initialization();
			GameHall.getInstance().enterRoom();
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
		try {
			output.writeUTF(request.toJSONString());
			output.flush();
			System.out.println(request.toJSONString());
		} catch (IOException ex) {
			System.out.println("Fail to send command to server.");
		}
		return false;

	}

}
