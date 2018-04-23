package org.hibernate.build.animalsniffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.AbstractTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import org.codehaus.mojo.animal_sniffer.SignatureBuilder;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class GenerateTask extends AbstractTask {
	public static final String STANDARD_NAME = "generateSnifferSignature";

	private final GenerateConfig config;

	public GenerateTask() {
		this.config = getProject().getExtensions()
				.getByType( SnifferExtension.class )
				.getGenerate();
	}

	@OutputFile
	private File getOutputFile() {
		return config.getOutputFile();
	}

	@InputFiles
	private FileCollection getApiFiles() {
		return config.getApiFiles();
	}

	@TaskAction
	public void generateSignature() {
		generateSignature( getApiFiles(), prepareOutputFile(), getLogger() );
	}

	private File prepareOutputFile() {
		final File outputFile = getOutputFile();

		if ( outputFile == null ) {
			throw new RuntimeException( "No output file specified" );
		}

		if ( outputFile.exists() ) {
			if ( outputFile.isDirectory() ) {
				getLogger().warn( "Specified output file was initially a directory : " + outputFile.getAbsolutePath() );
				final boolean deleted = outputFile.delete();
				if ( ! deleted ) {
					throw new RuntimeException(
							"Specified output file [" + outputFile.getAbsolutePath() + " is a directory which could not be deleted"
					);
				}
			}
		}
		else {
			if ( ! outputFile.getParentFile().exists() ) {
				final boolean made = outputFile.getParentFile().mkdirs();
				if ( !made ) {
					getLogger().warn( "Unable to make intermediate dirs for signature file : " + outputFile.getAbsolutePath() );
				}
			}

			try {
				final boolean created = outputFile.createNewFile();
				if ( ! created ) {
					getLogger().warn( "Unable to create signature file : " + outputFile.getAbsolutePath() );
				}
			}
			catch (IOException e) {
				getLogger().warn( "Unable to create signature file : " + outputFile.getAbsolutePath(), e );
			}
		}

		return outputFile;
	}

	public static void generateSignature(
			FileCollection files,
			File outputFile,
			Logger logger) {
		try ( final FileOutputStream outputStream = new FileOutputStream( outputFile ) ) {
			final SignatureBuilder signatureBuilder = new SignatureBuilder(
					outputStream,
					new AnimalSnifferToGradleLoggingBridge( logger )
			);

			for ( File file : files ) {
				signatureBuilder.process( file );
			}

			try {
				signatureBuilder.close();
			}
			catch (IOException e) {
				throw new RuntimeException( "Error writing signature to file", e );
			}
		}
		catch (IOException e) {
			throw new RuntimeException( "Error opening output stream to signature file", e );
		}
	}
}
