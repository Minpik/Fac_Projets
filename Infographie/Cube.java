import MyMath.Calculus;
import MyMath.Point;
import MyMath.Segment;

public class Cube extends WorldObject {
	public Cube(double size) {
		Point []points = new Point[8];
		points[0] = new Point(-size/2,-size/2,-size/2);
		points[1] = new Point(-size/2,size/2,-size/2);
		points[2] = new Point(size/2,size/2,-size/2);
		points[3] = new Point(size/2,-size/2,-size/2);
		points[4] = new Point(-size/2,-size/2,size/2);
		points[5] = new Point(-size/2,size/2,size/2);
		points[6] = new Point(size/2,size/2,size/2);
		points[7] = new Point(size/2,-size/2,size/2);
		for (int i=0; i<4; i++) {
			add(new Segment(points[i],points[(i+1)%4]));
			add(new Segment(points[i+4],points[(i+1)%4+4]));
			add(new Segment(points[i],points[i+4]));
		}
	}
}
