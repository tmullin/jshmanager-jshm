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
	public void execute() throws BuildException {
		Project p = getProject();
		log("Setting JSHM version to " + JSHManager.APP_VERSION_STRING, Project.MSG_INFO);
		p.setProperty("jshm.name", JSHManager.APP_NAME);
		p.setProperty("jshm.version", JSHManager.APP_VERSION_STRING);
		p.setProperty("jshm.major_version", String.valueOf(JSHManager.APP_MAJOR_VERSION));
		p.setProperty("jshm.minor_version", String.valueOf(JSHManager.APP_MINOR_VERSION));
		p.setProperty("jshm.point_version", String.valueOf(JSHManager.APP_POINT_VERSION));
		p.setProperty("jshm.revision", String.valueOf(JSHManager.APP_REVISION));
	}
}
