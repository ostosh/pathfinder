package pathfinder.search;

import java.util.ArrayList;

/*
 * IState interface for search problems 
 * to be solved by the Solver class
 * 
 */
public interface IState {
	public String getHeuisticType();
	public ArrayList<IState> getSuccessors();
	public IState getParent();
	public IState getUnboundCopy();
	public int getDepth();
	public int getCurrentCost();
	public int getHeuisticCost();
	public int getDelta();
	public int getTotalCost();
	public boolean isSameType(IState that);
}
