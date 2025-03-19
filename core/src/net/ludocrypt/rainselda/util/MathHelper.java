package net.ludocrypt.rainselda.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;

import net.ludocrypt.rainselda.Rainselda;

// yayayayayaya i know i know "this should be a separate class for each thing !!!" cmon please idfc
public class MathHelper {

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

    public static void drawRoundedRectangle(ShapeRenderer shapeRenderer, float x, float y, float width, float height, float radius) {
        drawArc(shapeRenderer, x + width - radius, y + height - radius, radius, 0, 90);
        drawArc(shapeRenderer, x + radius, y + height - radius, radius, 90, 90);
        drawArc(shapeRenderer, x + width - radius, y + radius, radius, 270, 90);
        drawArc(shapeRenderer, x + radius, y + radius, radius, 180, 90);

        shapeRenderer.line(x + radius, y, x + width - radius, y);
        shapeRenderer.line(x + width, y + radius, x + width, y + height - radius);
        shapeRenderer.line(x + width - radius, y + height, x + radius, y + height);
        shapeRenderer.line(x, y + height - radius, x, y + radius);
    }

    public static void drawArc(ShapeRenderer shapeRenderer, float cx, float cy, float radius, float startAngle, float arcAngle) {

        int segments = Math.max(1, (int) (6 * (float) Math.cbrt(radius) * (arcAngle / 360.0f)));
        float step = arcAngle / segments;
        float angle = startAngle;

        float xPrev = cx + radius * (float) Math.cos(Math.toRadians(angle));
        float yPrev = cy + radius * (float) Math.sin(Math.toRadians(angle));

        for (int i = 0; i <= segments; i++) {
            float rad = (float) Math.toRadians(angle);
            float xPos = cx + radius * MathUtils.cos(rad);
            float yPos = cy + radius * MathUtils.sin(rad);

            shapeRenderer.line(xPrev, yPrev, xPos, yPos);

            xPrev = xPos;
            yPrev = yPos;
            angle += step;
        }
    }

    // I hope you fucking die and kill yourself and your family too :smiling_face_with_three_hearts:
    public static void renderText(SpriteBatch batch, ShaderProgram fontShader, BitmapFont font, double x, double y, double scale, String text) {
        batch.begin();
        Matrix4 matBack = batch.getProjectionMatrix().cpy();
        Matrix4 mat = batch.getProjectionMatrix();

        mat.scl((float) scale);

        batch.setShader(fontShader);

        double scaleX = (640.0 / (double) Rainselda.INSTANCE.getWidth());
        double scaleY = (480.0 / (double) Rainselda.INSTANCE.getHeight());

        font.getData().setScale((float) scaleX, (float) scaleY);

        double transX = Rainselda.getScreenU(x) / scale;
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
                font.draw(batch, text, (float) transX, (float) transY);
                batch.flush();
            }
        }

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch.end();
        batch.setShader(null);
        batch.setProjectionMatrix(matBack);
    }

}
