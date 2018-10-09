package server;


public class Game {
	private static int MAXIMUM_PLAYER_NUMBER = 4;
	private static int MINIMUM_PLAYER_NUMBER = 2;
	private int roomID;
	private int hostID;
//	private boolean empty = true;
	public boolean full = false;
	private boolean inGame = false;
	private int numberOfPlayers;
	private int voteNumber = 0;
	private int spaceRemain = 400;
	private int turnNumber = 0;
	public int numberOfTurn = 0;
	private ConnectionManager[] clientsInRoom;
	public String players[];
	public int score[];
	public int numberOfReady = 0;
	public ConnectionManager currentPlayer;
	ServerStatus server;
	//ConnectionManager[] clients;
	public boolean votingTable[];
	public boolean passingTable[];
	boolean voting = false;
	int votingNumber = 0;
	public int passingNumber = 0;
	boolean playing = true;
	boolean votingResult = true;


	public Game(int playerNumber, ConnectionManager roomHost) {
		numberOfPlayers = 0;
		clientsInRoom = new ConnectionManager[MAXIMUM_PLAYER_NUMBER];
		clientsInRoom[0] = roomHost;
		players = new String[MAXIMUM_PLAYER_NUMBER];
		players[0] = roomHost.getName();
		players[1]="";
		players[2]="";
		players[3]="";
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
	public boolean insert(ConnectionManager client) {
		if(client == clientsInRoom[numberOfTurn]) {
			if(passingNumber != 0) {
			passingNumber = 0;
			}
			return true;
		}
		else {
			return false;
		}
	}
	public boolean start() {
		if(numberOfPlayers <= 1) {
			return false;
		}
		else {
		
			score = new int[numberOfPlayers];
			for(int i : score) {
				i = 0;
			}
			return true;
		}
	}
	public synchronized void vote(int index, boolean choice){
		if(voting = true) {
			if (votingNumber < (numberOfPlayers)) {
				votingTable[index] = choice;
				votingNumber++;
			}
			else {
				votingTable[index] = choice;
				for(boolean result : votingTable) {
					if (result = false) {
						votingResult = false;
						break;
					}
					numberOfTurn++;
					voting = false;
					votingNumber = 0;
				}
				
			}
		}
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
	public void changeScore(int scores) {
		score[getIndex(currentPlayer)] += scores;
		currentPlayer = null;
		numberOfTurn++;
		if(numberOfTurn==(numberOfPlayers)) {
			//currentPlayer = client;
			numberOfTurn = 0;
		}
	}
	public synchronized void finish() {
		
	}
	public synchronized int getIndex(ConnectionManager client) {
		for(int i =0; i < numberOfPlayers; i++) {
			if (clientsInRoom[i].equals(client)) {
				return i;
			}
		}
		return -1;
	}
}
