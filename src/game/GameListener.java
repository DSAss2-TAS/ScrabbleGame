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
	private String roomID;
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
				try {
					output.writeUTF(request.toJSONString());
					output.flush();
					System.out.println(request.toJSONString());
				} catch (IOException ex) {
					System.out.println("Fail to send command to server.");
				}
				
			} else {

				JOptionPane.showMessageDialog(MainFrame.getInstance(), "Sorry, Username is taken. Try another.");

			}
			break;
		
		case "ENTER_ROOM":
			// client creates a game room as host
			roomID = (String) comingMsg.get("content");
			GameRoom.getInstance(roomID, playerName, playerName);
			GameRoom.getInstance().initialization();
			GameHall.getInstance().enterRoom();
			break;
			
		case "INVITED":
			// client enters a game room as guest
			roomID = (String) comingMsg.get("content");
			String hostName = (String) comingMsg.get("host");
			GameRoom.getInstance(roomID, hostName, playerName);
			GameRoom.getInstance().initialization();
			GameRoom.getInstance().setPlayers((String) comingMsg.get("player1"), (String) comingMsg.get("player2"));
			GameHall.getInstance().enterRoom();
			break;
			
		case "INVITE":
			String result = (String) comingMsg.get("content");
			JOptionPane.showMessageDialog(GameRoom.getInstance(), result);
			break;
			
		case "NEW_PLAYER":
			GameRoom.getInstance().addNewPlayer((String) comingMsg.get("content"));
			break;
			
		case "GAME_START":
			GameRoom.getInstance().gameStart();
			
			break;
			
		case "PLACE_CHAR":
			// TODO determine where to put the countWord.
			boolean horizontal = ((String) comingMsg.get("direction")).equals("horizontal");
			int row = (int)((long) comingMsg.get("row"));
			int column = (int) ((long)comingMsg.get("column"));
			GameRoom.getInstance().countWord((String) comingMsg.get("content"), row,column,horizontal);
			int vote=JOptionPane.showConfirmDialog(GameRoom.getInstance(), "Do you agree to give score for this word?", "Vote", JOptionPane.YES_NO_OPTION);
			request.put("command", "VOTE");
			if(vote== JOptionPane.YES_OPTION){
				request.put("content", true);
			}
			else if(vote==JOptionPane.NO_OPTION){
				request.put("content", false);
			}
			else {	// voteResult==JOptionPane.CLOSED_OPTION
				JOptionPane.showMessageDialog(GameRoom.getInstance(), "The result would be Horizontal if you close it without selection.");
				request.put("content", true);
			}
			try {
				output.writeUTF(request.toJSONString());
				output.flush();
				System.out.println(request.toJSONString());
			} catch (IOException ex) {
				System.out.println("Fail to send VOTE command to server.");
			}
			break;
			
		case "VOTING_RESULT":
			boolean votingResult = (boolean) comingMsg.get("content");
			if(votingResult){
				JOptionPane.showMessageDialog(GameRoom.getInstance(), "Cheers, All accept the word!");
				// TODO do something to change the score!
				GameRoom.getInstance().refreshScore();
			}else{
				JOptionPane.showMessageDialog(GameRoom.getInstance(), "Oops, Someone disagree.");
			}
			GameRoom.getInstance().alternateTurn();
			break;
			
		case "PASS":
			GameRoom.getInstance().alternateTurn();
			break;
			
		case "ALL_PASS":
			GameRoom.getInstance().getWinnerScore();
			break;
			
		case "SOMEONE_QUIT":
			String playerName = (String) comingMsg.get("content");
			JOptionPane.showMessageDialog(GameRoom.getInstance(), "Player " + playerName + " quits. Going back to Game Hall!");
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
