package com.greenebeans.generation

import com.greenebeans.generation.tasks.GenerateTask
import org.gradle.testkit.runner.TaskOutcome

class VariantSourceSetGenerationIntegrationSpec extends AbstractIntegrationSpec {
    def setup() {
        file("src/main/cpp").mkdirs()
        file("src/main/cpp/main.cpp") << """
    #include "header.h"
    #include <stdio.h>

    int main(int argc, char**argv) {
        printf(BINARY_NAME "\\n");
    }
"""
        buildFile << """
plugins {
    id "org.gradle.cpp"
    id "com.greenebeans.empty"
}
model {
    components {
        main(NativeExecutableSpec) {
            binaries.all {
                lib library: 'generated', linkage: 'api'
            }
        }
    }
}
"""
    }

    def "can generate with single task"() {
        buildFile << """
task generateHeaders(type: ${GenerateTask.canonicalName}) {
    binaryName = "none"
}
model {
    components {
        generated(NativeLibrarySpec) {
            sources {
                generated(CppSourceSet) {
                    generatedBy \$.tasks.generateHeaders
                }
            }
        }
    }
}
"""
        when:
        build("installMainExecutable")
        then:
        result.task(":generateHeaders").outcome == TaskOutcome.SUCCESS
        def main = file("build/install/main/main")
        main.exists()
        main.absolutePath.execute().text == "none\n"
    }

    def "can generate with task per source set (manual)"() {
        buildFile << """
task generateHeadersDebug(type: ${GenerateTask.canonicalName}) {
    binaryName = "debug"
}

task generateHeadersRelease(type: ${GenerateTask.canonicalName}) {
    binaryName = "release"
}

model {
    buildTypes {
        debug
        release
    }
    components {
        generated(NativeLibrarySpec) {
            binaries.all {
                sources {
                    if (buildType == buildTypes.debug) {
                        generated(CppSourceSet) {
                            generatedBy \$.tasks.generateHeadersDebug
                        }
                    } else {
                        generated(CppSourceSet) {
                            generatedBy \$.tasks.generateHeadersRelease
                        }
                    }
                }
            }
        }
    }
}
"""
        when:
        build("installMainDebugExecutable")
        then:
        result.task(":generateHeadersDebug").outcome == TaskOutcome.SUCCESS
        def mainDebug = file("build/install/main/debug/main")
        mainDebug.exists()
        mainDebug.absolutePath.execute().text == "debug\n"

        when:
        build("installMainReleaseExecutable")
        then:
        result.task(":generateHeadersRelease").outcome == TaskOutcome.SUCCESS
        def mainRelease = file("build/install/main/release/main")
        mainRelease.exists()
        mainRelease.absolutePath.execute().text == "release\n"
    }

    def "can generate with task per source set (plugin)"() {
        def orig = buildFile.text
        buildFile.text = """
plugins {
    id "com.greenebeans.generation"
}

${orig}
"""
        buildFile << """
model {
    buildTypes {
        debug
        release
    }
}
"""
        when:
        build("installMainDebugExecutable")
        then:
        result.task(":generateHeadersDebug").outcome == TaskOutcome.SUCCESS
        def mainDebug = file("build/install/main/debug/main")
        mainDebug.exists()
        mainDebug.absolutePath.execute().text == "debug\n"

        when:
        build("installMainReleaseExecutable")
        then:
        result.task(":generateHeadersRelease").outcome == TaskOutcome.SUCCESS
        def mainRelease = file("build/install/main/release/main")
        mainRelease.exists()
        mainRelease.absolutePath.execute().text == "release\n"
    }
}
