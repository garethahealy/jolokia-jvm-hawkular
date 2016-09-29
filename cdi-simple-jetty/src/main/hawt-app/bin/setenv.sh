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

if [ -f ${AGENT_JAR} ]; then
  echo "Using local ${AGENT_JAR}"
else
  AGENT_DIR=${AGENT_DIR:-../../../../agent-embedded/target}
  echo "Agent not local, attempting to get from: ${AGENT_DIR}"
  cp ${AGENT_DIR}/${AGENT_JAR} .
fi

export JAVA_OPTIONS=-javaagent:${PWD}/${AGENT_JAR}=port=7777,host=127.0.0.1
