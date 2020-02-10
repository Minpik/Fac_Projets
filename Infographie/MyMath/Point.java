package MyMath;

public class Point extends Abstract4D {
	public Point(Point p) {
		super(p);
	}
	public Point(double []v) {
		super(v);
	}
	public Point(double x,double y,double z) {
		super(x,y,z);
	}
	public String toString() {
		return "["+get(0)+","+get(1)+","+get(2)+","+get(3)+"]";
	}
}
