package jshm;

public enum Platform {
	PS2,
	PS3,
	XBOX360,
	WII,
	PC,
	DS
	;
	
	private transient javax.swing.ImageIcon icon = null;
	
	public javax.swing.ImageIcon getIcon() {
		if (null == icon) {
			try {
				icon = new javax.swing.ImageIcon(
					GameTitle.class.getResource("/jshm/resources/images/platforms/" + this.toString() + "_32.png"));
			} catch (Exception e) {}
		}
		
		return icon;
	}
}
