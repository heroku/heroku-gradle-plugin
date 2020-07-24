package com.heroku.sdk;

import java.util.*;

public class HerokuExtension {
    private boolean includeBuildDir = true;
    private List<String> includes = new ArrayList<>();
    private String appName;
    private String jdkVersion;
    private Map<String, String> processTypes = new HashMap<>();
    private Map<String, String> configVars = new HashMap<>();
    private List<String> buildpacks = Collections.singletonList("heroku/jvm");

    public boolean isIncludeBuildDir() {
        return includeBuildDir;
    }

    public void setIncludeBuildDir(boolean includeBuildDir) {
        this.includeBuildDir = includeBuildDir;
    }

    public List<String> getIncludes() {
        return Collections.unmodifiableList(includes);
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getJdkVersion() {
        return jdkVersion;
    }

    public void setJdkVersion(String jdkVersion) {
        this.jdkVersion = jdkVersion;
    }

    public Map<String, String> getProcessTypes() {
        return Collections.unmodifiableMap(processTypes);
    }

    public void setProcessTypes(Map<String, String> processTypes) {
        this.processTypes = processTypes;
    }

    public Map<String, String> getConfigVars() {
        return Collections.unmodifiableMap(configVars);
    }

    public void setConfigVars(Map<String, String> configVars) {
        this.configVars = configVars;
    }

    public List<String> getBuildpacks() {
        return Collections.unmodifiableList(buildpacks);
    }

    public void setBuildpacks(List<String> buildpacks) {
        this.buildpacks = buildpacks;
    }
}
