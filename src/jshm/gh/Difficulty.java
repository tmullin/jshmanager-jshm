package jshm.gh;

public enum Difficulty {
	UNKNOWN(0),
	EASY(1),
	MEDIUM(2),
	HARD(3),
	EXPERT(4),
	CO_OP(5);
	
	public final int scoreHeroId;
	
	private Difficulty(final int scoreHeroId) {
		this.scoreHeroId = scoreHeroId;
	}
}
