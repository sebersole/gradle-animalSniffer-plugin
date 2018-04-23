package org.hibernate.build.animalsniffer;

import org.gradle.api.Project;

/**
 * @author Steve Ebersole
 */
public class Helper {
	public static final String EXTENSION_NAME = "sniffer";

	public static SnifferExtension maybeCreateExtension(Project project) {
		final SnifferExtension snifferExtension = project.getExtensions().findByType( SnifferExtension.class );
		if ( snifferExtension != null ) {
			return snifferExtension;
		}

		try {
			return project.getExtensions().create(
					EXTENSION_NAME,
					SnifferExtension.class,
					project
			);
		}
		catch (Exception e) {
			throw new RuntimeException( "Unable to create sniffer extension", e );
		}
	}
}
