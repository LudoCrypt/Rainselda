package net.ludocrypt.rainselda.scene;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import net.ludocrypt.rainselda.Rainselda;
import net.ludocrypt.rainselda.render.Viewport;

public class SceneTemplate extends Scene {

    Rainselda rainselda;
    SpriteBatch batch;
    Viewport viewport;

    @Override
    public void create() {
        ShaderProgram.pedantic = false;
        rainselda = Rainselda.INSTANCE;
        viewport = new Viewport(rainselda);
        batch = new SpriteBatch();
    }

    @Override
    public void render() {
        batch.begin();
        batch.setProjectionMatrix(this.viewport.composeMat());
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {
        this.batch.dispose();
    }

    @Override
    public InputAdapter getAdapter() {
        return null;
    }

    @Override
    public void filesDropped(String[] files) {

    }

}
