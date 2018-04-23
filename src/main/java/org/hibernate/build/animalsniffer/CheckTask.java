package org.hibernate.build.animalsniffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.gradle.api.GradleException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.AbstractTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

import org.codehaus.mojo.animal_sniffer.ClassListBuilder;
import org.codehaus.mojo.animal_sniffer.SignatureChecker;

import static org.hibernate.build.animalsniffer.CheckPlugin.CONFIGURATION_NAME;

/**
 * @author Steve Ebersole
 */
public class CheckTask extends AbstractTask {
	public static final String STANDARD_NAME = "checkSnifferSignatures";

	private final CheckConfig config;
	private final Configuration signatures;

	public CheckTask() {
		this.config = getProject().getExtensions().getByType( SnifferExtension.class ).getCheck();
		this.signatures = getProject().getConfigurations().getByName( CONFIGURATION_NAME );
	}

	@InputFiles
	private FileCollection getInputFiles() {
		// todo : inclusions/exclusions
		FileCollection files = signatures;
		for ( SourceSet sourceSet : config.getSourceSets() ) {
			files = files.plus( sourceSet.getOutput() );
		}
		return files;
	}

	@TaskAction
	@SuppressWarnings("unused")
	public void checkSignatures() throws IOException {
		final AnimalSnifferToGradleLoggingBridge loggingBridge = new AnimalSnifferToGradleLoggingBridge( getLogger() );

		for ( SourceSet sourceSet : config.getSourceSets() ) {

			for ( ResolvedArtifact resolvedArtifact : signatures.getResolvedConfiguration().getResolvedArtifacts() ) {
				final File signatureFile = resolvedArtifact.getFile();
				getLogger().lifecycle(
						"Starting AnimalSniffer checks [" + signatureFile.getName() +
								"] against SourceSet [" + sourceSet.getName() + "]"
				);

				final SignatureChecker signatureChecker = new SignatureChecker(
						new FileInputStream( signatureFile ),
						buildIgnores( sourceSet, loggingBridge ),
						loggingBridge
				);

				signatureChecker.setCheckJars( false );

				signatureChecker.setSourcePath( new ArrayList<>( sourceSet.getJava().getSrcDirs() ) );

				signatureChecker.process( sourceSet.getJava().getOutputDir() );

				if ( signatureChecker.isSignatureBroken() ) {
					throw new GradleException(
							"Signature errors found for SourceSet " + sourceSet.getName() +
									" against " + signatureFile.getAbsolutePath() + ". " +
									"Verify errors and ignore them with the proper annotation if needed."
					);
				}
			}

		}
	}


	private Set<String> buildIgnores(
			SourceSet sourceSet,
			AnimalSnifferToGradleLoggingBridge gradleLoggingBridge) {
		try {
			final ClassListBuilder clb = new ClassListBuilder( gradleLoggingBridge );

			if ( config.isExcludeCompileClasspath() ) {
				for ( File file : sourceSet.getCompileClasspath() ) {
					clb.process( file );
				}
			}

			// it's ignored types actually, not packages
			return clb.getPackages();
		}
		catch (IOException e) {
			throw new RuntimeException(
					"Problem building Animal Sniffer ignores for source set : " +
							getProject().getPath() + ".sourceSets." +
							sourceSet.getName()


			);
		}
	}
}
