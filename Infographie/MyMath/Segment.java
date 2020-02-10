package MyMath;
public class Segment {
	private Point begin, end;
	public Segment(Point begin, Point end) {
		this.begin = begin;
		this.end = end;
	}
	public Point getBegin() {
		return begin;
	}
	public Point getEnd() {
		return end;
	}
	public String toString() {
		return "{"+begin+","+end+"}";
	}
}
