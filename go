#!/bin/bash
mvn exec:java -Dexec.mainClass=com.mto.rajant.GPSManager -Dexec.args="$*"
