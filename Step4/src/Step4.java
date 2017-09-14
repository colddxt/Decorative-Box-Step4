/*
* Author    Xingtian Dong
* Version   1.2
* History   Updated By      Update Date     Update Reason
* 			Xingtian Dong 	08/29/2017		Seperate long main method to be more object oriented
* 			Xingtian Dong	09/09/2017		Add exceptions and test them
*           ==============  ===========     ================================================
*/

import java.util.*;
import javax.swing.JFrame;
import java.io.*;
import org.math.plot.*;
import java.awt.Color;
import Axis.axis.co;

public class Step4 {
	private ArrayList<DPrism> prisms = new ArrayList();
	private ArrayList<DSurface> xSurfaces = new ArrayList();
	private ArrayList<DSurface> ySurfaces = new ArrayList();
	private ArrayList<DSurface> zSurfaces = new ArrayList();
	
	public Step4(String unitTest) throws FileNotFoundException,IOException,Step4Exception{
		
		if(!unitTest.equals("unitTest"))
			throw new wrongUnitTestException(null, 0, "One input can only be used for unit test! The input"
					+ "should be 'unitTest'");
		
		//build prisms
		ArrayList<DPoint> points = new ArrayList();
		BufferedReader File1 = new BufferedReader( new FileReader("points.txt") );
		String tema;
		String[] temb;
		double[] temc = new double[3];
		int count = 0;
		while ( File1.ready() ){
			 tema = File1.readLine();
			 temb = tema.split(" ");
			 for(int i = 0; i < 3 ; i++)
				 temc[i] = Double.parseDouble(temb[i]);
			 points.add(new DPoint(temc[0], temc[1], temc[2]));
			 if(points.size() == 8){
				 DPrism temD = new DPrism(points);
				 temD.setName(count + "");
				 prisms.add(temD);
				 points = new ArrayList();
			 }
		}
		
		//Read in lines after union
		ArrayList<DLine> lines = new ArrayList();
		points = new ArrayList();
		BufferedReader File2 = new BufferedReader( new FileReader("lines.txt") );
		while ( File2.ready() ){
			 tema = File2.readLine();
			 temb = tema.split(" ");
			 for(int i = 0; i < 3 ; i++)
				 temc[i] = Double.parseDouble(temb[i]);
			 points.add(new DPoint(temc[0], temc[1], temc[2]));
			 if(points.size() == 2){
				 lines.add(new DLine(points.get(0), points.get(1)));
				 points = new ArrayList();
			 }
		}
		
		//Sort surfaces
		for(int i = 0; i < prisms.size(); i++){
			build(prisms.get(i));
			prisms.get(i).setName("" + i);
		}
			
		//Search lines
		for(DLine temL: lines)
			sortLine(temL);
		
		//sort the lines in each surface in the prism, so that lines can be in order
		int temInt = prisms.size();
		for(int i = 0; i < temInt; i++){
			sortSurfaceLines(prisms.get(i));
			prisms.get(i).prepareSplit();
			int temI = 1;
			while(prisms.get(i).checkSplit()){
				prisms.add(prisms.get(i).splitPrism(prisms.get(i).getName() + "." + temI));
				temI++;
			}
			System.out.println();
		}
		
		//recompute 'differentLines' for each surface
		for(int i = 0; i < prisms.size(); i++){
			sortSurfaceLines(prisms.get(i));
		}
		
		for(DPrism temPM: prisms)
			System.out.println(temPM.getName() + temPM);
		
		visualization();
	}
	
	
	/***********************************************************************************************************
	 * To do: Read the lines back to prisms.
	 * 
	 * The method is under the assumption that coplanarity doesn't exist.
	 * So each line will and only will be a part of two surfaces and the two surfaces are vertical to each other.
	 * 
	 * Under the assumption and the characters of lines. I classify the surfaces of prisms into three collections:
	 * xSurfaces, ySurfaces and zSurfaces. Take zSurfaces as example, all top and bottom surfaces are zSurfaces 
	 * because the z axis is vertical to all zSurfaces and the value of z coordinate of all the points in a zSurface
	 * should be the same.
	 * 
	 *  With this classification. I use the value of corresponding coordinate to sort the collection of surfaces.
	 *  Like I use the value of z coordinate to sort zSurfaces. I use binary search to sort and search the surfaces,
	 *  so that the behavior of searching will be O(logN).
	 *  
	 *  Because each line will and only will constructs two surfaces and the two surfaces are vertical to each other,
	 *  so I need and only need to sort the line twice in two collections of surfaces. Like if I read a line 
	 *  which is parallel to z axis. I only need to search it in xSurfaces and ySurfaces, because it can not exit in
	 *  zSurfaces.
	 ***********************************************************************************************************/
	//Sort all the surfaces, so that it will be easy to sort
	private void build(DPrism temD){
		if(xSurfaces.size() == 0){
			xSurfaces.add(temD.getBack());
			xSurfaces.add(temD.getFront());
			ySurfaces.add(temD.getLeft());
			ySurfaces.add(temD.getRight());
			zSurfaces.add(temD.getBottom());
			zSurfaces.add(temD.getTop());
		}
		else{
			xSurfaces.add(bSearchSurface(xSurfaces, 1, temD.getBack().original[1]), temD.getBack());
			xSurfaces.add(bSearchSurface(xSurfaces, 1, temD.getFront().original[1]), temD.getFront());
			ySurfaces.add(bSearchSurface(ySurfaces, 3, temD.getLeft().original[3]), temD.getLeft());
			ySurfaces.add(bSearchSurface(ySurfaces, 3, temD.getRight().original[3]), temD.getRight());
			zSurfaces.add(bSearchSurface(zSurfaces, 5, temD.getBottom().original[5]), temD.getBottom());
			zSurfaces.add(bSearchSurface(zSurfaces, 5, temD.getTop().original[5]), temD.getTop());
		}
	}
	
	//Check which collections of surfaces should I search the line from. Search it in two collections and put it the surfaces 
	//that it belongs to.
	private void sortLine(DLine temL) throws Step4Exception{
		ArrayList<DSurface> Surfaces1 = new ArrayList<DSurface>(), Surfaces2 = new ArrayList<DSurface>();
		int which1 = 0, which2 = 0;
		double key1 = 0, key2 = 0;
		String S1, S2; //Used for debug
		if(temL.getPoints()[0].getX() == temL.getPoints()[1].getX() && temL.getPoints()[0].getY() == temL.getPoints()[1].getY()){
			Surfaces1 = xSurfaces;
			Surfaces2 = ySurfaces;
			S1 = "xSurfaces";
			S2 = "ySurfaces";
			which1 = 1;
			which2 = 3;
			key1 = temL.getPoints()[0].getX();
			key2 = temL.getPoints()[0].getY();
		}
		else if(temL.getPoints()[0].getX() == temL.getPoints()[1].getX() && temL.getPoints()[0].getZ() == temL.getPoints()[1].getZ()){
			Surfaces1 = xSurfaces;
			Surfaces2 = zSurfaces;
			S1 = "xSurfaces";
			S2 = "zSurfaces";
			which1 = 1;
			which2 = 5;
			key1 = temL.getPoints()[0].getX();
			key2 = temL.getPoints()[0].getZ();
		}	
		else if(temL.getPoints()[0].getZ() == temL.getPoints()[1].getZ() && temL.getPoints()[0].getY() == temL.getPoints()[1].getY()){
			Surfaces1 = zSurfaces;
			Surfaces2 = ySurfaces;
			S1 = "zSurfaces";
			S2 = "ySurfaces";
			which1 = 5;
			which2 = 3;
			key1 = temL.getPoints()[0].getZ();
			key2 = temL.getPoints()[0].getY();
		}
		else
			throw new invalidLineException(null,0,"The input line " + temL + " is invalid. It doesn't parallel to any axis.");
		
		int search = bSearchSurface(Surfaces1, which1, key1);

		if(Surfaces1.get(search).checkLine(temL)){
			Surfaces1.get(search).addLine(temL);
			temL.getSurfaces()[0] = Surfaces1.get(search);
		}
		else{
			int temI = searchUpwards(Surfaces1, search, temL, key1, which1);
			if(temI != -1){
				Surfaces1.get(temI).addLine(temL);
				temL.getSurfaces()[0] = Surfaces1.get(temI);
			}
			else{
				temI = searchDownwards(Surfaces1, search, temL, key1, which1);
				if(temI != -1){
					Surfaces1.get(temI).addLine(temL);
					temL.getSurfaces()[0] = Surfaces1.get(temI);
				}
				else
					throw new invalidLineException(null,0, "The line " + temL +" should, but doesn't exit in " + S1); 
			}	
		}
		
		search = bSearchSurface(Surfaces2, which2, key2);
		if(Surfaces2.get(search).checkLine(temL)){
			Surfaces2.get(search).addLine(temL);
			temL.getSurfaces()[1] = Surfaces2.get(search);
		}
		else{
			int temI = searchUpwards(Surfaces2, search, temL, key2, which2);
			if(temI != -1){
				Surfaces2.get(temI).addLine(temL);
				temL.getSurfaces()[1] = Surfaces2.get(temI);
			}
			else{
				temI = searchDownwards(Surfaces2, search, temL, key2, which2);
				if(temI != -1){
					Surfaces2.get(temI).addLine(temL);
					temL.getSurfaces()[1] = Surfaces2.get(temI);
				}
				else
					throw new invalidLineException(null,0, "The line " + temL +" should, but doesn't exit in " + S2);
			}	
		}
	}
	
	//Sometimes several surfaces may have the same key value. Binary search can only find one of them.
	//So I use binary search followed by a linear search to search upwards and downwards to look for the
	//the correct surface
	private int searchUpwards(ArrayList<DSurface> Surfaces, int num, DLine temL, double key, int which){
		int search = num - 1;
		if(search >= 0)
			if(Surfaces.get(search).original[which] == key)
				if(Surfaces.get(search).checkLine(temL))
					return search;
				else return searchUpwards(Surfaces, search, temL, key, which);
		return -1;	
	}
	
	private int searchDownwards(ArrayList<DSurface> Surfaces, int num, DLine temL, double key, int which){
		int search = num + 1;
		if(search >= 0)
			if(Surfaces.get(search).original[which] == key)
				if(Surfaces.get(search).checkLine(temL))
					return search;
				else return searchUpwards(Surfaces, search, temL, key, which);
		return -1;	
	}
	
	//'which' is a parameter to distinguish xSurfaces, ySurfaces and zSurfaces
	private int bSearchSurface(ArrayList<DSurface> Surfaces, int which, double key)
	{
		int lo = 0, hi = Surfaces.size()-1, mid = 0;
		while(lo < hi)
		{
		    mid = lo/2 + hi/2 + (lo%2 + hi%2)/2;
		    if(Surfaces.get(mid).original[which] == key) return mid;
		    else if(Surfaces.get(mid).original[which] < key) lo = mid +1;
		         else hi = mid - 1;
		}
		if(Surfaces.get(lo).original[which] == key) return lo;
		else if (Surfaces.get(lo).original[which] < key) return lo + 1;
			 else return lo;
	}
	
    /***********************************************************************************
     * After read all the lines to the surfaces they belong to. Call sortLines() for each 
     * surface to sort the lines in order.
     ***********************************************************************************/
	private void sortSurfaceLines(DPrism temD){
		temD.getTop().sortLines();
		temD.getBottom().sortLines();
		temD.getFront().sortLines();
		temD.getLeft().sortLines();
		temD.getBack().sortLines();
		temD.getRight().sortLines();
	}
	
    /***********************************************************************************
     * Visualization of the result.
     ***********************************************************************************/
	private void visualization(){
		//visualization
	       Plot3DPanel plot = new Plot3DPanel();
	       int temC = 255/prisms.size();
	       for(int i = 0; i < prisms.size(); i++){
	    	   for(DSurface temS: prisms.get(i).getSurfaces()){
	    		   for(int k = 0; k < temS.getDifferentLines();k++){
	    			   if(temS.getOriBelong().length > 1){
		    			   int tem1 = -1;
		    			   int tem2 = 0;
		    			   int tem3 = 0;
		    			   while(tem1 != k){
		    				   if(temS.getOriBelong()[tem2] != tem3){
		    					   tem1++;
		    					   tem3 = temS.getOriBelong()[tem2];
		    				   }
		    				   else
		    					   tem2++;
		    			   }
		    			   ArrayList<DLine> temLList = temS.getOriConnectedLines(temS.getOriBelong()[tem2]);
		    			   if(temLList.size() != 0){
				    		   ArrayList<DPoint> temPList = new ArrayList(); //Add points in sequence
				    		   double[] x = new double[temLList.size() + 1];
				    		   double[] y = new double[temLList.size() + 1];
				    		   double[] z = new double[temLList.size() + 1];
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
				   			   for(int j = 0; j < temPList.size(); j++){
				   				   x[j] = temPList.get(j).getX();
				   				   y[j] = temPList.get(j).getY();
			    				   z[j] = temPList.get(j).getZ();
			    			   }
				   			   int temCo1 = 0, temCo2 = 0, temCo3 = 0;
				   			   switch(i%3){
				   			   case 0:
				   				   temCo1 = 25*i;  //temC*i
				   				   temCo2 = 255;
				   				   temCo3 = 255;
				   				   break;
				   			   case 1:
				   				   temCo1 = 255;
				   				   temCo2 = 25*i;
				   				   temCo3 = 255;
				   				   break;
				   			   case 2:
				   				   temCo1 = 255;
				   				   temCo2 = 255;
				   				   temCo3 = 25*i;
				   				   break;
				   			   }
				   			plot.addLinePlot("my plot", new Color(temCo1, temCo2, temCo3), x,y,z);
		    			   }
		   		   }
	    	   }
	   	   }
	   }
	   JFrame frame = new JFrame("Step4 visulization");
	   frame.setContentPane(plot);
	   frame.setVisible(true); //visualization end
	}
	
	public static void main(String[] args) throws FileNotFoundException,IOException,Step4Exception{
		try{
		Step4 test = new Step4("unitTest");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
