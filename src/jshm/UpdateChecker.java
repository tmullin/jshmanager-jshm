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
package jshm;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jshm.util.Util;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;


public class UpdateChecker {
	static final Logger LOG = Logger.getLogger(UpdateChecker.class.getName());
	
	public static final String UPDATE_URL =
		"http://jshm.sourceforge.net/version.xml";
	
	private static Info cache = null;
	
	public static Info getInfo() {
		return getInfo(true);
	}
	
	public static Info getInfo(boolean useCache) {
		if (useCache && null != cache) return cache;
		
		Info ret = null;
		
		try {
			NodeList nodes = null;
			NamedNodeMap attrs = null;
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(UPDATE_URL);
			doc.getDocumentElement().normalize();
			
			
			// get <latest version="..."/>
			nodes = doc.getElementsByTagName("latest");
			
			if (nodes.getLength() != 1)
				throw new IllegalArgumentException("expected to find 1 latest tag, found " + nodes.getLength());
			
			attrs = nodes.item(0).getAttributes();
			
			if (null == attrs || null == attrs.getNamedItem("version"))
				throw new IllegalArgumentException("latest tag did not have version attribute");
			
			String latestVersion = attrs.getNamedItem("version").getTextContent();
			
			
			//get <versions>...</versions>
			nodes = doc.getElementsByTagName("version");
			List<Version> versions = new ArrayList<Version>();
			
			for (int i = 0; i < nodes.getLength(); i++) {
				attrs = nodes.item(i).getAttributes();
				
				if (null == attrs)
					throw new IllegalArgumentException("version tag " + i + " didn't have any attributes");
				if (null == attrs.getNamedItem("version"))
					throw new IllegalArgumentException("version tag " + i + " didn't have version attribute");
				if (null == attrs.getNamedItem("oldVersion"))
					throw new IllegalArgumentException("version tag " + i + " didn't have oldVersion attribute");
				if (null == attrs.getNamedItem("url"))
					throw new IllegalArgumentException("version tag " + i + " didn't have url attribute");
				if (null == attrs.getNamedItem("directUrl"))
					throw new IllegalArgumentException("version tag " + i + " didn't have directUrl attribute");
				
				versions.add(new Version(
					attrs.getNamedItem("version").getTextContent(),
					attrs.getNamedItem("oldVersion").getTextContent(),
					attrs.getNamedItem("url").getTextContent(),
					attrs.getNamedItem("directUrl").getTextContent()
				));
			}
			
			ret = new Info(latestVersion, versions);
			LOG.fine(String.format("Retrieved update data, latest=%s, versions.size()=%s", latestVersion, versions.size()));
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Unable to retrieve update data", e);
		}
		
		cache = ret;
		return cache;
	}
	
	public static class Info {
		public final String latestVersion;
		public final List<Version> versions;
		
		public Info(String latestVersion, List<Version> versions) {
			this.latestVersion = latestVersion;
			this.versions = versions;
		}
		
		public boolean isUpdateAvailable() {
			int versionComp = Util.versionCompare(JSHManager.Version.VERSION, latestVersion);
			return versionComp < 0;
		}
		
		public String getUpdateUrl() {
			return versions.get(0).url;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("latest=");
			sb.append(latestVersion);
			sb.append('\n');
			
			for (Version v : versions) {
				sb.append("  ");
				sb.append(v);
				sb.append('\n');
			}
			
			return sb.toString();
		}
	}
	
	public static class Version {
		public final String
			version,
			oldVersion,
			url,
			directUrl;
		
		public Version(String ... args) {
			if (args.length != 4)
				throw new IllegalArgumentException("args must have 4 elements, got " + args.length);
			
			version = args[0];
			oldVersion = args[1];
			url = args[2];
			directUrl = args[3];
		}
		
		public String toString() {
			return String.format("%s,%s,%s,%s", version, oldVersion, url, directUrl);
		}
	}
}
