#!/usr/bin/env bash

set -o pipefail
set -eu

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

./gradlew clean build bintrayUpload -i -x groovydoc
