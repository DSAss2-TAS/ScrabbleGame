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
	public int score;
	private boolean myTurn;
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
			System.out.println("SocketException: Client " + playerNumber + " lost connection.");
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
			indexInRoom = game.getIndex(this);
			// TODO client receive command ENTER ROOM
			replyToClient.put("command", "ENTER_ROOM");
			//replyToClient.put("content", game.getRoomID());
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
			replyToClient.put("command", "SOMEONE_QUIT");
			replyToClient.put("content", "APPROVED");
			broadCastInRoom(game, replyToClient);
//			joinHall();
			break;
		case "playerList":
			// refreshPlayerList();
			break;
		case "invite":
			String name = (String)command.get("playerName");
			int index = serverStatus.getManager(name);
			if (game.getNumberOfPlayers() >= 4) {
				replyToClient.put("command", "Invite");
				replyToClient.put("content", "The room is full!");
			}
			else if (!serverStatus.getPlayerList().contains(name)) {
				replyToClient.put("command", "Invite");
				replyToClient.put("content", "The player is not avaliable!");
			}
			else if(index == -1) {
				replyToClient.put("command", "Invite");
				replyToClient.put("content", "The player is not avaliable!");
			}
			else {
				replyToClient.put("command", "Invite");
				replyToClient.put("content", "Success!");
				serverStatus.getClientList().get(index).joinRoom(game);
			}
			try{
				output.writeUTF(replyToClient.toJSONString());
				output.flush();
			}catch (IOException e){
                e.printStackTrace();
            }
			break;
		case "ready":
			game.numberOfReady++;
			ready();
			if(game.numberOfReady == game.getNumberOfPlayers()) {
				if(game.start()) {
					replyToClient.put("command", "Start!");
					try{
						output.writeUTF(replyToClient.toJSONString());
						output.flush();
					}catch (IOException e){
		                e.printStackTrace();
		            }
				}
			}
			break;
		case "insert":
			if(game.passingTable != null) {
				game.passingTable = null;
			}
			game.insert(this);
			char c = (char)command.get("content");
			boolean direction = (boolean)command.get("direction");
			replyToClient.put("command", "Inserting");
			replyToClient.put("content", c);
			replyToClient.put("direction", direction);
			broadCastInRoom(game, replyToClient);
			break;
		case "vote":
			if (game.voting == false) {
				game.voting = true;
				game.votingTable = new boolean [game.getNumberOfPlayers()];
				game.vote(indexInRoom, (boolean)command.get("content"));
			}
			else{
				game.vote(indexInRoom, (boolean)command.get("content"));
				if (game.voting == false) {
					replyToClient.put("command", "VotingResult");
					replyToClient.put("content", game.votingResult);
					broadCastInRoom(game, replyToClient);
					game.votingNumber = 0;
					game.votingResult = true;
				}
			}
			break;
		case "full":
			game.full = true;
		case "changeScore":
			score = (int)command.get("content");
			game.score[indexInRoom] += score;
			if (game.full = true) {
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
		case "pass":
			if(game.passingTable == null) {
				game.passingTable = new boolean[game.getNumberOfPlayers()];
				game.passingTable[game.numberOfTurn] = true;
				game.passingNumber = 1;
			}
			else {
				if(game.passingNumber < 3) {
					game.passingTable[game.numberOfTurn] = true;
					game.passingNumber++;
				}
				else {
					replyToClient.put("command", "GameOver");
					try {
						output.writeUTF(replyToClient.toJSONString());
						output.flush();
					} catch (IOException e) {
						System.out.println("Something wrong when send EXIT APPROVED message.");
					}
					serverStatus.clientQuitGame(game);
				}
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

	public synchronized void joinRoom(Game game) {
		game.addPlayer(this);
		this.game = game;
		inRoom = true;
		inHall = false;
		indexInRoom = game.getIndex(this);
		JSONObject replyToClient = new JSONObject();
		replyToClient.put("command", "Invited");
		try {
			output.writeUTF(replyToClient.toJSONString());
			output.flush();
		} catch (IOException e) {
			System.out.println("Something wrong when broadcast in room to player: " + this.getName());
		}
//		JSONArray 
		JSONObject replyToAll = new JSONObject();
		replyToAll.put("command", "NewInRoom");
//		String player[] = new String[game.getNumberOfPlayers()];
		replyToAll.put("player0", game.players[0]);
		replyToAll.put("player1", game.players[1]);
		replyToAll.put("player2", game.players[2]);
		replyToAll.put("player3", game.players[3]);
		broadCastInRoom(game, replyToAll);
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
