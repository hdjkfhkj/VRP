/**   
* @Title: ShortestPathFirst.java 
* @Package parkingScheme 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
* @author FlyingFish
* @date 2017-05-18
* @version V1.0   
*/
package parkingScheme;

import models.DispatchState;
import models.Map;
import models.ParkingLotManager;
import models.ParkingSpace;
import models.Task;
import models.VRP;
import routing.Routing;

/**
* <p> ���·�����Ȳ���</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-05-18
*/
public class ShortestPathFirst extends ParkingLotManager {

	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param parkingLot
	* @param router 
	*/
	int b,k,m;
	int inf=1000000000;
	int p;
	public ShortestPathFirst(Map parkingLot, Routing router) {
		super(parkingLot, router);
		// TODO Auto-generated constructor stub
	}


	/** (non-Javadoc)
	 * @see parkingScheme.SpaceDispatcher#restore(int)
	 */
	@Override
	public boolean restore(int restoreTime) {
		// TODO Auto-generated method stub
		return false;
	}


	/** (non-Javadoc)
	 * @see parkingScheme.SpaceDispatcher#parkingSpaceDispatch(models.VRP[], int)
	 */
	@Override
	public DispatchState parkingSpaceDispatch(VRP[] vrps, int id, int readyTime) {
		// TODO Auto-generated method stub
		if(readyTime < this.time)
			//�޸ĳ���ʱ��������������Ż���
			throw new IllegalStateException("�����������ȴ���");
		else if(readyTime > this.time)//ʱ�ӵ���
			stateChangeClockwise(readyTime);
				
		//����ǰ������̵�һ�����еĳ�λ���ȳ���
		int fessibleSpace = -1;
		int cost=0,mini=inf;
		int delay=0;
		int choose=0;
		for(int i = 0;i < parkingLot.allSpaces.size() ;i ++){
			ParkingSpace space = parkingLot.allSpaces.get(i);
			if(space.empty && space.firstPlanningEvent() == null)
				//��λΪ�գ��һ�û���ü����ų���
				delay=0;
			else if(!space.empty && space.firstPlanningEvent() != null){
				//��λ�г������Ͼ�Ҫ������
				Task pevent = space.firstPlanningEvent();
				if(pevent.realStartTime < readyTime + router.hops(parkingLot.in, space.location))
					//����ȴ�
					delay=0;
				//�����ٿ������������ֳɵĿճ�λû��
				else{
					//Ҫ��һ���
					delay = 1  +   space.firstPlanningEvent().realStartTime- readyTime - router.hops(parkingLot.in, space.location) ;
				}
			}
			else{
				delay=inf;//�а��ŵĳ�λ���ݲ�����
			}
			int dis=space.key;
			cost=b*delay+k*m*dis;
			if(cost<mini){
				mini=cost;
				choose=i;
			}
		}
		if(mini<p){
			fessibleSpace=choose;
		}
		
		if(fessibleSpace != -1){
			return new DispatchState(true,fessibleSpace,delay);
		}
		
		//�޳�λ�ɹ����ȣ���Ҫ�ȳ�����У�
		return new DispatchState(false,-1,-1);
	}
}
