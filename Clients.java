package pacman.server;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
//import java.net.*;
import java.net.Socket;
import javax.swing.Timer;

/*
 * Clients class, this manages a client messages
 */
public class Clients implements Runnable, ActionListener {
	private DataOutputStream output;
	private DataInputStream input;
	private Clients[] clients;
	public Socket connection;
	private int id;
	private Timer timer;
	private boolean gameFinished = false;
	private int playAgain = 0;
	
	public Clients(DataOutputStream output, DataInputStream input, Socket connection, int id) {
		this.output = output;
		this.input = input;
		this.clients = Server.clients;
		this.connection = connection;
		this.id = id;	
		
		// Every 200 milliseconds run actionPerformed 
		timer = new Timer(200, this);
		timer.start();
	}

	// check if the client is online and listens for move commands (move the client ID in the given direction if possible, see MazeLoader.java)
	@Override
	public void run() {		
		while(true) {			
			try {
				String message = input.readUTF();
				// If a client asks to disconnect remove it from the clients array
				if(message.equals("CLOSE_SIGNAL")) {
					System.out.println("Wants to close");
					System.out.println("Client " + Integer.toString(id) + " signed out!");
					connection.close();
					clients[id] = null;
					return;
				}
				// Process players (client) move 
				if(message.contains("CLIENT_MOVE")) {
					String [] pieces = message.split(",");
					Server.maze.movePlayer(pieces[2], pieces[3]);
					
					for(int i = 0; i < Server.MAX_CONNECTIONS; i++) {
						if(clients[i] != null) {
							synchronized(clients[i].output) {
								clients[i].output.writeUTF(Server.maze.serializeMaze());
							}
						}
					}
				}
				//
				if(message.equals("ASK_PLAY")) {
					clients[id].playAgain = 1;
				}
				if(message.equals("ASK_NO_PLAY")) {
					clients[id].playAgain = 2;
					
					System.out.println("Client " + Integer.toString(id) + " does not want to play again.\nBoth clients will be disconnected!");
					clients[0].connection.close();
					clients[1].connection.close();
					clients[0] = null;
					clients[1] = null;
				}
				
			} catch (IOException e) {
				// If a client is accidentally disconnected remove it from the clients array
				clients[id] = null;
				System.out.println("Client " + Integer.toString(id) + " connection was lost!");
				return;				
			}
		}		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// check how many clients are connected
		int connectedClients = 0;
		for(int i = 0; i < Server.MAX_CONNECTIONS; i++) {
			if(clients[i] != null) {
				connectedClients++;
			}
		}
		
		if(connectedClients == 2) {
			if(clients[0].gameFinished == false && clients[1].gameFinished == false && clients[0].playAgain == 1 && clients[1].playAgain == 1) {
				clients[0].playAgain = 0;
				clients[1].playAgain = 0;
			}
		}
		
		// If there are 2 connected clients and the game is not finished signal GAME_ON. For two connected clients and game finished signal GAME_FINISHED
		// and let the clients know that a new game will start soon
		// If there is a single client connected signal GAME_PAUSE
		
		for(int i = 0; i < Server.MAX_CONNECTIONS; i++) {
			if(clients[i] != null) {
				try {
					if(connectedClients == 2) {
						synchronized(clients[i].output) {
							if(clients[i].gameFinished && clients[0].playAgain == 1 && clients[1].playAgain == 1) {
							//if(clients[i].gameFinished && clients[i].playAgain == 1) {
								//clients[i].playAgain = 0;
								clients[i].gameFinished = false;
								Server.maze.mazeReset();								
								clients[i].output.writeUTF("GAME_RESET");
								//clients[i].output.writeUTF("GAME_PAUSE");
								clients[i].output.writeUTF(Server.maze.serializeMaze());								
								continue;								
							}
							if(Server.maze.checkWinner()) {	
								clients[i].output.writeUTF("GAME_FINISHED");
								clients[i].gameFinished = true;
							} else {								
								clients[i].output.writeUTF("GAME_ON," + Integer.toString(Server.maze.elapsedTime));
							}
						}
					} else {
						synchronized(clients[i].output) {
							clients[i].output.writeUTF("GAME_PAUSE");
						}
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				}
			}
		}		
	}
		
}
