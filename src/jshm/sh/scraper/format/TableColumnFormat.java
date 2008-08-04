package jshm.sh.scraper.format;

import java.util.*;
import java.util.regex.*;

import org.htmlparser.*;
import org.htmlparser.util.*;
import org.htmlparser.nodes.*;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableColumn;

/**
 * 
 * @author Tim Mullin
 *
 */
public class TableColumnFormat {
	// some basic types
	public static final TableColumnFormat PLAIN_TEXT =
		new TableColumnFormat(TextNode.class, NodeFormat.DEFAULT);
	public static final TableColumnFormat COMMA_NUMBER =
		new TableColumnFormat(TextNode.class, NodeFormat.KEEP_NUMBERS);
	public static final TableColumnFormat SONG_ID_LINK =
		new TableColumnFormat(LinkTag.class, TagFormat.SONG_ID_HREF);
	

	private static final Map<String, Map<String, NodeFormat>> ARG_MAP =
		new HashMap<String, Map<String, NodeFormat>>();
	private static final Map<String, Class<? extends Node>> TYPE_MAP =
		new HashMap<String, Class<? extends Node>>();
	
	static {
		Map<String, NodeFormat> tmp = new HashMap<String, NodeFormat>();
		tmp.put("", NodeFormat.DEFAULT);
		tmp.put("int", NodeFormat.KEEP_NUMBERS);
		tmp.put("float", NodeFormat.PARSE_FLOAT);
		ARG_MAP.put("text", tmp);
		TYPE_MAP.put("text", TextNode.class);
		
		tmp = new HashMap<String, NodeFormat>();
		tmp.put("", TagFormat.HREF);
		tmp.put("songid", TagFormat.SONG_ID_HREF);
		ARG_MAP.put("link", tmp);
		TYPE_MAP.put("link", LinkTag.class);
		
		tmp = new HashMap<String, NodeFormat>();
		tmp.put("", TagFormat.SRC);
		tmp.put("rating", TagFormat.STAR_RATING_SRC);
		ARG_MAP.put("img", tmp);
		TYPE_MAP.put("img", ImageTag.class);
	}
	
	private static final Map<String, Object[]> TAG_ARG_MAP =
		new HashMap<String, Object[]>();
	
	static {
		TAG_ARG_MAP.put("img", new Object[] { ImageTag.class, TagFormat.SRC});
		TAG_ARG_MAP.put("link", new Object[] { LinkTag.class, TagFormat.HREF});
	}
	
	// text|text=int|tag=img-src|text=int,-,int~tag=img
	
	private static final Pattern FORMAT_SPLIT_REGEX1 =
		Pattern.compile("~");
	private static final Pattern FORMAT_SPLIT_REGEX2 =
		Pattern.compile("=");
	private static final Pattern FORMAT_SPLIT_REGEX3 =
		Pattern.compile(",");
	
	public static TableColumnFormat factory(String format)
	throws FormatException {
		TableColumnFormat fmt = new TableColumnFormat();
		final String[] parts = FORMAT_SPLIT_REGEX1.split(format);

		for (String s : parts) {
			final String[] typeParts = FORMAT_SPLIT_REGEX2.split(s);
			
			if (typeParts.length > 2)
				throw new InvalidFormatException(s);
			
			String type = typeParts[0];
			String[] args = new String[0];
			
			if (typeParts.length == 2) {
				args = FORMAT_SPLIT_REGEX3.split(typeParts[1]);
			}
			
			Class<? extends Node> myClass = null;
			NodeFormat[] myFmts = new NodeFormat[args.length];
			
			if ("-".equals(type)) {
				continue; // ignore column
			} else if (ARG_MAP.containsKey(type)) {
				final Map<String, NodeFormat> curFmtMap = ARG_MAP.get(type);
				myClass = TYPE_MAP.get(type);
				
				if (args.length == 0) {
					myFmts = new NodeFormat[] {
						curFmtMap.get("")
					};
				}
				
				for (int i = 0; i < args.length; i++) {
					NodeFormat curFmt = null;
					
					if ("-".equals(args[i])) {
						// keep the null
					} else if (curFmtMap.containsKey(args[i])) {
						curFmt = curFmtMap.get(args[i]);
					} else {
						throw new InvalidFormatArgumentException(type + "=" + args[i]);
					}
					
					myFmts[i] = curFmt;
				}
			} else {
				throw new UndefinedFormatTypeException(type);
			}
			
			fmt.addType(myClass, myFmts);
		}
		
		return fmt;
	}
	
	/**
	 * This maps each type of node we're expecting in this
	 * column to a format to return for each instance of the
	 * node type we find.
	 */
	private List<Class<? extends Node>> types;
	private List<NodeFormat[]> formats;
	
	public TableColumnFormat() {
		this.types = new ArrayList<Class<? extends Node>>();
		this.formats = new ArrayList<NodeFormat[]>();
	}
	
	public TableColumnFormat(Class<? extends Node> nodeClass, NodeFormat ... formats) {
		this();
		
		addType(nodeClass, formats);
	}
	
	public TableColumnFormat(final Class<? extends Node>[] types, final NodeFormat[][] formats) {
		this();
		
		if (types.length != formats.length)
			throw new IllegalArgumentException("types and formats must have the same size");
		
		for (int i = 0; i < types.length; i++) {
			addType(types[i], formats[i]);
		}
	}
	
	public void addType(Class<? extends Node> nodeClass, NodeFormat ... formats) {
		if (null == formats)
			throw new IllegalArgumentException("formats cannot be null");
		
		this.types.add(nodeClass);
		this.formats.add(formats);
	}
	
	
	/**
	 * Cache for the NodeClassFilters
	 */
	private static final Map<Class<? extends Node>, NodeFilter> CLASS_FILTER_CACHE =
		new HashMap<Class<? extends Node>, NodeFilter>();
	
	private static NodeFilter getNodeFilter(Class<? extends Node> nodeType) {
		if (!CLASS_FILTER_CACHE.containsKey(nodeType)) {
			CLASS_FILTER_CACHE.put(nodeType, new NodeClassFilter(nodeType));
		}
		
		return CLASS_FILTER_CACHE.get(nodeType);
	}
	
	/**
	 * Returns the data to be found by this format.
	 * @param tc
	 * @return
	 */
	public String[] getData(TableColumn tc) {
		List<String> data = new ArrayList<String>();
		
		// for each of the node types we are interested in
		final int typesSize = types.size();
		
		for (int i = 0; i < typesSize; i++) {
			NodeFormat[] curFormats = formats.get(i);
			
			NodeFilter filter = getNodeFilter(types.get(i));
			NodeList nodes = tc.getChildren().extractAllNodesThatMatch(filter, true);
			final int nodesSize = nodes.size();
			
			// for each of the nodes of the current type we find
			for (int j = 0; j < nodesSize && j < curFormats.length; j++) {
				// get the text from the current node
				data.add(curFormats[j].getText(nodes.elementAt(j)));
			}
		}
		
		return data.size() > 0 ? data.toArray(new String[] {}) : null;
	}
	
	
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof TableColumnFormat)) return false;
		
		TableColumnFormat f = (TableColumnFormat) o;
		
		if (!this.types.equals(f.types)) return false;
		if (this.formats.size() != f.formats.size()) return false;
		
		for (int i = 0; i < this.formats.size(); i++) {
			if (!Arrays.equals(this.formats.get(i), f.formats.get(i)))
				return false;
		}
		
		return true;
	}
}
