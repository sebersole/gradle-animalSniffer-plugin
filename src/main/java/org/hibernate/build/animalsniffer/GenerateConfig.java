package org.hibernate.build.animalsniffer;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPluginConvention;

/**
 * @author Steve Ebersole
 */
public class GenerateConfig {
	private File outputFile;
	private FileCollection apiFiles;

	public GenerateConfig(Project project) {
		apiFiles = project.getConvention()
				.getPlugin( JavaPluginConvention.class )
				.getSourceSets()
				.getByName( "main" )
				.getOutput()
				.getClassesDirs();

		project.afterEvaluate(
				p -> {
					if ( this.outputFile == null ) {
						this.outputFile = new File(
								new File( project.getBuildDir(), "signatures" ),
								project.getName() + '-' + project.getVersion() + ".signature"
						);
					}
				}
		);
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public FileCollection getApiFiles() {
		return apiFiles;
	}

	public void setApiFiles(FileCollection apiFiles) {
		this.apiFiles = apiFiles;
	}
}
