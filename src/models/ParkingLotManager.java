/**   
* @Title: ParkingLotManager.java 
* @Package models 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-05-20
* @version V1.0   
*/
package models;

import java.util.List;

import dataStructure.other.HeapMin;
import parkingScheme.SpaceDispatcher;
import routing.Routing;

/**
* <p> ParkingLotManager</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-05-20
*/
public abstract class ParkingLotManager implements SpaceDispatcher{
	public Routing router;

	public Map parkingLot;
	
	protected ParkingLotManager(Map parkingLot, Routing router){

		this.parkingLot = parkingLot;
		this.router = router;
		
		emptySpaces = new HeapMin<ParkingSpace,Integer>(parkingLot.allSpaces);
		time = 0;
	}
	
	protected int time;
	
	protected HeapMin<ParkingSpace,Integer> emptySpaces;
	
	public void event(Task task){	
		parkingLot.allSpaces.get(task.parkingSpaceID).eventsOn.add(task);
	}
	
	/*
	public int pullOutAck(int parkingSpaceID,int time ){
		if(time < this.time)
			throw new IllegalArgumentException("请按");
		
		return 0;
	}*/
	
	protected ParkingSpace removeAEmptySpace(int spaceID){
		int locInHeap = parkingLot.allSpaces.get(spaceID).getElementLocation();
		emptySpaces.decreaseKey(locInHeap, -1);
		ParkingSpace removedSpace = emptySpaces.remove();
		removedSpace.updateKey(router.routing(parkingLot.in, removedSpace.location).size() 
				+ router.routing(parkingLot.out, removedSpace.location).size());
	//	removedSpace.updateKey(removedSpace.routings.get(0).size() 
	//			+ removedSpace.routings.get(1).size());
		
		return removedSpace;
	}
	
	protected void stateChangeClockwise(int time){
		if(time <= this.time)
			throw new IllegalStateException("-时间大于当前时间时才会调整");
		//注意到在系统所处时刻开始的事件对当时的快照结果无影响
		
		int nSpaces = parkingLot.allSpaces.size();
		for(int i = 0;i < nSpaces;i ++){
			List<Task> events = parkingLot.allSpaces.get(i).eventsOn;
			int j = -1;
			while(j + 1 < events.size()){
				Task event = events.get(j + 1);
				if(event.taskType == Task.PULL_IN && event.realFinishTime <= time)
					j ++;
				else if(event.taskType == Task.PULL_OUT && event.realStartTime < time)
					//对于出库情形，确保在给定时刻车位已空出来了
					j ++;				
			}
			if(j != -1){//j代表这段时间内（即(this.time,time]）会对所在车位发生影响的最后一项事件（不一定完成）
				if(events.get(j).taskType == Task.PULL_IN && parkingLot.allSpaces.get(i).empty){
					removeAEmptySpace(i);
					parkingLot.allSpaces.get(i).empty = false;
				}else if(events.get(i).taskType == Task.PULL_OUT && !parkingLot.allSpaces.get(i).empty){
					emptySpaces.add(parkingLot.allSpaces.get(i));
					parkingLot.allSpaces.get(i).empty = true;
				}
				
				//释放最后一个完成事件前面的所有事件
				for(int k = 0;k < j;k ++ )
					events.remove(0);
			}						
		}
		//记录当前系统时间
		this.time = time;
	}
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
