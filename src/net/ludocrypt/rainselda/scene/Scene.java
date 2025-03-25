package net.ludocrypt.rainselda.scene;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.Null;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.DirectContext;

public abstract class Scene extends ApplicationAdapter {

	/*
	 * Render to a skija canvas.
	 */
	public abstract void skijaRender(DirectContext ctx, Canvas canvas);

	/*
	 * Optional input adapter to track inputs, can be null.
	 */
	public @Null InputAdapter getAdapter() {
		return null;
	}

	/*
	 * Called when files are dropped into main window
	 */
	public void filesDropped(String[] files) {

	}

}
