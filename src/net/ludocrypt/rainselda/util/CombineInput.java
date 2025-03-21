package net.ludocrypt.rainselda.util;

import com.badlogic.gdx.InputAdapter;

public class CombineInput extends InputAdapter {

    InputAdapter adapterA;
    InputAdapter adapterB;

    public CombineInput(InputAdapter adapterA, InputAdapter adapterB) {
        this.adapterA = adapterA;
        this.adapterB = adapterB;
    }

    public boolean keyDown(int keycode) {
        adapterB.keyDown(keycode);
        return adapterA.keyDown(keycode);
    }

    public boolean keyUp(int keycode) {
        adapterB.keyUp(keycode);
        return adapterA.keyUp(keycode);
    }

    public boolean keyTyped(char character) {
        adapterB.keyTyped(character);
        return adapterA.keyTyped(character);
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        adapterB.touchDown(screenX, screenY, pointer, button);
        return adapterA.touchDown(screenX, screenY, pointer, button);
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        adapterB.touchUp(screenX, screenY, pointer, button);
        return adapterA.touchUp(screenX, screenY, pointer, button);
    }

    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        adapterB.touchCancelled(screenX, screenY, pointer, button);
        return adapterA.touchCancelled(screenX, screenY, pointer, button);
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        adapterB.touchDragged(screenX, screenY, pointer);
        return adapterA.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        adapterB.mouseMoved(screenX, screenY);
        return adapterA.mouseMoved(screenX, screenY);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        adapterB.scrolled(amountX, amountY);
        return adapterA.scrolled(amountX, amountY);
    }

}
