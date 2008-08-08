package jshm.scraper.format;

import java.util.regex.*;

public class RegexFormatCallback implements NodeFormatCallback {
	protected Pattern pattern;
	protected String replacement;
	protected int group;
	
	public RegexFormatCallback(final String regex) {
		this(regex, null);
	}
	
	public RegexFormatCallback(final String regex, final String replacement) {
		this(regex, replacement, -1);
	}
	
	public RegexFormatCallback(final String regex, final int group) {
		this(regex, null, group);
	}
	
	public RegexFormatCallback(final String regex, final String replacement, int group) {
		if (group < -1)
			throw new IllegalArgumentException("Group must be >= -1, given: " + group);
		
		this.pattern = Pattern.compile(regex);
		this.replacement = null != replacement ? replacement : "";
		this.group = group;
	}

	public String format(final String text) {
		Matcher m = pattern.matcher(text);
		
		if (-1 == group) {
			return m.replaceAll(replacement);
		} else {
			return m.matches() ? m.group(group) : replacement;
		}
	}

}
