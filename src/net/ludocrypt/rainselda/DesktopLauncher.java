package net.ludocrypt.rainselda;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        config.setForegroundFPS(60);
        config.setTitle("Rainselda");
        config.setWindowListener(new Lwjgl3WindowAdapter() {

            @Override
            public void filesDropped(String[] files) {
                Rainselda.INSTANCE.filesDropped(files);
            }

        });

        config.setWindowIcon("Icon.png", "Icon64.png", "Icon48.png", "Icon32.png", "Icon16.png");

        new Lwjgl3Application(Rainselda.INSTANCE, config);

    }

}
