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
package jshm.scraper.format;

import java.util.*;
import java.util.regex.*;

import org.htmlparser.*;
import org.htmlparser.util.*;
import org.htmlparser.nodes.*;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.Span;
import org.htmlparser.tags.TableColumn;

/**
 * 
 * @author Tim Mullin
 *
 */
public class TableColumnFormat {
	private static final Map<String, Map<String, NodeFormat>> ARG_MAP =
		new HashMap<String, Map<String, NodeFormat>>();
	private static final Map<String, Class<? extends Node>> TYPE_MAP =
		new HashMap<String, Class<? extends Node>>();
	
	static {
		addFormat("text", TextNode.class,
			NodeFormat.DEFAULT,
			"int", NodeFormat.KEEP_NUMBERS,
			"float", NodeFormat.PARSE_FLOAT);
		
		addFormat("link", LinkTag.class,
			TagFormat.HREF);
		
		addFormat("img", ImageTag.class,
			TagFormat.SRC);
		
		addFormat("span", Span.class,
			TagFormat.TITLE);
		
		jshm.sh.scraper.Formats.init();
	}

	/**
	 * Edits a format that {@link #factory(String)} is able to parse.
	 * @param typeName The format name to edit. Built-in types include "text", "img", "link".
	 * @param args See {@link #addFormat(String, Class, Object[])} for details. 
	 */
	public static void addFormat(final String typeName, final Object ... args) {
		addFormat(typeName, null, args);
	}
	
	/**
	 * Adds or edits a format that {@link #factory(String)} will be
	 * able to parse. 
	 * @param typeName The format name to add or edit. Built-in types include "text", "img", "link".
	 * @param nodeClass The Node class type to associate with the typeName
	 * when adding new formats. It must be null when editing an existing format.
	 * @param args This should contain alternating Strings and {@link NodeFormat}s that
	 * will serve as arguments in the format string for {@link #factory(String)}.
	 * You may have the first element be a NodeFormat instead of a String to
	 * specify the default NodeFormat.
	 * @see #addType(Class, NodeFormat[])
	 */
	public static void addFormat(final String typeName, Class<? extends Node> nodeClass, final Object ... args) {
		if (args.length < 1)
			throw new IllegalArgumentException("args must have at least 1 element");
		
		final boolean modifyingExistingType = ARG_MAP.containsKey(typeName);
		
		if (!modifyingExistingType && null == nodeClass)
			throw new IllegalArgumentException("nodeClass cannot be null when adding a new format");
		
		final Map<String, NodeFormat> tmp =
			modifyingExistingType
			? ARG_MAP.get(typeName)
			: new HashMap<String, NodeFormat>();
			
		boolean didDefault = false;
		int i = 0;
		
		// if the first arg is a NodeFormat, then it is to be
		// the default NodeFormat
		if (args[i] instanceof NodeFormat) {
			// there must be an odd number of args
			if ((args.length & 1) != 1) {
				throw new IllegalArgumentException("args must have an odd number of elements when the first is the default format");
			}
			
			tmp.put("", (NodeFormat) args[i]);
			didDefault = true;
			i++;
		} else if ((args.length & 1) != 0) {
			throw new IllegalArgumentException("args must have an even number of elements when the first is not the default format");
		}
		
		for (int j = i; j < args.length; j += 2) {
			if (!(args[j] instanceof String))
				throw new IllegalArgumentException("args[" + j + "] should be a String");
			if (!(args[j + 1] instanceof NodeFormat))
				throw new IllegalArgumentException("args[" + j + "] should be a NodeFormat");
			
			String key = (String) args[j];
			NodeFormat fmt = (NodeFormat) args[j + 1];
			
			tmp.put(key, fmt);
			
			if ("".equals(key))
				didDefault = true;
		}
		
		if (!didDefault)
			throw new IllegalArgumentException("args must contain a default format");
		
		ARG_MAP.put(typeName, tmp);
		
		if (null != nodeClass)
			TYPE_MAP.put(typeName, nodeClass);
	}
	
	// text|text=int|img|text=int,-,int~img
	
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
