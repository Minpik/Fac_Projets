import javafx.scene.paint.Color;

public class CreateSpace {
	static Datas createCubeInCube(int size, Color color) {
		Datas datas = new Datas(1, size);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				for (int k = 0; k < size; k++) {
					datas.addCube(new Cube(i - (size / 2), j - (size / 2), k - (size / 2), color));
				}
			}
		}

		return datas;
	}

	static Datas createLineInCube(int size, Color color, int x1, int y1, int z1, int x2, int y2, int z2) {
		Datas datas = new Datas(1, size);

		datas.addCube(new Cube(x1, y1, z1, color));
		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);
		int dz = Math.abs(z2 - z1);

		int ys, xs, zs;
		if (x2 > x1)
			xs = 1;
		else
			xs = -1;
		if (y2 > y1)
			ys = 1;
		else
			ys = -1;
		if (z2 > z1)
			zs = 1;
		else
			zs = -1;

		// x
		if (dx >= dy && dx >= dz) {
			int p1 = 2 * dy - dx;
			int p2 = 2 * dz - dx;
			while (x1 != x2) {
				x1 += xs;
				if (p1 >= 0) {
					y1 += ys;
					p1 -= 2 * dx;
				}
				if (p2 >= 0) {
					z1 += zs;
					p2 -= 2 * dx;
				}
				p1 += 2 * dy;
				p2 += 2 * dz;
				datas.addCube(new Cube(x1, y1, z1, color));
			}
		// y
		} else if (dy >= dx && dy >= dz) {
			int p1 = 2 * dx - dy;
			int p2 = 2 * dz - dy;
			while (y1 != y2) {
				y1 += ys;
				if (p1 >= 0) {
					x1 += xs;
					p1 -= 2 * dy;
				}
				if (p2 >= 0) {
					z1 += zs;
					p2 -= 2 * dy;
				}
				p1 += 2 * dx;
				p2 += 2 * dz;
				datas.addCube(new Cube(x1, y1, z1, color));
			}
		// z
		} else {
			int p1 = 2 * dy - dz;
			int p2 = 2 * dx - dz;
			while (z1 != z2) {
				z1 += zs;
				if (p1 >= 0) {
					y1 += ys;
					p1 -= 2 * dz;
				}
				if (p2 >= 0) {
					x1 += xs;
					p2 -= 2 * dz;
				}
				p1 += 2 * dy;
				p2 += 2 * dx;
				datas.addCube(new Cube(x1, y1, z1, color));
			}
		}

		return datas;
	}
}
