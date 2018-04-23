package org.hibernate.build.animalsniffer;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings("unused")
public class GeneratePlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		project.getPluginManager().apply( "java" );

		final SnifferExtension snifferExtension = Helper.maybeCreateExtension( project );

		final GenerateTask generateTask = project.getTasks().create( GenerateTask.STANDARD_NAME, GenerateTask.class );

		final String compileTaskName = project.getConvention().getPlugin( JavaPluginConvention.class )
				.getSourceSets()
				.getByName( "main" )
				.getClassesTaskName();

		generateTask.dependsOn( project.getTasks().getByName( compileTaskName ) );
	}
}
