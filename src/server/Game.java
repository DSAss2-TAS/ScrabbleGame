package server;

//The Game class is for running a individual game.
public class Game {
	private static int MAXIMUM_PLAYER_NUMBER = 4;
	private static int MINIMUM_PLAYER_NUMBER = 2;
	private static int MAXIMUM_INSERTION = 400;
	private int roomID;
	// private boolean empty = true;
	private boolean full = false;
	private boolean inGame = false;
	private int numberOfPlayers;
	private int numberOfLetter; // How many letters inserted, if reaches 400 then terminate the game
	private ConnectionManager[] clientsInRoom;
	public String players[]; // store players' name
	private int numberOfReady;
	private int passingNumber;// How many players have passed in aturn
	private int disagreeCount; // How many disagree votes in a turn
	private int votingNumber; // How many players have voted in a turn
	private boolean votingResult;

	// Constructor
	public Game(ConnectionManager roomHost) {
		numberOfPlayers = 0;
		clientsInRoom = new ConnectionManager[MAXIMUM_PLAYER_NUMBER];
		clientsInRoom[0] = roomHost;
		players = new String[MAXIMUM_PLAYER_NUMBER];
		players[0] = roomHost.getName();
		players[1] = "";
		players[2] = "";
		players[3] = "";
		numberOfLetter = 0;
		numberOfReady = 0;
		passingNumber = 0;
		disagreeCount = 0;
		votingNumber = 0;
		// the host player plays the first turn by default.
	}

	// Initialize a game
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
		players[numberOfPlayers] = client.getName();
		clientsInRoom[numberOfPlayers] = client;
		return numberOfPlayers++;
	}

	// If returns true, then the game should be terminated.
	public boolean pass() {
		passingNumber++;
		if (passingNumber == numberOfPlayers) {
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

	// Count how many players are ready, and start the game when all ready
	public boolean readyToStart() {
		numberOfReady++;
		if (numberOfPlayers >= MINIMUM_PLAYER_NUMBER && numberOfReady == numberOfPlayers) {

			return true;
		} else {
			return false;
		}
	}

	public void addLetter() {
		passingNumber = 0;
		numberOfLetter++;
		if (numberOfLetter == MAXIMUM_INSERTION) {
			full = true; // If full, terminate the game
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

			votingNumber = 0;
			disagreeCount = 0;
			// initialize voting number for future use
			return true;
		}
		return false;

	}

	public boolean isFull() {
		return full;
	}

	public boolean getVotingResult() {
		return votingResult;
	}
}
