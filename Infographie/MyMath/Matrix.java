package MyMath;

public class Matrix {
	private double [][]values;
	private static Matrix unity;
	static {
		unity = new Matrix(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1
				);
	}
	public static Matrix unity() {
		return unity;
	}
	public double get(int x,int y) {
		return values[x][y];
	}
	public static Matrix createTranslation(Vector v) {
		return new Matrix(
				1, 0, 0, v.get(0),
				0, 1, 0, v.get(1),
				0, 0, 1, v.get(2),
				0, 0, 0, 1
				);
	}
	/**
	 * 
	 * @param angle in radians
	 * @return
	 */
	public static Matrix createRotationX(double angle) {
		return new Matrix(
				1, 0, 0, 0,
				0, Math.cos(angle), -Math.sin(angle), 0,
				0, Math.sin(angle), Math.cos(angle), 0,
				0, 0, 0, 1
				);
	}
	public static Matrix createRotationY(double angle) {
		return new Matrix(
				Math.cos(angle), 0, Math.sin(angle), 0,
				0, 1, 0, 0,
				-Math.sin(angle), 0, Math.cos(angle), 0,
				0, 0, 0, 1
				);
	}
	public static Matrix createRotationZ(double angle) {
		return new Matrix(
				Math.cos(angle), -Math.sin(angle), 0, 0,
				Math.sin(angle), Math.cos(angle), 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1
				);
	}
	public static Matrix createPerspectiveZ(double distance) {
		return new Matrix(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 1/distance, 0);
	}
	public static Matrix createPerspectiveY(double distance) {
		return new Matrix(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 1/distance, 0, 0);
	}
	public static Matrix createPerspectiveX(double distance) {
		return new Matrix(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				1/distance, 0, 0, 0);
	}
	public static Matrix createParallelX() {
		return new Matrix(
				0, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 0);
	}
	public static Matrix createParallelY() {
		return new Matrix(
				1, 0, 0, 0,
				0, 0, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 0);
	}
	public static Matrix createParallelZ() {
		return new Matrix(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 0, 0,
				0, 0, 0, 0);
	}
	public Matrix(double a11, double a21, double a31, double a41,
			double a12, double a22, double a32, double a42,
			double a13, double a23, double a33, double a43,
			double a14, double a24, double a34, double a44) {
		values = new double[4][4];
		values[0][0] = a11;
		values[1][0] = a21;
		values[2][0] = a31;
		values[3][0] = a41;
		values[0][1] = a12;
		values[1][1] = a22;
		values[2][1] = a32;
		values[3][1] = a42;
		values[0][2] = a13;
		values[1][2] = a23;
		values[2][2] = a33;
		values[3][2] = a43;
		values[0][3] = a14;
		values[1][3] = a24;
		values[2][3] = a34;
		values[3][3] = a44;
	}
	public Matrix(double [][]values) {
		this.values = new double[4][4];
		for (int i=0; i<4; i++) {
			for (int j=0; j<4; j++) {
				this.values[i][j] = values[i][j];
			}
		}
	}
	public String toString() {
		String s = "";
		for (int j=0; j<4; j++) {
			s += "| ";
			for (int i=0; i<4; i++) {
				s += values[i][j]+" ";
			}
			s += "|\n";
		}
		return s;
	}
}
