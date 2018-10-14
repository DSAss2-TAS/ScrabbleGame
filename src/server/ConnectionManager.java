package server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//The ConnectionManager class is for each connected clients to use the thread of the server,
//and get or send various commands, with basically all useful functions. 

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
	private String inputStr;
	public int score;

	public ConnectionManager(Socket socket, int number, ServerStatus status) throws IOException {
		// Constructor
		clientSocket = socket;
		playerNumber = number;
		serverStatus = status;
		output = new DataOutputStream(clientSocket.getOutputStream());
		input = new DataInputStream(clientSocket.getInputStream());
	}

	@Override
	public void run() {

		try {
			// The JSON Parser
			JSONParser parser = new JSONParser();
			JSONObject comingCommand;
			String inputStr;
			while (!endListener && (inputStr = input.readUTF()) != null) {
				Thread.sleep(100);
				comingCommand = (JSONObject) parser.parse(inputStr);
				// Attempt to convert read data to JSON
				System.out
						.println("COMMAND RECEIVED from Client " + playerNumber + ": " + comingCommand.toJSONString());
				endListener = parseCommand(comingCommand);
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
		} finally {
			serverStatus.clientOffline(this);
		}

	}

	@SuppressWarnings("unchecked")
	// the switch case to handle request from client
	private synchronized boolean parseCommand(JSONObject command) {
		JSONObject replyToClient = new JSONObject();
		switch ((String) (command.get("command"))) {

		case "SET_NAME": // The client trying to set their names
			inputStr = (String) command.get("content"); // Get the String of inputed username
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
				output.flush(); // Send the JSON object command to client
			} catch (IOException e) {
				System.out.println("Something wrong when reply SET_NAME to client.");
			}
			break;
		case "LOGIN":
			playerName = ((String) command.get("content"));
			serverStatus.clientConnected(this);

			break;
		case "NEW_GAME": // For the client who wants to create a game room
			game = new Game(this);
			game.initialization(); // Initialize the room
			serverStatus.clientJoinGame(playerName); // Join the room, and vanish from the client list of game hall
			// indicate this client is the host of the game
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

		case "INVITE": // The client trying to invite another player to play in this room
			inputStr = (String) command.get("content"); // get the name of this player
			int index = serverStatus.getManager(inputStr); // Get the index of the invited client from the ArrayList
			replyToClient.put("command", "INVITE");
			if (game.getNumberOfPlayers() == 4) { // Reach the maximum player number
				replyToClient.put("content", "The room is full!");
			} else if (!serverStatus.getPlayerList().contains(inputStr)) {
				if (index == -1) {// If -1, then this client does not exist
					replyToClient.put("content", "The player does not exist!");
				} else { // Then this client is already in other rooms
					replyToClient.put("content", "The player is not avaliable!");
				}
			} else {
				replyToClient.put("content", "Invite " + inputStr + " Successfully!");
				serverStatus.getClientList().get(index).joinRoom(game);
			}
			try {
				output.writeUTF(replyToClient.toJSONString());
				output.flush();
			} catch (IOException e) {
				System.out.println("Something wrong when invite clients.");
			}
			break;

		case "READY":
			if (game.readyToStart()) {
				replyToClient.put("command", "GAME_START");
				broadCastInRoom(game, replyToClient);
			}
			break;

		case "PLACE_CHAR": // The client trying to insert a character
			game.addLetter();
			broadCastInRoom(game, command);
			break;

		case "VOTE": // Voting
			boolean choice = (boolean) command.get("content");

			if (game.vote(choice)) {
				replyToClient.put("command", "VOTING_RESULT");
				replyToClient.put("content", game.getVotingResult());
				broadCastInRoom(game, replyToClient);
				if (game.isFull()) { // all 400 tiles are filled
					serverStatus.clientQuitGame(game);
					game = null;
				}
			}

			break;

		case "PASS": // Pass
			if (game.pass()) { // If true, then all players passed.
				serverStatus.clientQuitGame(game);
				replyToClient.put("command", "ALL_PASS");
				broadCastInRoom(game, replyToClient);
				game = null;
			} else {
				broadCastInRoom(game, command);
			}
			break;

		case "QUIT": // One player pressed close button wants to quit the game room
			serverStatus.clientQuitGame(game);
			inputStr = (String) command.get("content");
			replyToClient.put("command", "SOMEONE_QUIT");
			replyToClient.put("content", inputStr);
			broadCastInRoom(game, replyToClient);
			game = null;
			break;

		case "EXIT":
			if (inRoom) {
				JSONObject replyToAll = new JSONObject();
				serverStatus.clientQuitGame(game);
				replyToAll.put("command", "SOMEONE_QUIT");
				replyToAll.put("content", playerName);
				broadCastInRoom(game, replyToAll);
			}
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

	// For sending messages to all players in room
	public synchronized void broadCastInRoom(Game game, JSONObject command) {
		ConnectionManager[] clients = game.getPlayerList(); // Get the ArrayList of all clients.
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

	// Get the number of the client
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

	// the client is invited by someone else to join a game
	public synchronized void joinRoom(Game game) {
		inRoom = true;
		inHall = false;
		this.game = game;
		serverStatus.clientJoinGame(playerName);
		JSONObject replyToClient = new JSONObject();
		replyToClient.put("command", "INVITED");
		replyToClient.put("content", Integer.toString(game.getRoomID()));
		replyToClient.put("host", game.getHostName());
		replyToClient.put("player1", game.players[1]);
		// could be null if client is the second player
		replyToClient.put("player2", game.players[2]); // could be null
		game.addPlayer(this);
		try {
			output.writeUTF(replyToClient.toJSONString());
			output.flush();
		} catch (IOException e) {
			System.out.println("Something wrong when broadcast in room to player: " + this.getName());
		}
		JSONObject replyToAll = new JSONObject();
		replyToAll.put("command", "NEW_PLAYER");
		replyToAll.put("content", playerName);
		broadCastInRoom(game, replyToAll);
	}

	public synchronized void leaveRoom() {
		inRoom = false;
		inHall = true;
	}

}
