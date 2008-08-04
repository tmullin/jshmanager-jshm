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

public class TableColumnFormat {
	// some basic types
	public static final TableColumnFormat PLAIN_TEXT =
		new TableColumnFormat(TextNode.class, NodeFormat.DEFAULT);
	public static final TableColumnFormat COMMA_NUMBER =
		new TableColumnFormat(TextNode.class, NodeFormat.KEEP_NUMBERS);
	public static final TableColumnFormat SONG_ID_LINK =
		new TableColumnFormat(LinkTag.class, NodeFormat.SONG_ID_LINK);
	
	private static final Pattern FORMAT_SPLIT_REGEX1 =
		Pattern.compile("~");
	private static final Pattern FORMAT_SPLIT_REGEX2 =
		Pattern.compile("=");
	private static final Pattern FORMAT_SPLIT_REGEX3 =
		Pattern.compile(",");

	private static final Map<String, NodeFormat> TEXT_ARG_MAP =
		new HashMap<String, NodeFormat>();
	
	static {
		TEXT_ARG_MAP.put("int", NodeFormat.KEEP_NUMBERS);
		TEXT_ARG_MAP.put("float", NodeFormat.PARSE_FLOAT);
	}
	
	private static final Map<String, Object[]> TAG_ARG_MAP =
		new HashMap<String, Object[]>();
	
	static {
		TAG_ARG_MAP.put("img", new Object[] { ImageTag.class, TagFormat.SRC});
		TAG_ARG_MAP.put("link", new Object[] { LinkTag.class, TagFormat.HREF});
	}
	
	// text|text=int|tag=img-src|text=int,-,int~tag=img
	
	public static TableColumnFormat factory(String format)
	throws FormatException {
		TableColumnFormat fmt = new TableColumnFormat();
		final String[] parts = FORMAT_SPLIT_REGEX1.split(format);

		for (String s : parts) {
			final String[] typeParts = FORMAT_SPLIT_REGEX2.split(s);
			
			if (typeParts.length > 2)
				throw new FormatException("Invalid format: " + s);
			
			String type = typeParts[0];
			String[] args = new String[0];
			
			if (typeParts.length == 2) {
				args = FORMAT_SPLIT_REGEX3.split(typeParts[1]);
			}
			
			Class<? extends Node> myClass = null;
			NodeFormat[] myFmts = new NodeFormat[args.length];
			
			if ("-".equals(type)) {
				continue; // ignore column
			} else if ("text".equals(type)) {
				myClass = TextNode.class;
				
				if (args.length == 0) {
					myFmts = new NodeFormat[] {
						NodeFormat.DEFAULT
					};
				}
				
				for (int i = 0; i < args.length; i++) {
					NodeFormat curFmt = null;
					
					if ("-".equals(args[i])) {
						// keep the null
					} else if (TEXT_ARG_MAP.containsKey(args[i])) {
						curFmt = TEXT_ARG_MAP.get(args[i]);
					} else {
						throw new FormatException("Unknown text arg: " + args[i]);
					}
					
					myFmts[i] = curFmt;
				}
			} else if (TAG_ARG_MAP.containsKey(type)) {
				Object[] arr = TAG_ARG_MAP.get(type);
				myClass = (Class<? extends Node>) arr[0];				
				
				if (args.length == 0) {
					myFmts = new NodeFormat[] {
						(NodeFormat) arr[1]
					};
				}
				
				for (int i = 0; i < args.length; i++) {
					if ("-".equals(args[i])) {
						myFmts[i] = null;
						continue;
					}
					
					myFmts[i] = new TagFormat(args[i]);
				}
			} else {
				throw new FormatException("Unknown node type: " + type);
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
	
	public String[] getData(TableColumn tc) {
		List<String> data = new ArrayList<String>();
		
		// for each of the node types we are interested in
		final int typesSize = types.size();
		
		for (int i = 0; i < typesSize; i++) {
			NodeFormat[] curFormats = formats.get(i);
			
			// TODO cache filters once we create them initially
			NodeFilter filter = new NodeClassFilter(types.get(i));
			NodeList nodes = tc.getChildren().extractAllNodesThatMatch(filter, true);
			final int nodesSize = nodes.size();
			
			// for each of the nodes of the current type we find
			for (int j = 0; j < nodesSize && j < curFormats.length; j++) {
				// get the text from the current node
				data.add(curFormats[j].getText(nodes.elementAt(j)));
			}
		}
		
		return data.toArray(new String[] {});
	}
}
