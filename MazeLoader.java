package pacman.maze;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/*
 * Maze class - this loads the maze configuration from a text file, processes player movements, updates the scores 
 * and can check if the game is finished (no more dots to "eat")
 */
public class MazeLoader {
	private char[][] maze;
	private int rows;
	private int columns;	
		
	private int totalDots = 0;
	private int noChecksForWinner = 0;
	private long startTime = 0;
	public int elapsedTime = 0;
	private int maxGameTime = 60000;
	
	/*
	 * Player class that stores the player position, score and symbol (character that represents the player in the maze array).
	 */
	class Player {
		// player position in the maze 
		public int row;
		public int col;
		// player score and symbol
		public int scoreID = 0;
		public char symbol;
		
		public Player(char symbol) {
			this.symbol = symbol;
			
			// given the player symbol (character) find the player initial position in the maze
			if(maze != null) {
				for(int i = 0; i < rows; i++) {
					for(int j = 0; j < columns; j++) {
						if(maze[i][j] == symbol) {
							row = i;
							col = j;
							break;
						}
					}
				}
			} 
		}
		
		// Move the character in the given direction
		// if the move is not possible (can't move through walls or the othe player) do nothing
		// if the move is possible update the maze, update the player position and change the score if necessary (only when the player "eats" a dot)
		public void move(String direction) {
			if(direction.equals("left")) {
				char pos = maze[row][col - 1]; 
				if( pos != 'X' && pos != 'R' && pos != 'B') {					
					maze[row][col] = ' ';
					maze[row][col - 1] = symbol;
					col = col - 1;
					if(pos == '.') {
						scoreID++;
					}
				}
			} else if(direction.equals("right")) {
				char pos = maze[row][col + 1]; 
				if( pos != 'X' && pos != 'R' && pos != 'B') {					
					maze[row][col] = ' ';
					maze[row][col + 1] = symbol;
					col = col + 1;
					if(pos == '.') {
						scoreID++;
					}
				}
			} else if(direction.equals("up")) {
				char pos = maze[row - 1][col]; 
				if( pos != 'X' && pos != 'R' && pos != 'B') {					
					maze[row][col] = ' ';
					maze[row - 1][col] = symbol;
					row = row - 1;
					if(pos == '.') {
						scoreID++;
					}
				}
				
			} else if(direction.equals("down")) {
				char pos = maze[row + 1][col]; 
				if( pos != 'X' && pos != 'R' && pos != 'B') {					
					maze[row][col] = ' ';
					maze[row + 1][col] = symbol;
					row = row + 1;
					if(pos == '.') {
						scoreID++;
					}
				}				
			}

		}
	}
	
	Player Red;
	Player Blue;
	
	public MazeLoader() {
		setup();
	}
	
	// initialize the maze
	private void setup() {
		// read the maze elements from a file
		URL url = MazeLoader.class.getResource("maze1.txt");
		List<String> lines = new ArrayList<String>();
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;			
			while((line = bufferedReader.readLine()) != null) {
				columns = line.length();
				lines.add(line);
			}
			bufferedReader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		rows = lines.size();
				
		maze = new char[rows][columns];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				maze[i][j] = lines.get(i).charAt(j);
				if(maze[i][j] == '.') {
					totalDots++;
				}
			}
		}	
		
		// initialize the two players
		Red = new Player('R');
		Blue = new Player('B');				
	}
	
	// Try to move player "id" in the specified direction
	public void movePlayer(String id, String direction) {
		if(id.equals("0")) {
			Blue.move(direction);
		}
		if(id.equals("1")) {
			Red.move(direction);
		}		
	}
	
	// check if the combines scores of the two players equals the total number of dots
	public boolean checkWinner() {
		if(noChecksForWinner == 0) {
			startTime = System.currentTimeMillis();
		} else {
			elapsedTime = (int)(System.currentTimeMillis() - startTime);
		}
		
		noChecksForWinner++;
		
		if(elapsedTime > maxGameTime) {
			return true;
		}
		
		if(totalDots == (Red.scoreID + Blue.scoreID) && totalDots != 0) {
			return true;
		}
		return false;
	}
	
	// reset the maze for a new game
	public void mazeReset() {
		noChecksForWinner = 0;
		elapsedTime = 0;
		startTime = 0;
		totalDots = 0;
		setup();
	}
	
	// print the maze as a 2D array (debugging purposes)
	public void printMaze() {
		System.out.println(Integer.toString(rows) + " rows");
		System.out.println(Integer.toString(columns) + " columns");
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				System.out.print(maze[i][j]);
			}
			System.out.println();
		}		
		System.out.println();
	}
	
	// serialize the maze, the resulting string will contain the scores of the players, the maze dimensions and the actual maze array linearized 
	public String serializeMaze() {
		String flat = "MAZE_CONF" + ",";
		flat += Blue.scoreID + ",";
		flat += Red.scoreID + ",";
		flat += Integer.toString(rows) + "," + Integer.toString(columns) + ",";
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				if(i == rows - 1 && j == columns - 1) {
					flat += maze[i][j];
				} else {
					flat += maze[i][j] + ",";
				}				
			}
		}				
		return flat;
	}

}
