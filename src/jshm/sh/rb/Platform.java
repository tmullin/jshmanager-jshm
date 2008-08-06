package jshm.sh.rb;

public enum Platform {
	PS2(1),
	XBOX360(2),
	PS3(3),
	WII(4)
	;
	
	public final int id;
	
	private Platform(final int id) {
		this.id = id;
	}
}
