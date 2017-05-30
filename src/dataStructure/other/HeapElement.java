/**   
* @Title: HeapElement.java 
* @Package week18th 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-01-17
* @version V1.0   
*/
package dataStructure.other;

/**
* <p> HeapElement</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-01-17
*/
public interface HeapElement<K> {

	void setElementLocation(int location);
	
	int getElementLocation();
	
	void updateKey(K key);
	
}
