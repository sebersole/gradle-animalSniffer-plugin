package org.hibernate.build.animalsniffer;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Steve Ebersole
 */
public class SimpleTests {
	public static final String PROJECT_NAME = "simple";

	@Test
	public void testGeneratingSignature() {
		final BuildResult buildResult = GradleRunner.create()
				.withProjectDir( TestHelper.projectDirectory( PROJECT_NAME ) )
				.withArguments(
						"clean",
						GenerateTask.STANDARD_NAME,
						"--stacktrace",
						"--refresh-dependencies",
						"--no-build-cache"
				)
				.withDebug( true )
				.withPluginClasspath()
				.build();

		System.out.println( buildResult.getOutput() );
	}

	@Test
	public void testCheckingSignature() {
		final BuildResult buildResult = GradleRunner.create()
				.withProjectDir( TestHelper.projectDirectory( PROJECT_NAME ) )
				.withArguments(
						"clean",
						CheckTask.STANDARD_NAME,
						"--stacktrace",
						"--refresh-dependencies",
						"--no-build-cache"
				)
				.withDebug( true )
				.withPluginClasspath()
				.build();

		System.out.println( buildResult.getOutput() );
	}

	@Test
	public void testGeneratingSignatureUpToDate() {
		GradleRunner.create()
				.withProjectDir( TestHelper.projectDirectory( PROJECT_NAME ) )
				.withArguments( "clean", "--refresh-dependencies" )
				.withDebug( true )
				.withPluginClasspath()
				.build();

		final BuildResult initialResult = GradleRunner.create()
				.withProjectDir( TestHelper.projectDirectory( PROJECT_NAME ) )
				.withArguments(
						GenerateTask.STANDARD_NAME,
						"--stacktrace"
				)
				.withDebug( true )
				.withPluginClasspath()
				.build();

		System.out.println( "Initial result..." );
		System.out.println( initialResult.getOutput() );

		assertThat(
				initialResult.task( ':' + GenerateTask.STANDARD_NAME ).getOutcome(),
				equalTo( TaskOutcome.SUCCESS )
		);

		final BuildResult secondResult = GradleRunner.create()
				.withProjectDir( TestHelper.projectDirectory( PROJECT_NAME ) )
				.withArguments(
						GenerateTask.STANDARD_NAME,
						"--stacktrace"
				)
				.withDebug( true )
				.withPluginClasspath()
				.build();

		System.out.println( "Second result..." );
		System.out.println( secondResult.getOutput() );

		assertThat(
				secondResult.task( ':' + GenerateTask.STANDARD_NAME ).getOutcome(),
				equalTo( TaskOutcome.UP_TO_DATE )
		);
	}

// todo : get up-to-date checking for check-task figured out
//
//	@Test
//	public void testCheckingSignatureUpToDate() {
//		GradleRunner.create()
//				.withProjectDir( TestHelper.projectDirectory( PROJECT_NAME ) )
//				.withArguments( "clean", "--refresh-dependencies" )
//				.withDebug( true )
//				.withPluginClasspath()
//				.build();
//
//		final BuildResult initialResult = GradleRunner.create()
//				.withProjectDir( TestHelper.projectDirectory( PROJECT_NAME ) )
//				.withArguments(
//						CheckTask.STANDARD_NAME,
//						"--stacktrace"
//				)
//				.withDebug( true )
//				.withPluginClasspath()
//				.build();
//
//		System.out.println( "Initial result..." );
//		System.out.println( initialResult.getOutput() );
//
//		assertThat(
//				initialResult.task( ':' + CheckTask.STANDARD_NAME ).getOutcome(),
//				equalTo( TaskOutcome.SUCCESS )
//		);
//
//		final BuildResult secondResult = GradleRunner.create()
//				.withProjectDir( TestHelper.projectDirectory( PROJECT_NAME ) )
//				.withArguments(
//						CheckTask.STANDARD_NAME,
//						"--debug",
//						"--stacktrace"
//				)
//				.withDebug( true )
//				.withPluginClasspath()
//				.build();
//
//		System.out.println( "Second result..." );
//		System.out.println( secondResult.getOutput() );
//
//		assertThat(
//				secondResult.task( ':' + CheckTask.STANDARD_NAME ).getOutcome(),
//				equalTo( TaskOutcome.UP_TO_DATE )
//		);
//	}
}
