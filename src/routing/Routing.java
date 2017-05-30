/**   
* @Title: Routing.java 
* @Package routing 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-05-20
* @version V1.0   
*/
package routing;

import java.util.ArrayList;

import models.Point;

/**
* <p>路由接口</p>
* <p>为车位与出、入口间，车位之间提供路由支持</p>
* @author FlyingFish
* @date 2017-05-20
*/
public interface Routing {
	/** 
	* <p>两点间路由 </p> 
	* <p>包括起点、终点 </p> 
	* @param start
	* @param end
	* @return  
	* @see #hops(Point, Point)
	*/
	ArrayList<Point> routing(Point start,Point end);
	
	/** 
	* <p>当前路由在两点间经过的跳数 </p> 
	* <p>注意起点和终点应与<Code> Map </Code>对象中出口、入口或车位元素相对应 </p> 
	* @param start
	* @param end
	* @return 
	*/
	int hops(Point start,Point end);
}
