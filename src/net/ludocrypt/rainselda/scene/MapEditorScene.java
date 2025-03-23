package net.ludocrypt.rainselda.scene;

import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.ludocrypt.rainselda.Rainselda;
import net.ludocrypt.rainselda.region.MapObject;
import net.ludocrypt.rainselda.region.Mapos;
import net.ludocrypt.rainselda.region.Region;
import net.ludocrypt.rainselda.region.Room;
import net.ludocrypt.rainselda.render.RenderHelper;
import net.ludocrypt.rainselda.render.RenderHelper.Anchor;
import net.ludocrypt.rainselda.render.ShapeRenderer;
import net.ludocrypt.rainselda.render.Viewport;

public class MapEditorScene extends Scene {

	Region region;

	Rainselda rainselda;
	SpriteBatch batch;
	Viewport viewport;

	Texture logo;

	// TODO: Theme
	BitmapFont font;
	ShaderProgram fontShader;

	ShaderProgram shapesShader;

	Stage stage;

	// TODO: Theme
	float xBuffer = 5;
	float yBuffer = 5;
	float radius = 10;

	float textSpacingX = 5;
	float textSpacingY = 10;

	// The boarder lines for the like three main columns idk
	float[] columnBoarders = new float[4];

	// For right clicking
	boolean hasRightClicked;
	int rightClickColumn = -1;
	int rightClickX, rightClickY;

	// TODO: Un-hardcode
	int rightClickWidth;

	boolean awaitingRoom;

	String[] mapExtraWidgets = new String[] { "Add Room", "Add Note", "Add Death", "Add Life", "Add Piss", "Add Shit" };

	public MapEditorScene(Region region) {
		this.region = region;
	}

	@Override
	public void create() {
		ShaderProgram.pedantic = false;

		this.rainselda = Rainselda.INSTANCE;
		this.viewport = new Viewport(rainselda);
		this.batch = new SpriteBatch();

		this.logo = new Texture("Logo.png");

		this.font = new BitmapFont(Gdx.files.internal("fonts/Carlito-Regular.fnt"));
		this.font.setUseIntegerPositions(false);

		this.fontShader = new ShaderProgram(Gdx.files.internal("shaders/msdf.vsh"), Gdx.files.internal("shaders/msdf.fsh"));
		this.shapesShader = new ShaderProgram(Gdx.files.internal("shaders/shapes.vsh"), Gdx.files.internal("shaders/shapes.fsh"));

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
					MapEditorScene.this.columnBoarders[this.draggedOffset] = RenderHelper.clamp(x, MapEditorScene.this.columnBoarders[this.draggedOffset - 1] + 2 * xBuffer + radius + radius, MapEditorScene.this.columnBoarders[this.draggedOffset + 1] - 2 * xBuffer - radius - radius);
				}
			}

		});

		// The input listener to right clicking in the middle box
		this.stage.addListener(new InputListener() {

			boolean clicked = false;

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				MapEditorScene.this.rightClickColumn = -1;

				if (button == 1) {
					this.clicked = true;
					MapEditorScene.this.hasRightClicked = false;
					return true;
				} else if (button == 0) {
					if (MapEditorScene.this.hasRightClicked) {
						if (MapEditorScene.this.rightClickIndex(MapEditorScene.this.mapExtraWidgets.length) == 0) {

							MapEditorScene.this.awaitingRoom = true;
						}
					} else if (MapEditorScene.this.awaitingRoom) {
						Mapos world = MapEditorScene.this.viewport.worldSpace(ShapeRenderer.unfixScaleCentered(new Mapos(x, y)));
						MapEditorScene.this.region.addRoom(new Room(), world);
					}
				}

				this.clicked = false;
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
						MapEditorScene.this.rightClickWidth = 65;
						return;
					}
				}

				MapEditorScene.this.rightClickColumn = -1;
				MapEditorScene.this.hasRightClicked = false;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {

			}

		});

		this.stage.addListener(this.viewport);
	}

	@Override
	public void render() {
		ScreenUtils.clear(0, 0, 0, 1);

		this.updateMouseIcon();

		this.drawBackground();
		this.drawRooms();
		this.drawColumns();
		this.drawLogo();
		this.drawRightClickWidget();

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
		double boxHeight = this.rainselda.getHeight() / 8.0;

		Mapos scale = ShapeRenderer.affixScale(this.logo, boxHeight);

		this.batch.setColor(Color.WHITE);
		this.batch.setShader(null);
		this.batch.draw(this.logo, 0, 480 - (float) scale.getY(), (float) scale.getX(), (float) scale.getY());
		this.batch.end();
	}

	private void drawColumns() {
		double boxHeight = (7.0 * rainselda.getHeight() / 8.0) - this.yBuffer;

		this.batch.setColor(RenderHelper.hexToColor("494949"));
		for (int i = 0; i < 3; i++) {
			ShapeRenderer.drawShapeFixed(this.batch, this.shapesShader, this.logo, ShapeRenderer.SHAPE_ROUND_RECT_FIXED, 2, false, this.columnBoarders[i] + this.xBuffer, this.yBuffer, (this.columnBoarders[i + 1] - this.columnBoarders[i]) - 2 * this.xBuffer, boxHeight, this.radius * 2);
		}

	}

	private void drawBackground() {
		// TODO: Theme
		this.batch.setColor(RenderHelper.hexToColor("141414"));
		ShapeRenderer.drawShapeFixed(this.batch, this.shapesShader, this.logo, ShapeRenderer.SHAPE_RECT, 2, true, 0, 0, this.rainselda.getWidth(), this.rainselda.getHeight(), 0.0);
	}

	private void drawRightClickWidget() {
		if (this.hasRightClicked) {

			// TODO: Theme
			this.batch.setColor(RenderHelper.hexToColor("141414"));
			ShapeRenderer.drawShapeFixed(this.batch, this.shapesShader, this.logo, ShapeRenderer.SHAPE_ROUND_RECT, 3, true, this.rightClickX, this.rightClickY - this.mapExtraWidgets.length * this.textSpacingY * 2, this.rightClickWidth, this.mapExtraWidgets.length * this.textSpacingY * 2, 0.2);

			// TODO: Theme
			this.batch.setColor(RenderHelper.hexToColor("494949"));
			ShapeRenderer.drawShapeFixed(this.batch, this.shapesShader, this.logo, ShapeRenderer.SHAPE_ROUND_RECT, 3, false, this.rightClickX, this.rightClickY - this.mapExtraWidgets.length * this.textSpacingY * 2, this.rightClickWidth, this.mapExtraWidgets.length * this.textSpacingY * 2, 0.2);

			int i = 0;
			for (String option : this.mapExtraWidgets) {
				double transX = this.rightClickX;
				double transY = this.rightClickY - i * this.textSpacingY * 2 - this.textSpacingY + 4;

				// TODO: Theme
				if (rightClickIndex(this.mapExtraWidgets.length) == i) {
					font.setColor(Color.GREEN);
				} else {
					font.setColor(Color.WHITE);
				}

				RenderHelper.renderText(batch, fontShader, font, transX, transY, 0.25, this.rightClickWidth, option, Anchor.CENTER);

				i++;
			}
		}
	}

	private int rightClickIndex(int options) {
		int x = Gdx.input.getX();
		int y = MapEditorScene.this.rainselda.getHeight() - Gdx.input.getY();

		if (x - this.textSpacingX < this.rightClickX) {
			return -1;
		}

		if (x - this.rightClickWidth + this.textSpacingX > this.rightClickX) {
			return -1;
		}

		int index = (int) Math.floor(-(y - this.rightClickY) / (this.textSpacingY * 2));

		if (index >= 0 && index < options) {
			return index;
		}

		return -1;
	}

	private void drawRooms() {
		Matrix4 projMat = this.batch.getProjectionMatrix().cpy();

		this.batch.getProjectionMatrix().mul(this.viewport.composeMat());

		Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
		Gdx.gl.glScissor((int) (this.columnBoarders[1] + this.xBuffer), (int) this.yBuffer, (int) (this.columnBoarders[2] - this.columnBoarders[1] - this.xBuffer - this.xBuffer), (int) (7.0 * rainselda.getHeight() / 8.0 - this.yBuffer));

		for (Entry<MapObject, Mapos> entry : this.region.getMap().entrySet()) {
			MapObject object = entry.getKey();
			Mapos pos = entry.getValue();

			if (object instanceof Room room) {
				this.batch.setColor(Color.WHITE);
				drawRoom(pos);
			}

		}

		drawRoom(this.viewport.worldSpace(ShapeRenderer.unfixScaleCentered(this.rainselda.mouse())));

		Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);

		this.batch.setProjectionMatrix(projMat);
	}

	private void drawRoom(Mapos pos) {
		Mapos scale = ShapeRenderer.affixScale(32, 24);
		ShapeRenderer.drawShape(this.batch, this.shapesShader, this.logo, ShapeRenderer.SHAPE_ROUND_RECT, 3, false, this.viewport.screenSpaceFix(pos.getX(), pos.getY()).sub(scale.mul(0.5)), scale.getX(), scale.getY(), 32, 24, 0.5);
	}

	@Override
	public void resize(int width, int height) {
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

//		this.viewport.setMouse(0, 1);
		this.viewport.pushMouse();
	}

	@Override
	public void dispose() {
		this.batch.dispose();
		this.stage.dispose();
		this.font.dispose();
		this.fontShader.dispose();
		this.logo.dispose();
		this.shapesShader.dispose();
	}

	@Override
	public InputAdapter getAdapter() {
		return this.stage;
	}

}
