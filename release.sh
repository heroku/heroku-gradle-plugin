#!/usr/bin/env bash

set -o pipefail
set -eu

if cat build.gradle | grep -q '^version = .*-SNAPSHOT'; then
  echo "ERROR ! Must set version to non-snapshot first."
  exit 1
fi

get_property() {
  local propFile=$1
  local propName=$2
  local propDefault=${3:-""}

  if [ -f ${propFile} ]; then
    local propValue=$(sed '/^\#/d' ${propFile} | grep "${propName}"  | tail -n 1 | cut -d "=" -f2- | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')
    echo "${propValue:-$propDefault}"
  else
    echo "${propDefault}"
  fi
}

export BINTRAY_USER="$(get_property ~/.bintray/.credentials user)"
export BINTRAY_KEY="$(get_property ~/.bintray/.credentials password)"

./gradlew clean build bintrayUpload publishPlugins -i -x groovydoc
