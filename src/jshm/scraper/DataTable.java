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
package jshm.scraper;

import jshm.scraper.format.TableRowFormat;

import org.htmlparser.NodeFilter;

/**
 * This stores some information about the table layouts
 * of the different data tables.
 */
public abstract class DataTable {		
	public static final String DEFAULT_HEADER_CSS_CLASS = "headrow";
	public static final String DEFAULT_TIER_CSS_CLASS = "tier1";
	public static final TableRowFormat TIER_ROW_FORMAT =
		TableRowFormat.factory("text|-");
	
	public final String headerCssClass;
	public final String tierCssClass;
	public final int tierColspan;
	public final int tierColspanMax;
	public final int tierChildNodeCount;
	public final int rowChildNodeCount;
	public final TableRowFormat rowFormat;

	public DataTable(
		final int tierColspan,
		final int tierChildNodeCount,
		final int rowChildNodeCount,
		final String rowFormat) {
		
		this(tierColspan, tierColspan, tierChildNodeCount, rowChildNodeCount, rowFormat);
	}
	
	public DataTable(
		final int tierColspan,
		final int tierColspanMax,
		final int tierChildNodeCount,
		final int rowChildNodeCount,
		final String rowFormat) {
		
		this(null, null, tierColspan, tierColspanMax, tierChildNodeCount, rowChildNodeCount, rowFormat);
	}
	
	public DataTable(
		final String headerCssClass,
		final String tierCssClass,
		final int tierColspan,
		final int tierColspanMax,
		final int tierChildNodeCount,
		final int rowChildNodeCount,
		final String rowFormat) {
		
		this.headerCssClass = null != headerCssClass ? headerCssClass : DEFAULT_HEADER_CSS_CLASS;
		this.tierCssClass = null != tierCssClass ? tierCssClass : DEFAULT_TIER_CSS_CLASS;
		this.tierColspan = tierColspan;
		this.tierColspanMax = tierColspanMax;
		this.tierChildNodeCount = tierChildNodeCount;
		this.rowChildNodeCount = rowChildNodeCount;
		this.rowFormat = TableRowFormat.factory(rowFormat);
	}
	
	/**
	 * 
	 * @return An array of NodeFilters that determines how to find
	 * the relevant table.
	 */
	public abstract NodeFilter[] getFilters();
}
