import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import MyMath.*;

public class MyPanel extends JPanel {
	private Dimension dimension;
	private WorldObject object;
	public MyPanel(int w,int h) {
		dimension = new Dimension(w,h);
	}
	public void setWorldObject(WorldObject c) {
		object = c;
		repaint();
	}
	@Override
	public Dimension getPreferredSize() {
		return dimension;
	}
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, dimension.width, dimension.height);
		g.setColor(Color.BLACK);
		Matrix m = Matrix.createPerspectiveZ(400);
//		Matrix m = Matrix.createParallelZ();
		WorldObject currentObject = object;
		if (currentObject!=null) {
			for (Segment s : currentObject) {
//				System.out.println("---"+ s);
				Point b = s.getBegin();
				Point e = s.getEnd();
				// clip?
				b = Calculus.multiply(m, b);
				e = Calculus.multiply(m, e);
				// in case of parallel projection
				//   homogeneous coordinates are degenerated, take care of
				// in case of perspective projection
				//   may need to homogenize...
//				System.out.println("    "+b+" "+e);
				if (b.getT()!=0) b.homogenize();
				if (e.getT()!=0) e.homogenize();
//				System.out.println("     "+b+" "+e);
				// center on screen... (aka viewport transform)
				g.drawLine((int)(b.getX()+dimension.getWidth()/2), (int)(b.getY()+dimension.getHeight()/2),
						(int)(e.getX()+dimension.getWidth()/2), (int)(e.getY()+dimension.getHeight()/2));
			}
		}
	}
}
