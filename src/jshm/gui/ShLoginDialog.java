package jshm.gui;

import java.awt.Frame;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.jdesktop.swingx.JXLoginDialog;
import org.jdesktop.swingx.JXLoginPane.SaveMode;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.auth.PasswordStore;
import org.jdesktop.swingx.auth.UserNameStore;

public class ShLoginDialog extends JXLoginDialog {
	public ShLoginDialog() {
		this(null, false);
	}
	
	public ShLoginDialog(Frame owner) {
		this(owner, true);
	}
	
	public ShLoginDialog(Frame owner, boolean modal) {
		super(owner, "ScoreHero Login", modal);

		getPanel().setLoginService(new LoginService() {
			@Override
			public boolean authenticate(String name, char[] password,
					String server) throws Exception {
				
				String pw = new String(password);
				
				if (pw.isEmpty()) {
					pw = new String(getPanel().getPasswordStore().get(name, ""));
				}
				
				jshm.sh.Client.getAuthCookies(name, pw);
				return true;
			}
			
		});
		
		getPanel().setPasswordStore(new PasswordStore() {
			Properties props = new Properties();
			
			@Override
			public char[] get(String username, String server) {
				System.out.println("getting pass for: " + username);
				if (props.isEmpty()) {
					try {
						System.out.println("loading pass");
						props.load(new FileInputStream("data/passwords.properties"));
					} catch (Exception e) {
						return null;
					}
				}
				
				if (!props.containsKey(username)) return null;
				return props.get(username).toString().toCharArray();
			}

			@Override
			public boolean set(String username, String server,
					char[] password) {
				System.out.println("setting pw for " + username);
				props.setProperty(username, String.valueOf(password));
				
				try {
					props.store(new FileOutputStream("data/passwords.properties", false), "");
				} catch (Exception e) {
					return false;
				}
				
				return true;
			}
		});
		
		getPanel().setUserNameStore(new UserNameStore() {						
			Properties props = new Properties();
			
			@Override
			public void addUserName(String userName) {
				props.setProperty(userName, "");
			}

			@Override
			public boolean containsUserName(String name) {
				return props.containsKey(name);
			}

			@Override
			public String[] getUserNames() {
				if (props.isEmpty()) loadUserNames();
				
//				System.out.println("getting user names...");
				List<String> keys = new ArrayList<String>();
				
				for (Object o : props.keySet()) {
					keys.add(o.toString());
//					System.out.println(o);
				}
				
				Collections.sort(keys);
				
				return keys.toArray(new String[0]);
			}

			@Override
			public void loadUserNames() {
				System.out.println("loading user names...");
				try {
					props.load(new FileInputStream("data/users.properties"));
				} catch (Exception e) {}
			}

			@Override
			public void removeUserName(String userName) {
				props.remove(userName);
			}

			@Override
			public void saveUserNames() {
				try {
					props.store(new FileOutputStream("data/users.properties", false), "");
				} catch (Exception e) {}
			}

			@Override
			public void setUserNames(String[] names) {
				props.clear();
				
				for (String s : names) {
					props.setProperty(s, "");
				}
			}
		});

		getPanel().setSaveMode(SaveMode.BOTH);

		try {
			getPanel().setUserName(
				getPanel().getUserNameStore().getUserNames()[0]);
		} catch (ArrayIndexOutOfBoundsException e) {}
	}
}
