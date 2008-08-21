/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008 Tim Mullin
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * -----LICENSE END-----
 */
package jshm.hibernate;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import jshm.Game;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * Defines how to store a "fake enum". This will store
 * an object via its toString() in a varchar column and restore
 * it with the class's static valueOf(String) method.
 * 
 * <p>
 * A fake enum is like a real enum in that there is a
 * set of limited unique elements with unique string representations
 * but for the sake of extensibility they are not implemented
 * as an enum. The fake enum class is responsible for maintaining
 * a static list of these elements. {@link jshm.Game} is an example.
 * </p>
 * @author Tim Mullin
 *
 */
public abstract class FakeEnumUserType implements UserType {
	private static final int[] SQL_TYPES = {
		Types.VARCHAR
	};
	
	private Object invokeValueOf(Object value) throws IllegalArgumentException, SecurityException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		return method.invoke(null, (String) value);
	}
	
	protected final Class<?> clazz;
	protected Method method;
	
	protected FakeEnumUserType(Class<?> clazz) {
		this.clazz = clazz;
		
		try {
			method = clazz.getMethod("valueOf", String.class);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("no valueOf() method found when constructing " + this.getClass().getName(), e);
		}
	}
	
	@Override
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {

		try {
			return invokeValueOf(cached);
		} catch (Exception e) {
			throw new RuntimeException("class does not have a static valueOf() methods", e);
		}
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return value.toString();
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if (null != x) return x.equals(y);
		return null == x && null == y;
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException {
		
	    Object result = null;
		String gameAsString = rs.getString(names[0]);

		if (!rs.wasNull()) {
			try {
				result = invokeValueOf(gameAsString);
			} catch (Exception e) {
				throw new RuntimeException("class does not have a static valueOf() methods", e);
			}
		}
	    
	    return result;

	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index)
			throws HibernateException, SQLException {
		
	    if (value == null) {
			st.setString(index, "");
		} else {
			String gameAsString = value.toString();
			st.setString(index, gameAsString);
		}
	}

	@Override
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	@Override
	public Class<Game> returnedClass() {
		return Game.class;
	}

	@Override
	public int[] sqlTypes() {
		return SQL_TYPES;
	}

}
