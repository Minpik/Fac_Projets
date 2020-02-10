import java.util.Iterator;
import java.util.Vector;

import MyMath.Calculus;
import MyMath.Matrix;
import MyMath.Point;
import MyMath.Segment;

public class WorldObject implements Iterable<Segment> {
	private Vector<Segment> segments;
	private Matrix transform;
	protected WorldObject() {
		segments = new Vector<>();
	}
	protected void add(Segment s) {
		segments.add(s);
	}
	private WorldObject(Vector<Segment> vs) {
		segments = (Vector<Segment>) vs.clone();
	}
	public final void addTransform(Matrix m) {
		transform = MyMath.Calculus.multiply(m,transform);
	}
	public final void resetTransform() {
		transform = MyMath.Matrix.unity();
	}
	public WorldObject getTransformedObject() {
		Vector<Segment> vs = new Vector<>();
		for (Segment s : this) {
			Point b = s.getBegin();
			b = Calculus.multiply(transform, b);
			Point e = s.getEnd();
			e = Calculus.multiply(transform, e);
			Segment ns = new Segment (b,e);
			vs.add(ns);			
		}
		return new WorldObject(vs);
	}
	public Matrix getTransform() {
		return transform;
	}
	@Override
	public final Iterator<Segment> iterator() {
		return segments.iterator();
	}
}
