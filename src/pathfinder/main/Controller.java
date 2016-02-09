package pathfinder.main;

import pathfinder.puzzle.AlgoType;
import pathfinder.puzzle.HeuristicType;
import pathfinder.puzzle.PuzzleState;
import pathfinder.search.Solution;
import pathfinder.search.Solver;
import pathfinder.ui.Home;
import pathfinder.ui.Loading;
import pathfinder.ui.Results;


public class Controller {
	public static  final int HOME = 0;
	public static final int LOAD = 1;
	public static final int RESULTS = 2;
	public static final int EXIT = 3;
	private static final byte[] SOLUTION = new byte[]{1,2,3,4,0,5,6,7,8};
	
	private static Home home = new Home();
	private static Loading loading = new Loading();
	private static Results results = new Results();
	
	public void init(){
		
		
	}
	
	
	public static  void run(int next){
		closeAll();
		if(next == HOME)
			home.open();	
		else if (next == LOAD)
			loading.open();
		else if (next == RESULTS)
			results.open();
		else if(next == EXIT)
			exit();
	}
	
	
	public static void closeAll(){
		home.close();
		loading.close();
		results.close();
	}
	
	
	public static void exit(){
		home.dispose();
		loading.dispose();
		results.dispose();
	}
	
	public static Solution getSolution(byte[] problem, AlgoType alg, HeuristicType hueristic){

		Solution solution = null;

		if(alg.equals(AlgoType.BFS))
			solution = Solver.solveBFS(new PuzzleState(problem, hueristic), new PuzzleState(SOLUTION, hueristic));
		else if(alg.equals(AlgoType.DFS))
			solution = Solver.solveDFS(new PuzzleState(problem, hueristic), new PuzzleState(SOLUTION, hueristic));
		else if(alg.equals(AlgoType.UC))
			solution = Solver.solveUC(new PuzzleState(problem, hueristic), new PuzzleState(SOLUTION, hueristic));
		else if(alg.equals(AlgoType.ID))
			solution = Solver.solveID(new PuzzleState(problem, hueristic), new PuzzleState(SOLUTION, hueristic));
		else if(alg.equals(AlgoType.AS))
			solution = Solver.solveAS(new PuzzleState(problem, hueristic), new PuzzleState(SOLUTION, hueristic));
		return solution;

	}
	
	
	public static void dumpSolution(Solution solution){
		results.setResultsText(solution.toString());
	}
	
	public static void main(String[] args){
		//byte[] beg = new byte[]{5,6,7,4,0,8,2,3,1};
		//byte[] sol = new byte[]{1,2,3,4,0,5,6,7,8};
		
		//Solution s1 = solver.solveAS();
		/*s1.printSolution();
		s1 = solver.solveUC();
		s1.printSolution();
		s1 = solver.solveID();
		s1.printSolution();
		//s1 = solver.solveDFS();
		//s1.printSolution();
		s1 = solver.solveBFS();
		s1.printSolution();*/

		run(0);


	
	    
	}

	
	
}

