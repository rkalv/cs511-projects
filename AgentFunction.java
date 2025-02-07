/*
 * Class that defines the agent function.
 * 
 * Written by James P. Biagioni (jbiagi1@uic.edu)
 * for CS511 Artificial Intelligence II
 * at The University of Illinois at Chicago
 * 
 * Last modified 2/19/07 
 * 
 * DISCLAIMER:
 * Elements of this application were borrowed from
 * the client-server implementation of the Wumpus
 * World Simulator written by Kruti Mehta at
 * The University of Texas at Arlington.
 * 
 */

import java.util.Random;

class AgentFunction {
	
	// string to store the agent's name
	// do not remove this variable
	private String agentName = "Agent Smith";
	
	// all of these variables are created and used
	// for illustration purposes; you may delete them
	// when implementing your own intelligent agent
	private int[] actionTable;
	private boolean bump;
	private boolean glitter;
	private boolean breeze;
	private boolean stench;
	private boolean scream;
	private Random rand;

	public AgentFunction() {
		// initialize random number generator
		rand = new Random();
	}

	public int process(TransferPercept tp) {
		// read in the current percepts
		bump = tp.getBump();
		glitter = tp.getGlitter();
		breeze = tp.getBreeze();
		stench = tp.getStench();
		scream = tp.getScream();

		// Rule 1: Prioritize grabbing gold immediately
		if (glitter) {
			return Action.GRAB;
		}

		// Rule 2: Shoot Wumpus if stench is detected
		if (stench && hasArrow) {
			return Action.SHOOT;
		}

		// Rule 3: Avoid pits and infinite loop sequence by no op on breeze.
		if (breeze) {
			return Action.NO_OP;
		}

		//Rule 4: Avoid walls by turning left. Non-deterministic action added to avoid repreated sequence.
		if (bump) {
			return (Math.random() < 0.8 ? Action.TURN_LEFT : Action.GO_FORWARD);
		}

		//Default: move forward to explore
		return Action.GO_FORWARD;
	}

	// public method to return the agent's name
	public String getAgentName() {
		return agentName;
	}
}