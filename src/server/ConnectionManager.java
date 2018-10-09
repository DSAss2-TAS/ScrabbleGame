package server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ConnectionManager implements Runnable {
	private int playerNumber;
	private String playerName;

	private Socket clientSocket;
	private DataInputStream input;
	private DataOutputStream output;
	private boolean endListener = false;
	private boolean inHall = false;
	private boolean inRoom = false;
	private boolean ready = false;
	private static ServerStatus serverStatus;
	private Game game;

	public ConnectionManager(Socket socket, int number, ServerStatus status) throws IOException {

		clientSocket = socket;
		playerNumber = number;
		serverStatus = status;
		output = new DataOutputStream(clientSocket.getOutputStream());
		input = new DataInputStream(clientSocket.getInputStream());
	}

	@Override
	public void run() {
		// synchronized (serverStatus) {

		try {
			// The JSON Parser
			JSONParser parser = new JSONParser();
			JSONObject comingCommand;
			String inputStr;
			while (!endListener && (inputStr = input.readUTF()) != null) {
				Thread.sleep(100);
				comingCommand = (JSONObject) parser.parse(inputStr);
				// if (comingCommand != null) {
				// Attempt to convert read data to JSON
				System.out
						.println("COMMAND RECEIVED from Client " + playerNumber + ": " + comingCommand.toJSONString());
				endListener = parseCommand(comingCommand);
				// }
			}
			output.close();
			input.close();
			clientSocket.close();
		} catch (InterruptedException e) {
			System.out.println("InterruptedException: Something wrong when sleep thread.");
		} catch (SocketException e) {
			System.out.println("Oops, Client " + playerNumber + " lost connection.");
		} catch (ParseException e) {
			System.out.println("ParseException when reading command from client.");
		} catch (IOException e) {
			System.out.println("IOException when reading command from client.");
			// }
		}
		
		
	}

	@SuppressWarnings("unchecked")
	/////////////////// handleCommand
	private synchronized boolean parseCommand(JSONObject command) {
		JSONObject replyToClient = new JSONObject();
		switch ((String) (command.get("command"))) {
		case "SET_NAME":
			String inputStr = (String) command.get("content");
				if (serverStatus.getPlayerList().contains(inputStr)) {
					replyToClient.put("command", "SET_NAME");
					replyToClient.put("content", "FAIL");
					replyToClient.put("username", inputStr);
				} else {
					replyToClient.put("command", "SET_NAME");
					replyToClient.put("content", "SUCCESS");
					replyToClient.put("username", inputStr);
				}
			try {
				output.writeUTF(replyToClient.toJSONString());
				output.flush();
			} catch (IOException e) {
				System.out.println("Something wrong when reply SET_NAME to client.");
			}
			break;
		case "LOGIN":
			playerName = ((String) command.get("content"));
			serverStatus.clientConnected(this);
			// login = true;
			// inHall = true;
			// results.put("login", "success");
			// try {
			// output.writeUTF(results.toJSONString());
			// output.flush();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			break;
		case "NEW_GAME":
			game = new Game(playerNumber, this);
			game.startUp();
			replyToClient.put("command", "ENTER_ROOM");
			replyToClient.put("content", Integer.toString(game.getRoomID()));
			try {
				output.writeUTF(replyToClient.toJSONString());
				output.flush();
			} catch (IOException e) {
				System.out.println("Something wrong when send room ID to host.");
			}
			inHall = false;
			inRoom = true;
			break;
		case "QUIT":
			serverStatus.clientQuitGame(game);
			// TODO player 1 quit game, broadcast to other players 
			replyToClient.put("command", "QUIT");
			replyToClient.put("content", "APPROVED");
			broadCastInRoom(game, replyToClient);
//			joinHall();
			break;
		case "playerList":
			// refreshPlayerList();
			break;
		case "invite":
			int index = (int) command.get("playerIndex");
			String name = (String) command.get("playerName");
			if (game.getNumberOfPlayers() >= 4) {
				replyToClient.put("Invite", "Sorry, The room is full!");
			} else if (serverStatus.getClientList().get(index).getName() != name) {
				replyToClient.put("Invite", "Sorry, Cannot find this player online!");
			} else if (serverStatus.getClientList().get(index).isInHall() == false) {
				replyToClient.put("Invite", "Sorry, The player is already in a game!");
			} else {
				serverStatus.getClientList().get(index).joinRoom();
				replyToClient.put("Invite", "Success!");
			}
			try {
				output.writeUTF(replyToClient.toJSONString());
				output.flush();
			} catch (IOException e) {
				System.out.println("Something wrong when invite clients.");
			}
			break;
		case "ready":
			ready = true;
			ready();
			break;
		// case "insert":
		// broadCastInRoom(game.getPlayerList(), command);
		// break;
		case "vote":
			break;

		case "pass":
			break;
		case "EXIT":
			// if client already login with valid user name .
			if (command.get("content") != "") {
				serverStatus.clientOffline(this);
			}
			// if client exits before send username to server, don't update list.
			replyToClient.put("command", "EXIT");
			replyToClient.put("content", "APPROVED");
			try {
				output.writeUTF(replyToClient.toJSONString());
				output.flush();
			} catch (IOException e) {
				System.out.println("Something wrong when send EXIT APPROVED message.");
			}
			return true;
		}
		return false;
	}

	public synchronized void broadCastInRoom(Game game, JSONObject command) {
		ConnectionManager[] clients = game.getPlayerList();
		DataOutputStream output;
		for (int i = 0; i < game.getNumberOfPlayers(); i++) {
			try {
				output = clients[i].getOutputStream();
				output.writeUTF(command.toJSONString());
				output.flush();
			} catch (IOException e) {
				System.out.println("Something wrong when broadcast in room to player: " + clients[i].getName());
			}
		}
	}

	// public synchronized void setRoomID(int roomID) {
	// this.roomID = roomID;
	// }
	//
	// public synchronized int getRoomID() {
	// return roomID;
	// }

	public synchronized int getPlayerNumber() {
		return playerNumber;
	}

	public synchronized String getName() {
		return playerName;
	}

	public synchronized DataOutputStream getOutputStream() {
		return output;
	}

	public synchronized boolean isInHall() {
		return inHall;
	}

	public synchronized void joinHall() {
		inHall = true;
		inRoom = false;
	}

	public synchronized void leaveHall() {
		inHall = false;
	}

	public synchronized void joinRoom() {
		inRoom = true;
		inHall = false;
	}

	public synchronized void leaveRoom() {
		inRoom = false;
		inHall = true;
	}

	public synchronized void ready() {
		ready = true;
	}

	public synchronized void notReady() {
		ready = false;
	}

}
