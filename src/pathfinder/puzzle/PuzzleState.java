package pathfinder.puzzle;

import java.util.ArrayList;
import java.util.Arrays;

import pathfinder.search.IState;

/*
 * PuzzleState concrete implementation of IState
 * for NxN puzzle game
 * 
 */

public class PuzzleState implements IState{
	PuzzleState parent;
	byte[] board;//flatten board
	int cost;
	int delta;//cost change from last state
	int estimate;
	int depth;
	HeuristicType heuristic;
	
	//public constructor to be called when problem is defined
	//e.g. beginning, end  states with no parent, cost, delta, 
	//estimate or depth.
	public PuzzleState(byte[] board, HeuristicType heuristic){
		this.parent = null;
		this.board = Arrays.copyOf(board, board.length);
		this.cost = 0;
		this.delta = 0;
		this.heuristic = heuristic;
		this.estimate = getEstimate();
		this.depth = 0;
		
	}
	
	//private constructor to be called when a state is copying
	//and mutating itself into sucessor states
	private PuzzleState(PuzzleState parent, byte[] board, int cost, int delta, int depth, HeuristicType heuristic){
		this.parent = parent;
		this.board = Arrays.copyOf(board, board.length);
		this.cost = cost;
		this.delta = delta;
		this.heuristic = heuristic;
		this.estimate = getEstimate();
		this.depth = depth;
	}
	
	//equals
	// implementation based on eight puzzle board
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof PuzzleState))
			return false;
		return Arrays.equals(board, ((PuzzleState)obj).board);
	}
	//hashCode
	// implementation based on eight puzzle board
	@Override
	public int hashCode(){
		return Arrays.hashCode(board);
	}
	
	//toString
	// implementation based on eight puzzle board
	// and metadata of state
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < board.length; i++){
			sb.append(board[i]);
			sb.append(" ");
		}
		sb.append(" : cost = " + cost);
		sb.append(" : estimate = " + estimate);
		sb.append(" : move cost = " + delta);
		sb.append(" : depth = " + depth);
		return sb.toString();
	}
	
	//getSuccessors
	// get all successor states by searching flattened 
	// eight puzzle board for adjacent 0s and mutating current
	// state into this new state when found
	@Override
	public ArrayList<IState> getSuccessors() {
		ArrayList<IState> successors = new ArrayList<IState>();
		int k = (int) Math.sqrt(board.length);//assume board is always a NxN matrix
		
		//nested loop to handle flattened array
		for(int i = 0; i < k; i++){
			for(int j = 0; j < k; j++){
				if(j < k -1 && board[k*i+j+1] == 0) //empty right
					successors.add(getSuccesor(k*i+j, k*i+j+1));
				else if(j > 0 && board[k*i+j-1] == 0)//empty left	
					successors.add(getSuccesor(k*i+j, k*i+j-1));
				else if(i < k -1 && board[k*(i+1)+j] == 0) //empty below
					successors.add(getSuccesor(k*i+j, k*(i+1)+j));
				else if(i > 0 && board[k*(i-1)+j] == 0) // empty above		
					successors.add(getSuccesor(k*i+j, k*(i-1)+j));
				
			}
		}		
		return successors;
	}
	
	//getHeuisticType
	// convert heuristic member into string representation
	// for logging
	@Override
	public String getHeuisticType() {
		if(heuristic == null)
			return "none";
		return heuristic.name();
	}
	
	//getParent
	// get parent IState
	@Override
	public IState getParent(){
		return parent;
	}
	
	//getUnboundCopy
	// get copy of current state without 
	// parent reference
	@Override
	public IState getUnboundCopy(){
		return new PuzzleState(this.board, this.heuristic);
	}
	
	//getDepth
	// get depth
	@Override
	public int getDepth() {
		return depth;
	}
	//getCurrentCost
	// get total cost to reach state
	@Override
	public int getCurrentCost() {
		return cost;
	}
	
	//getDelta
	// get delta cost to reach state
	@Override
	public int getDelta() {
		return delta;
	}
	
	//getHeuisticCost
	// get estimate field this is set at 
	// object init to eliminate redundant
	// costly calculations
	@Override
	public int getHeuisticCost() {
		return estimate;
	}

	//getTotalCost
	// get cost + estimate field 
	@Override
	public int getTotalCost() {
		return cost + estimate;
	}

	//isSameType
	// prevents two different concrete 
	// implementations of IState from being
	// to the Solver, ending up in a cycle
	@Override
	public boolean isSameType(IState that) {
		if(!(that instanceof PuzzleState))
			return false;
		return true;
	}
	
	//getSuccesor
	// create successor state by copying current 
	// board (primitive does not require deep copy)
	// swaping indexes to and from, passing to private board
	// and metadata to private constructor
	private PuzzleState getSuccesor(int to, int from){
		byte[] tmp = Arrays.copyOf(board, board.length);
		byte x = tmp[from];
		byte y = tmp[to];
		tmp[from]= y;
		tmp[to] = x;
		
		return new PuzzleState(this, tmp, cost+y, y, depth+1, this.heuristic);
	}
	
	
	//getEstimate
	// hook method for calculating specific heuristic 
	// estimate based on  HeuristicType enum field
	private int getEstimate(){
		if(this.heuristic.equals(HeuristicType.A1))
			return getMisplacedEstimate();
		else if(this.heuristic.equals(HeuristicType.A2))
			return getManhattanDistanceEstimate();
		else if(this.heuristic.equals(HeuristicType.A3))
			return getManhattanCostEstimate();
		else
			return getZeroEstimate();
	}
	
	//getSolutionIndex
	// get board index of a specific val
	// in the solution state
	
	// TODO: only works for eight puzzle
	//  need to rework to handle NxN matrix
	private int getSolutionIndex(int val){
		if(val == 0)
			return 4;
		else if(val < 4 || val == 7)
			return val - 1;
		else if(val == 8)
			return 3;
		else if(val == 4)
			return 5;
		else if(val == 6)
			return 7;
		else if(val == 5)
			return 8;	
		else
			return 0;
	}	
	
	//getZeroEstimate
	// return zero for algos that
	// do not include an estimate
	private int getZeroEstimate(){
		return 0;
	}
	
	//getMisplacedEstimate
	// add the total number of  misplaced
	// values for the current state
	private int getMisplacedEstimate(){
		int est = 0;
		for(int i = 0; i < board.length; i++){
			int val = board[i];
			if(val == 0)
				continue;//ignore duplicate cost of zero
			int pos = getSolutionIndex(val);
			est += Math.abs(pos - i) == 0 ? 0 : 1;
		}
		return est;
	}
	
	//getMisplacedEstimate
	// sum the Manhattan Distance for
	// all values of the current state
	private int getManhattanDistanceEstimate(){
		int est = 0;
		for(int i = 0; i < board.length; i++){
			int val = board[i];
			if(val == 0)
				continue;//ignore duplicate cost of zero
			int pos = getSolutionIndex(val);
			
			//Manhattan distance for flat board
			est += (Math.abs(pos%3 - i%3) + Math.abs(pos/3 - i/3));
		}
		return est;
	}
	
	//getMisplacedEstimate
	// sum the value weighted Manhattan Distance for
	// all values of the current state
	private int getManhattanCostEstimate(){
		int est = 0;
		for(int i = 0; i < board.length; i++){
			int val = board[i];
			if(val == 0)
				continue;//ignore duplicate cost of zero
			int pos = getSolutionIndex(val);
			
			//Manhattan distance for flat board, weighted by value of position
			est += (Math.abs(pos%3 - i%3) + Math.abs(pos/3 - i/3))*val;
		}
		return est;
	}
	
}
