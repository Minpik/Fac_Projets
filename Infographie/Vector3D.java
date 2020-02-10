public class Vector3D {
	private double x, y, z, t;

	static double dotProduct(Vector3D v1, Vector3D v2) {
		return v1.getX() * v2.getX() + v1.getY() * v2.getY() + v1.getZ() * v2.getZ();
	}

	static Vector3D crossProduct(Vector3D v1, Vector3D v2) {
		return new Vector3D(v1.getY() * v2.getZ() - v1.getZ() * v2.getY(),
				v1.getZ() * v2.getX() - v1.getX() * v2.getZ(), v1.getX() * v2.getY() - v1.getY() * v2.getX());
	}

	Vector3D(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.t = 1;
	}

	Vector3D(double values[]) {
		if (values.length == 4) {
			x = values[0];
			y = values[1];
			z = values[2];
			t = values[3];
		}
	}

	void scale() {
		x /= t;
		y /= t;
		z /= t;
		t /= t;
	}

	void normalize() {
		double k = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
		x /= k;
		y /= k;
		z /= k;
	}
	
	final double getX() {
		return x;
	}

	final double getY() {
		return y;
	}

	final double getZ() {
		return z;
	}

	final double getT() {
		return t;
	}

	final double get(int i) {
		switch (i) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		case 3:
			return t;
		default:
			return 0;
		}
	}

	@Override
	public String toString() {
		return x + ", " + y + ", " + z + ", " + t;
	}
}
