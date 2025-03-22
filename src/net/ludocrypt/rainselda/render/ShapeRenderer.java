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

	public static void drawShapeFixed(SpriteBatch batch, ShaderProgram shapesShader, Texture texture, int shape, int thickness, boolean fill, Mapos pos, Mapos size, double radius) {
		drawShape(batch, shapesShader, texture, shape, thickness, fill, affixScale(pos.getX(), pos.getY()), affixScale(size.getX(), size.getY()), (int) size.getX(), (int) size.getY(), radius);
	}

	public static void drawShapeFixed(SpriteBatch batch, ShaderProgram shapesShader, Texture texture, int shape, int thickness, boolean fill, double x, double y, Mapos size, double radius) {
		drawShape(batch, shapesShader, texture, shape, thickness, fill, affixScale(x, y), affixScale(size.getX(), size.getY()), (int) size.getX(), (int) size.getY(), radius);
	}

	public static void drawShapeFixed(SpriteBatch batch, ShaderProgram shapesShader, Texture texture, int shape, int thickness, boolean fill, Mapos pos, double width, double height, double radius) {
		drawShape(batch, shapesShader, texture, shape, thickness, fill, affixScale(pos.getX(), pos.getY()), affixScale(width, height), (int) width, (int) height, radius);
	}

	public static void drawShapeFixed(SpriteBatch batch, ShaderProgram shapesShader, Texture texture, int shape, int thickness, boolean fill, double x, double y, double width, double height, double radius) {
		drawShape(batch, shapesShader, texture, shape, thickness, fill, affixScale(x, y), affixScale(width, height), (int) width, (int) height, radius);
	}

	public static void drawShape(SpriteBatch batch, ShaderProgram shapesShader, Texture texture, int shape, int thickness, boolean fill, Mapos pos, Mapos size, int resX, int resY, double radius) {
		drawShape(batch, shapesShader, texture, shape, thickness, fill, pos.getX(), pos.getY(), size.getX(), size.getY(), resX, resY, radius);
	}

	public static void drawShape(SpriteBatch batch, ShaderProgram shapesShader, Texture texture, int shape, int thickness, boolean fill, double x, double y, Mapos size, int resX, int resY, double radius) {
		drawShape(batch, shapesShader, texture, shape, thickness, fill, x, y, size.getX(), size.getY(), resX, resY, radius);
	}

	public static void drawShape(SpriteBatch batch, ShaderProgram shapesShader, Texture texture, int shape, int thickness, boolean fill, Mapos pos, double width, double height, int resX, int resY, double radius) {
		drawShape(batch, shapesShader, texture, shape, thickness, fill, pos.getX(), pos.getY(), width, height, resX, resY, radius);
	}

	public static void drawShape(SpriteBatch batch, ShaderProgram shapesShader, Texture texture, int shape, int thickness, boolean fill, double x, double y, double width, double height, int resX, int resY, double radius) {
		batch.begin();
		batch.setShader(shapesShader);

		shapesShader.setUniformi("u_shape", shape);
		shapesShader.setUniformi("u_res", resX, resY);
		shapesShader.setUniformi("u_fill", fill ? 1 : 0);
		shapesShader.setUniformf("u_radius", (float) radius);
		shapesShader.setUniformi("u_thickness", thickness);

		batch.draw(texture, (float) x, (float) y, (float) width, (float) height, -1, -1, 1, 1);
		batch.end();
	}

	public static Mapos affixScale(Texture texture, double height) {
		return affixScale((double) texture.getWidth() / (double) texture.getHeight() * height, height);
	}

	public static Mapos affixScale(double width, double height) {
		double widthScale = 640.0 / (double) Rainselda.INSTANCE.getWidth();
		double heightScale = 480.0 / (double) Rainselda.INSTANCE.getHeight();

		return new Mapos(width * widthScale, height * heightScale);
	}

}
