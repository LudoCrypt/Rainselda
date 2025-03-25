package net.ludocrypt.rainselda.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import net.ludocrypt.rainselda.Rainselda;
import net.ludocrypt.rainselda.region.Mapos;

public class ShapeRenderer {

	public static final int SHAPE_RECT = 0;
	public static final int SHAPE_ROUND_RECT = 1;
	public static final int SHAPE_ROUND_RECT_FIXED = 2;

	final SpriteBatch batch;
	final ShaderProgram shapesShader;
	final Texture texture;

	int shape;
	int thickness;
	boolean fill;

	boolean useRes;
	int resX;
	int resY;

	double radius;

	boolean falloff;
	double falloffX;
	double falloffY;

	double falloffMag;
	double falloffSteepness;

	public ShapeRenderer(SpriteBatch batch, ShaderProgram shapesShader, Texture texture) {
		this.batch = batch;
		this.shapesShader = shapesShader;
		this.texture = texture;
	}

	public ShapeRenderer radius(double radius) {
		this.radius = radius;
		return this;
	}

	public ShapeRenderer shape(int shape) {
		this.shape = shape;
		return this;
	}

	public ShapeRenderer thickness(int thickness) {
		this.thickness = thickness;
		return this;
	}

	public ShapeRenderer falloff(boolean falloff) {
		this.falloff = falloff;
		return this;
	}

	public ShapeRenderer falloffX(double falloffX) {
		this.falloffX = falloffX;
		return this;
	}

	public ShapeRenderer falloffY(double falloffY) {
		this.falloffY = falloffY;
		return this;
	}

	public ShapeRenderer falloffMag(double falloffMag) {
		this.falloffMag = Math.max(falloffMag, 0.0);
		return this;
	}

	public ShapeRenderer falloffSteepness(double falloffSteepness) {
		this.falloffSteepness = Math.max(falloffSteepness, 0.0);
		return this;
	}

	public ShapeRenderer fill(boolean fill) {
		this.fill = fill;
		return this;
	}

	public ShapeRenderer useRes(boolean useRes) {
		this.useRes = useRes;
		return this;
	}

	public ShapeRenderer resX(int resX) {
		this.resX = resX;
		this.useRes = true;
		return this;
	}

	public ShapeRenderer resY(int resY) {
		this.resY = resY;
		this.useRes = true;
		return this;
	}

	public void drawShapeFixed(Mapos pos, Mapos size) {
		if (!this.useRes) {
			this.resX = (int) size.getX();
			this.resY = (int) size.getY();
		}

		this.drawShape(affixScale(pos.getX(), pos.getY()), affixScale(size.getX(), size.getY()));
	}

	public void drawShapeFixed(double x, double y, Mapos size) {
		this.drawShapeFixed(new Mapos(x, y), new Mapos(size.getX(), size.getY()));
	}

	public void drawShapeFixed(Mapos pos, double width, double height) {
		this.drawShapeFixed(new Mapos(pos.getX(), pos.getY()), new Mapos(width, height));
	}

	public void drawShapeFixed(double x, double y, double width, double height) {
		this.drawShapeFixed(new Mapos(x, y), new Mapos(width, height));
	}

	public void drawShape(Mapos pos, Mapos size) {
		this.drawShape(pos.getX(), pos.getY(), size.getX(), size.getY());
	}

	public void drawShape(double x, double y, Mapos size) {
		this.drawShape(x, y, size.getX(), size.getY());
	}

	public void drawShape(Mapos pos, double width, double height) {
		this.drawShape(pos.getX(), pos.getY(), width, height);
	}

	public void drawShape(double x, double y, double width, double height) {
		this.batch.begin();
		this.batch.setShader(this.shapesShader);

		this.shapesShader.setUniformi("u_shape", this.shape);
		this.shapesShader.setUniformi("u_res", this.resX, this.resY);
		this.shapesShader.setUniformi("u_fill", this.fill ? 1 : 0);
		this.shapesShader.setUniformf("u_radius", (float) this.radius);
		this.shapesShader.setUniformi("u_thickness", this.thickness);

		this.shapesShader.setUniformi("u_falloff", this.falloff ? 1 : 0);
		this.shapesShader.setUniformf("u_falloffCenter", (float) this.falloffX, (float) this.falloffY);
		this.shapesShader.setUniformf("u_falloffMag", (float) this.falloffMag, (float) this.falloffSteepness);

		this.batch.draw(this.texture, (float) x, (float) y, (float) width, (float) height, -1, -1, 1, 1);
		this.batch.end();
	}

	public static Mapos affixScale(Texture texture, double height) {
		return affixScale((double) texture.getWidth() / (double) texture.getHeight() * height, height);
	}

	public static Mapos affixScale(Mapos size) {
		return affixScale(size.getX(), size.getY());
	}

	public static Mapos affixScale(double width, double height) {
		double widthScale = 640.0 / (double) Rainselda.INSTANCE.getWidth();
		double heightScale = 480.0 / (double) Rainselda.INSTANCE.getHeight();

		return new Mapos(width * widthScale, height * heightScale);
	}

	public static Mapos affixScaleCentered(Mapos size) {
		return affixScaleCentered(size.getX(), size.getY());
	}

	public static Mapos affixScaleCentered(double width, double height) {
		return affixScale(width - 320, height - 240).add(320, 240);
	}

	public static Mapos unfixScale(Mapos size) {
		return unfixScale(size.getX(), size.getY());
	}

	public static Mapos unfixScale(double width, double height) {
		double widthScale = width * (double) Rainselda.INSTANCE.getWidth();
		double heightScale = height * (double) Rainselda.INSTANCE.getHeight();

		return new Mapos(widthScale / 640.0, heightScale / 480.0);
	}

	public static Mapos unfixScaleCentered(double width, double height) {
		return unfixScale(width - 320, height - 240).add(320, 240);
	}

	public static Mapos unfixScaleCentered(Mapos size) {
		return unfixScaleCentered(size.getX(), size.getY());
	}

}
