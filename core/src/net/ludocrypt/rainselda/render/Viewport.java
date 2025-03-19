package net.ludocrypt.rainselda.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import net.ludocrypt.rainselda.Rainselda;

/*
 * Controls the camera movement
 */
public class Viewport extends InputAdapter {

    Rainselda rainselda;

    Matrix4 mat;
    Matrix4 transMat;

    boolean locked;

    int lastX;
    int lastY;

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

        float scaleY = (float) ((double) this.rainselda.getWidth() / (double) this.rainselda.getHeight());
        float scaleX = (float) ((double) this.rainselda.getHeight() / (double) this.rainselda.getWidth());
        projMat.scale(this.rainselda.getHeight() < this.rainselda.getWidth() ? scaleX : 1, this.rainselda.getHeight() > this.rainselda.getWidth() ? scaleY : 1, 1);

        projMat.scl(0.01f);

        if (inv) {
            projMat.inv();
        }

        return projMat;
    }

    public Matrix4 composeMat() {
        return this.composeMat(false);
    }

    public Vector2 worldSpace(Vector2 screenSpace) {
        Matrix4 invMat = this.composeMat(true);

        float[] pos = new float[] { screenSpace.x, screenSpace.y, 0, 1 };
        Matrix4.mulVec(invMat.val, pos);

        pos[0] /= pos[3];
        pos[1] /= pos[3];

        return new Vector2(pos[0], pos[1]);
    }

    public Vector2 screenSpace(Vector2 worldSpace) {
        Matrix4 mat = this.composeMat(false);

        float[] pos = new float[] { worldSpace.x, worldSpace.y, 0, 1 };
        Matrix4.mulVec(mat.val, pos);

        pos[0] /= pos[3];
        pos[1] /= pos[3];

        return new Vector2(pos[0], pos[1]);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
//        this.zoom(Rainselda.getU() * 2 - 1, Rainselda.getV() * 2 - 1, 1.1, (int) amountY);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            this.setMouse(((screenX - this.lastX) / (double) this.rainselda.getWidth()) * 2, (-((screenY - this.lastY) / (double) this.rainselda.getHeight())) * 2);
        }

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 2) {
            this.lastX = screenX;
            this.lastY = screenY;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == 2) {
            this.pushMouse();
        }

        return false;
    }

}