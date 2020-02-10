import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javafx.scene.paint.Color;

public class Datas {
	// private ArrayList<Cube> cubes;
	private Cube[] cubes;
	private int format;
	private int size;

	public Datas(int format, int size) {
		super();
		cubes = new Cube[(int) Math.pow(size, 3)];
		this.format = format;
		this.size = size;
	}

	final Cube[] getCubes() {
		return cubes;
	}

	void addCube(Cube cube) {
		int origin = -(size / 2);
		System.out.println(((cube.getZ() - origin) * size) + ((cube.getY() - origin) * (int) Math.pow(size, 2))
				+ (cube.getX() - origin));
		cubes[((cube.getZ() - origin) * size) + ((cube.getY() - origin) * (int) Math.pow(size, 2))
				+ (cube.getX() - origin)] = cube;
	}

	void removeCube(Cube cube) {
		int origin = -(size / 2);
		cubes[((cube.getZ() - origin) * size) + ((cube.getY() - origin) * (int) Math.pow(size, 2))
				+ (cube.getX() - origin)] = null;
	}

	static Datas load(String path) {
		Datas datas = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));

			int format = Integer.parseInt(reader.readLine());
			int size = Integer.parseInt(reader.readLine());
			datas = new Datas(format, size);
			// int origin = -(size / 2);

			String line = reader.readLine();
			Scanner scanner = new Scanner(line);
			// scanner.useDelimiter(" ");
			if (format == 1) {
				// for (int i = 0; i < Math.pow(size, 3); i++) {
				// datas.addCube(new Cube());
				// }
				for (int y = 0; y < size; y++) {
					for (int z = 0; z < size; z++) {
						for (int x = 0; x < size; x++) {
							if (scanner.nextInt() == 1)
								datas.addCube(new Cube(x - (size / 2), y - (size / 2), z - (size / 2), Color.GREY));
						}
					}
				}
			} else {
				int r, g, b;
				for (int y = 0; y < size; y++) {
					for (int z = 0; z < size; z++) {
						for (int x = 0; x < size; x++) {
							r = scanner.nextInt();
							g = scanner.nextInt();
							b = scanner.nextInt();
							if (r != -1 && g != -1 && b != -1) {
								datas.addCube(
										new Cube(x - (size / 2), y - (size / 2), z - (size / 2), Color.rgb(r, g, b)));
							}
						}
					}
				}
			}

			scanner.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return datas;
	}

	void save(String path) {
		/*
		 * if (!new File("save").exists()) { new File("save").mkdir(); }
		 */

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
			writer.write(format + "\n");
			writer.write(size + "\n");
			for (int i = 0; i < cubes.length; i++) {
				if (cubes[i] != null) {
					if (format == 1) {
						writer.write("1 ");
					} else {
						writer.write(String.valueOf(((int)cubes[i].getColor().getRed()) * 255) + " ");
						writer.write(String.valueOf(((int)cubes[i].getColor().getGreen()) * 255) + " ");
						writer.write(String.valueOf(((int)cubes[i].getColor().getBlue() * 255)) + " ");
					}
				} else {
					if (format == 1) {
						writer.write("0 ");
					} else {
						writer.write("-1 -1 -1 ");
					}
				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * final ArrayList<Cube> getCubes() { return new ArrayList<Cube>(cubes);
	 * //return cubes; }
	 * 
	 * void addCube(Cube cube) { cubes.add(cube); }
	 * 
	 * void removeCube(Cube cube) { cubes.remove(cube); }
	 */
}