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
package jshm.gui;

import java.awt.Component;
import java.awt.Frame;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.logging.Logger;

import jshm.util.Crypto;

import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.auth.PasswordStore;

public class ShLoginPanel extends JXLoginPane {
	static final Logger LOG = Logger.getLogger(ShLoginPanel.class.getName());
	
	public static Status showDialog() {
		return showDialog(null);
	}
	
	public static Status showDialog(Component parent) {
		return JXLoginPane.showLoginDialog(parent, new ShLoginPanel());
	}
	
	public ShLoginPanel() {
		this(null);
	}
	
	public ShLoginPanel(Frame owner) {
		super();
		
		setPasswordStore(PASS_STORE);
		setSaveMode(SaveMode.PASSWORD);
		setUserName(PASS_STORE.getUsername());
		setPassword(PASS_STORE.getPassword().toCharArray());
		
		setLoginService(new LoginService() {
			@Override
			public boolean authenticate(String name, char[] password,
					String server) throws Exception {
				
				String pw = new String(password);				
				jshm.sh.Client.getAuthCookies(name, pw);
				return true;
			}
			
		});

		try {
			setUserName(
				getUserNameStore().getUserNames()[0]);
		} catch (ArrayIndexOutOfBoundsException e) {}
	}
	
	private static final MyPasswordStore PASS_STORE = new MyPasswordStore();
	
	/**
	 * This {@link PasswordStore} stores a single username/password
	 * pair. Calling set() will overrite the previously saved pair.
	 */
	private static class MyPasswordStore extends PasswordStore {
		Properties props = new Properties();
		
		public void load() throws Exception {
			LOG.finest("Loading password from data/passwords.properties");
			props.load(new FileInputStream("data/passwords.properties"));
		}
		
		public String getUsername() {
			if (props.isEmpty()) {
				try {
					load();
				} catch (Exception e) {
					return "";
				}
			}
			
			for (Object key : props.keySet()) {
//				System.out.println("returning: " + key);
				return key.toString();
			}
			
//			System.out.println("returing blank");
			return "";
		}
		
		public String getPassword() {
			if (props.isEmpty()) {
				try {
					load();
				} catch (Exception e) {
					return "";
				}
			}
			
			for (Object key : props.keySet()) {
//				System.out.println("returning pass: " + key);
				return Crypto.decrypt(props.getProperty(key.toString()));
			}
			
//			System.out.println("returning blank pass");
			return "";
		}
		
		@Override
		public char[] get(String username, String server) {
			LOG.finer("Retrieving password for " + username);
			
			if (props.isEmpty()) {
				try {
					load();
				} catch (Exception e) {
					return null;
				}
			}
			
			if (!props.containsKey(username)) return null;
			return Crypto.decrypt(
				props.get(username).toString()).toCharArray();
		}

		@Override
		public boolean set(String username, String server,
				char[] password) {
			LOG.finer("Setting password for " + username);
			props.clear();
			props.setProperty(username,
				Crypto.encrypt(String.valueOf(password)));
			
			LOG.finest("  encrypted=" + props.getProperty(username));
			
			try {
				props.store(new FileOutputStream("data/passwords.properties", false), "");
			} catch (Exception e) {
				return false;
			}
			
			return true;
		}
	};
}
