package com.heroku.sdk.gradle

import java.util.regex.Pattern

import org.gradle.api.Project

import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.google.common.base.Preconditions
import com.google.common.base.Strings

class HerokuPluginExtension {

    String appName

    String jdkVersion

    String jdkUrl

    String stack

    String slugFilename = "slug.tgz"

    Boolean includeBuildDir = true

    Map<String,String> processTypes = [:]

    Map<String,String> configVars = [:]

    List<String> includes = []

    List<String> buildpacks = []

    public List<File> getIncludedFiles(File gradleDir) {
      List<File> files = new ArrayList<File>(includes.size());

      for (String s : includes) {
        if (s.contains("*")) {
          String[] dirs = s.split(Pattern.quote(File.separator));
          String pattern = dirs[dirs.length-1];
          File basedir = new File(gradleDir, s.replace(pattern, ""));
          Collection<File> listFiles = FileUtils.listFiles(basedir, new WildcardFileFilter(pattern), null);
          files.addAll(listFiles);
        } else {
          files.add(new File(s));
        }
      }

      return files;
    }

    public void resolvePathsAndValidate() {
        //Preconditions.checkArgument(!Strings.isNullOrEmpty(appName), "heroku.appName is required.")
    }
}
