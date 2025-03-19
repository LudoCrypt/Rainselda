package net.ludocrypt.rainselda;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

import net.harawata.appdirs.AppDirsFactory;
import net.ludocrypt.rainselda.scene.MapEditorScene;
import net.ludocrypt.rainselda.scene.Scene;

public class Rainselda extends ApplicationAdapter {
    public static final Rainselda INSTANCE = new Rainselda();

    private int width;
    private int height;

    private Scene currentScene;

    public Rainselda() {
        currentScene = new MapEditorScene();
    }

    @Override
    public void create() {
        getCurrentScene().create();

        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                if (getCurrentScene().getAdapter() != null) {
                    return getCurrentScene().getAdapter().keyDown(keycode);
                }

                return false;
            }

            @Override
            public boolean keyUp(int keycode) {

                if (currentScene.getAdapter() != null) {
                    return currentScene.getAdapter().keyUp(keycode);
                }

                return false;
            }

            @Override
            public boolean keyTyped(char character) {

                if (currentScene.getAdapter() != null) {
                    return currentScene.getAdapter().keyTyped(character);
                }

                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {

                if (getCurrentScene().getAdapter() != null) {
                    return getCurrentScene().getAdapter().touchDown(screenX, screenY, pointer, button);
                }

                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {

                if (getCurrentScene().getAdapter() != null) {
                    return getCurrentScene().getAdapter().touchUp(screenX, screenY, pointer, button);
                }

                return false;
            }

            @Override
            public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {

                if (getCurrentScene().getAdapter() != null) {
                    return getCurrentScene().getAdapter().touchCancelled(screenX, screenY, pointer, button);
                }

                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {

                if (getCurrentScene().getAdapter() != null) {
                    return getCurrentScene().getAdapter().touchDragged(screenX, screenY, pointer);
                }

                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {

                if (getCurrentScene().getAdapter() != null) {
                    return getCurrentScene().getAdapter().mouseMoved(screenX, screenY);
                }

                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {

                if (getCurrentScene().getAdapter() != null) {
                    return getCurrentScene().getAdapter().scrolled(amountX, amountY);
                }

                return false;
            }
        });
    }

    @Override
    public void render() {
        ScreenUtils.clear(1, 1, 1, 1);
        getCurrentScene().render();
    }

    @Override
    public void dispose() {
        getCurrentScene().dispose();
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        getCurrentScene().resize(width, height);
    }

    @Override
    public void pause() {
        getCurrentScene().pause();
    }

    @Override
    public void resume() {
        getCurrentScene().resume();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getAspectRatio() {
        return getAspectRatio(false);
    }

    public float getAspectRatio(boolean inv) {
        return inv ? ((float) height / (float) width) : ((float) width / (float) height);
    }

    public void filesDropped(String[] files) {
        getCurrentScene().filesDropped(files);
    }

    public void setCurrentScene(Scene scene) {
        if (this.currentScene != null) {
            this.currentScene.dispose();
        }

        this.currentScene = scene;
        this.currentScene.create();
        this.currentScene.resize(getWidth(), getHeight());
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    /*
     * Home config directory
     */
    public static File getConfigDir() {
        return new File(AppDirsFactory.getInstance().getUserConfigDir("Rainselda", "1.0.0", "LudoCrypt"));
    }

    /*
     * Global mouse position inside monitor
     */
    public static Point getGlobalMousePosition() {
        return MouseInfo.getPointerInfo().getLocation();
    }

    /*
     * Gets the u coordinate 0-1 over the screen.
     */
    public static double getU(double x) {
        return x / Rainselda.INSTANCE.width;
    }

    /*
     * Gets the v coordinate 0-1 over the screen.
     */
    public static double getV(double y) {
        return y / Rainselda.INSTANCE.height;
    }

    /*
     * Gets the u coordinate 0-640 over the screen.
     */
    public static double getScreenU(double x) {
        return getU(x) * 640.0;
    }

    /*
     * Gets the v coordinate 0-480 over the screen.
     */
    public static double getScreenV(double y) {
        return getV(y) * 480.0;
    }

}
