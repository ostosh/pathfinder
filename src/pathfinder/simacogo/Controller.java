package pathfinder.simacogo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import pathfinder.search.IState;
import pathfinder.search.Solver;


/*
 * Controller provides a simple text based
 * interface between the search implementation 
 * and the text UI interface for NxN puzzle connect 
 * four/ go mashup game
 */

public class Controller {
	private int plys;
	private GameState gameState;
	
	
	//public constructor to init new game
	Controller(int plys, int width){
		this.plys = plys;
		byte[] board = new byte[width*width];
		gameState = new GameState(board);		
	}
	
	
	//getUserMove
	// parse valid user move
	private int getUserMove(){
		ArrayList<Integer> openMoves = gameState.getAvailableMoves();
		System.out.println("Enter column to insert chip: Available="+ openMoves.toString());
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		int userMove;
		try {
			userMove = Integer.parseInt(in.readLine());
		} catch (Exception e) {
			System.out.println("Error: cannot parse invalid selection.");
			userMove = -1;
		}
		
		if(!openMoves.contains(userMove)){
			System.out.println("Error: move " + userMove +" is not available.");
			return getUserMove();
		}else
			return userMove;
	}
	
	//getGameDiffculty
	// parse valid user diffculty setting
	private static int getGameDiffculty(){
		System.out.println("Enter game difficulty [1-12]:");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		int userChoice;
		try {
			userChoice = Integer.parseInt(in.readLine());
		} catch (Exception e) {
			System.out.println("Error: cannot parse invalid selection.");
			userChoice = -1;
		}
		
		if(userChoice < 1 || userChoice > 12){
			System.out.println("Error: game difficulty " + userChoice +" is not available.");
			return getGameDiffculty();
		}else
			return userChoice;
	}
	
	//getGameWidth
	// parse valid user board size setting
	private static int getGameWidth(){
		System.out.println("Enter game board size [3-12]:");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		int userChoice;
		try {
			userChoice = Integer.parseInt(in.readLine());
		} catch (Exception e) {
			System.out.println("Error: cannot parse invalid selection.");
			userChoice = -1;
		}
		
		if(userChoice < 3 || userChoice > 12){
			System.out.println("Error: game board size " + userChoice +" is not available.");
			return getGameWidth();
		}else
			return userChoice;
	}
	
	//processUserMove
	// process user move for round
	private void processUserMove(){
		int userMove = getUserMove();
		int index = gameState.getNextEmpty(userMove);
		gameState = gameState.getSuccesor(index);	
	}
	
	//processAIMove
	// process AI move for round
	private void processAIMove(){
		IState solutionState =  Solver.solveMinMax(gameState, plys).getEndState();
		gameState = (GameState)solutionState.getUnboundCopy();//TODO could be improved to just update based on index	
	}
	
	//playRound
	// play round of game
	private void playRound(){
		processUserMove();
		processAIMove();
	}
	
	//printState
	// print current game state
	private void printState(){
		System.out.print(gameState.toString());
	}
	
	//playGame
	// play game
	private void playGame(){
		//until game state is finished
		while(gameState.hasNext()){
			playRound();
			printState();
		}
		System.out.print("Game Over....");
	}
	
	//main
	// application entry point, launch text based UI
	public static void main(String[] args){
		int plys = getGameDiffculty();
		int width = getGameWidth();
		new Controller(plys,width).playGame();
	
	}
	
	
}
