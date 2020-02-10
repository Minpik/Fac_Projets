import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import MyMath.Matrix;
import MyMath.Vector;

public class mainProjection {
	public static void createUI() {
		JFrame f = new JFrame();
		JPanel p = new JPanel();
		f.setContentPane(p);
		p.setLayout(new BorderLayout());
		MyPanel mp = new MyPanel(500,500);
		p.add(mp, BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
		
		Runnable r = () -> {
			WorldObject obj = new Sphere(8,16,100);//new Pyramid(100,200);//new Cube(100);
			int i = 0;
			while (true) {
				obj.resetTransform();
				obj.addTransform(Matrix.createRotationY(Math.PI*2/100*i));
				obj.addTransform(Matrix.createRotationX(Math.PI*2/100*i/3));
				obj.addTransform(Matrix.createTranslation(new Vector(0,0,500)));
				mp.setWorldObject(obj.getTransformedObject());
				try {
					Thread.sleep((int)(1/24.0*1000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i++;
			}
		};
		Thread t = new Thread(r);
		t.start();

	}
	public static void main(String[] args) {
		SwingUtilities.invokeLater(()->createUI());
	}

}
