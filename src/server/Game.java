package server;

public class Game {
	private static int MAXIMUM_PLAYER_NUMBER = 4;
	private static int MINIMUM_PLAYER_NUMBER = 2;
	private int roomID;
	private String hostName;
	private boolean empty = true;
	private boolean full = false;
	private boolean inGame = false;
	private int numberOfPlayers = 0;
	private int voteNumber = 0;
	private int spaceRemain = 400;
	private int turnNumber = 0;
	private int passNumber = 0;
	private ConnectionManager[] clientsInRoom;
	private ServerStatus serverStatus;
	private int players[];

	public Game(int playerNumber, ConnectionManager roomHost, ServerStatus status) {
		players = new int[MAXIMUM_PLAYER_NUMBER];
		players[0] = playerNumber;
		this.serverStatus = status;
		clientsInRoom = new ConnectionManager[MAXIMUM_PLAYER_NUMBER];
		clientsInRoom[0] = roomHost;
	}

	public void startUp() {
		roomID = serverStatus.gameStarted(this);
		hostName = clientsInRoom[0].getName();
		addPlayer(clientsInRoom[0]);
		empty = false;
		numberOfPlayers = 1;
	}

	public String getHostName() {
		return hostName;
	}

	public int getRoomID() {
		return roomID;
	}

	public void addPlayer(ConnectionManager client) {
//		client.setRoomID(roomID);
		players[numberOfPlayers] = client.getPlayerNumber();
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
