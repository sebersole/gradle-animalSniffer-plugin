package org.hibernate.build.animalsniffer;

import org.codehaus.mojo.animal_sniffer.logging.Logger;

/**
 * @author Steve Ebersole
 */
public class AnimalSnifferToGradleLoggingBridge implements Logger {
	private final org.slf4j.Logger gradleLogger;

	public AnimalSnifferToGradleLoggingBridge(org.slf4j.Logger gradleLogger) {
		this.gradleLogger = gradleLogger;
	}

	@Override
	public void info(String message) {
		gradleLogger.info( message );
	}

	@Override
	public void info(String message, Throwable t) {
		gradleLogger.info( message, t );
	}

	@Override
	public void debug(String message) {
		gradleLogger.debug( message );
	}

	@Override
	public void debug(String message, Throwable t) {
		gradleLogger.debug( message, t );
	}

	@Override
	public void warn(String message) {
		gradleLogger.warn( message );
	}

	@Override
	public void warn(String message, Throwable t) {
		gradleLogger.warn( message, t );
	}

	@Override
	public void error(String message) {
		gradleLogger.error( message );
	}

	@Override
	public void error(String message, Throwable t) {
		gradleLogger.error( message, t );
	}
}
