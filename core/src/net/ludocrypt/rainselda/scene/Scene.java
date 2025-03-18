package net.ludocrypt.rainselda.scene;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.Null;

public abstract class Scene extends ApplicationAdapter {

    /*
     * Optional input adapter to track inputs, can be null.
     */
    public abstract @Null InputAdapter getAdapter();

    /*
     * Called when files are dropped into main window
     */
    public abstract void filesDropped(String[] files);

}
