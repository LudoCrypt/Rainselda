package net.ludocrypt.rainselda.scene;

import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.DirectContext;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.FontEdging;
import io.github.humbleui.skija.FontStyle;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.Typeface;
import io.github.humbleui.types.Rect;
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
	Typeface typeface;
	Font font;

	ShaderProgram shapesShader;
	ShapeRenderer shapeRenderer;

	Stage stage;

	// TODO: Theme
	float xBuffer = 5;
	float yBuffer = 5;
	float radius = 10;

	float textSpacingX = 5;
	float textSpacingY = 25;

	// The boarder lines for the like three main columns idk
	float[] columnBoarders = new float[4];

	// For right clicking
	boolean hasRightClicked;
	int rightClickColumn = -1;
	int rightClickX, rightClickY;

	// TODO: Un-hardcode
	int rightClickWidth;

	boolean awaitingRoom;
	Mapos alignedRoom = null;

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

		this.typeface = Typeface.makeFromName("Segoe UI", FontStyle.NORMAL);
		this.font = new Font(this.typeface, 13);
		this.font.setSubpixel(true);
		this.font.setEdging(FontEdging.SUBPIXEL_ANTI_ALIAS);

		this.shapesShader = new ShaderProgram(Gdx.files.internal("shaders/shapes.vsh"), Gdx.files.internal("shaders/shapes.fsh"));
		this.shapeRenderer = new ShapeRenderer(this.batch, this.shapesShader, this.logo);

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
						if (MapEditorScene.this.rightClickIndex(MapEditorScene.this.mapExtraWidgets) == 0) {
							MapEditorScene.this.awaitingRoom = true;
						}
					} else if (MapEditorScene.this.awaitingRoom) {
						MapEditorScene.this.awaitingRoom = false;

						Mapos roomPos = MapEditorScene.this.viewport.worldSpace(ShapeRenderer.unfixScaleCentered(new Mapos(x, y)));

						if (MapEditorScene.this.alignedRoom != null) {
							roomPos = MapEditorScene.this.alignedRoom;
							MapEditorScene.this.alignedRoom = null;
						}

						MapEditorScene.this.region.addRoom(new Room(), roomPos);
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
						MapEditorScene.this.rightClickWidth = 80;
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
	public void skijaRender(DirectContext ctx, Canvas canvas) {
		ScreenUtils.clear(0, 0, 0, 1);

		this.updateMouseIcon();

		this.drawBackground();
		this.drawRooms();
		this.drawColumns();
		this.drawLogo();
		this.drawRightClickWidget(ctx, canvas);

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
		this.shapeRenderer.shape(ShapeRenderer.SHAPE_ROUND_RECT_FIXED).thickness(2).fill(false).useRes(false).radius(this.radius * 2);

		for (int i = 0; i < 3; i++) {
			this.shapeRenderer.drawShapeFixed(this.columnBoarders[i] + this.xBuffer, this.yBuffer, (this.columnBoarders[i + 1] - this.columnBoarders[i]) - 2 * this.xBuffer, boxHeight);
		}

	}

	private void drawBackground() {
		// TODO: Theme
		this.batch.setColor(RenderHelper.hexToColor("141414"));
		this.shapeRenderer.shape(ShapeRenderer.SHAPE_RECT).thickness(2).fill(true).useRes(false);
		this.shapeRenderer.drawShapeFixed(0, 0, this.rainselda.getWidth(), this.rainselda.getHeight());
	}

	private void drawRightClickWidget(DirectContext ctx, Canvas canvas) {
		if (this.hasRightClicked) {

			double bW = this.rightClickWidth;
			double bH = this.mapExtraWidgets.length * this.textSpacingY;

			// TODO: Theme
			this.batch.setColor(RenderHelper.hexToColor("141414"));
			this.shapeRenderer.shape(ShapeRenderer.SHAPE_ROUND_RECT).thickness(3).fill(true).useRes(false).radius(0.2);
			this.shapeRenderer.drawShapeFixed(this.rightClickX, this.rightClickY - this.mapExtraWidgets.length * this.textSpacingY, bW, bH);

			// TODO: Theme
			this.batch.setColor(RenderHelper.hexToColor("494949"));
			this.shapeRenderer.shape(ShapeRenderer.SHAPE_ROUND_RECT).thickness(3).fill(false).useRes(false).radius(0.2);
			this.shapeRenderer.drawShapeFixed(this.rightClickX, this.rightClickY - this.mapExtraWidgets.length * this.textSpacingY, bW, bH);

			int i = 0;
			for (String option : this.mapExtraWidgets) {
				double transX = this.rightClickX;
				double transY = this.rightClickY - i * this.textSpacingY;

				Paint paint = RenderHelper.paint(Color.WHITE);

				// TODO: Theme
				if (rightClickIndex(this.mapExtraWidgets) == i) {
					paint = RenderHelper.paint(Color.GREEN);
				}

				RenderHelper.renderText(ctx, canvas, this.font, paint, option, transX, transY, this.rightClickWidth, this.textSpacingY, Anchor.CENTER);

				i++;
			}
		}
	}

	private Rect rightClickWidgetBounds(int idx) {
		double transX = this.rightClickX;
		double transY = this.rightClickY - idx * this.textSpacingY;

		return RenderHelper.textBounds(this.font, this.mapExtraWidgets[idx], transX, transY, this.rightClickWidth, this.textSpacingY, Anchor.CENTER);
	}

	private int rightClickIndex(String[] options) {
		int x = Gdx.input.getX();
		int y = MapEditorScene.this.rainselda.getHeight() - Gdx.input.getY();

		if (x - this.textSpacingX < this.rightClickX) {
			return -1;
		}

		if (x - this.rightClickWidth + this.textSpacingX > this.rightClickX) {
			return -1;
		}

		int index = (int) Math.floor(-(y - this.rightClickY) / (this.textSpacingY));

		if (index >= 0 && index < options.length) {
			Rect bounds = this.rightClickWidgetBounds(index);

			if (x > bounds.getLeft() && x < bounds.getRight() && Gdx.input.getY() < bounds.getBottom() && Gdx.input.getY() > bounds.getTop()) {
				return index;
			}
		}

		return -1;
	}

	private void drawRooms() {
		Matrix4 projMat = this.batch.getProjectionMatrix().cpy();

		this.batch.getProjectionMatrix().mul(this.viewport.composeMat());

		Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
		Gdx.gl.glScissor((int) (this.columnBoarders[1] + this.xBuffer), (int) this.yBuffer, (int) (this.columnBoarders[2] - this.columnBoarders[1] - this.xBuffer - this.xBuffer), (int) (7.0 * rainselda.getHeight() / 8.0 - this.yBuffer));

		Mapos mousePos = this.viewport.worldSpace(ShapeRenderer.unfixScaleCentered(this.rainselda.mouse()));
		Room closestRoom = null;
		double closestDist = Double.MAX_VALUE;

		for (Entry<MapObject, Mapos> entry : this.region.getMap().entrySet()) {
			MapObject object = entry.getKey();
			Mapos pos = entry.getValue();

			if (object instanceof Room room) {
				this.batch.setColor(Color.WHITE);
				drawRoom(pos);

				double d = pos.distanceSqr(mousePos);
				if (d < closestDist) {
					closestRoom = room;
					closestDist = d;
				}

			}

		}

		if (this.awaitingRoom) {
			if (closestRoom != null) {
				Mapos center = this.region.getMap().get(closestRoom);
				Mapos nearIdx = center.sub(mousePos).div(32, 24).round();

				double opacity = RenderHelper.lerp(0.3, 0, center.sub(mousePos).div(60).magnitudeSqr());
				this.batch.setColor(Color.GRAY.cpy().mul(1, 1, 1, (float) opacity));

				int b = 4;
				if (opacity > 0 && Math.abs(nearIdx.getX()) <= b && Math.abs(nearIdx.getY()) <= b) {

					this.shapeRenderer.falloff(true).falloffX(Gdx.input.getX()).falloffY(this.rainselda.getHeight() - Gdx.input.getY()).falloffMag(new Mapos(60, 0).mul(this.viewport.composeMat(false).setTranslation(0, 0, 0)).getX()).falloffSteepness(5.0);

					int r = b;
					for (int ix = -r; ix <= r; ix++) {
						for (int iy = -r; iy <= r; iy++) {
							drawRoom(center.add(32 * ix, 24 * iy));
						}
					}

					this.shapeRenderer.falloff(false);

					this.batch.setColor(Color.WHITE);
					drawRoom(center);

					this.alignedRoom = center.add(32 * -nearIdx.getX(), 24 * -nearIdx.getY());
					this.batch.setColor(Color.GREEN.cpy().mul(1, 1, 1, (float) opacity));
					drawRoom(this.alignedRoom);
				} else {
					this.alignedRoom = null;
				}
			}

			this.batch.setColor(Color.WHITE);
			this.shapeRenderer.falloff(false);
			drawRoom(mousePos);
		}

		Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);

		this.batch.setProjectionMatrix(projMat);
	}

	private void drawRoom(Mapos pos) {
		Mapos scale = ShapeRenderer.affixScale(32, 24);
		this.shapeRenderer.shape(ShapeRenderer.SHAPE_ROUND_RECT).thickness(3).fill(false).resX(32).resY(24).radius(0.5);
		this.shapeRenderer.drawShape(this.viewport.screenSpaceFix(pos.getX(), pos.getY()).sub(scale.mul(0.5)), scale.getX(), scale.getY());
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

		this.viewport.pushMouse();
	}

	@Override
	public void dispose() {
		this.batch.dispose();
		this.stage.dispose();
		this.logo.dispose();
		this.shapesShader.dispose();
	}

	@Override
	public InputAdapter getAdapter() {
		return this.stage;
	}

}
