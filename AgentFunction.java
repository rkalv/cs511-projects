import java.util.ArrayList;

class AgentFunction {
	private String agentName = "Agent Smith";
	private boolean bump, glitter, breeze, stench, scream;
	private String[][] worldState;
	private int worldSize = 4;
	private int[] currentLocation;
	private boolean arrowUsed = false;
	private char direction = 'E';
	private boolean wumpusAlive = true;
	private ArrayList<Integer> actionQueue = new ArrayList<>();

	private void updateCoordinates()
	{
		if(direction == 'E')
		{
			if(currentLocation[1] + 1 < 4)
				currentLocation[1]++;
		}

		if(direction == 'W')
		{
			if(currentLocation[1] - 1 >= 0)
				currentLocation[1]--;
		}

		if(direction == 'N')
		{
			if(currentLocation[0] - 1 >= 0)
				currentLocation[0]--;
		}

		if(direction == 'S')
		{
			if(currentLocation[0] + 1 < 4)
				currentLocation[0]++;
		}
	}

	private void printWorldState(){
		System.out.println("--------------------------------------------");
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				System.out.print("| " + worldState[i][j]);
			}
			System.out.println("|");
		}
		System.out.println("--------------------------------------------");
	}

	public AgentFunction() {
		worldState = new String[worldSize][worldSize];
		currentLocation = new int[]{3, 0};

		for (int i = 0; i < worldSize; i++) {
			for (int j = 0; j < worldSize; j++) {
				worldState[i][j] = States.UNKNOWN;
			}
		}
		worldState[currentLocation[0]][currentLocation[1]] = States.SAFE;
	}

	public int process(TransferPercept tp) {
		glitter = tp.getGlitter();

		if (glitter) {
			return Action.GRAB;
		}

		if (!actionQueue.isEmpty()) {
			return actionQueue.remove(0);
		}
		return computeNewActions(tp);
	}

	private int computeNewActions(TransferPercept tp) {
		updateState(tp);
		printWorldState();
		// If a stench is detected and the Wumpus is alive, try to shoot
		if (stench && !arrowUsed && wumpusAlive) {
			arrowUsed = true;
			return Action.SHOOT;
		}

		if(stench && arrowUsed && wumpusAlive){
			return Action.NO_OP;
		}

		if (breeze) {
			if (currentLocation[0] == 0 && currentLocation[1] == 0){
				return Action.NO_OP;
			}
			// Step 1: Find an adjacent unknown square
			int[] nextUnknown = findAdjacentUnknown();
			if (nextUnknown != null) {
				generateMoveSequence(nextUnknown);
				return actionQueue.remove(0);
			}

			// Step 2: Backtrack to previous safe position
			// Step 2: Move back to the previous safe location
			int[] previousLocation = getPreviousLocation();
			if (previousLocation != null) {
				generateMoveSequence(previousLocation);
				return actionQueue.remove(0);
			}3



			// Step 3: If no unknown squares exist, do nothing
			return Action.NO_OP;
		}

		if (bump) {
			handleBump();
			return Action.TURN_LEFT;
		}

		// Explore nearest unknown cell that is NOT adjacent to a pit
		int[] nextUnknown = findLeastRiskyUnknown();
		if (nextUnknown != null) {
			generateMoveSequence(nextUnknown);
			return actionQueue.remove(0);
		}


		updateCoordinates();
		return Action.GO_FORWARD;
	}

	private int[] getPreviousLocation() {
		int x = currentLocation[0];
		int y = currentLocation[1];

		// Check where the agent came from (opposite of the current direction)
		if (direction == 'N' && isValid(x + 1, y)) return new int[]{x + 1, y}; // Came from below
		if (direction == 'S' && isValid(x - 1, y)) return new int[]{x - 1, y}; // Came from above
		if (direction == 'E' && isValid(x, y - 1)) return new int[]{x, y - 1}; // Came from left
		if (direction == 'W' && isValid(x, y + 1)) return new int[]{x, y + 1}; // Came from right

		return null; // No valid previous location found
	}

	private int[] findAdjacentUnknown() {
		int x = currentLocation[0];
		int y = currentLocation[1];

		int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // Right, Down, Left, Up

		for (int[] dir : directions) {
			int nx = x + dir[0], ny = y + dir[1];
			if (isValid(nx, ny) && worldState[nx][ny].equals(States.UNKNOWN)) {
				return new int[]{nx, ny};
			}
		}

		return null; // No unknown squares found
	}

	private int[] findLeastRiskyUnknown() {
		int x = currentLocation[0];
		int y = currentLocation[1];

		int[][] directions = { {0, 1}, {1, 0}, {0, -1}, {-1, 0} }; // Right, Down, Left, Up
		int[] bestMove = null;
		int minPitRisk = Integer.MAX_VALUE;

		for (int[] dir : directions) {
			int nx = x + dir[0];
			int ny = y + dir[1];

			if (isValid(nx, ny) && worldState[nx][ny].equals(States.UNKNOWN)) {
				int pitRisk = countAdjacentPitPossible(nx, ny);
				if (pitRisk < minPitRisk) {
					minPitRisk = pitRisk;
					bestMove = new int[]{nx, ny};
				}
			}
		}

		return bestMove;
	}
	private void handleBump() {
		System.out.println("Bump detected at (" + currentLocation[0] + "," + currentLocation[1] + ") facing " + direction);

		// Handle Corners
		if (currentLocation[0] == 0 && currentLocation[1] == worldSize - 1) { // Top-right
			if (direction == 'N') turnAndMove('W');
			else if (direction == 'E') turnAndMove('S');
		} else if (currentLocation[0] == worldSize - 1 && currentLocation[1] == worldSize - 1) { // Bottom-right
			if (direction == 'S') turnAndMove('W');
			else if (direction == 'E') turnAndMove('N');
		} else if (currentLocation[0] == 0 && currentLocation[1] == 0) { // Top-left
			if (direction == 'N') turnAndMove('E');
			else if (direction == 'W') turnAndMove('S');
		} else if (currentLocation[0] == worldSize - 1 && currentLocation[1] == 0) { // Bottom-left
			if (direction == 'S') turnAndMove('E');
			else if (direction == 'W') turnAndMove('N');
		}

		// Handle Walls
		else if (currentLocation[0] == 0 && direction == 'N') turnAndMove('E'); // Top Wall
		else if (currentLocation[0] == worldSize - 1 && direction == 'S') turnAndMove('W'); // Bottom Wall
		else if (currentLocation[1] == 0 && direction == 'W') turnAndMove('N'); // Left Wall
		else if (currentLocation[1] == worldSize - 1 && direction == 'E') turnAndMove('S'); // Right Wall

		// Remove action safely
		if (!actionQueue.isEmpty()) {
			actionQueue.remove(0);
		}
	}



	private void turnAndMove(char newDirection) {
		turnTowards(newDirection);
		updateCoordinates();
		actionQueue.add(Action.GO_FORWARD);
	}

	private char getNewDirectionAfterLeftTurn() {
		if (direction == 'N') return 'W';
		if (direction == 'W') return 'S';
		if (direction == 'S') return 'E';
		return 'N'; // If facing East
	}


	private int countAdjacentPitPossible(int x, int y) {
		int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
		int pitCount = 0;

		for (int[] dir : directions) {
			int nx = x + dir[0], ny = y + dir[1];
			if (isValid(nx, ny) && worldState[nx][ny].equals(States.PIT_POSSIBLE)) {
				pitCount++;
			}
		}

		return pitCount;
	}

	private void generateMoveSequence(int[] target) {
		char desiredDirection = getDirectionTo(target[0], target[1]);
		turnTowards(desiredDirection);
		updateCoordinates();
		actionQueue.add(Action.GO_FORWARD);
	}

	private void turnTowards(char desiredDirection) {
		if (direction == desiredDirection) return;
		if ((direction == 'N' && desiredDirection == 'E') ||
				(direction == 'E' && desiredDirection == 'S') ||
				(direction == 'S' && desiredDirection == 'W') ||
				(direction == 'W' && desiredDirection == 'N')) {
			actionQueue.add(Action.TURN_RIGHT);
		} else if ((direction == 'N' && desiredDirection == 'W') ||
				(direction == 'W' && desiredDirection == 'S') ||
				(direction == 'S' && desiredDirection == 'E') ||
				(direction == 'E' && desiredDirection == 'N')) {
			actionQueue.add(Action.TURN_LEFT);
		} else {
			actionQueue.add(Action.TURN_LEFT);
			actionQueue.add(Action.TURN_LEFT);
		}
		direction = desiredDirection;
	}

	private char getDirectionTo(int x, int y) {
		if (x > currentLocation[0]) return 'S';
		if (x < currentLocation[0]) return 'N';
		if (y > currentLocation[1]) return 'E';
		return 'W';
	}

	private boolean isValid(int x, int y) {
		return x >= 0 && x < worldSize && y >= 0 && y < worldSize;
	}

	private void updateState(TransferPercept tp) {
		// Update the internal state based on current percepts
		bump = tp.getBump();
		glitter = tp.getGlitter();
		breeze = tp.getBreeze();
		stench = tp.getStench();
		scream = tp.getScream();

		// Mark the current cell as visited
		worldState[currentLocation[0]][currentLocation[1]] = States.SAFE;

		// Update state based on percepts
		if (breeze) {
			worldState[currentLocation[0]][currentLocation[1]] = States.BREEZE;
			markAdjacentCells(States.PIT_POSSIBLE);
		}
		if (stench) {
			worldState[currentLocation[0]][currentLocation[1]] = States.STENCH;
			markAdjacentCells(States.WUMPUS_POSSIBLE);
		}
		if (scream) {
			wumpusAlive = false;
			// Wumpus is dead, mark all WUMPUS_POSSIBLE cells as SAFE
			markWumpusDead();
		}
	}

	private void markWumpusDead() {
		// Mark all WUMPUS_POSSIBLE cells as SAFE
		for (int i = 0; i < worldSize; i++) {
			for (int j = 0; j < worldSize; j++) {
				if (worldState[i][j].equals(States.WUMPUS_POSSIBLE) || worldState[i][j].equals(States.STENCH)) {
					worldState[i][j] = States.SAFE;
				}
			}
		}
	}

	private void markAdjacentCells(String state) {
		int x = currentLocation[0];
		int y = currentLocation[1];

		// Check and mark adjacent cells
		if (x > 0 && worldState[x - 1][y].equals(States.UNKNOWN)) {
			worldState[x - 1][y] = state; // Up
		}
		if (x < worldSize - 1 && worldState[x + 1][y].equals(States.UNKNOWN)) {
			worldState[x + 1][y] = state; // Down
		}
		if (y > 0 && worldState[x][y - 1].equals(States.UNKNOWN)) {
			worldState[x][y - 1] = state; // Left
		}
		if (y < worldSize - 1 && worldState[x][y + 1].equals(States.UNKNOWN)) {
			worldState[x][y + 1] = state; // Right
		}
	}

	static class States {
		public static final String PIT = "pit";
		public static final String PIT_POSSIBLE = "pit_possible";
		public static final String WUMPUS = "wumpus";
		public static final String WUMPUS_POSSIBLE = "wumpus_possible";
		public static final String BREEZE = "breeze";
		public static final String STENCH = "stench";
		public static final String SAFE = "safe";
		public static final String UNKNOWN = "unknown";
	}
	// public method to return the agent's name
	public String getAgentName() {
		return agentName;
	}
}