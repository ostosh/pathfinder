package pathfinder.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

/*
 * Solver class solves any abstract problem with
 * a concrete implementation of IState for its start and
 * solution states
 */

public class Solver {
	
	
	//solveMinMax
	// recursive entry for solution search
	// based on minmax algorithm
	public static Solution solveMinMax(IState start, int depth){
		Entry problem = new Entry(start, start.getCurrentCost());
		Entry solution = solveMinMax(problem, Integer.MIN_VALUE, Integer.MAX_VALUE, depth, true);
		return new Solution("MinMax Search", "na", "na", solution.getIState());
	}
	
	//solveMinMax
	// solution search based
	// on minmax algorithm
	// optimized with alpha-
	// beta pruning
	private static Entry solveMinMax(Entry start, int lb, int ub, int depth, boolean isMax){
		
		//return if max search depth reached
		if(depth == 0)
			return start;
		
		//get successors and return if non-exist
		ArrayList<IState> successors = start.state.getSuccessors();
		if(successors.size() == 0)
			return start;
		
		Entry optimal = null;
		
		//get optimal successor solution for alternating min/max
		for(IState succesor : successors){
			Entry problem = new Entry(succesor, succesor.getCurrentCost());
			//recursive minmax solution search for successor
			Entry solution = solveMinMax(problem, lb, ub, depth-1, isMax ? false : true);
			boolean optimalFound = false;
			
			if(optimal == null){//first pass, init optimal
				optimal = new Entry(succesor, solution.getPathCost());
				optimalFound = true;
			}else if(isMax && solution.getPathCost() > optimal.getPathCost()){
				optimal = new Entry(succesor, solution.getPathCost());
				optimalFound = true;
			}else if(!isMax && solution.getPathCost() < optimal.getPathCost()){
				optimal = new Entry(succesor, solution.getPathCost() );
				optimalFound = true;
			}
			
			if(isMax && optimalFound){
				if(optimal.getPathCost() > ub)//check for prune opportunity
					return optimal;
				lb = optimal.getPathCost();//reset lower prune bound
			}else if(!isMax && optimalFound){
				if(optimal.getPathCost() < lb)//check for prune opportunity
					return optimal;
				ub = optimal.getPathCost();//reset upper prune bound
			}
		}
		return optimal;
	}	
	
	//solveBFS
	// solution search based on breadth
	// first search algorithm
	public static Solution solveBFS(IState problem, IState solution){
		Measure m = new Measure();
		m.resetTime();
		m.resetMem();
		
		
		//init search data structure and add start state
		Solution foundSolution = null;
		ArrayList<IState> toVisit = new ArrayList<IState>();
		HashSet<IState> isVisited = new HashSet<IState>();
		toVisit.add(problem);
		
		//search until there are no more successor states
		//to visit
		while(!toVisit.isEmpty()){
			m.updateTime();
			m.updateMem();  
			
			//get next state (FIFO), and check if solution
			IState current = toVisit.remove(0);
			if(current.equals(solution))
				 return foundSolution = new Solution("Breadth First Search",  m.getTime() + " ms", m.getMem() + " mb", current);
	
			//get successor states and queue
			for(IState succesor : current.getSuccessors()){
				if(isVisited.contains(succesor))
					continue;
				
				toVisit.add(succesor);
			}
			
			//create unbound (no parent copy) to reduce memory
			//for visited checking
			isVisited.add(current.getUnboundCopy());
		}
		
		return foundSolution;
	}
	
	//solveDFS
	// solution search based on depth
	// first search algorithm
	public static Solution solveDFS(IState problem, IState solution){
		Measure m = new Measure();
		m.resetTime();
		m.resetMem();
		
		
		//init search data structure and add start state
		Solution foundSolution = null;
		ArrayList<IState> toVisit = new ArrayList<IState>();
		HashSet<IState> isVisited = new HashSet<IState>();
		toVisit.add(problem);
		
		//search until there are no more successor states
		//to visit
		while(!toVisit.isEmpty()){
			m.updateTime();
			m.updateMem();  
			
			//get next state (LIFO), and check if solution
			IState current = toVisit.remove(toVisit.size()-1);
			
			if(current.equals(solution))
				 return foundSolution = new Solution("Depth First Search",  m.getTime() + " ms", m.getMem() + " mb", current);
			
			//get successor states and queue
			for(IState succesor : current.getSuccessors()){
				if(isVisited.contains(succesor))
					continue;
				
				toVisit.add(succesor);
			}
			//create unbound (no parent copy) to reduce memory
			//for visited checking
			isVisited.add(current.getUnboundCopy());
		}
	
		return foundSolution;
	}
	
	//solveID
	// solution search based on iterative
	// deepening search algorithm
	public static Solution solveID(IState problem, IState solution){
		Measure m = new Measure();
		m.resetTime();
		m.resetMem();
		
		
		Solution foundSolution = null;
		
		
		//TODO: find a way to remove cycle, can this be done
		//a inner, no more successor states check which is 
		//not bound by depth?
		
		//search until at depth i until foundSolution
		for(int i = 0; foundSolution == null; i++){
			//init search data structures and add start state
			ArrayList<IState> toVisit = new ArrayList<IState>();
			HashSet<IState> isVisited = new HashSet<IState>();
			toVisit.add(problem);
			
			//search until no more successor states at depth i
			while(!toVisit.isEmpty()){
				m.updateTime();
				m.updateMem(); 
				
				//get next state (LIFO), and check if solution
				IState current = toVisit.remove(toVisit.size()-1);
				if(current.equals(solution))
					 return foundSolution = new Solution("Iterative Deepening", m.getTime() + " ms", m.getMem() + " mb", current);
				
				//do not add successors if current states hits depth limit
				if(current.getDepth() >= i)
					continue;
				
				//get successor states and queue
				for(IState succesor : current.getSuccessors()){
					if(isVisited.contains(succesor))
						continue;
					
					toVisit.add(succesor);
				}
				
				//create unbound (no parent copy) to reduce memory
				//for visited checking
				isVisited.add(current.getUnboundCopy());
			}
		}
		
		return foundSolution;
	}	
	
	//solveID
	// solution search based on uniform
	// cost algorithm
	public static Solution solveUC(IState problem, IState solution){
		Measure m = new Measure();
		m.resetTime();
		m.resetMem();
		
		
		//init search data structure and add start state
		Solution foundSolution = null;
		HashMap<IState, Integer> costs = new HashMap<IState, Integer>();
		//pq based on generic UC comparator
		PriorityQueue<IState> toVisit = new PriorityQueue<IState>(1, comparatorUC());
		HashSet<IState> isVisited = new HashSet<IState>();
		toVisit.add(problem);
		
		//search until there are no more successor states
		//to visit
		while(!toVisit.isEmpty()){
			m.updateTime();
			m.updateMem();  
			
			//get next state (cost adjusted PQ), and check if solution
			IState current = toVisit.poll();
			
			if(current.equals(solution))
				 return foundSolution = new Solution("Uniform Cost", m.getTime() + " ms", m.getMem() + " mb", current);
			
			//get successor states and queue
			for(IState succesor : current.getSuccessors()){
				if(isVisited.contains(succesor))
					continue;
				
				//compare cost if already seen (but not visited)
				Integer previousCost = costs.get(succesor);
				if(previousCost != null){
					if(succesor.getCurrentCost() > previousCost.intValue())
						continue;//existing is cheaper, ignore new
					else
						toVisit.remove(succesor);//new is cheaper, remove existing
				}
				
				//create unbound (no parent copy) to reduce memory
				//for cost checking
				costs.put(current.getUnboundCopy(), succesor.getCurrentCost());
				toVisit.offer(succesor);
			}
			
			
			//visited, no longer need for cost checking
			costs.remove(current);
			//create unbound (no parent copy) to reduce memory
			//for visited checking
			isVisited.add(current.getUnboundCopy());
		}
		
		return foundSolution;
	}
	
	//solveAS
	// solution search based on A
	// star algorithm
	public static Solution solveAS(IState problem, IState solution){
		Measure m = new Measure();
		m.resetTime();
		m.resetMem();
		
		//init search data structure and add start state
		Solution foundSolution = null;
		HashMap<IState, Integer> costs = new HashMap<IState, Integer>();
		PriorityQueue<IState> toVisit = new PriorityQueue<IState>(1, comparatorAS());
		HashSet<IState> isVisited = new HashSet<IState>();
		toVisit.add(problem);
		
		//search until there are no more successor states
		//to visit
		while(!toVisit.isEmpty()){
			m.updateTime();
			m.updateMem(); 
			
			//get next state (cost adjusted PQ), and check if solution
			IState current = toVisit.poll();
			
			if(current.equals(solution))
				 return foundSolution = new Solution("A*",  m.getTime() + " ms", m.getMem() + " mb", current);
			
			//get successor states and queue
			for(IState succesor : current.getSuccessors()){
				if(isVisited.contains(succesor))
					continue;
				
				//compare cost if already seen (but not visited)
				Integer previousCost = costs.get(succesor);
				if(previousCost != null){
					
					if(succesor.getTotalCost() > previousCost.intValue())
						continue;//existing is cheaper, ignore new
					else
						toVisit.remove(succesor);//existing is cheaper, ignore new
						
				}
				//create unbound (no parent copy) to reduce memory
				//for cost checking
				costs.put(current.getUnboundCopy(), succesor.getTotalCost());
				toVisit.offer(succesor);
			}

			//visited, no longer need for cost checking
			costs.remove(current);
			//create unbound (no parent copy) to reduce memory
			//for visited checking
			isVisited.add(current.getUnboundCopy());
			
		}

		return foundSolution;
		
	}
	
	//comparatorUC
	// comparator ranks two IStates
	// based on current cost (g) for 
	// uniform cost algorithm
	private static Comparator<IState> comparatorUC(){
		return new Comparator<IState>(){
			public int compare(IState x, IState y){
				if(x.getCurrentCost() < y.getCurrentCost())
					return -1;
				else if (x.getCurrentCost() > y.getCurrentCost())
					return 1;
				else
					return 0;
				
			}
		};
	}
	
	//comparatorAS
	// comparator ranks two IStates
	// based on total cost (h+g) for 
	// A star algorithm
	private static Comparator<IState> comparatorAS(){
		return new Comparator<IState>(){
			public int compare(IState x, IState y){
				if(x.getTotalCost() < y.getTotalCost())
					return -1;
				else if (x.getTotalCost() > y.getTotalCost())
					return 1;
				else
					return x.getHeuisticCost() - y.getHeuisticCost();
				
			}
		};
	}
	

}
