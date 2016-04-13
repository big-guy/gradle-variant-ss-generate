package com.greenebeans.generation.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class GenerateTask extends DefaultTask {
    @OutputDirectory File headerDir = new File(project.buildDir, "headers")
    @OutputDirectory File sourceDir = new File(project.buildDir, "src")
    @Input String binaryName

    @TaskAction
    void processIdlFiles() {
        new File(headerDir, "header.h").text = """
#define BINARY_NAME "$binaryName"
"""
    }
}
