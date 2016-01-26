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
	IState start;
	IState solution;
	Runtime runtime = Runtime.getRuntime();
	int time = 0;
	long mem = 0;
	
	public Solver(IState problem, IState solution){
		if(problem == null)
			throw new IllegalArgumentException("Error: begining state cannot be null");
		if(solution == null)
			throw new IllegalArgumentException("Error: solution state cannot be null");
		if(!problem.isSameType(solution))
			throw new IllegalArgumentException("Error: begining and solution state must be same type");
		
		this.start = problem;
		this.solution = solution;
	}
	
	//solveBFS
	// solution search based on breadth
	// first search algorithm
	public Solution solveBFS(){
		resetTime();
		resetMem();
		
		//init search data structure and add start state
		Solution foundSolution = null;
		ArrayList<IState> toVisit = new ArrayList<IState>();
		HashSet<IState> isVisited = new HashSet<IState>();
		toVisit.add(start);
		
		//search until there are no more successor states
		//to visit
		while(!toVisit.isEmpty()){
			updateTime();
			updateMem(); 
			
			//get next state (FIFO), and check if solution
			IState current = toVisit.remove(0);
			if(current.equals(solution))
				 return foundSolution = new Solution("Breadth First Search", time + " ms", mem + " mb", current);
	
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
	public Solution solveDFS(){
		resetTime();
		resetMem();
		
		//init search data structure and add start state
		Solution foundSolution = null;
		ArrayList<IState> toVisit = new ArrayList<IState>();
		HashSet<IState> isVisited = new HashSet<IState>();
		toVisit.add(start);
		
		//search until there are no more successor states
		//to visit
		while(!toVisit.isEmpty()){
			updateTime();
			updateMem(); 
			
			//get next state (LIFO), and check if solution
			IState current = toVisit.remove(toVisit.size()-1);
			
			if(current.equals(solution))
				 return foundSolution = new Solution("Depth First Search", time + " ms", mem + " mb", current);
			
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
	public Solution solveID(){
		resetTime();
		resetMem();
		
		Solution foundSolution = null;
		
		
		//TODO: find a way to remove cycle, can this be done
		//a inner, no more successor states check which is 
		//not bound by depth?
		
		//search until at depth i until foundSolution
		for(int i = 0; foundSolution == null; i++){
			//init search data structures and add start state
			ArrayList<IState> toVisit = new ArrayList<IState>();
			HashSet<IState> isVisited = new HashSet<IState>();
			toVisit.add(start);
			
			//search until no more successor states at depth i
			while(!toVisit.isEmpty()){
				updateTime();
				updateMem(); 
				
				//get next state (LIFO), and check if solution
				IState current = toVisit.remove(toVisit.size()-1);
				if(current.equals(solution))
					 return foundSolution = new Solution("Iterative Deepening",  time + " ms", mem + " mb", current);
				
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
	public Solution solveUC(){
		resetTime();
		resetMem();
		
		//init search data structure and add start state
		Solution foundSolution = null;
		HashMap<IState, Integer> costs = new HashMap<IState, Integer>();
		//pq based on generic UC comparator
		PriorityQueue<IState> toVisit = new PriorityQueue<IState>(1, comparatorUC());
		HashSet<IState> isVisited = new HashSet<IState>();
		toVisit.add(start);
		
		//search until there are no more successor states
		//to visit
		while(!toVisit.isEmpty()){
			updateTime();
			updateMem(); 
			
			//get next state (cost adjusted PQ), and check if solution
			IState current = toVisit.poll();
			
			if(current.equals(solution))
				 return foundSolution = new Solution("Uniform Cost",  time + " ms", mem + " mb", current);
			
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
	public Solution solveAS(){
		resetTime();
		resetMem();
		
		//init search data structure and add start state
		Solution foundSolution = null;
		HashMap<IState, Integer> costs = new HashMap<IState, Integer>();
		PriorityQueue<IState> toVisit = new PriorityQueue<IState>(1, comparatorAS());
		HashSet<IState> isVisited = new HashSet<IState>();
		toVisit.add(start);
		
		//search until there are no more successor states
		//to visit
		while(!toVisit.isEmpty()){
			updateTime();
			updateMem(); 
			
			//get next state (cost adjusted PQ), and check if solution
			IState current = toVisit.poll();
			
			if(current.equals(solution))
				 return foundSolution = new Solution("A*",  time + " ms", mem + " mb", current);
			
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
	
	//resetTime
	// reset time field
	private void resetTime(){
		time = 0;
	}
	
	//resetMem
	// reset mem field
	private void resetMem(){
		mem = 0;
		runtime.freeMemory();
	}
	
	//updateTime
	// increment time field
	private void updateTime(){
		time++;
	}
	
	//updateMem
	// update mem field for new max
	private void updateMem(){
		int scale = 1048576;
		mem = Math.max(mem, (runtime.totalMemory() - runtime.freeMemory())/scale);
	}
}
