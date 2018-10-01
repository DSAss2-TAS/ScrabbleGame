package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ConnectionManager implements Runnable {
	private int playerNumber;
	private String playerName;

	private Socket clientSocket;
	private DataInputStream input;
	private DataOutputStream output;
	private boolean login = false;
	private boolean inHall = false;
	private boolean inRoom = false;
	private boolean ready = false;
	private ServerStatus serverStatus;
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
		synchronized (serverStatus) {

			 if (serverStatus.getClientList().size() > 0) {
				 serverStatus.clientConnected(this);
				 refreshPlayerList();
			 }
			try {
				// The JSON Parser
				JSONParser parser = new JSONParser();
				JSONObject comingCommand;
				String inputStr;
				while ((inputStr = input.readUTF()) != null) {
					Thread.sleep(100);
					comingCommand = (JSONObject) parser.parse(inputStr);
					// if (comingCommand != null) {
					// Attempt to convert read data to JSON
					System.out.println(
							"COMMAND RECEIVED from Client " + playerNumber + ": " + comingCommand.toJSONString());
					parseCommand(comingCommand);
					// }
				}
			} catch (InterruptedException e) {
				System.out.println("InterruptedException: Something wrong when sleep thread.");
			} catch (SocketException e) {
				System.out.println("The Client "+ playerNumber +" is offline.");
			} catch (ParseException e) {
				System.out.println("ParseException when reading command from client.");
			} catch (IOException e) {
				System.out.println("IOException when reading command from client.");
			} 
		}
	}

	@SuppressWarnings("unchecked")
	/////////////////// handleCommand
	private synchronized void parseCommand(JSONObject command) {
		JSONObject replyToClient = new JSONObject();
		switch ((String) (command.get("command"))) {
		case "login":
			setName((String) command.get("username"));
			login = true;
			inHall = true;
			// results.put("login", "success");
			// try {
			// output.writeUTF(results.toJSONString());
			// output.flush();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			break;
		case "newGame":
			game = new Game(playerNumber, this, serverStatus);

			game.startUp();
			replyToClient.put("roomID", game.getRoomID());
			replyToClient.put("CreateRoom", "Success!");
//			serverStatus.clientOffline(this);
			refreshPlayerList();
			try {
				output.writeUTF(replyToClient.toJSONString());
				output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			inHall = false;
			inRoom = true;
			break;
		case "quitRoom":
			joinHall();
			refreshPlayerList();
			break;
		case "playerList":
			refreshPlayerList();
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
				e.printStackTrace();
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
		case "disconnected":
			serverStatus.clientOffline(this);
			refreshPlayerList();
			break;
		}
	}

	public synchronized void broadCast(ArrayList<ConnectionManager> clients, JSONObject command) {
		for (ConnectionManager client : clients) {
			try {
				output.writeUTF(command.toJSONString());
				output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void broadCastInRoom(ConnectionManager[] clients, JSONObject command) {
		for (ConnectionManager client : clients) {
			try {
				output.writeUTF(command.toJSONString());
				output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void refreshPlayerList() {
		try {
			JSONObject results = new JSONObject();
			int index = 0;
			String[] list = new String[100];
			for (ConnectionManager client : serverStatus.getClientList()) {
				list[index] = client.getName();
				index++;
			}
			results.put("command", "REFRESH_PLAYER_LIST");
			results.put("content", list);
			broadCast(serverStatus.getClientList(), results);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public synchronized void setRoomID(int roomID) {
//		this.roomID = roomID;
//	}
//
//	public synchronized int getRoomID() {
//		return roomID;
//	}

	public synchronized void setPlayerNumber(int number) {
		playerNumber = number;
	}

	public synchronized int getPlayerNumber() {
		return playerNumber;
	}

	public synchronized void setName(String name) {
		this.playerName = name;
	}

	public synchronized String getName() {
		return playerName;
	}

	public synchronized void login() {
		login = true;
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
