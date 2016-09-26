[![Build Status](https://travis-ci.org/garethahealy/jolokia-jvm-hawkular.svg?branch=master)](https://travis-ci.org/garethahealy/jolokia-jvm-hawkular)

# jolokia-jvm-hawkular
PoC to investigate whether its possible to extend the Jolokia JVM Agent to push data into Hawkular Metrics.
The Jolokia Agent is embedded with collectors which collect and then send to Hawkular Metrics.

## Build
### Agent
    cd agent
    mvn clean install
    export AGENT_DIR=$(pwd)
    
### cdi-simple-jetty
    cd cdi-simple-jetty
    mvn clean install
    
## Running example
The cdi-simple-jetty example is based on:

    https://github.com/fabric8io/ipaas-quickstarts/tree/cc3eb91c365936e40b759d321974329167073bc3/archetypes/cdi-camel-http-client-archetype 

Once it is built, you can run the example by:

    target/hawt-app/bin/run.sh

You can then go to:

    http://localhost:8080/camel/hello?name=bob

## Running Hawkular Metrics
To run hawkular, you can use the following repo, which contains all the need Dockerfiles

    https://github.com/garethahealy/hawkular-aio-docker

Since the version of the Java Client i am using isnt currently released, you will need to build from source:
    
    https://github.com/garethahealy/hawkular-client-java
