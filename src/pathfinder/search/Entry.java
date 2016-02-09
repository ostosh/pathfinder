package pathfinder.search;


/*
 * Entry class provides simple abstraction to
 * state cost from cost of path branch to 
 * min-max search 
 */
 class Entry{
	IState state;
	int pathCost;
	Entry(IState state, int pathCost){
		this.state = state;
		this.pathCost = pathCost;
		
	}
	
	//getPathCost
	// get pathCost field
	int getPathCost(){
		return pathCost;
	}
	
	//getIState
	// get state field
	IState getIState(){
		return state.getUnboundCopy();
	}
	
}