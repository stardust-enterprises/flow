package enterprises.stardust

import enterprises.stardust.flow.gradle.FlowEntrypointPlugin

/**
 * This just serves to load the build-logic plugin into the project's buildscript's classpath.
 */

apply<FlowEntrypointPlugin>()
