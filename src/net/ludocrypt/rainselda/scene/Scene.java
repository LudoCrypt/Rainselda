package net.ludocrypt.rainselda.scene;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.Null;

public abstract class Scene extends ApplicationAdapter {

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
