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
package jshm.internal.tasks;

import jshm.JSHManager;

import org.apache.tools.ant.*;

/**
 * This Ant task serves to retrieve JSHManager's
 * version so that it can be used in the build file.
 * @author Tim Mullin
 *
 */
public class JshmVersion extends Task {
	@Override
	public void execute() throws BuildException {
		Project p = getProject();
		
		String version = String.format("%s.%s.%s.%s%s",
			JSHManager.Version.MAJOR,
			JSHManager.Version.MINOR,
			JSHManager.Version.POINT,
			JSHManager.Version.REVISION,
			JSHManager.Version.IS_BETA ? "beta" : "");
		
		log("Setting JSHM version to " + version, Project.MSG_INFO);
		p.setProperty("jshm.name", JSHManager.Version.NAME);
		p.setProperty("jshm.version", version);
		p.setProperty("jshm.last_version", JSHManager.Version.LAST);
		p.setProperty("jshm.major_version", String.valueOf(JSHManager.Version.MAJOR));
		p.setProperty("jshm.minor_version", String.valueOf(JSHManager.Version.MINOR));
		p.setProperty("jshm.point_version", String.valueOf(JSHManager.Version.POINT));
		p.setProperty("jshm.revision", String.valueOf(JSHManager.Version.REVISION));
	}
}
