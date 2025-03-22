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

	public void setMouse(double x, double y) {
		if (!this.locked) {
			this.transMat = new Matrix4();
			this.transMat.translate((float) x, (float) y, 0);
		}
	}

	public void pushMouse() {
		Matrix4 mat = new Matrix4();
		mat.mul(this.transMat);
		mat.mul(this.mat);
		this.mat = mat;
		this.setMouse(0, 0);
	}

	public void zoom(double x, double y, double zoomScale, int dir) {
		if (!locked) {
			zoomScale += Math.abs(dir / 10.0);

			if (dir > 0) {
				zoomScale = 1 / zoomScale;
			}

			float scaleX = this.mat.val[Matrix4.M00];
			float scaleY = this.mat.val[Matrix4.M11];

			float translateX = this.mat.val[Matrix4.M03];
			float translateY = this.mat.val[Matrix4.M13];

			float worldX = ((float) x - translateX) / scaleX;
			float worldY = ((float) y - translateY) / scaleY;

			this.mat.val[Matrix4.M00] *= zoomScale;
			this.mat.val[Matrix4.M11] *= zoomScale;

			this.mat.val[Matrix4.M03] = (float) x - worldX * this.mat.val[Matrix4.M00];
			this.mat.val[Matrix4.M13] = (float) y - worldY * this.mat.val[Matrix4.M11];
		}
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
		Matrix4 invMat = this.composeMat(true);

		float[] pos = new float[] { (float) screenSpace.getX(), (float) screenSpace.getY(), 0, 1 };
		Matrix4.mulVec(invMat.val, pos);

		pos[0] /= pos[3];
		pos[1] /= pos[3];

		return new Mapos(pos[0], pos[1]);
	}

	public Mapos screenSpace(Mapos worldSpace) {
		Matrix4 mat = this.composeMat(false);

		float[] pos = new float[] { (float) worldSpace.getX(), (float) worldSpace.getY(), 0, 1 };
		Matrix4.mulVec(mat.val, pos);

		pos[0] /= pos[3];
		pos[1] /= pos[3];

		return new Mapos(pos[0], pos[1]);
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
		this.setMouse(x - this.lastX, y - this.lastY);
	}

	@Override
	public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
		this.zoom(x, y, 1.1, (int) amountY);
		return false;
	}

}