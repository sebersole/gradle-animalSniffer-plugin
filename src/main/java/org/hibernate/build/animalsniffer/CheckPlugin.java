package org.hibernate.build.animalsniffer;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.SourceSet;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings("WeakerAccess")
public class CheckPlugin implements Plugin<Project> {
	public static final String CONFIGURATION_NAME = "sniffer";

	@Override
	public void apply(Project project) {
		project.getPluginManager().apply( "java" );

		final SnifferExtension snifferExtension = Helper.maybeCreateExtension( project );

		project.getConfigurations().maybeCreate( CONFIGURATION_NAME );

		final CheckTask checkTask = project.getTasks().create( CheckTask.STANDARD_NAME, CheckTask.class );
		project.getTasks().add( checkTask );

		project.afterEvaluate(
				p -> {
					for ( SourceSet sourceSet : snifferExtension.getCheck().getSourceSets() ) {
						Task compileTask = p.getTasks().findByName( sourceSet.getCompileTaskName( "groovy" ) );
						if ( compileTask == null ) {
							compileTask = p.getTasks().findByName( sourceSet.getCompileJavaTaskName() );
						}

						if ( compileTask == null ) {
							throw new RuntimeException( "Could not locate compile task for sourceSet [" + sourceSet.getName() + "]" );
						}

						checkTask.dependsOn( compileTask );
					}
				}
		);
	}
}
