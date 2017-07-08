package pacman.server;
import java.io.*;
import java.net.*;

import pacman.maze.MazeLoader;


public class Server {
	public static final int PORT = 7778;							
	public static final int MAX_CONNECTIONS = 2;					
	public static Clients[] clients = new Clients[MAX_CONNECTIONS]; 
	public static volatile boolean CLOSE_SIGNAL = false;			
	
	// Socket parameters
	static ServerSocket server;
	static DataInputStream input;
	static  DataOutputStream output;
	
	// maze data
	static volatile MazeLoader maze;
	
	
	//static volatile long gameStartTime = -1;
		
	public static void main(String[] args) {
		System.out.println("SERVER");
		
		// Load the Maze
		maze = new MazeLoader();
		
		try {
			
			System.out.println("The server is starting ...");
			server = new ServerSocket(PORT);
			System.out.println("Listening on port " + Integer.toString(PORT));
			
			
			ServerShutdown serverCommands = new ServerShutdown();
			Thread serverCommandsThread = new Thread(serverCommands);
			serverCommandsThread.start();
			
			// Wait for connection
			while(true) {
				// Block until we have a connection
				Socket connection = server.accept();
				boolean foundAvailableSlot = false;
				// Check if there is an available slot		
				for(int i = 0; i < MAX_CONNECTIONS; i++) {
					if(clients[i] == null) {
						// If we have a slot available (less than 2 connected clients) put the client on a separate thread
						foundAvailableSlot = true;
						System.out.println("Received connection request from " + connection.getInetAddress());
						output = new DataOutputStream(connection.getOutputStream());
						input = new DataInputStream(connection.getInputStream());
						
						maze.mazeReset();
						clients[i] = new Clients(output, input, connection, i);
						output.writeUTF("ID," + Integer.toString(i));						
						output.writeUTF(Server.maze.serializeMaze());
						
						Thread thread = new Thread(clients[i]);
						thread.start();
						break;
					}					
				}
				
				
				if(!foundAvailableSlot) {
					connection.close();
				}
			}
		} catch(Exception e) {
			try {
				server.close();
			} catch (IOException e1) {
			}
			System.out.println(e);
		}
	}
}
