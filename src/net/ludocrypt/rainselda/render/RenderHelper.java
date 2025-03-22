package net.ludocrypt.rainselda.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

import net.ludocrypt.rainselda.Rainselda;
import net.ludocrypt.rainselda.region.Mapos;

// yayayayayaya i know i know "this should be a separate class for each thing !!!" cmon please idfc
public class RenderHelper {

	public static double lerp(double a, double b, double t) {
		return a + (b - a) * t;
	}

	public static double clamp(double t, double min, double max) {
		return Math.max(Math.min(t, max), min);
	}

	public static float clamp(float t, float min, float max) {
		return Math.max(Math.min(t, max), min);
	}

	public static Color hexToColor(String hex) {
		if (hex.startsWith("#")) {
			hex = hex.substring(1);
		}

		int r = hex.length() >= 2 ? Integer.parseInt(hex.substring(0, 2), 16) : 255;
		int g = hex.length() >= 4 ? Integer.parseInt(hex.substring(2, 4), 16) : 0;
		int b = hex.length() >= 6 ? Integer.parseInt(hex.substring(4, 6), 16) : 0;
		int a = hex.length() >= 8 ? Integer.parseInt(hex.substring(6, 8), 16) : 255;

		float fr = r / 255f;
		float fg = g / 255f;
		float fb = b / 255f;
		float fa = a / 255f;

		return new Color(fr, fg, fb, fa);
	}

	// I hope you fucking die and kill yourself and your family too :smiling_face_with_three_hearts:
	public static void renderText(SpriteBatch batch, ShaderProgram fontShader, BitmapFont font, double x, double y, double scale, double width, String text, Anchor anchor) {
		batch.begin();
		Matrix4 matBack = batch.getProjectionMatrix().cpy();
		Matrix4 mat = batch.getProjectionMatrix();

		mat.scl((float) scale);

		batch.setShader(fontShader);

		Mapos fontScale = ShapeRenderer.affixScale(1, 1);

		font.getData().setScale((float) fontScale.getX(), (float) fontScale.getY());

		double transX = Rainselda.getScreenU(anchor.worldOffset(x, width)) / scale;
		double transY = Rainselda.getScreenV(y) / scale;

		for (int j = 0; j < 2; j++) {

			if (j == 0) {
				batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			} else {
				batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE, GL20.GL_SRC_ALPHA, GL20.GL_ONE);
			}

			for (int k = 0; k < 3; k++) {
				fontShader.setUniformi("u_colorize", j);
				fontShader.setUniformi("u_offset", k);

				font.getCache().clear();
				GlyphLayout layout = font.getCache().addText(text, 0, 0);

				font.draw(batch, text, (float) anchor.textOffset(transX, layout.width), (float) transY);
				batch.flush();
			}
		}

		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		batch.end();
		batch.setShader(null);
		batch.setProjectionMatrix(matBack);
	}

	public enum Anchor {
		LEFT,
		RIGHT,
		CENTER;

		private double worldOffset(double x, double width) {
			switch (this) {
				case CENTER:
					return x + width / 2 - 1;
				case LEFT:
					return x;
				case RIGHT:
					return x + width;
				default:
					return x;
			}
		}

		private double textOffset(double x, double width) {
			switch (this) {
				case CENTER:
					return x - width / 2;
				case LEFT:
					return x;
				case RIGHT:
					return x - width;
				default:
					return x;
			}
		}
	}

}
