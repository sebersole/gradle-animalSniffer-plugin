package org.hibernate.build.animalsniffer;

import java.util.HashSet;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

/**
 * @author Steve Ebersole
 */
public final class CheckConfig {
	private final Project project;
	private Set<SourceSet> sourceSets = new HashSet<>();
	private boolean excludeCompileClasspath = true;

	public CheckConfig(Project project) {
		this.project = project;
		sourceSet( "main" );
	}

	public Set<SourceSet> getSourceSets() {
		return sourceSets;
	}

	public void setSourceSets(Set<SourceSet> sourceSets) {
		this.sourceSets = sourceSets;
	}

	public void sourceSet(SourceSet sourceSet) {
		this.sourceSets.add( sourceSet );
	}

	public void sourceSet(String sourceSetName) {
		sourceSet(
				project.getConvention()
						.getPlugin( JavaPluginConvention.class )
						.getSourceSets()
						.getByName( sourceSetName )
		);
	}

	public boolean isExcludeCompileClasspath() {
		return excludeCompileClasspath;
	}

	public void setExcludeCompileClasspath(boolean excludeCompileClasspath) {
		this.excludeCompileClasspath = excludeCompileClasspath;
	}
}
