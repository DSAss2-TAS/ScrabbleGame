package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import clientGUI.GameHall;

public class ServerStatus {
	private static ServerStatus instance;
	private final int MAX_CLIENT_NUMBER = 100;
	private final int MAX_ROOM_NUMBER = 50;
	private static ArrayList<ConnectionManager> clientList;
	private static ArrayList<Game> roomList;
	private static ArrayList<String> playersInHall;
	private static ArrayList<Integer> availableRoomID;

	public static ServerStatus getInstance() {
		if (instance == null) {
			instance = new ServerStatus();
		}
		return instance;
	}

	private ServerStatus() {
		clientList = new ArrayList<>();
		roomList = new ArrayList<>();
		playersInHall = new ArrayList<>();
		availableRoomID = new ArrayList<>();
		for (int i = 1; i <= MAX_ROOM_NUMBER; i++) {
			availableRoomID.add(i);

		}
	}

	// client sends valid user name to login into hall
	public synchronized void clientConnected(ConnectionManager client) {
		clientList.add(client);
		playersInHall.add(client.getName());
		refreshPlayerList();
	}

	// client creates a game room as host to start a game
	public synchronized int gameStarted(Game game) {
		System.out.println("Here is status gameStarted");
		roomList.add(game);
		playersInHall.remove(game.getHostName());
		refreshPlayerList();
		return availableRoomID.remove(0);
	}

	// client quits or ends a game from a room and back to hall
	public synchronized void clientQuitGame(Game game) {
		availableRoomID.add(game.getRoomID());
		ConnectionManager[] list = game.getPlayerList();
		for (int i = 0; i < game.getNumberOfPlayers(); i++) {
//			System.out.println("list[0] is: "+list[i].getName());
			playersInHall.add(list[i].getName());

		}
		roomList.remove(game);
		Collections.sort(availableRoomID);
		refreshPlayerList();
	}

	public synchronized void clientOffline(ConnectionManager client) {
		clientList.remove(client);
		playersInHall.remove(client.getName());
		refreshPlayerList();
	}

	public synchronized ArrayList<Game> getRoomList() {
		return roomList;
	}

	public synchronized ArrayList<ConnectionManager> getClientList() {
		return clientList;
	}

	public synchronized ArrayList<String> getPlayerList() {
		return playersInHall;
	}

	public synchronized int getRoomNumber() {
		return roomList.size();
	}

	public void refreshPlayerList() {
		try {
			JSONObject results = new JSONObject();
			JSONArray list = new JSONArray();

			for (String name : playersInHall) {
				// System.out.println("!!refreshPlayerList!! the players are: "
				// + name);
				JSONObject player = new JSONObject();
				player.put("name", name);
				list.add(player);
			}
			// System.out.println("!!refreshPlayerList!! playerlist size is: " +
			// list.size());
			results.put("command", "REFRESH_PLAYER_LIST");
			results.put("content", list);
			broadCast(results);
		} catch (Exception e) {
			System.out.println("ServerStatus: Fail to refresh player list.");
		}
	}

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
