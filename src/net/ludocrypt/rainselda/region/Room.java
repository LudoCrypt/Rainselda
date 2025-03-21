package net.ludocrypt.rainselda.region;

public class Room extends MapObject {

	public Room() {
		super();
	}

	public enum RoomTag {
		NONE("None"),
		SHELTER("Shelter"),
		ANCIENTSHELTER("Ancient Shelter"),
		GATE("Gate"),
		SWARMROOM("Swarm Room"),
		PERF_HEAVY("Performance Heavy"),
		SCAVOUTPOST("Scav Outpost"),
		SCAVTRADER("Scav Trader"),
		NOTRACKERS("No Trackers"),
		ARENA("Arena");

		String displayName;

		private RoomTag(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}

	}

}
