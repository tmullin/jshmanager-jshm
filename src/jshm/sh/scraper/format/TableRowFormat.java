package jshm.sh.scraper.format;

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
		Pattern.compile("\\s*\\|\\s*");
	
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
			
			data.add(curFormat.getData((TableColumn) children.elementAt(i)));
		}
		
		return data.toArray(new String[][] {});
	}
}
