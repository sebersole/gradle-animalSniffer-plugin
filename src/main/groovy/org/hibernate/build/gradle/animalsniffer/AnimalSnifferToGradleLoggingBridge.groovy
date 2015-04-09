package org.hibernate.build.gradle.animalsniffer

import groovy.transform.Canonical
import org.codehaus.mojo.animal_sniffer.logging.Logger

/**
 * @author Steve Ebersole
 */
@Canonical
public class AnimalSnifferToGradleLoggingBridge implements Logger {
	@Delegate
	org.slf4j.Logger logger
}
