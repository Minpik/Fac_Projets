import javafx.scene.paint.Color;

public class Face {
	private Vector3D points[];
	private int index;
	private Color color;
	private Cube cube;
	
	Face(Cube cube, int index, Color color) {
		this.cube = cube;
		this.index = index;
		this.color = color;
		
		this.points = new Vector3D[4];
	}
	
	final Cube getCube() {
		return cube;
	}
	
	void setPoint(int i, Vector3D point) {
		points[i] = point;
	}
	
	final Vector3D[] getPoints() {
		return points;
	}
	
	final int getIndex() {
		return index;
	}
	
	final Color getColor() {
		return color;
	}
	
	boolean canDisplay() {
		Vector3D v1, v2, n;
		v1 = new Vector3D(points[1].getX() - points[0].getX(),
				points[1].getY() - points[0].getY(),
				points[1].getZ() - points[0].getZ());
		
		v2 = new Vector3D(points[3].getX() - points[0].getX(),
				points[3].getY() - points[0].getY(),
				points[3].getZ() - points[0].getZ());
		
		n = Vector3D.crossProduct(v2, v1);
		n.normalize();
		
		return Vector3D.dotProduct(n, new Vector3D(points[0].getX(), points[0].getY(), points[0].getZ())) <= 0;
	}
	
	// retourne la coordonnée Z du milieu de la face
	double getZToCompare() {
		Vector3D v = new Vector3D(points[2].getX() - points[0].getX(),
				points[2].getY() - points[0].getY(),
				points[2].getZ() - points[0].getZ());
		
		return points[0].getZ() + (v.getZ() / 2);
	}
}
