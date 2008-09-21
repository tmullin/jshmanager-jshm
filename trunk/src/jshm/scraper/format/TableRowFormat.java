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

import org.htmlparser.util.*;
import org.htmlparser.tags.*;

/**
 * This class serves to describe the format of a row of
 * tabular data to make it easier to extract needed data.
 * @author Tim
 *
 */
public class TableRowFormat {
	private static final Pattern FORMAT_SPLIT_PATTERN =
		Pattern.compile("\\|");
	
	public static TableRowFormat factory(final String format)
	throws FormatException {
		TableRowFormat fmt = new TableRowFormat();
		String[] parts = FORMAT_SPLIT_PATTERN.split(format);
		
		for (String s : parts) {
			fmt.addColumn(TableColumnFormat.factory(s));
		}
		
		return fmt;
	}
	
	List<TableColumnFormat> formats;
	
	public TableRowFormat() {
		formats = new ArrayList<TableColumnFormat>();
	}
	
	public TableRowFormat(final TableColumnFormat ... formats) {
		this();
		
		if (null == formats)
			throw new IllegalArgumentException("formats cannot be null");
		
		for (TableColumnFormat f : formats) {
			addColumn(f);
		}
	}
	
	private void addColumn(final TableColumnFormat format) {
		formats.add(format);
	}
	
	/**
	 * 
	 * @param row
	 * @return A 2-dimensional String array of data. The first dimension
	 * directly corresponds to each column that this format has. It may be
	 * null if a given column is ignored via "-". The second dimension
	 * corresponds to each piece of information found in that column
	 * according to this format. 
	 * @throws FormatException
	 */
	public String[][] getData(final TableRow row)
	throws FormatException {
		List<String[]> data = new ArrayList<String[]>();
		
		NodeList children = row.getChildren();
		final int childrenSize = children.size();
		
		for (int i = 0; i < childrenSize; i++) {
			TableColumnFormat curFormat = null;
			
			try {
				curFormat = formats.get(i);
			} catch (IndexOutOfBoundsException e) {
				// TODO throw exception here and force a null
				// format for every column in the table we don't
				// care about?
				break;
			}
			
			// ignoring this column
			if (null == curFormat) continue;
			
			if (!(children.elementAt(i) instanceof TableColumn))
				throw new FormatException("Received a TableRow where one of the child nodes isn't a TableColumn");
			
			String[] curData = curFormat.getData((TableColumn) children.elementAt(i));
			
//			if (null != curData)
				data.add(curData);
		}
		
		return data.toArray(new String[][] {});
	}
}
