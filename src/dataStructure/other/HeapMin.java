/**   
* @Title: Heap.java 
* @Package week18th 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-01-16
* @version V1.0   
*/
package dataStructure.other;

import java.util.List;

/**
* <p>实现小顶堆</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-01-16
*/
public class HeapMin<E extends HeapElement<K>,K> {
	private java.util.List<E> list = new java.util.ArrayList<E>(); 
	
	/*设置一个比较器，可以选择使用自然序（此时comparator = null）或比较器来给元素排序；
	 * 另一方面，通过比较器可以将一些自然序已经确定的类型（如Integer）的小顶堆变成大顶堆*/
	//private final java.util.Comparator<? super E> comparator; 
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p>  
	*/
	public HeapMin() {
		// TODO Auto-generated constructor stub
		list.add(null);
	}

	/** 
	* <p>Title: </p> 
	* <p>0号位置不用 </p> 
	* @param elements 
	*/
	public HeapMin(E[] elements){
		list.add(null);
		for(int i = 0;i < elements.length;i ++)
			add(elements[i]);
	}

	public HeapMin(List<E> elements){
		list.add(null);
		for(int i = 0;i < elements.size();i ++)
			add(elements.get(i));
	}
	
	
	public static void main(String[] args){
		//用堆来排序
		Integer[] elems = {49,38,65,97,76,13,27,49};
		
		HeapEAdapter[] elemsAdp = HeapEAdapter.getAdapters(elems);
		HeapMin heap = new HeapMin(elemsAdp);
		System.out.println("初始堆（元素格式：数据/位置）");
		System.out.println(heap);		
		System.out.println("将堆中4号位置49改为9");
		//elemsAdp[3].value = 9;
		heap.decreaseKey(4, 9);
		System.out.println(heap);		
		
		System.out.println("堆排序：");
		int times = elems.length;
		while(times -- > 0){
			HeapElement he = heap.remove();
			System.out.println(heap + " " + he);
		}
		
	}
	
	/** 
	* <p>添加一个元素到堆 </p> 
	* <p>Description: </p> 
	* @param e 
	*/
	public void add(E e){
		list.add(e);
		shiftUp(size());
	}

	/** 
	* <p>返回头元素，但不从堆中移除</p> 
	* <p>Description: </p> 
	* @return 
	*/
	public E peek(){
		if(size() == 0)
			return null;
		else
			return list.get(1);
	}
	
	/** 
	* <p>移除并返回堆顶元素，如果堆为空，返回null</p> 
	* <p>Description: </p> 
	* @return 
	*/
	public E remove(){
		if(size() == 0)
			return null;
		if(size() == 1)
			return list.remove(1);
			
		//注意从1号位置开始放元素
		E removedElem = list.get(1);
			
		//将最后一个位置上的元素赋给第一个位置上的元素
		list.set(1, list.get(size()));
		
		//去掉最后一个位置上的元素
		list.remove(size());
		
		//对第一个位置上的元素进行筛选
		shiftDown(1);
		return removedElem;
	}
	
	/** 
	* <p>减小某个节点的值</p> 
	* <p>Description: </p> 
	* @param elemLoc
	* @param key 注意key值应该是“减小”的，所谓大小是就自然序（默认）或者比较器而言 
	*/
	public void decreaseKey(int elemLoc,K key ){	
		list.get(elemLoc).updateKey(key);
		shiftUp(elemLoc);
	}
		
	/** 
	* <p>向上筛选 </p> 
	* <p>按小顶堆设计的操作，对范围在[1,elementLoc]的元素进行内部调整，使其在elementLoc位置上的元素满足堆的定义，前提是在范围[1,elementLoc - 1]上的元素满足堆的定义。</p> 
	* @param elementLoc 
	*/
	protected void shiftUp(int elementLoc){
		int newLoc = elementLoc;
		
		//预先保存elementLoc位置上的元素
		E elem = list.get(elementLoc);
		
		//强制转换，前提是E实现了Comparable接口
		//之所以使用Comparable<? super E>，是考虑到实现Comparable接口的可以是E或E的父类
		@SuppressWarnings("unchecked")
		Comparable<? super E> key =( Comparable<? super E>)list.get(elementLoc);

		for(list.set(0, list.get(elementLoc))//设置哨兵
			;key.compareTo(list.get(newLoc/ 2))< 0;newLoc /= 2){
			
			//先做好标记，再将父节点下移
			list.get(newLoc / 2).setElementLocation(newLoc);
			list.set(newLoc, list.get(newLoc / 2));		
		}
		
		//将目标节点移到最后的位置
		elem.setElementLocation(newLoc);
		list.set(newLoc, elem);
		
		//哨兵解除
		list.set(0, null);
	}
	
	/** 
	* <p>向下筛选</p> 
	* <p>按小顶堆设计的操作，对以elementLo位置为顶的堆(成员范围不超过[elementLoc,size()])进行内部调整，使其堆顶最小。前提是该顶其余元素符合小顶堆定义。</p> 
	* @param elementLoc 待调整堆顶
	*/
	protected void shiftDown(int elementLoc){
		int newLoc  = elementLoc,child;
		E elem = list.get(elementLoc);
		
		for(; newLoc * 2 <= size() ;newLoc = child){
			//找最小的子节点
			child = newLoc * 2;
					
			Comparable<? super E> keyChild =( Comparable<? super E>)list.get(child);			
			if(child < size() && keyChild.compareTo(list.get(child + 1) )  > 0)
				//如果右节点存在且比左节点小，选右节点
				keyChild = ( Comparable<? super E>)list.get(++ child);	
			
			if(keyChild.compareTo(elem) < 0){//孩子节点更小
				list.get(child).setElementLocation(newLoc);
				list.set(newLoc, list.get(child));
			}else
				break;//直接退出，这时不能再用child去更新newLoc（newLoc = child）				
		}
		
		//将目标节点移到最后的位置
		elem.setElementLocation(newLoc);
		list.set(newLoc, elem);		
	}
	
	/** 
	* <p>返回堆的大小，同时也是堆中最后一个节点的编号</p> 
	* <p>Description: </p> 
	* @return 
	*/
	public int size(){
		return list.size() - 1;
	}
	
	public boolean isEmpty(){
		return size() <= 0;
	}
	
	public String toString(){		
		return list.toString();
	}
}
