# gradle-animalSniffer-plugin

AnimalSniffer plugin for Gradle

Usage
-----

First you must make the plugin available to your build...

    buildscript {
    	repositories {
    		mavenCentral()
		}
		dependencies {
			classpath 'org.hibernate.build.gradle:gradle-animalSniffer-plugin:<version>'
		}
	}

	apply plugin: 'org.hibernate.build.gradle.animalsniffer'

*where `<version>` indicates the version you would like to use*


The plugin adds a configuration name `animalSnifferSignature` where you can add the AnimalSniffer signatures
you would like it to verify against.  For example, to verify against the Java 6 signature:

    dependencies {
        animalSnifferSignature 'org.codehaus.mojo.signature:java16:1.0@signature'
    }

By default the plugin will verify the main source set for the project.  You can ask it to verify additional source sets as well:

    animalSniffer {
        sourceSets += project.sourceSets.test
    }

By default the plugin will also ask AnimalSniffer to exclude all classes from all dependencies in the source set's
compileClasspath (`org.gradle.api.tasks.SourceSet.getCompileClasspath`).  You can ask it to not exclude them:

    animalSniffer {
        excludeCompileClasspath = false
    }

*Be aware however that this effectively means you will need signatures for all your compile time dependencies.*
