package net.ludocrypt.rainselda.region;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Region {

	List<Subregion> subregions = new ArrayList<>();
	Map<MapObject, Mapos> map = new HashMap<>();
	List<Creator> creators = new ArrayList<>();
	String comments = "";
	File home = new File("region");

	public void addRoom(Room room, Mapos pos) {
		this.map.put(room, pos);
	}

	public Map<MapObject, Mapos> getMap() {
		return map;
	}

}
