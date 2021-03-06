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
package jshm.sh.scraper;

import jshm.scraper.DataTable;

import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;

public class RbDataTable extends DataTable {
	public static final RbDataTable
	TOP_SCORES = new RbDataTable(3, 3, 8,
		"-|text~link=songid,songid,songid|text=int|img=rating|text=int|text=int|text|span"
	),
	MANAGE_SCORES = new RbDataTable(3, 3, 10,
		"-|text=int|link=songid~text|-|text=int~link=picvid,picvid|img=rating|text=int|text=int|text|span"
	),
	
	DELETE_SCORES = new RbDataTable(-1, -1, 6,
		"input|text=int|img=rating~text=float|text=int|text=int|text"
	) {
		private transient NodeFilter[] filters = null;
		
		@Override
		public NodeFilter[] getFilters() {
			if (null == filters) {
				TagNameFilter filter0 = new TagNameFilter("TABLE");
				HasAttributeFilter filter1 = new HasAttributeFilter("border", "0");
				HasAttributeFilter filter2 = new HasAttributeFilter("cellpadding", "5");
				NodeFilter[] array0 = new NodeFilter[] {
					filter0, filter1, filter2
				};
	
				AndFilter filter3 = new AndFilter();
				filter3.setPredicates(array0);
	
				HasParentFilter filter4 = new HasParentFilter();
				filter4.setRecursive(false);
				filter4.setParentFilter(filter3);
	
				filters = new NodeFilter[] { filter4 };
			}
			
			return filters;
		}
	} //,
	
//	GH_FORUM_INDEX = new DataTable(2, 2, 5,
//		"-|text=,~link=forumid,-,-,subforums|-|-|-")
	;
	
	private static final NodeFilter[] DEFAULT_FILTERS;
	
	/**
	 * This sets up the filters for the Guitar Hero page
	 * formatting.
	 */
	static {        
        AndFilter parentPredicate = new AndFilter (new NodeFilter[] {
        	new TagNameFilter("TABLE"),
        	new HasAttributeFilter("border", "0"),
        	new HasAttributeFilter("cellspacing", "0"),
        	new NotFilter(new OrFilter(new NodeFilter[] {
        		new HasAttributeFilter("width"),
        		new HasAttributeFilter("cellpadding")
        	}))
        });

        AndFilter mainFilter = new AndFilter (new NodeFilter[] {
    		new HasParentFilter(parentPredicate, false),
    		new TagNameFilter("TR")/*,
    		new HasAttributeFilter("height", "30")
    		*/
        });
        
        DEFAULT_FILTERS = new NodeFilter[] {
        	mainFilter
        };
	}
	
	public RbDataTable(
		final int tierColspan,
		final int tierChildNodeCount,
		final int rowChildNodeCount,
		final String rowFormat) {
		
		super(tierColspan, tierChildNodeCount, rowChildNodeCount, rowFormat);
	}
	
	public NodeFilter[] getFilters() {
		return DEFAULT_FILTERS;
	}
}
