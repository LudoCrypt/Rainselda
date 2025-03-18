package net.ludocrypt.rainselda.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.ludocrypt.rainselda.Rainselda;
import net.ludocrypt.rainselda.render.Viewport;
import net.ludocrypt.rainselda.util.MathHelper;

public class MapEditorScene extends Scene {

    Rainselda rainselda;
    SpriteBatch batch;
    Viewport viewport;
    ShapeRenderer shapeRenderer;

    Texture logo;

    Stage stage;

    // TODO: Theme
    float xBuffer = 10;
    float yBuffer = 10;
    float radius = 10;

    // The boarder lines for the like three main columns idk
    float[] columnBoarders = new float[4];

    @Override
    public void create() {
        ShaderProgram.pedantic = false;
        rainselda = Rainselda.INSTANCE;
        viewport = new Viewport(rainselda);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        logo = new Texture("Logo.png");

        stage = new Stage(new ScreenViewport());

        columnBoarders[0] = 0;
        columnBoarders[1] = (640.0f - xBuffer) / 3.0f;
        columnBoarders[2] = (2.0f * (640.0f - xBuffer)) / 3.0f;
        columnBoarders[3] = 640.0f - xBuffer;

        // The input listener to resize the columns
        stage.addListener(new InputListener() {

            boolean dragging = false;
            int draggedOffset = 0;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 0) {
                    int index = columnIndex();

                    if (index != -1) {
                        this.dragging = true;
                        this.draggedOffset = index;
                        return true;
                    }
                }

                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                this.dragging = false;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (this.dragging) {
                    // i love it how the float x up top is FUCKNIG 0-640 GRAHGHHGFJFJFJFJHFHJFHJ fuck you
                    x = Gdx.input.getX();
                    MapEditorScene.this.columnBoarders[this.draggedOffset] = MathHelper.clamp(x, MapEditorScene.this.columnBoarders[this.draggedOffset - 1] + xBuffer + radius + radius, MapEditorScene.this.columnBoarders[this.draggedOffset + 1] - xBuffer - radius - radius);
                }
            }

        });
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setAutoShapeType(true);

        // Draw the background
        {
            // TODO: Theme
            shapeRenderer.setColor(MathHelper.hexToColor("141414"));
            shapeRenderer.rect(0, 0, rainselda.getWidth(), rainselda.getHeight());
        }

        // Draw the boxes with the things like the map and side panels idk how to call it
        {
            shapeRenderer.set(ShapeType.Line);

            // TODO: Theme
            shapeRenderer.setColor(MathHelper.hexToColor("494949"));

            float boxHeight = (7.0f * rainselda.getHeight() / 8.0f) - yBuffer;

            for (int i = 0; i < 3; i++) {
                MathHelper.drawRoundedRectangle(shapeRenderer, columnBoarders[i] + xBuffer, yBuffer, columnBoarders[i + 1] - columnBoarders[i] - xBuffer, boxHeight, radius);
            }
        }

        // Draw the logo (surely theres a better way to do this :sob:)
        {
            float aspect = (float) logo.getWidth() / (float) logo.getHeight();

            float widthScale = 640.0f / rainselda.getWidth();
            float heightScale = 480.0f / rainselda.getHeight();

            float boxHeight = rainselda.getHeight() / 8.0f;

            batch.draw(logo, 0, 480 - boxHeight * heightScale, aspect * boxHeight * widthScale, boxHeight * heightScale);
        }

        // Update the mouse icon
        {
            int index = columnIndex();

            if (index != -1) {
                Gdx.graphics.setSystemCursor(SystemCursor.HorizontalResize);
            } else {
                Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
            }
        }

        shapeRenderer.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        batch.end();
    }

    private int columnIndex() {
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();
        float boxHeight = (7.0f * rainselda.getHeight() / 8.0f) - yBuffer;

        if (rainselda.getHeight() - mouseY - boxHeight < 0) {
            if (Math.abs(mouseX - columnBoarders[1]) < xBuffer) {
                return 1;
            } else if (Math.abs(mouseX - columnBoarders[2]) < xBuffer) {
                return 2;
            }
        }

        return -1;
    }

    @Override
    public void resize(int width, int height) {
        // For some reason the projection matrix only gets set once so this just updates it to match the current resolution
        shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        shapeRenderer.updateMatrices();

        // istg if another one of these things doesnt do it automatically im gonna kill somepony
        stage.getViewport().setScreenBounds(0, 0, width, height);

        float oldWidth = columnBoarders[3] + xBuffer;

        // Normalize between 0-1
        columnBoarders[1] /= oldWidth;
        columnBoarders[2] /= oldWidth;

        columnBoarders[0] = 0;
        columnBoarders[1] *= width;
        columnBoarders[2] *= width;
        columnBoarders[3] = width - xBuffer;
    }

    @Override
    public void dispose() {
        this.batch.dispose();
        this.shapeRenderer.dispose();
    }

    @Override
    public InputAdapter getAdapter() {
        return this.stage;
    }

}
