
public class Matrix {
	private double values[][];
	private static Matrix unity;
	static {
		unity = new Matrix(new double[][] {
			{1, 0, 0, 0},
			{0, 1, 0, 0},
			{0, 0, 1, 0},
			{0, 0, 0, 1}
		});
	}
	
	static Matrix unity() {
		return unity;
	}
	
	static Matrix projection(double distance) {
		return new Matrix(new double[][] {
			{1, 0, 0, 0},
			{0, 1, 0, 0},
			{0, 0, 1, 0},
			{0, 0, 1 / distance, 0}
		});
	}
	
	static Matrix rotationX(double angle) {
		return new Matrix(new double[][] {
			{1, 0, 0, 0},
			{0, Math.cos(angle), -Math.sin(angle), 0},
			{0, Math.sin(angle), Math.cos(angle), 0},
			{0, 0, 0, 1}
		});
	}
	
	static Matrix rotationY(double angle) {
		return new Matrix(new double[][] {
			{Math.cos(angle), 0, Math.sin(angle), 0},
			{0, 1, 0, 0},
			{-Math.sin(angle), 0, Math.cos(angle), 0},
			{0, 0, 0, 1}
		});
	}
	
	static Matrix rotationZ(double angle) {
		return new Matrix(new double[][] {
			{Math.cos(angle), -Math.sin(angle), 0, 0},
			{Math.sin(angle), Math.cos(angle), 0, 0},
			{0, 0, 1, 0},
			{0, 0, 0, 1}
		});
	}
	
	static Matrix translation(Vector3D vector) {
		return new Matrix(new double[][] {
			{1, 0, 0, vector.getX()},
			{0, 1, 0, vector.getY()},
			{0, 0, 1, vector.getZ()},
			{0, 0, 0, 1}
		});
	}
	
	Matrix(double[][] v) {
		values = new double[v.length][v[0].length];
		for (int i = 0; i < v.length; i++) {
			for (int j = 0; j < v[i].length; j++) {
				values[i][j] = v[i][j];
			}
		}
	}
	
	Vector3D multipliedBy(Vector3D vector) {
		double v[] = new double[4];
		for (int i = 0; i < values.length; i++) {
			double sum = 0;
			for (int j = 0; j < values[i].length; j++) {
				sum += values[i][j] * vector.get(j);
			}
			v[i] = sum;
		}
		return new Vector3D(v);
	}
	
	Matrix multipliedBy(Matrix matrix) {
		double v[][] = new double[values.length][matrix.values[0].length];
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < matrix.values[0].length; j++) {
				double sum = 0;
				for (int k = 0; k < values[i].length; k++) {
					sum += values[i][k] * matrix.values[k][j]; 
				}
				v[i][j] = sum;
			}
		}
		return new Matrix(v);
	}
	
	/*final double getValue(int i, int j) {
		return values[i][j];
	}*/
	
	/*final int getNbRows() {
		return values.length;
	}
	
	final int getNbCols() {
		return values[0].length;
	}*/

	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[0].length; j++) {
				s += values[i][j] + ", ";
			}
			s += "\n";
		}
		
		return s;
	}
}
