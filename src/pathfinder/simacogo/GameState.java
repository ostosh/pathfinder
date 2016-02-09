package pathfinder.simacogo;

import java.util.ArrayList;
import java.util.Arrays;

import pathfinder.search.IState;

/*
 * PuzzleState concrete implementation of IState
 * for NxN puzzle connect four / go mashup game
 * 
 */
public class GameState implements IState{
	
	private class CellState {
		static final byte E = 0;
		static final byte O = 1;
		static final byte X = 2;

	}
	
	GameState parent;
	byte[] board;//flatten board
	byte move;
	int score;//minmax score
	
	//public constructor to be called when problem is defined
	//e.g. beginning, end  states with no parent, cost, delta, 
	//estimate or depth.
	public GameState(byte[] board){
		this.parent = null;
		this.move = CellState.O;
		Arrays.fill(board, CellState.E);
		this.board = Arrays.copyOf(board, board.length);
		this.score = 0;
		
	}
	
	//private constructor to be called when a state is copying
	//and mutating itself into successor states without a parent
	//reference
	private GameState(byte[] board, byte nextMove, int score) {
		this.board = Arrays.copyOf(board, board.length);
		this.move = nextMove;
		this.score = score;
	}

	//equals
	// implementation based on game board
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof GameState))
			return false;
		return Arrays.equals(board, ((GameState)obj).board);
	}
	
	//hashCode
	// implementation based on game board
	@Override
	public int hashCode(){
		return Arrays.hashCode(board);
	}
	
	//toString
	// implementation based on game board
	// and score
	@Override
	public String toString(){
		int k = (int) Math.sqrt(board.length);//assume board is always a NxN matrix
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < board.length; i++){
			sb.append(board[i] == CellState.O ? "O" : board[i] == CellState.X ? "X" : "-");
			sb.append(" ");
			if((i+1)%k==0)
				sb.append("\n");
			
		}
		sb.append("score = " + score);
		sb.append("\n");
		return sb.toString();
	}
	
	//getSuccessors
	// get all successor states by searching flattened
	// board for empty adjacent cells
	@Override
	public ArrayList<IState> getSuccessors() {
		ArrayList<IState> successors = new ArrayList<IState>();
		int k = (int) Math.sqrt(board.length);//assume board is always a NxN matrix
		for(int i = 0; i < k; i++){
			if(board[i] == CellState.E){//check first row if col is not full
				for(int j = i; j < board.length; j += k){//deep search column for next empty cell in col
					if( j+k >= (board.length) || board[j+k] != CellState.E){
						successors.add(getSuccesor(j));
						break;
					}
				}
			}
		}		
		return successors;
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
		return new GameState(this.board, this.move, score);
	}
	
	//getDepth
	// get depth
	@Override
	public int getDepth() {
		return 0;
	}
	//getCurrentCost
	// get total cost to reach state
	@Override
	public int getCurrentCost() {
		return score;
	}
	
	//isSameType
	// prevents two different concrete 
	// implementations of IState from being
	// to the Solver, ending up in a cycle
	@Override
	public boolean isSameType(IState that) {
		if(!(that instanceof GameState))
			return false;
		return true;
	}
	
	//getSuccesor
	// create successor state by copying current 
	// board (primitive array does not require deep copy)
	// marking index to with piece of player move
	// and calculating the move's incremental cost
	GameState getSuccesor(int to){
		byte[] tmp = Arrays.copyOf(board, board.length);
		//mark player piece
		tmp[to] = this.move;
		
		//get incremental cost of move
		int scoreIncrement = getMoveScore(to);
		//calculate minmax and add to current score
		int newScore = score + (this.move == CellState.O ? -scoreIncrement : scoreIncrement);
		
		byte nextMove = this.move == CellState.O ? CellState.X : CellState.O;
		return new GameState(tmp, nextMove, newScore);

	}
	
	//hasNext
	// check first board row of board for available moves (empty cells)
	boolean hasNext(){
		int k = (int) Math.sqrt(board.length);//assume board is always a NxN matrix
		for(int i = 0; i < k; i++){
			if(this.board[i] == CellState.E) 
				return true;
		}
		return false;
		
	}
	
	//hasNext
	// get available moves (empty cells) from first row of board
	ArrayList<Integer> getAvailableMoves(){
		int k = (int) Math.sqrt(board.length);//assume board is always a NxN matrix
		ArrayList<Integer> cols = new ArrayList<Integer>();
		for(int i = 0; i < k; i++){
			if(this.board[i] == CellState.E) 
				cols.add(i);
		}
		return cols;
	}
	
	
	//getNextEmpty
	// deep dive col to find first empty cell
	int getNextEmpty(int col){
		int k = (int) Math.sqrt(board.length);//assume board is always a NxN matrix
		for(int j = col; j < board.length; j += k){//deep search column for next empty cell in col
			if( j+k >= (board.length) || board[j+k] != CellState.E){
				return j;
			}
		}
		
		return -1;
		
	}
	
	//getMoveScore
	// calculate incremental score of move at pos
	// scoring:
	// 1. N,E,S,W adjacent match = 4
	// 2. NE,SE,SW,NW adjacent match = 2
	// 3. N,E,S,W adjacent empty = 2
	// 4. NE,SE,SW,NW adjacent empty = 1
	// 5. N,E,S,W adjacent opponent = 2 
	// 	which negates opponents empty point in sum
	// 6. NE,SE,SW,NW adjacent opponent = 1
	//  which negates opponents empty point in sum
	
	private int getMoveScore(int pos){
		int score = 0;
		int k = (int) Math.sqrt(board.length);//assume board is always a NxN matrix
		
		int E = (pos) % k + 1;
		if(E < k)//E
			score += 2;

		int W = (pos) % k  - 1;
		if(W >= 0)//W
			score += 2;
		
		int S = pos + k;
		if(S < board.length)//S
			score += 2;
		
		int N = pos - k;
		if(N >= 0)//N
			score += 2;
	
		if(S < board.length && E < k)//SE
			score += 1;
		
		if(S < board.length && W >= 0)//SW
			score += 1;
		
		if(N >= 0 && E < k)//NE
			score += 1;
		
		if(N >= 0 && W >= 0)//NW
			score += 1;
		
		return score;
	}
	
	@Override
	//getHeuisticType
	// null, NA method implemented from IState interface
	public String getHeuisticType() {
		return null;
	}
	
	//getDelta
	// null, NA method implemented from IState interface
	@Override
	public int getDelta() {
		return 0;
	}
	
	//getHeuisticCost
	// null, NA method implemented from IState interface
	@Override
	public int getHeuisticCost() {
		return 0;
	}
	//getTotalCost
	// null, NA method implemented from IState interface
	@Override
	public int getTotalCost() {
		return 0;
	}

}
