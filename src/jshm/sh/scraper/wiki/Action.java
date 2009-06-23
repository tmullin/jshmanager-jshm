package jshm.sh.scraper.wiki;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single wiki action. Ex:
 * <pre>
 * {{songinfo
 *     genre="Metal"
 *     album="City of Evil"
 *     year="2005"
 *     artist="Avenged Sevenfold"}}
 * </pre>
 * @author Tim Mullin
 *
 */
public class Action {
	public String name;
	public final Map<String, String> args = new HashMap<String, String>();
	
	public Action() {
		this(null);
	}
	
	public Action(String name) {
		this.name = name;
	}
	
	public String set(String key, String value) {
		return args.put(key, value);
	}
	
	public String get(String key) {
		return args.get(key);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("{{");
		sb.append(name);
		
		for (String key : args.keySet()) {
			sb.append('\n');
			sb.append("    ");
			sb.append(key);
			sb.append('=');
			sb.append('"');
			sb.append(args.get(key));
			sb.append('"');
		}
		
		sb.append("}}");
		
		return sb.toString();
	}
}
