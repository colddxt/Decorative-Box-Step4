import Axis.axis.co;
public class DLine {
	private DPoint[] points;
	private DSurface[] surfaces;
	
	public DLine(DPoint a, DPoint b){
		points = new DPoint[2];
		surfaces = new DSurface[2];
		points[0] = a;
		points[1] = b;
	}
	
	//Return the array of points that build the line
	public DPoint[] getPoints(){
		return points;
	}
	
	//Return the array of surfaces that the line belongs to
	public DSurface[] getSurfaces(){
		return surfaces;
	}
	
	//Check if the line is connected with another line
	public boolean checkConnected(DLine that){
		if(points[0].compareTo(that.getPoints()[0]) || points[1].compareTo(that.getPoints()[1]) ||
				points[1].compareTo(that.getPoints()[0]) || points[0].compareTo(that.getPoints()[1]))
			return true;
		else return false;
	}
	
	//Check if two surfaces are too near to each other
	public boolean checkLength(){
		return false;
	}
	
	//Check if two lines are the same
	public boolean compareTo(DLine that){
		if(points[0].compareTo(that.getPoints()[0]) && points[1].compareTo(that.getPoints()[1]) ||
				points[1].compareTo(that.getPoints()[0]) && points[0].compareTo(that.getPoints()[1]))
			return true;
		else return false;
	}
	
	public String toString(){
		return points[0] + " " + points[1];
	}

}