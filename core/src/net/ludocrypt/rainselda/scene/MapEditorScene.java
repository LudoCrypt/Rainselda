package net.ludocrypt.rainselda.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.ludocrypt.rainselda.Rainselda;
import net.ludocrypt.rainselda.render.Viewport;
import net.ludocrypt.rainselda.util.MathHelper;

public class MapEditorScene extends Scene {

    Rainselda rainselda;
    SpriteBatch batch;
    Viewport viewport;
    ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        ShaderProgram.pedantic = false;
        rainselda = Rainselda.INSTANCE;
        viewport = new Viewport(rainselda);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw the background
        // TODO: Theme
        shapeRenderer.setColor(MathHelper.hexToColor("141414"));
        shapeRenderer.rect(0, 0, rainselda.getWidth(), rainselda.getHeight());

        shapeRenderer.end();
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // For some reason the projection matrix only gets set once so this just updates it to match the current resolution
        shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        shapeRenderer.updateMatrices();
    }

    @Override
    public void dispose() {
        this.batch.dispose();
        this.shapeRenderer.dispose();
    }

}
