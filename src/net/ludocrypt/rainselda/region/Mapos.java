package net.ludocrypt.rainselda.region;

import com.badlogic.gdx.math.Matrix4;

public class Mapos {
	double x;
	double y;
	int z;

	public Mapos() {
	}

	public Mapos(double x, double y) {
		this.x = x;
		this.y = y;
		this.z = 0;
	}

	public Mapos(Mapos pos) {
		this.x = pos.x;
		this.y = pos.y;
		this.z = pos.z;
	}

	public Mapos(double x, double y, int z) {
		if (z > 2 || z < 0)
			throw new UnsupportedOperationException("Cannot have a layer outside of the range 0-2");

		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public Mapos mul(Matrix4 mat) {
		float[] pos = new float[] { (float) x, (float) y, 0, 1 };
		Matrix4.mulVec(mat.val, pos);

		pos[0] /= pos[3];
		pos[1] /= pos[3];

		return new Mapos(pos[0], pos[1]);
	}

	@Override
	public String toString() {
		return Double.toString(x) + ", " + Double.toString(y) + ", " + z;
	}

}
