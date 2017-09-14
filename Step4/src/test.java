import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.math.plot.*;
public class test {
	public static void main(String[] args){
		double[] x = {2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0}; 
		double[] y = {0.0, 2.0, 2.0, 1.0, 1.0, 0.0, 0.0}; 
		double[] z = {0.0, 0.0, 1.0, 1.0, 2.0, 2.0, 0.0};
		Plot3DPanel plot = new Plot3DPanel();
		plot.addLinePlot("my plot", x,y,z);
		JFrame frame = new JFrame("a plot panel");
		   frame.setContentPane(plot);
		   frame.setVisible(true); //visualization end

	}

}
