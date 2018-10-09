package server;

public class Game {
	private static int MAXIMUM_PLAYER_NUMBER = 4;
	private static int MINIMUM_PLAYER_NUMBER = 2;
	private int roomID;
	private int hostID;
//	private boolean empty = true;
	private boolean full = false;
	private boolean inGame = false;
	private int numberOfPlayers;
	private int voteNumber = 0;
	private int spaceRemain = 400;
	private int turnNumber = 0;
	private int passNumber = 0;
	private ConnectionManager[] clientsInRoom;
	private String players[];

	public Game(int playerNumber, ConnectionManager roomHost) {
		numberOfPlayers = 0;
		clientsInRoom = new ConnectionManager[MAXIMUM_PLAYER_NUMBER];
		clientsInRoom[0] = roomHost;
		players = new String[MAXIMUM_PLAYER_NUMBER];
		players[0] = roomHost.getName();
		hostID = playerNumber;
//		empty = false;
	}

	public void startUp() {
		System.out.println("Here is game startUp");
		roomID = ServerStatus.getInstance().gameStarted(this);
		numberOfPlayers++;
	}

	public String getHostName() {
		return players[0];
	}

	public int getRoomID() {
		return roomID;
	}

	public void addPlayer(ConnectionManager client) {
//		client.setRoomID(roomID);
		players[numberOfPlayers] = client.getName();
		clientsInRoom[numberOfPlayers] = client;
		numberOfPlayers++;
	}

	public void removePlayer() {

	}

	// public void insert(ConnectionManager client) {
	// if(players.indexOf(playerNumber) = )
	// }
	// public boolean vote(int playerNumber, boolean yesOrNo) {
	//
	// }
	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public ConnectionManager[] getPlayerList() {
		return clientsInRoom;
	}
}
