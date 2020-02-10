import javafx.scene.paint.Color;

public class Cube {
	private int x, y, z;
	private Vector3D points[];
	private Color color;
	private static int SIZE = 50;
	
	Cube() {
		this.points = new Vector3D[8];
	}

	Cube(int x, int y, int z, Color color) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.color = color;
		points[0] = new Vector3D((x * SIZE) - (SIZE / 2), (y * SIZE) - (SIZE / 2), (z * SIZE) - (SIZE / 2)); // devant_haut_gauche
		points[1] = new Vector3D((x * SIZE) + (SIZE / 2), (y * SIZE) - (SIZE / 2), (z * SIZE) - (SIZE / 2)); // devant_haut_droit
		points[2] = new Vector3D((x * SIZE) + (SIZE / 2), (y * SIZE) + (SIZE / 2), (z * SIZE) - (SIZE / 2)); // devant_bas_droit
		points[3] = new Vector3D((x * SIZE) - (SIZE / 2), (y * SIZE) + (SIZE / 2), (z * SIZE) - (SIZE / 2)); // devant_bas_gauche
		points[4] = new Vector3D((x * SIZE) - (SIZE / 2), (y * SIZE) - (SIZE / 2), (z * SIZE) + (SIZE / 2)); // derriere_haut_gauche
		points[5] = new Vector3D((x * SIZE) + (SIZE / 2), (y * SIZE) - (SIZE / 2), (z * SIZE) + (SIZE / 2)); // derriere_haut_droit
		points[6] = new Vector3D((x * SIZE) + (SIZE / 2), (y * SIZE) + (SIZE / 2), (z * SIZE) + (SIZE / 2)); // derriere_bas_droit
		points[7] = new Vector3D((x * SIZE) - (SIZE / 2), (y * SIZE) + (SIZE / 2), (z * SIZE) + (SIZE / 2)); // derriere_bas_gauche
	}
	
	final Cube getTransformedCube(Matrix transformation) {
		Cube cube = new Cube();
		cube.color = color;
		for (int i = 0; i < points.length; i++) {
			cube.points[i] = transformation.multipliedBy(points[i]);
		}
		
		return cube;
	}

	final int getX() {
		return x;
	}

	final int getY() {
		return y;
	}

	final int getZ() {
		return z;
	}

	final Vector3D getPoint(int i) {
		return points[i];
	}

	final Color getColor() {
		return color;
	}
}
