package org.hibernate.build.animalsniffer;

import org.gradle.api.Project;

/**
 * The Gradle extension object for the Animal Sniffer plugin
 *
 * @author Steve Ebersole
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SnifferExtension {
	private final Project project;

	private final CheckConfig checkConfig;
	private final GenerateConfig generateConfig;

	public SnifferExtension(Project project) {
		this.project = project;

		this.checkConfig = new CheckConfig( project );
		this.generateConfig = new GenerateConfig( project );
	}

	public CheckConfig getCheck() {
		return checkConfig;
	}

	public GenerateConfig getGenerate() {
		return generateConfig;
	}

}
