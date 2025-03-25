package net.ludocrypt.rainselda;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import io.github.humbleui.skija.BackendRenderTarget;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.ColorSpace;
import io.github.humbleui.skija.DirectContext;
import io.github.humbleui.skija.FramebufferFormat;
import io.github.humbleui.skija.PixelGeometry;
import io.github.humbleui.skija.Surface;
import io.github.humbleui.skija.SurfaceColorFormat;
import io.github.humbleui.skija.SurfaceOrigin;
import io.github.humbleui.skija.SurfaceProps;
import io.github.humbleui.skija.impl.Library;
import net.harawata.appdirs.AppDirsFactory;
import net.ludocrypt.rainselda.region.Mapos;
import net.ludocrypt.rainselda.region.Region;
import net.ludocrypt.rainselda.render.ShapeRenderer;
import net.ludocrypt.rainselda.scene.MapEditorScene;
import net.ludocrypt.rainselda.scene.Scene;

public class Rainselda extends ApplicationAdapter {
	public static final Rainselda INSTANCE = new Rainselda();

	private int width;
	private int height;

	private Scene currentScene;

	private DirectContext context;
	private BackendRenderTarget renderTarget;
	private Surface surface;
	private Canvas canvas;

	public Rainselda() {
		this.currentScene = new MapEditorScene(new Region());
	}

	private void initSkia() {
		if (this.surface != null) {
			this.surface.close();
		}

		if (this.renderTarget != null) {
			this.renderTarget.close();
		}

		this.renderTarget = BackendRenderTarget.makeGL(this.width, this.height, 0, 8, 0, FramebufferFormat.GR_GL_RGBA8);
		this.surface = Surface.wrapBackendRenderTarget(this.context, this.renderTarget, SurfaceOrigin.BOTTOM_LEFT, SurfaceColorFormat.RGBA_8888, ColorSpace.getDisplayP3(), new SurfaceProps(PixelGeometry.RGB_H));
		this.canvas = this.surface.getCanvas();
	}

	@Override
	public void create() {
		if ("false".equals(System.getProperty("skija.staticLoad"))) {
			Library.load();
		}
		this.context = DirectContext.makeGL();
		this.initSkia();
		this.currentScene.create();
		Gdx.input.setInputProcessor(this.currentScene.getAdapter());
	}

	@Override
	public void render() {
		this.currentScene.skijaRender(context, canvas);
	}

	@Override
	public void dispose() {
		this.currentScene.dispose();
	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		this.initSkia();
		this.currentScene.resize(width, height);
	}

	@Override
	public void pause() {
		this.currentScene.pause();
	}

	@Override
	public void resume() {
		this.currentScene.resume();
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public double getAspectRatio() {
		return getAspectRatio(false);
	}

	public double getAspectRatio(boolean inv) {
		return inv ? ((double) this.height / (double) this.width) : ((double) this.width / (double) this.height);
	}

	public Mapos mouse() {
		return new Mapos(ShapeRenderer.affixScale(Gdx.input.getX(), this.height - Gdx.input.getY()));
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
		this.currentScene.resize(this.width, this.height);
		Gdx.input.setInputProcessor(this.currentScene.getAdapter());
	}

	public Scene getCurrentScene() {
		return this.currentScene;
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

}
