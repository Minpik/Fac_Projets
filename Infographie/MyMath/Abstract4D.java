package MyMath;

public abstract class Abstract4D {
	private double []coordinates;
	protected Abstract4D(Abstract4D c) {
		this(c.coordinates);
	}
	public Abstract4D(double []v) {
		coordinates = new double[4];
		for (int i=0; i<4; i++) coordinates[i] = v[i];
	}
	public Abstract4D(double x,double y,double z) {
		coordinates = new double[4];
		coordinates[0] = x;
		coordinates[1] = y;
		coordinates[2] = z;
		coordinates[3] = 1;
	}
	public double get(int i) { return coordinates[i]; }
	public double getX() { return get(0); }
	public double getY() { return get(1); }
	public double getZ() { return get(2); }
	public double getT() { return get(3); }
	public void homogenize() { 
		for (int i=0; i<4; i++) coordinates[i] /= coordinates[3];
	}

}
