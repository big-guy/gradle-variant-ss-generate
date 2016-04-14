package com.greenebeans.generation;

import java.util.Arrays;

class AutoTestedSample {
    private final String title;
    private final String srcFile;
    private final String text;
    private final String supportFiles;
    private final String[] commandLine;

    AutoTestedSample(String srcFile, String title, String[] commandLine, String supportFiles, String text) {
        this.srcFile = srcFile;
        this.title = title;
        this.commandLine = commandLine;
        this.supportFiles = supportFiles;
        this.text = text;
    }

    public String toString() {
        return String.format("%s: %s (cli: %s, supportFiles: %s)", srcFile, title, Arrays.asList(commandLine), supportFiles.length()!=0);
    }

    public String getText() {
        return text;
    }

    public String[] getCommandLine() {
        return commandLine;
    }

    public String getSupportFiles() {
        return supportFiles;
    }
}