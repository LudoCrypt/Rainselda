package net.ludocrypt.rainselda.render;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import net.ludocrypt.rainselda.Rainselda;
import net.ludocrypt.rainselda.region.Mapos;

/*
 * Controls the camera movement
 */
public class Viewport extends InputListener {

	Rainselda rainselda;

	Matrix4 mat;
	Matrix4 transMat;

	boolean locked;

	float lastX;
	float lastY;

	public Viewport(Rainselda rainselda) {
		this.rainselda = rainselda;
		this.mat = new Matrix4();
		this.transMat = new Matrix4();
	}

	public void lock() {
		this.locked = true;
	}

	public void unlock() {
		this.locked = !this.locked;
	}

	public void setMouse(Mapos pos) {
		if (!this.locked) {
			this.transMat = new Matrix4();
			this.transMat.translate((float) pos.getX(), (float) pos.getY(), 0);
		}
	}

	public void setMouse(double x, double y) {
		setMouse(new Mapos(x, y));
	}

	public void pushMouse() {
		Matrix4 mat = new Matrix4();
		mat.mul(this.transMat);
		mat.mul(this.mat);
		this.mat = mat;
		this.setMouse(0, 0);
	}

	public void zoom(Mapos pos, double zoomScale, int dir) {
		if (!locked) {
			zoomScale += Math.abs(dir / 10.0);

			if (dir > 0) {
				zoomScale = 1 / zoomScale;
			}

			float scaleX = this.mat.val[Matrix4.M00];
			float scaleY = this.mat.val[Matrix4.M11];

			float translateX = this.mat.val[Matrix4.M03];
			float translateY = this.mat.val[Matrix4.M13];

			float worldX = ((float) pos.getX() - translateX) / scaleX;
			float worldY = ((float) pos.getY() - translateY) / scaleY;

			this.mat.val[Matrix4.M00] *= zoomScale;
			this.mat.val[Matrix4.M11] *= zoomScale;

			this.mat.val[Matrix4.M03] = (float) pos.getX() - worldX * this.mat.val[Matrix4.M00];
			this.mat.val[Matrix4.M13] = (float) pos.getY() - worldY * this.mat.val[Matrix4.M11];
		}
	}

	public void zoom(double x, double y, double zoomScale, int dir) {
		zoom(new Mapos(x, y), zoomScale, dir);
	}

	public Matrix4 composeMat(boolean inv) {
		Matrix4 projMat = new Matrix4();
		projMat.mul(this.transMat);
		projMat.mul(this.mat);

		if (inv) {
			projMat.inv();
		}

		return projMat;
	}

	public Matrix4 composeMat() {
		return this.composeMat(false);
	}

	public Mapos worldSpace(Mapos screenSpace) {
		return screenSpace.mul(this.composeMat(true));
	}

	public Mapos screenSpace(Mapos worldSpace) {
		return worldSpace.mul(this.composeMat(false));
	}

	public Mapos worldSpaceFix(Mapos screenSpace) {
		screenSpace = screenSpace.mul(this.composeMat(false));
		screenSpace = ShapeRenderer.unfixScale(screenSpace);
		screenSpace = screenSpace.mul(this.composeMat(true));
		return screenSpace;
	}

	public Mapos screenSpaceFix(Mapos worldSpace) {
		worldSpace = worldSpace.mul(this.composeMat(false));
		worldSpace = ShapeRenderer.affixScale(worldSpace);
		worldSpace = worldSpace.mul(this.composeMat(true));
		return worldSpace;
	}

	public Mapos screenSpaceFix(double x, double y) {
		return screenSpaceFix(new Mapos(x, y));
	}

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		if (button == 2) {
			this.lastX = x;
			this.lastY = y;
			return true;
		}

		return false;
	}

	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
		this.pushMouse();
	}

	@Override
	public void touchDragged(InputEvent event, float x, float y, int pointer) {
		this.setMouse(ShapeRenderer.unfixScale(x - this.lastX, y - this.lastY));
	}

	@Override
	public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
		this.zoom(ShapeRenderer.unfixScale(x, y), 1.1, (int) amountY);
		return false;
	}

}