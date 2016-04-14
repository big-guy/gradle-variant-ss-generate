package com.greenebeans.generation

import groovy.io.FileType

class AutoTestedSamplesUtil {
    public List<AutoTestedSample> getSamples(String dir) {
        def samples = []
        def sourceDir = findDir(dir)
        sourceDir.traverse(
                type         : FileType.FILES,
                nameFilter   : ~/.*\.groovy|.*\.java/) {
            def sample = extractSampleFrom(it)
            if (sample) {
                samples << sample
            }
        }
        return samples
    }

    public void copySupportFiles(AutoTestedSample sample, File destination) {
        if (sample.supportFiles) {
            // TODO: Files.copy(findDir(sample.supportFiles).toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    private File findDir(String dir) {
        def workDir = currentDir()
        def candidates = [ dir, "../../$dir" ].collect {
            new File(workDir, it)
        }

        def found = candidates.find { it.exists() }
        if (!found) {
            throw new RuntimeException("""Couldn't find the root folder :-( Please update the logic so that it detects the root folder correctly.
I tried looking for a root folder here: $candidates""")
        }

        return found
    }

    AutoTestedSample extractSampleFrom(File file) {
        AutoTestedSample sample = null
        file.text.eachMatch(/(?ms).*?<pre autoTested.*? title="(.*)" commandLine="(.*)" files="(.*)">(.*?)<\/pre>(.*?)/) {
            sample = new AutoTestedSample(file.name, it[1], it[2].split(" "), it[3], cleanupSample(it[4]))
        }
        return sample
    }

    private String cleanupSample(String sampleText) {
        sampleText = sampleText.replaceAll(/(?m)^\s*?\*/, '')
        sampleText = sampleText.replace('&lt;', '<')
        sampleText = sampleText.replace('&gt;', '>')
        sampleText = sampleText.replace('&amp;', '&')
        sampleText = sampleText.replaceAll(/\{@literal ([^}]+)}/, '$1')
        sampleText
    }

    private File currentDir() {
        return new File(System.getProperty("user.dir"));
    }
}
