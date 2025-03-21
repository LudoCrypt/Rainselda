package net.ludocrypt.rainselda.region;

public enum Progress {
	DONE("Done"),
	NEEDS_REVIEW("Needs Review"),
	IN_PROGRESS("In Progress"),
	TODO("TODO");

	String displayName;

	private Progress(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
