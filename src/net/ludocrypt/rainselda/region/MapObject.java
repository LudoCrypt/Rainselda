package net.ludocrypt.rainselda.region;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.badlogic.gdx.graphics.Color;

public class MapObject {

    String name = "";
    Optional<Color> dispColor = Optional.empty();
    String comments = "";
    String hoverNote = "";
    List<File> media = new ArrayList<File>();
    boolean canSelect = true;

    public MapObject() {
    }

    public MapObject(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Optional<Color> getDispColor() {
        return this.dispColor;
    }

    public String getComments() {
        return this.comments;
    }

    public String getHoverNote() {
        return this.hoverNote;
    }

    public List<File> getMedia() {
        return this.media;
    }

    public boolean canSelect() {
        return this.canSelect;
    }

}
