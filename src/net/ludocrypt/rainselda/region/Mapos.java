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

	public Mapos add(Mapos other) {
		return new Mapos(x + other.x, y + other.y);
	}

	public Mapos sub(Mapos other) {
		return new Mapos(x - other.x, y - other.y);
	}

	public Mapos add(double xOther, double yOther) {
		return new Mapos(x + xOther, y + yOther);
	}

	public Mapos sub(double xOther, double yOther) {
		return new Mapos(x - xOther, y - yOther);
	}

	public Mapos mul(double factor) {
		return new Mapos(x * factor, y * factor);
	}

	public Mapos mul(double otherX, double otherY) {
		return new Mapos(x * otherX, y * otherY);
	}

	public Mapos mul(Mapos other) {
		return new Mapos(x * other.x, y * other.y);
	}

	public Mapos div(double factor) {
		return new Mapos(x / factor, y / factor);
	}

	public Mapos div(double otherX, double otherY) {
		return new Mapos(x / otherX, y / otherY);
	}

	public Mapos div(Mapos other) {
		return new Mapos(x / other.x, y / other.y);
	}

	public Mapos floor() {
		return new Mapos(Math.floor(x), Math.floor(y));
	}

	public Mapos round() {
		return new Mapos(Math.round(x), Math.round(y));
	}

	public Mapos neg() {
		return new Mapos(-x, -y);
	}

	public double dot(Mapos other) {
		return x * other.x + y * other.y;
	}

	public double magnitude() {
		return Math.sqrt(this.dot(this));
	}

	public double magnitudeSqr() {
		return this.dot(this);
	}

	public double magnitudeMann() {
		return Math.abs(this.x) + Math.abs(this.y);
	}

	public Mapos normalize() {
		double mag = magnitude();
		return mag == 0 ? this : mul(1.0 / mag);
	}

	public Mapos lerp(Mapos other, double t) {
		return new Mapos(x + (other.x - x) * t, y + (other.y - y) * t);
	}

	public Mapos lerp(double xOther, double yOther, double t) {
		return new Mapos(x + (xOther - x) * t, y + (yOther - y) * t);
	}

	public Mapos perpendicular() {
		return new Mapos(-y, x);
	}

	public double distance(Mapos other) {
		double dx = x - other.x;
		double dy = y - other.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public double distanceSqr(Mapos other) {
		double dx = x - other.x;
		double dy = y - other.y;
		return dx * dx + dy * dy;
	}

	public double distanceMann(Mapos other) {
		double dx = x - other.x;
		double dy = y - other.y;
		return Math.abs(dx) + Math.abs(dy);
	}

	@Override
	public String toString() {
		return Double.toString(x) + ", " + Double.toString(y) + ", " + z;
	}

}
