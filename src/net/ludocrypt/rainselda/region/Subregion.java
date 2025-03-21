package net.ludocrypt.rainselda.region;

import java.util.Optional;

import com.badlogic.gdx.graphics.Color;

public class Subregion extends MapObject {

    public Subregion(String name, Color color) {
        super(name);
        this.dispColor = Optional.of(color);
    }

}
