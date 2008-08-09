package jshm;

import javax.persistence.*;

import org.hibernate.validator.*;

/**
 * Represents a part played for a particular Score.
 * @author Tim Mullin
 *
 */
@Entity
public class Part {
	private int			id;

	private float 		hitPercent;
	private int 		streak;
	private Instrument 	instrument;
	
	@Id
	public int getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(int id) {
		this.id = id;
	}
	
	@Range(min=0, max=1)
	public float getHitPercent() {
		return hitPercent;
	}
	
	public void setHitPercent(float hitPercent) {
		if (hitPercent < 0.0f || 1.0f < hitPercent)
			throw new IllegalArgumentException("hitPercent must be between 0.0f and 1.0f inclusive");
		this.hitPercent = hitPercent;
	}
	
	@Min(0)
	public int getStreak() {
		return streak;
	}
	
	public void setStreak(int streak) {
		if (streak < 0)
			throw new IllegalArgumentException("streak must be >= 0");
		this.streak = streak;
	}
	
	@Enumerated(EnumType.STRING)
	public Instrument getInstrument() {
		return instrument;
	}
	
	public void setInstrument(Instrument instrument) {
		if (null == instrument)
			throw new IllegalArgumentException("instrument cannot be null");
		this.instrument = instrument;
	}
}
