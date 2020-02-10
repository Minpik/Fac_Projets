import MyMath.Point;
import MyMath.Segment;

public class Pyramid extends WorldObject {
	public Pyramid(int baseWidth, int height) {
		Point []points = new Point[5];
		points[0] = new Point(-baseWidth/2,-baseWidth/2,0);
		points[1] = new Point(-baseWidth/2,baseWidth/2,0);
		points[2] = new Point(baseWidth/2,baseWidth/2,0);
		points[3] = new Point(baseWidth/2,-baseWidth/2,0);
		points[4] = new Point(0,0,height);
		for (int i=0; i<4; i++) {
			add(new Segment(points[i],points[(i+1)%4]));
			add(new Segment(points[4],points[i]));
		}
	}
}
