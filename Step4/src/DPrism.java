/*
* Author    Xingtian Dong
* Version   1.0 
* History   Updated By      Update Date     Update Reason
* 			Xingtian Dong	09/09/2017		Add exceptions and test them
*           ==============  ===========     ================================================
*/

import java.util.*;
import Axis.axis.co;

public class DPrism {
	private String name = new String();
	private ArrayList<DLine> lines;
	private DSurface[] surfaces = new DSurface[6];
	private int[][] surfaceContains = new int[6][];//Store 'belong' value of all the surfaces
	private boolean[][] surfaceStatus = new boolean[6][];//Represent if a bunch of connected lines has been added to a surface
	private boolean[] LineCheck;  //Represent if a line has been checked, the lines true value are connected to each other
	private ArrayList<DPoint> oriPoints; //Store the input points, used for split
	
	public DPrism(ArrayList<DPoint> temP) throws Step4Exception{
		oriPoints = temP;
		if(temP.size() != 8)
			throw new invalidInitialPrismException(null,0,"The input should be 8 corners of a prism");
		
		surfaces[0] = new DSurface(temP.get(0),temP.get(2));
		surfaces[0].setWhichSurface(0);
		surfaces[0].setPrism(this);
		surfaces[1] = new DSurface(temP.get(4),temP.get(6));
		surfaces[1].setWhichSurface(1);
		surfaces[1].setPrism(this);
		surfaces[2] = new DSurface(temP.get(7),temP.get(2));
		surfaces[2].setWhichSurface(2);
		surfaces[2].setPrism(this);
		surfaces[3] = new DSurface(temP.get(6),temP.get(1));
		surfaces[3].setWhichSurface(3);
		surfaces[3].setPrism(this);
		surfaces[4] = new DSurface(temP.get(5),temP.get(0));
		surfaces[4].setWhichSurface(4);
		surfaces[4].setPrism(this);
		surfaces[5] = new DSurface(temP.get(4),temP.get(3));
		surfaces[5].setWhichSurface(5);
		surfaces[5].setPrism(this);
	}
	
	public DSurface[] getSurfaces(){
		return surfaces;
	}
	
	//Return top surface
	public DSurface getTop(){
		return surfaces[0];
	}
	
	//Return bottom surface
	public DSurface getBottom(){
		return surfaces[1];
	}
		
	//Return Front surface
	public DSurface getFront(){
		return surfaces[2];
	}
	
	//Return right surface
	public DSurface getRight(){
		return surfaces[3];
	}
	
	//Return back surface
	public DSurface getBack(){
		return surfaces[4];
	}
	
	//Return left surface
	public DSurface getLeft(){
		return surfaces[5];
	}
	
	//Get the name of the prism
	public String getName(){
		return name;
	}
	
	//Set top surface
	public void setTop(DSurface newSurface){
		surfaces[0] = newSurface;
	}
	
	//Set bottom surface
	public void setBottom(DSurface newSurface){
		surfaces[1] = newSurface;
	}
	
	//Set front surface
	public void setFront(DSurface newSurface){
		surfaces[2] = newSurface;
	}
	
	//Set right surface
	public void setRight(DSurface newSurface){
		surfaces[3] = newSurface;
	}
	
	//Set back surface
	public void setBack(DSurface newSurface){
		surfaces[4] = newSurface;
	}
	
	//Set left surface
	public void setLeft(DSurface newSurface){
		surfaces[5] = newSurface;
	}
	
	//Rename the prism
	public void setName(String newName){
		name = newName;
	}
	
	//Put all the lines from each surface into an ArrayList, delete duplicate lines
	//Also set up 'surfaceContains' and 'surfaceStatus'
	public void prepareSplit(){
		lines = new ArrayList();
		for(DSurface temS : surfaces)
			for(DLine temL : temS.getLines())
				if( !lines.contains(temL))
					lines.add(temL);
		LineCheck = new boolean[lines.size()];
		for(int i = 0; i < 6; i++){
			surfaceContains[i] = new int[surfaces[i].getDifferentLines()];
			surfaceStatus[i] = new boolean[surfaces[i].getDifferentLines()];
			int temI1 = 0, temI2 = 0;
			for(int tem: surfaces[i].getBelong())
				if(tem!=temI2){
					surfaceContains[i][temI1] = tem;
					temI2 = tem;
					temI1++;
			}
		}
	}
	
	//Check if a prism needs to split after union
	//It will also set the value of LineCheck of all the connected lines to true
	public boolean checkSplit() throws Step4Exception{
		int temI = 0;
		while(LineCheck[temI])
			temI++;
		Queue<DLine> lineQ = new Queue();
		lineQ.enqueue(lines.get(temI));
		LineCheck[temI] = true;
		while(!lineQ.isEmpty()){
			DLine temD = lineQ.dequeue();
			ArrayList<DLine> temLList;
			if(temD.getSurfaces()[0].getPrism().getName().equals(name)){   //check if the surface is in this prism
				temLList = temD.getSurfaces()[0].getConnectedLinesAndHole(temD);
				for(DLine temDD: temLList)
					if(!LineCheck[lines.indexOf(temDD)]){
						LineCheck[lines.indexOf(temDD)] = true;
						lineQ.enqueue(temDD);
					}
			}
			if(temD.getSurfaces()[1].getPrism().getName().equals(name)){
				temLList = temD.getSurfaces()[1].getConnectedLinesAndHole(temD);
				for(DLine temDD: temLList)
					if(!LineCheck[lines.indexOf(temDD)]){
						LineCheck[lines.indexOf(temDD)] = true;
						lineQ.enqueue(temDD);
					}
			}
		}
		
		for(boolean temB: LineCheck)
			if(!temB)
				return true;
		return false;
	}
	
	//Split the prism into two and return a new one
	public DPrism splitPrism(String name) throws Step4Exception{
		DPrism temD = new DPrism(oriPoints);
		temD.setName(name);
		for(int i = lines.size() - 1; i >= 0; i--)
			if(LineCheck[i]){
				if(lines.get(i).getSurfaces()[0].getPrism().getName().equals(getName())){
					temD.getSurfaces()[lines.get(i).getSurfaces()[0].getWhichSurface()].addLine(lines.get(i));//add the line to new surface
					lines.get(i).getSurfaces()[0].deleteLine(lines.get(i));  //delete the line from original surface
					}
				if(lines.get(i).getSurfaces()[1].getPrism().getName().equals(getName())){
					temD.getSurfaces()[lines.get(i).getSurfaces()[1].getWhichSurface()].addLine(lines.get(i));
					lines.get(i).getSurfaces()[1].deleteLine(lines.get(i));
				}
//				System.out.println(lines.get(i));
				lines.remove(i);
			}
		for(int i = 0; i < 6; i ++){
			temD.getSurfaces()[i].sortLines();
			temD.getSurfaces()[i].setColor(this.getSurfaces()[i].getColor());
			temD.getSurfaces()[i].setMaterial(this.getSurfaces()[i].getMaterial());
			temD.getSurfaces()[i].setThickness(this.getSurfaces()[i].getThickness());
		}
		LineCheck = new boolean[lines.size()];
		return temD;
	}
	
	public String toString(){
		String temp = "Top: \n" + surfaces[0] + "\n" + "Bottom:\n" + surfaces[1] + "\n" + "Front:\n" + surfaces[2] + "\n" + "Right:\n"
	+ surfaces[3] + "\n" + "Back:\n" + surfaces[4] + "\n" + "Left:\n" + surfaces[5] + "\n";
		return temp;
	}
}