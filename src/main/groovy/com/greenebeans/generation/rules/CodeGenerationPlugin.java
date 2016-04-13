package com.greenebeans.generation.rules;

import com.greenebeans.generation.tasks.GenerateTask;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.language.cpp.CppSourceSet;
import org.gradle.model.ModelMap;
import org.gradle.model.Mutate;
import org.gradle.model.Path;
import org.gradle.model.RuleSource;
import org.gradle.nativeplatform.NativeBinarySpec;
import org.gradle.nativeplatform.NativeLibrarySpec;
import org.gradle.platform.base.ComponentSpecContainer;

public class CodeGenerationPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("org.gradle.cpp");
    }

    public static class Rules extends RuleSource {
        @Mutate
        public void createGeneratedComponent(@Path("components") ComponentSpecContainer componentSpecs) {
            componentSpecs.create("generated", NativeLibrarySpec.class);
        }

        @Mutate
        public void generateTasks(ModelMap<Task> tasks) {
            tasks.create("generateHeadersDebug", GenerateTask.class, new Action<GenerateTask>() {
                @Override
                public void execute(GenerateTask generateTask) {
                    generateTask.setBinaryName("debug");
                }
            });
            tasks.create("generateHeadersRelease", GenerateTask.class, new Action<GenerateTask>() {
                @Override
                public void execute(GenerateTask generateTask) {
                    generateTask.setBinaryName("release");
                }
            });
        }

        @Mutate
        public void generateSourceSets(@Path("components.generated.binaries") ModelMap<NativeBinarySpec> binaries /*, @Path("tasks") ModelMap<GenerateTask> tasks*/) {
            binaries.afterEach(new Action<NativeBinarySpec>() {
                @Override
                public void execute(NativeBinarySpec nativeBinarySpec) {
                    if (nativeBinarySpec.getBuildType().getName().equals("debug")) {
                        nativeBinarySpec.getSources().create("generated", CppSourceSet.class, new Action<CppSourceSet>() {
                            @Override
                            public void execute(CppSourceSet cppSourceSet) {
                                // cppSourceSet.generatedBy(tasks.get("generateHeadersDebug"));
                            }
                        });
                    } else {
                        nativeBinarySpec.getSources().create("generated", CppSourceSet.class, new Action<CppSourceSet>() {
                            @Override
                            public void execute(CppSourceSet cppSourceSet) {
                                // cppSourceSet.generatedBy(tasks.get("generateHeadersRelease"));
                            }
                        });
                    }
                }
            });
        }
    }
}
