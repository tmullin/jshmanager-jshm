package jshm.hibernate;

import jshm.Game;

public class GameUserType extends FakeEnumUserType {
	public GameUserType() {
		super(Game.class);
	}
}