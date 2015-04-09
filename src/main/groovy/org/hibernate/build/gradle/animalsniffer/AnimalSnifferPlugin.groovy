package org.hibernate.build.gradle.animalsniffer

import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.SourceSet

import org.codehaus.mojo.animal_sniffer.ClassListBuilder
import org.codehaus.mojo.animal_sniffer.SignatureChecker
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class AnimalSnifferPlugin implements Plugin<Project> {
	private Logger logger = LoggerFactory.getLogger( this.class )

	public static final String SIGNATURE_CONFIG_NAME = "animalSnifferSignature";
	public static final String EXTENSION_NAME = "animalSniffer"

	@Override
	void apply(Project project) {
		final Configuration configuration = project.configurations.maybeCreate( SIGNATURE_CONFIG_NAME )
		final AnimalSnifferExtension extension = project.extensions.create( EXTENSION_NAME, AnimalSnifferExtension, project )

		project.afterEvaluate( new AfterProjectEvaluationAction( extension, configuration ) );
	}

	private static Set<String> buildIgnores(
			Project project,
			SourceSet sourceSet,
			AnimalSnifferExtension extension,
			AnimalSnifferToGradleLoggingBridge gradleLoggingBridge) {
		def ClassListBuilder clb = new ClassListBuilder( gradleLoggingBridge )

		// Any references to classes from the current project are fine
		clb.process( project.file( sourceSet.output.classesDir ) );

		if ( extension.excludeCompileClasspath ) {
			sourceSet.compileClasspath.each{ File classpathItem->
				clb.process( classpathItem );
			}
		}

		// it's ignored types actually, not packages
		return clb.getPackages()
	}

	class AfterProjectEvaluationAction implements Action<Project> {
		final AnimalSnifferExtension extension;
		final Configuration signatureConfiguration;

		AfterProjectEvaluationAction(
				AnimalSnifferExtension extension,
				Configuration signatureConfiguration) {
			this.extension = extension
			this.signatureConfiguration = signatureConfiguration
		}

		@Override
		void execute(Project project) {
			extension.sourceSets.each{ SourceSet sourceSet->
				Task compileTask = project.tasks.findByName( sourceSet.getCompileTaskName( "groovy" ) )
				if ( compileTask == null ) {
					compileTask = project.tasks.findByName( sourceSet.compileJavaTaskName )
				}

				compileTask.doLast(
						new AnimalSnifferExecutionAction( sourceSet, extension, signatureConfiguration )
				);
			}
		}
	}

	class AnimalSnifferExecutionAction implements Action<Task> {
		final SourceSet sourceSet;
		final AnimalSnifferExtension extension;
		final Configuration signatureConfiguration;

		AnimalSnifferExecutionAction(
				SourceSet sourceSet,
				AnimalSnifferExtension extension,
				Configuration signatureConfiguration) {
			this.sourceSet = sourceSet
			this.extension = extension
			this.signatureConfiguration = signatureConfiguration
		}

		@Override
		void execute(Task task) {
			if ( extension.skip ) {
				return;
			}

			def gradleLoggingBridge = new AnimalSnifferToGradleLoggingBridge( logger )
			def ignores = buildIgnores(
					task.project,
					sourceSet,
					extension,
					gradleLoggingBridge
			)

			def signatures = signatureConfiguration.resolvedConfiguration.resolvedArtifacts*.file

			signatures.each{ File file->
				task.logger.lifecycle( "Starting AnimalSniffer checks using [${file.name}] against [sourceSets.${sourceSet.name}]" )

				SignatureChecker signatureChecker = new SignatureChecker(
						new FileInputStream( file ),
						ignores,
						gradleLoggingBridge
				)
				signatureChecker.setCheckJars( false );


				List<File> sourceDirs = new ArrayList<File>();
				sourceDirs.addAll( sourceSet.java.srcDirs )
				signatureChecker.setSourcePath( sourceDirs )

				signatureChecker.process( task.project.file( sourceSet.output.classesDir ) );

				if ( signatureChecker.isSignatureBroken() ) {
					throw new GradleException(
							"Signature errors found for SourceSet ${sourceSet.name} against ${file.name}. " +
									"Verify errors and ignore them with the proper annotation if needed."
					);
				}
			}
		}
	}
}


