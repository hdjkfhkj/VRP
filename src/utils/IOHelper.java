/**   
* @Title: IOHelper.java 
* @Package util 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-05-18
* @version V1.0   
*/
package utils;

import java.io.BufferedInputStream;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import models.VRP;
import routing.Routing;

/**
* <p> IOHelper</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-05-18
*/
public class IOHelper {

	private static PrintWriter output;
	private static Scanner input;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static Initiation loadFromFile(String filePath) throws FileNotFoundException{
			return null;
	}

	public static void out2file(int nRobots, int watingTime, int energyConsumption,VRP[] applications,Routing router,String filePath){
			
	}
	
	public static Initiation loadFromConsole(){		
		return null;
	}
	
	public static void out2console(int nRobots, int watingTime, int energyConsumption,VRP[] applications,Routing router,String filePath){
		
	}	
}
