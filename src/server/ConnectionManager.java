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
	private int indexInRoom;
	private String inputStr;
	public int score;

	public ConnectionManager(Socket socket, int number, ServerStatus status) throws IOException {

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
		}

	}

	@SuppressWarnings("unchecked")
	/////////////////// handleCommand
	private synchronized boolean parseCommand(JSONObject command) {
		JSONObject replyToClient = new JSONObject();
		switch ((String) (command.get("command"))) {
		case "SET_NAME":
			inputStr = (String) command.get("content");
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

			break;
		case "NEW_GAME":
			game = new Game(this);
			game.initialization();
			serverStatus.clientJoinGame(playerName);
			// indicate this client is the host of the game
			indexInRoom = 0;
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
			inputStr = (String) command.get("content");
			// TODO player 1 quit game, broadcast to other players
			replyToClient.put("command", "SOMEONE_QUIT");
			replyToClient.put("content", inputStr);
			broadCastInRoom(game, replyToClient);
			// joinHall();
			break;
		case "playerList":
			// refreshPlayerList();
			break;
		case "INVITE":
			inputStr = (String) command.get("content");
			int index = serverStatus.getManager(inputStr);
			replyToClient.put("command", "INVITE");
			if (game.getNumberOfPlayers() == 4) {
				replyToClient.put("content", "The room is full!");
			} else if (!serverStatus.getPlayerList().contains(inputStr)) {
				if (index == -1) {
					replyToClient.put("content", "The player does not exist!");
				} else {
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

		case "READY": // TODO client listen GAME START
			if (game.readyToStart()) {
				replyToClient.put("command", "GAME_START");
				broadCastInRoom(game, replyToClient);
			}
			break;
		case "INSERT":
			game.insert();
			// char c = (char) command.get("content");
			// boolean direction = (boolean) command.get("direction");
			// int row = (int) command.get("row");
			// int column = (int) command.get("column");
			// replyToClient.put("command", "Inserting");
			// replyToClient.put("content", c);
			// replyToClient.put("direction", direction);
			broadCastInRoom(game, command);
			break;
		case "VOTE":
			boolean choice = (boolean) command.get("content");

			if (game.vote(choice)) {
				replyToClient.put("command", "VotingResult");
				replyToClient.put("content", game.getVotingResult());
				broadCastInRoom(game, replyToClient);
			}

			break;

		case "pass":
			if (game.pass()) {
				replyToClient.put("command", "GAME_OVER");
				// TODO send the winner and his score to all players
				// replyToClient.put("content", game.getVotingResult());
				broadCastInRoom(game, replyToClient);
			}
			break;
		// TODO client receive voting result
		case "CHANGE_SCORE":
			score = (int) command.get("content");
			game.score[indexInRoom] += score;
			if (game.isFull()) {
				replyToClient.put("command", "GameOver");
				try {
					output.writeUTF(replyToClient.toJSONString());
					output.flush();
				} catch (IOException e) {
					System.out.println("Something wrong when send EXIT APPROVED message.");
				}
				serverStatus.clientQuitGame(game);
			}
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
		indexInRoom = game.addPlayer(this);
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
