/**   
* @Title: RobotsControllerI.java 
* @Package schedulingScheme 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-05-24
* @version V1.0   
*/
package schedulingScheme;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import models.DispatchState;
import models.Map;
import models.Point;
import models.Robot;
import models.RobotsControlCenter;
import models.Task;
import models.VRP;
import parkingScheme.SpaceDispatcher;
import routing.Routing;

/**
* <p> RobotsControllerI</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-05-24
*/
public class RobotsControllerI extends RobotsControlCenter {

	
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param map
	* @param router
	* @param dispatcher
	* @param k
	* @param p
	* @param a
	* @param b
	* @param appls 
	*/
	public RobotsControllerI(Map map, Routing router, SpaceDispatcher dispatcher, int k, int p, int a, int b,
			VRP[] appls) {
		super(map, router, dispatcher, k, p, a, b, appls);
		// TODO Auto-generated constructor stub
	}

	

	
	/** (non-Javadoc)
	 * @see schedulingScheme.Scheduling#scheduling(int)
	 */
	@Override
	public int scheduling(int nRobots) {
		// TODO Auto-generated method stub
		this.createRobotsSet(nRobots);
		this.createTaskList();
		
		while(!pullInTasks.isEmpty() && !pullOutTasks.isEmpty()){
			
			/*
			if(!pullInTasks.isEmpty()){
				taskQ.add(pullInTasks.remove(0));
			}*/
			
			
		}
		
		return 0;
	}

	private List<Robot> robotsQ = new LinkedList<Robot>();
	private List<Task> taskQ1 = new ArrayList<Task>();
	private List<Task> taskQ2 = new LinkedList<Task>();
	
	protected List<Tuple> taskSelection(){
		
		//先查看接下来的第一个入库任务
		Task taskIn = this.pullInTasks.get(0);
		
		//查看第一个空闲机器人
		Robot robot = robots.peek();
		DispatchState ds ;
		
		//记录当前实际可以执行的所有的出库任务，放到taskQ2中
		int realStartTime = Math.max(taskIn.startTime, robot.completementTime);
		while(!pullOutTasks.isEmpty() && pullOutTasks.peek().startTime <= realStartTime)
			taskQ2.add(pullOutTasks.remove());
		

		if(!(ds = dispatcher.parkingSpaceDispatch(applications, 
				taskIn.carID,realStartTime)).success){
			//如果没有车位可供分配
			
			if(!taskQ2.isEmpty()){
				//如果当前有可以执行的出库任务，选最早开始的去执行
				Tuple pullOut = new Tuple(taskQ2.remove(0),robots.remove());
				List<Tuple> result = new ArrayList<Tuple>();
				result.add(pullOut);
				return result;
			}else{
				//否则，等待并执行一个出库任务
				Tuple pullOut = new Tuple(pullOutTasks.remove(),robots.remove());	
				List<Tuple> result = new ArrayList<Tuple>();
				result.add(pullOut);
				return result;
			}				
		}else if(robot.completementTime >
			taskIn.startTime + this.applications[taskIn.carID].longestWatingTime){
		//	如果分配了车位，但机器人赶不上，拒载
			
			List<Tuple> result = new ArrayList<Tuple>();
			result.add(new Tuple(taskIn,null));
			return result;
		}else{
			//	如果分配了车位，并且机器人可以赶上
			
			//定义一个时间周期[takeIn,startTime,timeBound]
			//timeBound是第一个机器人在完成入库任务后返回
			//入口的时刻
			int timeBound = router.hops(map.allSpaces.get(ds.parkingSpaceID).location, 
					map.in) * 2 + ds.delay + realStartTime  ;
			//int nTaskOut = taskQ2.size();
			
			//将时间周期内的入库任务放到taskQ1
			//一次执行的入库任务数目不会超过总的机器人数目
			while(!pullInTasks.isEmpty() && taskIn.startTime < timeBound 
					&&taskQ1.size() < robots.size()){
				taskQ1.add(taskIn);
				taskIn = pullInTasks.remove(0);
			}
			
			List<Tuple> result = new ArrayList<Tuple>();
			
			//一次性指派的机器人数目的上界
			int robotsBound = Math.min(
					taskQ2.size() + taskQ1.size(),robots.size());
			int taskQ1Index = 0;
			//boolean interruption = false;
			int refusalCount = 0;
			boolean refuse ;
			while(taskQ1Index < taskQ1.size() ){
				refuse = true;
				if(taskQ1Index ==  taskQ1.size() - 1){
					//记录有多少个机器人赶得上入库队列中的
					//最后一个任务
					if(robots.peek().completementTime > 
						taskQ1.get(taskQ1Index).startTime +
							applications[taskQ1.get(taskQ1Index).carID].longestWatingTime)
								result.add(new Tuple(taskQ1.get(taskQ1Index),null));
					
					while( robotsQ.size() <= robotsBound
							&&robots.peek().completementTime <= 
								taskQ1.get(taskQ1Index).startTime +
									applications[taskQ1.get(taskQ1Index).carID].longestWatingTime){
						Robot exeRobot = robots.remove();
						robotsQ.add(exeRobot);
						if(refuse)
							//只在第一次进入时添加
							result.add(new Tuple(taskQ1.get(taskQ1Index),exeRobot));
						refuse = false;

					}					
				}else{	//没到输入任务队列中的最后一个
					if(robots.peek().completementTime > 
							taskQ1.get(taskQ1Index).startTime +
							applications[taskQ1.get(taskQ1Index).carID].longestWatingTime)
						result.add(new Tuple(taskQ1.get(taskQ1Index),null));
					else if(!robots.isEmpty()){
						Robot exeRobot = robots.remove();
						robotsQ.add(exeRobot);	
						refuse = false;
						result.add(new Tuple(taskQ1.get(taskQ1Index),exeRobot));
					}
				}
				
				if(refuse)
					refusalCount++;
				taskQ1Index++;
			}

			
			int robotsOffset = robotsQ.size() - taskQ1.size() + refusalCount;
			if(robotsOffset == 0){
				//如果每个机器人都有入库任务
				return result;
			}
			
			
			//有的机器人还没有任务
			List<Task> solution = taskQ1;
			for(int i = robotsOffset;i > 0  && !taskQ2.isEmpty();i --){
				//分配出库任务
				solution.add(taskQ2.remove(0));
			}
			
			//禁忌搜索
			
		}
			
		return null;
	}
	
	private int lastRealStartTimeIn = 0;
	private int lastRST = 0;
	
	public class TabuSearch{
		private List<Task> solution;
		private List<Robot> robots;
		
		private int[][] tabuTable;
		
		private int bestCost ;
		private List<Task> bestSolution;
		private int iterationCount = 0;
				
		public TabuSearch(List<Task> initSolution,List<Robot> robots){
			solution = initSolution;
			this.robots = robots;
			tabuTable = new int[initSolution.size()][initSolution.size()];
		}
		
		public List<Task> solve(){
			iterationCount = 0;
			bestCost = evaluate(solution);
			bestSolution = solution;
			
			while(!stop()){
				
			}
			
			
			return bestSolution;
		}
		
		
		public int evaluate(List<Task> solution){
			int cost = 0;
			int taskIndex = 0,robotIndex = 0; 
			
			dispatcher.restore(lastRST);
			
			while(taskIndex < solution.size()){
				//按顺序处理每个任务
				Task task = solution.get(taskIndex);
				Robot robot = robots.get(robotIndex);
				if(task.taskType == Task.PULL_IN){//入库任务
					if(robot.completementTime > task.startTime 
						+ applications[task.carID].longestWatingTime){
						//机器人赶不上
						cost += fPanishment;
					}else{
						int realStartTime =
								Math.max(robot.completementTime,task.startTime);
						DispatchState ds = dispatcher.parkingSpaceDispatch(applications, task.carID, realStartTime);
						if(!ds.success || realStartTime+ds.delay > task.startTime + applications[task.carID].longestWatingTime)
							//因为等待车位而延迟，导致机器人赶不上任务
							cost += fPanishment;
						else{//机器人赶得上
							
							//注意先申请先处理
							realStartTime = Math.max(realStartTime+ds.delay, lastRealStartTimeIn);
							cost += fWating * (realStartTime - task.startTime);
							robotIndex++;
						}
					}			
				}else if(task.taskType == Task.PULL_OUT){//出库任务
					int realStartTime = Math.max(robot.completementTime,task.startTime);
					cost += fWating * (realStartTime - task.startTime );
					robotIndex++;																			
				}else
					throw new IllegalStateException("只考虑入库和出库任务!");
				taskIndex++;
			}
			
			return cost;
		}
		
		public boolean stop(){
			return iterationCount == 1000;
		}
		
		public int bestNeighbor(Point neighborLoc){
			
			
			int i = getNextPullOutTask( 0,solution);
			while(i > 0)
				for(int j = 0;j < tabuTable.length;j ++)
					if(tabuTable[i][j] == 0){
						
					}
						
			
			return 0;
		}
		
		public int aspiration(int bestNeighborCost,Point finalBestNeighbor){
			return 0;
		}
		
		private int getNextPullOutTask(int start,List<Task> solution){
			java.util.ListIterator<Task> iter = solution.listIterator(start);
			int pullOutIndex = -1;
			while(iter.hasNext() && pullOutIndex == -1){
				
				if(iter.next().taskType == Task.PULL_OUT)
					pullOutIndex = iter.previousIndex();
			}
			return pullOutIndex;
		}
		
		private List<Task> neighbor(List<Task> solution,int i,int j){
			Task temp = solution.get(i);
			solution.set(i, solution.get(j));
			solution.set(j, temp);
			
			return solution;
		}
	}
	
	protected static class Tuple{
		public Task t;
		public Robot r ;
		public Tuple(Task t,Robot r){
			this.t = t;
			this.r = r;
		}
	}
}
