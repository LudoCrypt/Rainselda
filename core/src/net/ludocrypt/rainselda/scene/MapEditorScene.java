package net.ludocrypt.rainselda.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
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

    // TODO: Theme
    BitmapFont font;

    Stage stage;

    // TODO: Theme
    float xBuffer = 5;
    float yBuffer = 10;
    float radius = 10;

    // The boarder lines for the like three main columns idk
    float[] columnBoarders = new float[4];

    // For right clicking
    boolean hasRightClicked;
    int rightClickColumn = -1;
    int rightClickX, rightClickY;

    @Override
    public void create() {
        ShaderProgram.pedantic = false;

        this.rainselda = Rainselda.INSTANCE;
        this.viewport = new Viewport(rainselda);
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.shapeRenderer.setAutoShapeType(true);

        this.logo = new Texture("Logo.png");

        this.font = MathHelper.generateFontOfSize(new FreeTypeFontGenerator(Gdx.files.internal("ProggyVector-Regular.ttf")), 48);

        this.stage = new Stage(new ScreenViewport());

        this.columnBoarders[0] = 0;
        this.columnBoarders[1] = 640.0f / 3.0f;
        this.columnBoarders[2] = (2.0f * 640.0f) / 3.0f;
        this.columnBoarders[3] = 640.0f;

        // The input listener to resize the columns
        this.stage.addListener(new InputListener() {

            boolean dragging = false;
            int draggedOffset = 0;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 0) {
                    int index = MapEditorScene.this.columnWallIndex();

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
                    MapEditorScene.this.columnBoarders[this.draggedOffset] = MathHelper.clamp(x, MapEditorScene.this.columnBoarders[this.draggedOffset - 1] + 2 * xBuffer + radius + radius, MapEditorScene.this.columnBoarders[this.draggedOffset + 1] - 2 * xBuffer - radius - radius);
                }
            }

        });

        // The input listener to right clicking in the middle box
        this.stage.addListener(new InputListener() {

            boolean clicked = false;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!this.clicked && button == 1) {
                    this.clicked = true;
                    return true;
                }

                this.clicked = false;
                MapEditorScene.this.rightClickColumn = -1;
                MapEditorScene.this.hasRightClicked = false;
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 1) {
                    if (this.clicked) {
                        MapEditorScene.this.rightClickColumn = MapEditorScene.this.columnIndex();
                        MapEditorScene.this.hasRightClicked = true;
                        MapEditorScene.this.rightClickX = Gdx.input.getX();
                        MapEditorScene.this.rightClickY = MapEditorScene.this.rainselda.getHeight() - Gdx.input.getY();
                        return;
                    }
                }

                MapEditorScene.this.rightClickColumn = -1;
                MapEditorScene.this.hasRightClicked = false;
            }

        });
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);

        this.updateMouseIcon();

        this.drawBackground();
        this.drawColumns();
        this.drawLogo();

        if (this.hasRightClicked) {

            // TODO: Theme
            int textSpacing = 8;

            String[] options = new String[] { "Add Room", "Add Note" };

            this.shapeRenderer.begin(ShapeType.Line);

            MathHelper.drawRoundedRectangle(shapeRenderer, rightClickX, rightClickY - options.length * textSpacing * 4, 100, options.length * textSpacing * 4, 5);

            this.shapeRenderer.end();

            this.batch.begin();

            float scale = 2.7f;

            this.font.getData().setScale((640.0f / (float) rainselda.getWidth()) / scale, (640.0f / (float) rainselda.getHeight()) / scale);

            int i = 0;
            for (String option : options) {
                this.font.draw(batch, option, Rainselda.getScreenU(rightClickX + 5), Rainselda.getScreenV(rightClickY - i * textSpacing * 4 - textSpacing));
                i++;
            }

            this.batch.end();
        }

        this.stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        this.stage.draw();

    }

    private int columnWallIndex() {
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();
        float boxHeight = (7.0f * this.rainselda.getHeight() / 8.0f) - this.yBuffer;

        if (this.rainselda.getHeight() - mouseY - boxHeight < 0) {
            if (Math.abs(mouseX - this.columnBoarders[1]) < this.xBuffer) {
                return 1;
            } else if (Math.abs(mouseX - this.columnBoarders[2]) < this.xBuffer) {
                return 2;
            }
        }

        return -1;
    }

    private int columnIndex() {
        if (this.columnWallIndex() != -1) {
            return -1;
        }

        int mouseX = Gdx.input.getX();

        if (mouseX < this.columnBoarders[1] + this.xBuffer) {
            return 0;
        } else if (mouseX < this.columnBoarders[2] + this.xBuffer) {
            return 1;
        } else if (mouseX < this.columnBoarders[3] + this.xBuffer) {
            return 2;
        }

        return -1;
    }

    private void updateMouseIcon() {
        int wallIndex = this.columnWallIndex();

        if (wallIndex != -1) {
            Gdx.graphics.setSystemCursor(SystemCursor.HorizontalResize);
            return;
        }

//        int columnIndex = columnIndex();
//        if (columnIndex == 1) {
//            Gdx.graphics.setSystemCursor(SystemCursor.Hand);
//            return;
//        }

        Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
    }

    private void drawLogo() {
        this.batch.begin();
        float aspect = (float) this.logo.getWidth() / (float) this.logo.getHeight();

        float widthScale = 640.0f / this.rainselda.getWidth();
        float heightScale = 480.0f / this.rainselda.getHeight();

        float boxHeight = this.rainselda.getHeight() / 8.0f;

        this.batch.draw(this.logo, 0, 480 - boxHeight * heightScale, aspect * boxHeight * widthScale, boxHeight * heightScale);
        this.batch.end();
    }

    private void drawColumns() {
        this.shapeRenderer.begin(ShapeType.Line);

        // TODO: Theme
        this.shapeRenderer.setColor(MathHelper.hexToColor("494949"));

        float boxHeight = (7.0f * rainselda.getHeight() / 8.0f) - this.yBuffer;

        for (int i = 0; i < 3; i++) {
            MathHelper.drawRoundedRectangle(this.shapeRenderer, this.columnBoarders[i] + this.xBuffer, this.yBuffer, (this.columnBoarders[i + 1] - this.columnBoarders[i]) - 2 * this.xBuffer, boxHeight, this.radius);
        }

        this.shapeRenderer.end();
    }

    private void drawBackground() {
        this.shapeRenderer.begin(ShapeType.Filled);

        // TODO: Theme
        this.shapeRenderer.setColor(MathHelper.hexToColor("141414"));
        this.shapeRenderer.rect(0, 0, rainselda.getWidth(), rainselda.getHeight());

        this.shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        // For some reason the projection matrix only gets set once so this just updates it to match the current resolution
        this.shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        this.shapeRenderer.updateMatrices();

        // istg if another one of these things doesnt do it automatically im gonna kill somepony
        this.stage.getViewport().setScreenBounds(0, 0, width, height);

        float oldWidth = columnBoarders[3];

        // Normalize between 0-1
        this.columnBoarders[1] /= oldWidth;
        this.columnBoarders[2] /= oldWidth;

        this.columnBoarders[0] = 0;
        this.columnBoarders[1] *= width;
        this.columnBoarders[2] *= width;
        this.columnBoarders[3] = width;

        this.rightClickColumn = -1;
        this.hasRightClicked = false;
    }

    @Override
    public void dispose() {
        this.batch.dispose();
        this.shapeRenderer.dispose();
        this.stage.dispose();
    }

    @Override
    public InputAdapter getAdapter() {
        return this.stage;
    }

}
