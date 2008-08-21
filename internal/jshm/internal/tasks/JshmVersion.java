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
