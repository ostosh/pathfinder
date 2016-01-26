package pathfinder.search;

import java.util.ArrayList;

/*
 * Solution class provides meta data
 * for solution presentation and loggin
 * 
 */
public class Solution {
	IState end;
	String type;
	String time;
	String space;
	
	
	Solution(String type, String time, String space, IState end){
		this.type = type;
		this.end = end;
		this.time = time;
		this.space = space;	
	}
	
	//getOrderedPath
	// reserve solution into an ordered
	// path array from problem start to finish
	public ArrayList<IState> getOrderedPath(){
		IState current = end;
		ArrayList<IState> orderdResults = new ArrayList<IState>();
		while(current != null){
			orderdResults.add(0, current);
			current = current.getParent();
		}
		return orderdResults;
	}
	
	//printSolution
	// print the solution's toString
	// method
	public void printSolution(){
		System.out.print(toString());
	}
	
	//toString
	// includes ordered solution path 
	// and meta data
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Results for ");
		sb.append(type);
		sb.append(": ");
		sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		ArrayList<IState> orderdResults = getOrderedPath();
		for(IState state : orderdResults){
			sb.append(state.toString());
			sb.append(System.lineSeparator());	
		}
		sb.append(System.lineSeparator());
		sb.append("summary: ");
		sb.append("time= ");
		sb.append(time);
		sb.append("; max space= ");
		sb.append(space);
		sb.append(System.lineSeparator());
		sb.append("end results for ");
		sb.append(type);
		sb.append(" heuristic: ");
		sb.append(end.getHeuisticType());
		sb.append(System.lineSeparator());
		return sb.toString();
	}
	
}
