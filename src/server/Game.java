package server;

public class Game {
	private static int MAXIMUM_PLAYER_NUMBER = 4;
	private static int MINIMUM_PLAYER_NUMBER = 2;
	private static int MAXIMUM_INSERTION = 400;
	private int roomID;
	// private boolean empty = true;
	private boolean full = false;
	private boolean inGame = false;
	private int numberOfPlayers;
	private int numberOfInsertion;
//	public int indexOfTurn;
	private ConnectionManager[] clientsInRoom;
	public String players[]; // store players' name
	private int numberOfReady;
	public int score[];
	private int passingNumber;
	private int disagreeCount;
	private int votingNumber;
	private boolean votingResult;

	public Game(ConnectionManager roomHost) {
		numberOfPlayers = 0;
		clientsInRoom = new ConnectionManager[MAXIMUM_PLAYER_NUMBER];
		clientsInRoom[0] = roomHost;
		players = new String[MAXIMUM_PLAYER_NUMBER];
		players[0] = roomHost.getName();
		players[1] = "";
		players[2] = "";
		players[3] = "";
		numberOfInsertion = 0;
		// the default first turn is host player
//		indexOfTurn = 0;
		numberOfReady = 0;
		passingNumber = 0;
		disagreeCount = 0;
		votingNumber = 0;
	}

	public void initialization() {

		roomID = ServerStatus.getInstance().getAvailableRoomID();
		numberOfPlayers++;
	}

	public String getHostName() {
		return players[0];
	}

	public int getRoomID() {
		return roomID;
	}

	public int addPlayer(ConnectionManager client) {
		// TODO check if invited player got the roomID
		players[numberOfPlayers] = client.getName();
		clientsInRoom[numberOfPlayers] = client;

		return numberOfPlayers++;
	}

	public boolean pass() {
		passingNumber++;
		if (passingNumber==numberOfPlayers){
			return true;
		}
		return false;
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public ConnectionManager[] getPlayerList() {
		return clientsInRoom;
	}

	public boolean readyToStart() {
		numberOfReady++;
		if (numberOfPlayers >= MINIMUM_PLAYER_NUMBER && numberOfReady == numberOfPlayers) {
			score = new int[numberOfPlayers];
			for (int i : score) {
				i = 0;
			}
			return true;
		} else {
			return false;
		}
	}

	public void insert() {
		passingNumber = 0;
		numberOfInsertion++;
		if (numberOfInsertion==MAXIMUM_INSERTION){
			full = true;
		}
	}

	public synchronized boolean vote(boolean agree) {
		votingNumber++;
		if (!agree) {

			disagreeCount++;
		}
		if (votingNumber == numberOfPlayers) {
			// all players in the game have voted
			if (disagreeCount > 0) {
				votingResult = false;
			} else {
				votingResult = true;
			}
//			if (indexOfTurn >= numberOfPlayers) {
//				indexOfTurn = 0;
//			} else {
//				indexOfTurn++;
//			}
			votingNumber = 0;
			disagreeCount = 0;
			// initialize voting number for future use
			return true;
		}
		return false;

	}
	
	public boolean isFull(){
		return full;
	}
	public boolean getVotingResult(){
		return votingResult;
	}
}
