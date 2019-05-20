#!/bin/bash

mvn install:install-file -Dfile=./local-jars/atdplib-model.jar -DgroupId=edu.upc -DartifactId=atdplib-model -Dversion=1.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
