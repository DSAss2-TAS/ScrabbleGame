package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	// client creates a game room as host to start a game
	public synchronized int gameStarted(Game game) {
		roomList.add(game);
		playersInHall.remove(game.getHostName());
		return availableRoomID.remove(0);
	}

	// client quits or ends a game from a room and back to hall
	public synchronized void clientQuitGame(Game game) {
		availableRoomID.add(game.getRoomID());
		playersInHall.add(game.getHostName());
		roomList.remove(game);
		Collections.sort(availableRoomID);
	}


	// client sends user name to login into hall
	public synchronized void clientConnected(ConnectionManager client) {
		clientList.add(client);
		playersInHall.add(client.getName());
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

}
