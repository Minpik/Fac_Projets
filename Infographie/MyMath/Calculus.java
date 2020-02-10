package MyMath;

public class Calculus {
	public static Vector multiply(Matrix m,Vector v) {
		double []r = new double[4];
		for (int j=0; j<4; j++) {
			r[j] = 0;
			for (int i=0; i<4; i++) {
				r[j] += m.get(i, j)*v.get(i); 
			}
		}
		return new Vector(r);
	}
	public static Point multiply(Matrix m,Point p) {
		double []r = new double[4];
		for (int j=0; j<4; j++) {
			r[j] = 0;
			for (int i=0; i<4; i++) {
				r[j] += m.get(i, j)*p.get(i); 
			}
		}
		return new Point(r);
	}
	public static Matrix multiply(Matrix m1, Matrix m2) {
		double [][]values = new double[4][4];
		for (int i=0; i<4; i++) {
			for (int j=0; j<4; j++) {
				values[i][j] = 0;
				for (int k=0; k<4; k++) {
					values[i][j] += m1.get(k,j)*m2.get(i,k);
				}
			}
		}
		return new Matrix(values);
	}
}
