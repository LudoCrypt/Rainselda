package net.ludocrypt.rainselda.render;

import com.badlogic.gdx.graphics.Color;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.DirectContext;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.TextBlob;
import io.github.humbleui.skija.shaper.Shaper;
import io.github.humbleui.types.Rect;
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

	public static Paint paint(Color color) {
		return new Paint().setColor(Color.rgba8888(color.a, color.r, color.g, color.b));
	}

	public static void renderText(DirectContext ctx, Canvas canvas, Font font, Paint paint, String text, double x, double y, double boxWidth, double boxHeight, Anchor anchor) {
		TextBlob blob = Shaper.make().shape(text, font);

		if (blob != null) {
			Rect bounds = font.measureText(text);
			Mapos pos = anchor.anchor(new Mapos(boxWidth, boxHeight), new Mapos(bounds.getWidth(), bounds.getHeight())).add(x, y);
			canvas.drawTextBlob(blob, (float) pos.getX(), (float) (Rainselda.INSTANCE.getHeight() - pos.getY()), paint);
		}

		ctx.flush();
		ctx.resetGLAll();
	}

	public enum Anchor {
		TOP_LEFT(0, 0),
		TOP(1, 0),
		TOP_RIGHT(2, 0),
		LEFT(0, 1),
		CENTER(1, 1),
		RIGHT(2, 1),
		BOTTOM_LEFT(0, 2),
		BOTTOM(1, 2),
		BOTTOM_RIGHT(2, 2);

		int ax;
		int ay;

		private Anchor(int ax, int ay) {
			this.ax = ax;
			this.ay = ay;
		}

		// Anchor's both the X and Y coordinates
		public Mapos anchor(Mapos shapeBounds, Mapos textBounds) {
			return new Mapos(anchorX(shapeBounds.getX(), textBounds.getX()), anchorY(shapeBounds.getY(), textBounds.getY()));
		}

		private double anchorX(double shapeBound, double textBound) {
			return switch (ax) {
				case 0 -> 0;
				case 1 -> shapeBound / 2.0 - textBound / 2.0;
				case 2 -> shapeBound - textBound;
				default -> throw new IllegalArgumentException("Unexpected value: " + ax);
			};
		}

		private double anchorY(double shapeBound, double textBound) {
			return switch (ay) {
				case 0 -> textBound / 2.0;
				case 1 -> textBound - shapeBound / 2.0;
				case 2 -> -shapeBound + 3.0 * textBound / 2.0;
				default -> throw new IllegalArgumentException("Unexpected value: " + ay);
			};
		}

	}

}
