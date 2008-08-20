package jshm.gui;

import java.awt.Frame;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.jdesktop.swingx.JXLoginDialog;
import org.jdesktop.swingx.JXLoginPane.SaveMode;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.auth.PasswordStore;

public class ShLoginDialog extends JXLoginDialog {
	public ShLoginDialog() {
		this(null, false);
	}
	
	public ShLoginDialog(Frame owner) {
		this(owner, true);
	}
	
	public ShLoginDialog(Frame owner, boolean modal) {
		super(owner, "ScoreHero Login", modal);
		
		getPanel().setPasswordStore(PASS_STORE);
		getPanel().setSaveMode(SaveMode.PASSWORD);
		getPanel().setUserName(PASS_STORE.getUsername());
		getPanel().setPassword(PASS_STORE.getPassword().toCharArray());
		
		getPanel().setLoginService(new LoginService() {
			@Override
			public boolean authenticate(String name, char[] password,
					String server) throws Exception {
				
				String pw = new String(password);				
				jshm.sh.Client.getAuthCookies(name, pw);
				return true;
			}
			
		});

		try {
			getPanel().setUserName(
				getPanel().getUserNameStore().getUserNames()[0]);
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
				return props.getProperty(key.toString());
			}
			
//			System.out.println("returning blank pass");
			return "";
		}
		
		@Override
		public char[] get(String username, String server) {
			if (props.isEmpty()) {
				try {
					load();
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
			props.clear();
			props.setProperty(username, String.valueOf(password));
			
			try {
				props.store(new FileOutputStream("data/passwords.properties", false), "");
			} catch (Exception e) {
				return false;
			}
			
			return true;
		}
	};
}
