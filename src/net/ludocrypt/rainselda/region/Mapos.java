package net.ludocrypt.rainselda.region;

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

}
