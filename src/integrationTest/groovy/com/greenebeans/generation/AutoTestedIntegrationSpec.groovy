package com.greenebeans.generation

import spock.lang.Shared
import spock.lang.Unroll

public class AutoTestedIntegrationSpec extends AbstractIntegrationSpec {

    @Shared def util = new AutoTestedSamplesUtil()

    @Unroll
    def "running #sample from src"() {
        given:
        buildFile << sample.text
        util.copySupportFiles(sample, projectDir)
        expect:
        build(sample.commandLine)
        where:
        sample << util.getSamples("src")
    }
}
