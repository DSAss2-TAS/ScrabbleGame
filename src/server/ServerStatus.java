package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

//The ServerStatus class is for manage game and client instances, providing broadcast functions.
public class ServerStatus {
	private static ServerStatus instance;
	private final int MAX_CLIENT_NUMBER = 100;
	private final int MAX_ROOM_NUMBER = 50;
	private static ArrayList<ConnectionManager> clientList; // Load all connected clients
	private static ArrayList<String> playersInHall;
	private static ArrayList<Integer> availableRoomID;

	// For getting the only one static instance of the game
	public static ServerStatus getInstance() {
		if (instance == null) {
			instance = new ServerStatus();
		}
		return instance;
	}

	// Constructor
	private ServerStatus() {
		clientList = new ArrayList<>();
		// roomList = new ArrayList<>();
		playersInHall = new ArrayList<>();
		availableRoomID = new ArrayList<>();
		for (int i = 1; i <= MAX_ROOM_NUMBER; i++) {
			availableRoomID.add(i);

		}
	}

	// For getting the index of the client with a unique name
	public int getManager(String name) {
		int index;
		for (ConnectionManager client : clientList) {
			if (client.getName().equals(name)) {
				return clientList.indexOf(client);
			}
		}
		return -1;
	}

	// client sends valid user name to login into hall
	public synchronized void clientConnected(ConnectionManager client) {
		clientList.add(client);
		playersInHall.add(client.getName());
		refreshPlayerList();
	}

	// For joining the game room
	public synchronized void clientJoinGame(String playerName) {
		playersInHall.remove(playerName);
		refreshPlayerList();
	}

	// client creates a game room as host
	public synchronized int getAvailableRoomID() {
		return availableRoomID.remove(0);
	}

	// client quits or ends a game from a room and back to hall
	public synchronized void clientQuitGame(Game game) {
		availableRoomID.add(game.getRoomID());
		ConnectionManager[] list = game.getPlayerList();
		for (int i = 0; i < game.getNumberOfPlayers(); i++) {
			playersInHall.add(list[i].getName());

		}
		Collections.sort(availableRoomID);
		refreshPlayerList();
	}

	// Invoked when the client quit the hall
	public synchronized void clientOffline(ConnectionManager client) {
		clientList.remove(client);
		playersInHall.remove(client.getName());
		refreshPlayerList(); // Tell other clients.
	}

	// Get the client list in hall
	public synchronized ArrayList<ConnectionManager> getClientList() {
		return clientList;
	}

	// Get the client list in game room
	public synchronized ArrayList<String> getPlayerList() {
		return playersInHall;
	}

	// For broadcasting the client list when changed
	public void refreshPlayerList() {
		try {
			JSONObject results = new JSONObject();
			JSONArray list = new JSONArray();

			for (String name : playersInHall) {
				JSONObject player = new JSONObject();
				player.put("name", name);
				list.add(player);
			}
			results.put("command", "REFRESH_PLAYER_LIST");
			results.put("content", list);
			broadCast(results);
		} catch (Exception e) {
			System.out.println("ServerStatus: Fail to refresh player list.");
		}
	}

	// Broadcasting for general usage
	public synchronized void broadCast(JSONObject command) {
		DataOutputStream output;
		System.out.println("ServerStatus broadCast: " + command.toJSONString());
		for (ConnectionManager client : clientList) {
			try {
				output = client.getOutputStream();
				output.writeUTF(command.toJSONString());
				output.flush();
			} catch (IOException e) {
				System.out.println("Something wrong when update playerlist to client: " + client.getName());
			}
		}
	}
}
