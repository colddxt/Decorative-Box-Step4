/*
* Author    Xingtian Dong
* Version   1.0 
* History   Updated By      Update Date     Update Reason
*           ==============  ===========     ================================================
*/

import Axis.axis.co;

public class DPoint{
	private double x;
	private double y;
	private double z;
	
	public DPoint(double a, double b, double c){
		x = a;
		y = b;
		z = c;
	}
	
	public DPoint(double[] abc){
		if(abc.length != 3){
			System.out.println("The length of input array is not correct");
			System.exit(0);
		}
		x = abc[0];
		y = abc[1];
		z = abc[2];
	}
	
	public double getCoordinate(co check){ //return coordinate according to the input axis
		if(check == co.x)
			return x;
		else if(check == co.y)
			return y;
		else
			return z;
	}
	public double getX(){
		return x;
	}
		
	public double getY(){
		return y;
	}
		
	public double getZ(){
		return z;
	}
		
	public double[] getPoint(){
		double[] pois = new double[3];
		pois[0] = x;
		pois[1] = y;
		pois[2] = z;
		return pois;
	}
	
	//check if the point is the same with the input point
	public boolean compareTo(DPoint that){
		if( x == that.getX() && y == that.getY() && z == that.getZ())
			return true;
		else return false;
	}
	
	public String toString(){
		return "(" + x + "," + y + "," + z +")";
	}

}