package jshm.hibernate;

import jshm.GameTitle;

public class GameTitleUserType extends FakeEnumUserType {
	public GameTitleUserType() {
		super(GameTitle.class);
	}
}