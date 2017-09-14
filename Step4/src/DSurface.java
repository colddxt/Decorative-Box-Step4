/*
* Author    Xingtian Dong
* Version   1.0 
* History   Updated By      Update Date     Update Reason
*           ==============  ===========     ================================================
*/

import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.partitioning.Region.Location;

import java.util.*;
import Axis.axis.co;

public class DSurface{
	private String material;
	private double thickness;
	private int color;
	private DPrism prism;  //changed
	private ArrayList<DLine> lines;  //All the lines in the surface
	public double[] original; //boundary of the surface, xmin, xmax, ymin, ymax, zmin, zmax
	private int[] belong;     //represent which bunch of lines are connected to each other
	                          //and the value of holes are negative of the value of their boundary
	private int[] oriBelong;  //represent which bunch of lines are connected to each other
	private static int count = 0;//count how many surfaces have been created
	private int number;          //give each surface an ID
	private int differentLines; //number of different circles connected by lines
	private int whichSurface;   //0-top, 1-bottom, 2-front, 3-right, 4-back, 5-left
	
	public DSurface(DPoint a, DPoint b){
		number = count;
		count++;
		lines = new ArrayList<DLine>();
		original = new double[6];
		if(a.getX() < b.getX()){     //set up the boundary of the surface
			original[0] = a.getX();
			original[1] = b.getX();
		}
		else{
			original[1] = a.getX();
			original[0] = b.getX();
		}
		
		if(a.getY() < b.getY()){
			original[2] = a.getY();
			original[3] = b.getY();
		}
		else{
			original[3] = a.getY();
			original[2] = b.getY();
		}
		
		if(a.getZ() < b.getZ()){
			original[4] = a.getZ();
			original[5] = b.getZ();
		}
		else{
			original[5] = a.getZ();
			original[4] = b.getZ();
		}
	}
	
	public int getWhichSurface(){
		return whichSurface;
	}
	
	public int[] getBelong(){
		return belong;
	}
	
	public int[] getOriBelong(){
		return oriBelong;
	}
	
	public int getDifferentLines(){
		return differentLines;
	}
	public int getNumber(){
		return number;
	}
	
	public int getCount(){
		return count;
	}

	//Return the Prism it belongs to
	public DPrism getPrism(){
		return prism;
	}
	
	//Return the thickness of the surface
	public double getThickness(){
		return thickness;
	}
	
	//Return the color of the material of the surface
	public int getColor(){
		return color;
	}
	
	//Return the material 
	public String getMaterial(){
		return material;
	}
	
	//Return the lines that construct the surface
	public ArrayList<DLine> getLines(){
		return lines;
	}
	
	public void setPrism(DPrism temP){
		prism = temP;
	}
	
	public void setWhichSurface(int tem){
		whichSurface = tem;
	}
	
	//Set the material of the surface
	public void setMaterial(String materialName){
		material = materialName;
	}
	
	//Set the thickness of the surface
	public void setThickness(double newThickness){
		thickness = newThickness;
	}
	
	//Set the color of the surface
	public void setColor(int newColor){
		color = newColor;
	}
	
	//Check if a line is in the surface
	public boolean checkLine(DLine newLine){
		if(newLine.getPoints()[0].getX() <= original[1] && newLine.getPoints()[0].getX() >= original[0])
			if(newLine.getPoints()[0].getY() <= original[3] && newLine.getPoints()[0].getY() >= original[2])
				if(newLine.getPoints()[0].getZ() <= original[5] && newLine.getPoints()[0].getZ() >= original[4])
					if(newLine.getPoints()[1].getX() <= original[1] && newLine.getPoints()[1].getX() >= original[0])
						if(newLine.getPoints()[1].getY() <= original[3] && newLine.getPoints()[1].getY() >= original[2])
							if(newLine.getPoints()[1].getZ() <= original[5] && newLine.getPoints()[1].getZ() >= original[4])
								return true;
		return false;
	}
	
	//Add a new line that construct the surface
	public void addLine(DLine newLine){
		boolean temB = true;
		for(DLine temD: lines)  //check for duplicate lines
			if(temD.compareTo(newLine))
				temB = false;
		if(temB)
			lines.add(newLine);
	}
	
	//Sort the lines so that the lines will be clockwise or anti-clockwise
	//Also check which lines are connected to each other, and set the value
	//of 'oriBelong'
	public void sortLines(){
		if(lines.size() > 0){
			belong = new int[lines.size()];
			oriBelong = new int[lines.size()];
			differentLines = 1;
			belong[0] = differentLines;
			oriBelong[0] = differentLines;
			boolean temB = false;
			for(int i = 0; i < lines.size(); i++){
				temB = false;
				for(int j = i + 1; j < lines.size(); j++){
					if(lines.get(i).checkConnected(lines.get(j))){
						DLine tem = lines.remove(j);
						lines.add(i+1, tem);
						temB = true;
					}	
				}
				if(!temB && i != lines.size() - 1) differentLines++;
				if(i < lines.size()-1){
					belong[i+1] = differentLines;
					oriBelong[i+1] = differentLines;
				}
			}
			if(differentLines > 1)
				checkHole();
		}
		else
			differentLines = 0;
	}
	
	//Use Apache Math library
	//Build closed 2D areas according to the different bunch of connected lines
	//Check if an area is in another area, if so, it is a hole of the second area
	private void checkHole(){
		DPoint[][] tem = new DPoint[differentLines][0];
		//put all the points of a bunch of connected lines into clockwise or anti-clockwise sequence
		for(int k = 0; k < getDifferentLines();k++){
			   int tem1 = -1;
			   int tem2 = 0;
			   int tem3 = 0;
			   while(tem1 != k){
				   if(getOriBelong()[tem2] != tem3){
					   tem1++;
					   tem3 = getOriBelong()[tem2];
				   }
				   else
					   tem2++;
			   }
			   ArrayList<DLine> temLList = getOriConnectedLines(getBelong()[tem2]);
			   if(temLList.size() != 0){
		 		   ArrayList<DPoint> temPList = new ArrayList(); //Add points in sequence
		 		   DPoint temP1, temP2, temP3, temP4;
		 		   temP1 = temLList.get(0).getPoints()[0];
		 		   temP2 = temLList.get(0).getPoints()[1];
		 		   temP3 = temLList.get(1).getPoints()[0];
		 		   temP4 = temLList.get(1).getPoints()[1];
		 		   if(temP1.compareTo(temP3)){
		 			   temPList.add(temP2);
		 			   temPList.add(temP1);
						   temPList.add(temP4);
		 		   }else if(temP1.compareTo(temP4)){
		 			   temPList.add(temP2);
					   temPList.add(temP1);
					   temPList.add(temP3);
		 		   }else if(temP2.compareTo(temP3)){
		 			   temPList.add(temP1);
		 			   temPList.add(temP2);
		 			   temPList.add(temP4);
		 		   }else{
		 			   temPList.add(temP1);
		 			   temPList.add(temP2);
		 			   temPList.add(temP3);
		 		   }
		 		   for(int j = 2; j < temLList.size(); j++)
		 			   if(temLList.get(j).getPoints()[0].compareTo(temPList.get(temPList.size()-1)))
		 				   temPList.add(temLList.get(j).getPoints()[1]);
		 			   else
		 				   temPList.add(temLList.get(j).getPoints()[0]);
		 		   tem[k] = new DPoint[temPList.size()];
		 		   for(int j = 0; j < temPList.size(); j++)
		 			   tem[k][j] = temPList.get(j);
			   }
		}
		
		//Convert 3D points to 2D Vectors
		Vector2D[][] temArray = new Vector2D[tem.length][];
		co temco1;
		co temco2;
		double temI1, temI2;
		if(whichSurface==0||whichSurface==1){
			temI1 = original[1];
			temI2 = original[3];
			temco1 = co.x;
			temco2 = co.y;
		}
		else if(whichSurface==2||whichSurface==4){
			temI1 = original[3];
			temI2 = original[5];
			temco1 = co.y;
			temco2 = co.z;
		}else{
			temI1 = original[1];
			temI2 = original[5];
			temco1 = co.x;
			temco2 = co.z;
		}
		for(int i = 0; i < tem.length; i++){
			int asd = tem[i].length;
			temArray[i] = new Vector2D[tem[i].length];
			for(int j = 0; j < tem[i].length; j++)
				temArray[i][j] = new Vector2D(tem[i][j].getCoordinate(temco1),tem[i][j].getCoordinate(temco2));
		}
		
		//change all the point-sets to an anti-clockwise sequence
		PolygonsSet[] temSet = new PolygonsSet[tem.length];
		for(int i= 0; i < tem.length; i++){
			temSet[i] = new PolygonsSet(0.1, temArray[i]);
			if(temSet[i].checkPoint(new Vector2D(temI1 + 1,temI2 + 1)).compareTo(Location.valueOf("OUTSIDE"))!=0){
				int temLength = tem[i].length;
				Vector2D[] temTran = new Vector2D[temLength];
				for(int j = 0; j < temLength; j++)
					temTran[j] = temArray[i][temLength - j -1];
				temArray[i] = temTran;
				temSet[i] = new PolygonsSet(0.1, temArray[i]);
			}
		}
		
		//check if an area is a hole of another area
		for(int i= 0; i < tem.length; i++)
			for(int j = i + 1; j < tem.length;j++){
				if(temSet[i].checkPoint(temArray[j][0]).compareTo(Location.valueOf("INSIDE"))==0){
					DLine temL = new DLine(tem[j][0], tem[j][1]);
					int temBelong = 0;
					for(int k = 0; k < lines.size(); k++)
						if(lines.get(k).compareTo(temL))
							temBelong = belong[k];
					for(int k = 0; k < belong.length;k++)
						if(belong[k] == temBelong)
							belong[k] = - i - 1;
				}else if(temSet[j].checkPoint(temArray[i][0]).compareTo(Location.valueOf("INSIDE"))==0){
					DLine temL = new DLine(tem[i][0], tem[i][1]);
					int temBelong = 0;
					for(int k = 0; k < lines.size(); k++)
						if(lines.get(k).compareTo(temL))
							temBelong = belong[k];
					for(int k = 0; k < belong.length;k++)
						if(belong[k] == temBelong)
							belong[k] = - i - 1;
				}
			}	
	}
	
	//Delete lines when split surface
	public void deleteLine(DLine deLine){
		if(checkLine(deLine)){
			int coun = lines.indexOf(deLine);
			int[] tem1 = new int[belong.length-1];
			int[] tem2 = new int[belong.length-1];
			for(int i = 0, j = 0; i < lines.size(); i++)
				if(i != coun){
					tem1[j] = belong[i];
					tem2[j] = oriBelong[i];
					j++;
				}
			belong = tem1;
			oriBelong = tem2;
		}
		lines.remove(deLine);
	}
	
	
	//Find connected lines and other lines that form a hole in it
	public ArrayList<DLine> getOriConnectedLines(int temI){
		ArrayList<DLine> tem = new ArrayList();
		for(int i = 0; i < lines.size(); i++)
			if(oriBelong[i] == temI)
				tem.add(lines.get(i));
		return tem;
	}
	
	//Find the lines connected before split
	public ArrayList<DLine> getConnectedLinesAndHole(DLine temD) throws Step4Exception{
		int temI = getBelongNum(temD);
		ArrayList<DLine> tem = new ArrayList();
		for(int i = 0; i < lines.size(); i++)
			if(belong[i] == temI || belong[i] == -temI)
				tem.add(lines.get(i));
		return tem;
	}
	
	public int getBelongNum(DLine temD) throws Step4Exception{
		if(lines.contains(temD))
			return belong[lines.indexOf(temD)];
		else
			throw new noSuchLineInSurfaceException(null,0,"There is no such line in surface " + number);			
	}
		
	public String toString(){
		String temp = new String();
		for(int i = 0; i < lines.size(); i++)
			temp += "Line " + i + ": " + lines.get(i) + "\n";
		return temp;
	}
}