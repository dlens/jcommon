package com.dlens.common2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Common2Version {
	private static String resourceName = "/git.properties";
	private static Properties resourceProperties = getResourceFile();
	
	public static String getResourceName() {
		return "git.properties";
	}
	
	private static Properties getResourceFile() {
		InputStream resourceAsStream = Common2Version.class.getResourceAsStream(resourceName);
		Properties rval = new Properties();
		try {
			rval.load(resourceAsStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rval;
	}
	
	public static int getVersionMajor() {
		String version = getVersion();
		String pieces[] = version.split("\\.");
		String majV = pieces[0];
		try {
			return Integer.parseInt(majV);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return -1;
		}
	}

	private static String getVersion() {
		return resourceProperties.getProperty("git.build.version", null);
	}

	public static String getVersionPostfix() {
		String version = getVersion();
		String pieces[] = version.split("-");
		if (pieces.length > 1) {
			return "-"+pieces[1];
		} else {
			return "";
		}
	}

	public static String getVCSStatus() {
		String isDirty = resourceProperties.getProperty("git.dirty", "false");
		boolean isDirtyp = Boolean.parseBoolean(isDirty);
		if (isDirtyp) {
			return "Uncommited changes";
		} else {
			return "Clean";
		}
	}

}
