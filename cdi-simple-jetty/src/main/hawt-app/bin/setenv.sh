#!/usr/bin/env bash

###
# #%L
# Fabric8 :: Quickstarts :: CDI :: Camel with Jetty as HTTP server
# %%
# Copyright (C) 2013 - 2016 Gareth Healy
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#      http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# #L%
###

AGENT_JAR=${AGENT_JAR:-jolokia-jvm-hawkular-agent-embedded-1.0.0-SNAPSHOT-agent.jar}
DEPLOYMENTS_DIR=/deployments/bin
AGENT_JAR_FILE=

if [ -f ${PWD}/${AGENT_JAR} ]; then
    echo "Using local ${PWD}/${AGENT_JAR}"
    AGENT_JAR_FILE=${PWD}/${AGENT_JAR}
else
    if [ -f ${DEPLOYMENTS_DIR}/${AGENT_JAR} ]; then
        echo "Using deployments ${DEPLOYMENTS_DIR}/${AGENT_JAR}"
        AGENT_JAR_FILE=${DEPLOYMENTS_DIR}/${AGENT_JAR}
    else
        AGENT_DIR=${AGENT_DIR:-../../../../agent-embedded/target}
        echo "Agent not local, attempting to get from target: ${AGENT_DIR}"
        cp ${AGENT_DIR}/${AGENT_JAR} .
        AGENT_JAR_FILE=${PWD}/${AGENT_JAR}
    fi
fi

## Used when NON-SSL
#JAVA_OPTIONS=-javaagent:${AGENT_JAR_FILE}=port=7777,host=127.0.0.1

# When Jolokia is already configured, and we want to use our embedded agent
JAVA_OPTIONS="${JAVA_OPTIONS/\/opt\/jolokia\/jolokia.jar/$AGENT_JAR_FILE}"
