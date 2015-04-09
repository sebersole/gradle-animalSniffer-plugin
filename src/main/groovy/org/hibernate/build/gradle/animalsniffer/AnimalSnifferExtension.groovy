package org.hibernate.build.gradle.animalsniffer

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet

/**
 * @author Steve Ebersole
 */
public class AnimalSnifferExtension {
	Set<SourceSet> sourceSets
	boolean excludeCompileClasspath = true
	boolean skip = false

	AnimalSnifferExtension(Project project) {
		sourceSets = [project.getConvention().findPlugin( JavaPluginConvention.class ).sourceSets.findByName( "main" )]
	}
}
