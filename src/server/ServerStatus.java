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

	public synchronized int gameStarted(Game game) {
		roomList.add(game);
		playersInHall.remove(game.getHostName());
		return availableRoomID.remove(0);
	}

	public synchronized void clientQuitGame(Game game) {
		roomList.remove(game);
		availableRoomID.add(game.getRoomID());
		Collections.sort(availableRoomID);
//		playersInHall.add(client.getName());
	}

	public synchronized ArrayList<Game> getRoomList() {
		return roomList;
	}

	public synchronized void clientConnected(ConnectionManager client) {
		clientList.add(client);
		playersInHall.add(client.getName());
	}

	public synchronized void clientOffline(ConnectionManager client) {
		clientList.remove(client);
		playersInHall.remove(client.getName());
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
