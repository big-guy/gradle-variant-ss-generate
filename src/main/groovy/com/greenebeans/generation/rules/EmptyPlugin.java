package com.greenebeans.generation.rules;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * <pre autoTested title="Example title" commandLine="help" files="samples/example-empty">
 *    println "sample!"
 *    assert file("foobar").exists()
 * </pre>
 */
public class EmptyPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {

    }
}
