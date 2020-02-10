import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

public class MainController implements Initializable {
	@FXML
	private Canvas canvas;
	@FXML
	private CheckMenuItem menuItemDelete; 
	@FXML
	private CheckMenuItem menuItemColor;
	@FXML
	private MenuItem save;
	
	private Datas datas;
	private double rotateX, rotateY, rotateZ;
	private double transX, transY, transZ;
	private SimpleBooleanProperty canDelete;
	private SimpleBooleanProperty displayColor;
	private List<Face> facesTransform;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.datas = new Datas(2, 3);
		datas.addCube(new Cube(-1, 0, 0, Color.BLUE));
		datas.addCube(new Cube(0, 0, 0, Color.WHITE));
		datas.addCube(new Cube(1, 0, 0, Color.RED));
		
		//this.datas = CreateSpace.createCubeInCube(3, Color.DARKGRAY);
		//this.datas = CreateSpace.createLineInCube(21, Color.DARKGREY, 0, 0, 0, -1, 0, 10);
		
		this.transZ = 200;
		
		this.canDelete = new SimpleBooleanProperty(true);
		this.canDelete.bind(menuItemDelete.selectedProperty());
		
		this.displayColor = new SimpleBooleanProperty(true);
		this.displayColor.bind(menuItemColor.selectedProperty());
		
		this.facesTransform = new ArrayList<Face>();

		/*Matrix m1 = new Matrix(new double[][] { { 0, 3, 5 }, { 5, 5, 2 } });

		// Matrix m1 = Matrix.unity();

		Matrix m2 = new Matrix(new double[][] { { 3, 4 }, { 3, -2 }, { 4, -2 } });

		// Matrix m2 = Matrix.rotationX(1);

		Matrix m3 = m1.multipliedBy(m2);
		System.out.println(m3);

		Vector3D v1 = new Vector3D(1, 2, 3);
		Vector3D v2 = m3.multipliedBy(v1);
		System.out.println(v2);*/

		new MyAnimationTimer().start();
	}

	class MyAnimationTimer extends AnimationTimer {
		private GraphicsContext gc;

		public MyAnimationTimer() {
			super();
			gc = canvas.getGraphicsContext2D();
			gc.setFont(new Font("System", 15));
			gc.setFill(Color.BLACK);
			gc.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
		}

		@Override
		public void handle(long now) {
			gc.setFill(Color.BLACK);
			gc.fillRect((-canvas.getWidth() / 2), (-canvas.getHeight() / 2), canvas.getWidth(), canvas.getHeight());

			/*
			 * gc.setFill(Color.BLUE); int radius = 10; gc.fillOval(50-radius, 50-radius,
			 * radius, radius);
			 */

			// on crée une matrice unitaire puis on applique les diverses transformations
			Matrix transformation = Matrix.unity();
			transformation = Matrix.rotationX(Math.toRadians(rotateX)).multipliedBy(transformation);
			transformation = transformation.multipliedBy(Matrix.rotationY(Math.toRadians(rotateY)));
			transformation = transformation.multipliedBy(Matrix.rotationZ(Math.toRadians(rotateZ)));
			transformation = Matrix.translation(new Vector3D(transX, transY, transZ)).multipliedBy(transformation);
			
			// on crée les faces à partir des points transformés par la matrice de transformation
			facesTransform.clear();
			//ListIterator<Cube> li = datas.getCubes().listIterator();
			// while (li.hasNext()) {
			for (int i = 0; i < datas.getCubes().length; i++) {
				// Cube normalCube = li.next();
				Cube normalCube = datas.getCubes()[i];
				if (normalCube != null) {
					Cube transformCube = normalCube.getTransformedCube(transformation);
					
					Face f;
					for (int j = 0; j < 6; j++) {
						f = new Face(normalCube, j, transformCube.getColor());
						int points[] = getFacePointsIndices(j);
						for (int k = 0; k < points.length; k++) {
							f.setPoint(k, transformCube.getPoint(points[k]));
						}

						if (f.canDisplay())
							facesTransform.add(f);
					}
				}
			}
			
			// on tri les faces en fonction de z
			Collections.sort(facesTransform, Collections.reverseOrder(Comparator.comparing(Face::getZToCompare)));
			
			// chaque face
			for (Face face : facesTransform) {
				//System.out.println(face.getPoints()[0].getZ());
				/*// chaque face
				for (int i = 0; i < 6; i++) {
					int points[] = getFacePointsIndices(i);
					Vector3D transformed[] = new Vector3D[points.length];
					for (int j = 0; j < points.length; j++) {
						transformed[j] = cube.getPoint(points[j]);
					}*/
				
				int points[] = getFacePointsIndices(face.getIndex());
				Vector3D projected[] = new Vector3D[points.length];
				for (int j = 0; j < points.length; j++) {
					projected[j] = Matrix.projection(400).multipliedBy(face.getPoints()[j]);

					if (projected[j].getT() != 0)
						projected[j].scale();
				}
					
				// couleur
				if (displayColor.getValue()) {
					gc.setFill(face.getColor());
					displayPolygon(projected);
				}

				// grille
				gc.setStroke(Color.WHITE);
				for (int j = 0; j < points.length; j++) {
					connectPoints(projected[j], projected[(j + 1) % 4]);
				}
				//}
			}
		}
		
		void connectPoints(Vector3D p1, Vector3D p2) {
			gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		}
		
		void displayPolygon(Vector3D points[]) {
			int n = points.length;
			double x[] = new double[n];
			double y[] = new double[n];
			
			for (int i = 0; i < n; i++) {
				x[i] = points[i].getX();
				y[i] = points[i].getY();
			}
			
			gc.fillPolygon(x, y, n);
		}
	}
	
	int[] getFacePointsIndices(int face) {
		int points[] = new int[4];
		switch (face) {
		case 0: // devant
			points[0] = 0;
			points[1] = 1;
			points[2] = 2;
			points[3] = 3;
			break;
		case 1: // haut
			points[0] = 4;
			points[1] = 5;
			points[2] = 1;
			points[3] = 0;
			break;
		case 2: // derrière
			points[0] = 7;
			points[1] = 6;
			points[2] = 5;
			points[3] = 4;
			break;
		case 3: // bas
			points[0] = 3;
			points[1] = 2;
			points[2] = 6;
			points[3] = 7;
			break;
		case 4: // droite
			points[0] = 1;
			points[1] = 5;
			points[2] = 6;
			points[3] = 2;
			break;
		case 5: // droite
			points[0] = 4;
			points[1] = 0;
			points[2] = 3;
			points[3] = 7;
			break;
		}

		return points;
	}

	@FXML
	private void onKeyPressed(KeyEvent event) {
		switch (event.getCode()) {
		case UP:
			rotateX -= 5;
			break;
		case DOWN:
			rotateX += 5;
			break;
		case LEFT:
			rotateY -= 5;
			break;
		case RIGHT:
			rotateY += 5;
			break;
		case W:
			rotateZ -= 5;
			break;
		case X:
			rotateZ += 5;
			break;
		case Z:
			transY -= 5;
			break;
		case S:
			transY += 5;
			break;
		case Q:
			transX -= 5;
			break;
		case D:
			transX += 5;
			break;
		default:
			break;
		}
	}
	
	@FXML
	private void onScroll(ScrollEvent event) {
		if (event.getDeltaY() > 0)
			transZ -= 5;
		else
			transZ += 5;
		
		if (transZ < 0)
			transZ = 0;
	}

	@FXML
	private void onMouseClicked(MouseEvent event) {
		double x = event.getX() - canvas.getWidth() / 2;
		double y = event.getY() - canvas.getHeight() / 2;

		Vector3D mouseVector = new Vector3D(x, y, 0);
		System.out.println(mouseVector);
		
		if (canDelete.getValue()) {
			// chaque face
			// for (Face face : facesTransform) {
			for (int i = facesTransform.size() - 1; i >= 0; i--) {
				Face face = facesTransform.get(i);
				int points[] = getFacePointsIndices(face.getIndex());
				Vector3D projected[] = new Vector3D[points.length];
				for (int j = 0; j < points.length; j++) {
					projected[j] = Matrix.projection(400).multipliedBy(face.getPoints()[j]);

					if (projected[j].getT() != 0)
						projected[j].scale();
				}

				// aire du polygone
				double polygoneArea = area(projected[0], projected[1], projected[2])
						+ area(projected[0], projected[3], projected[2]);

				// somme de toutes aires des triangles créés à partir de la souris
				double sumTrianglesArea = area(projected[0], mouseVector, projected[1])
						+ area(projected[1], mouseVector, projected[2]) + area(projected[3], mouseVector, projected[2])
						+ area(projected[0], mouseVector, projected[3]);
				// System.out.println(polygoneArea);
				// System.out.println(sumTrianglesArea);

				if ((int) (polygoneArea) == (int) (sumTrianglesArea)) {
					datas.removeCube(face.getCube());
					return;
				}
			}
		}
	}
	
	double area(Vector3D p1, Vector3D p2, Vector3D p3) {
		return Math.abs((p1.getX() * (p2.getY() - p3.getY()) + p2.getX() * (p3.getY() - p1.getY())
				+ p3.getX() * (p1.getY() - p2.getY())) / 2.0);
	}
	
	@FXML
	void load() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
		if (new File("save").exists())
			fileChooser.setInitialDirectory(new File("save"));
		else
			fileChooser.setInitialDirectory(new File("."));
		File f = fileChooser.showOpenDialog(Main.getStage());
		if (f != null) {
			datas = Datas.load(f.getAbsolutePath());
			reset();
		}
	}
	
	@FXML
	void save() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
		fileChooser.setInitialFileName("save");
		if (new File("save").exists())
			fileChooser.setInitialDirectory(new File("save"));
		else
			fileChooser.setInitialDirectory(new File("."));
		File f = fileChooser.showSaveDialog(Main.getStage());
		if (f != null) {
			datas.save(f.getAbsolutePath());
		}
	}
	
	@FXML
	void reset() {
		this.rotateX = 0;
		this.rotateY = 0;
		this.rotateZ = 0;
		
		this.transZ = 200;
		this.transX = 0;
		this.transY = 0;
	}
}
