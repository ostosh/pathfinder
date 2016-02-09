package pathfinder.search;

public class Measure {
	private Runtime runtime = Runtime.getRuntime();
	private int time = 0;
	private long mem = 0;
	
	//getTime
	// get time field
	int getTime(){
		return time;
	}
	
	//getMem
	// get mem field	
	long getMem(){
		return mem;
	}
	
	//resetTime
	// reset time field
	void resetTime(){
		time = 0;
	}
	
	//resetMem
	// reset mem field
	void resetMem(){
		mem = 0;
		runtime.freeMemory();
	}
	
	//updateTime
	// increment time field
	void updateTime(){
		time++;
	}
	
	//updateMem
	// update mem field for new max
	void updateMem(){
		int scale = 1048576;
		mem = Math.max(mem, (runtime.totalMemory() - runtime.freeMemory())/scale);
	}
		
}
