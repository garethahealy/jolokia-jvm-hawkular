[![Build Status](https://travis-ci.org/garethahealy/jolokia-jvm-hawkular.svg?branch=master)](https://travis-ci.org/garethahealy/jolokia-jvm-hawkular)

# jolokia-jvm-hawkular
PoC to investigate whether its possible to extend the Jolokia JVM Agent to push data into Hawkular Metrics.
The Jolokia Agent is embedded with collectors which collect and then send to Hawkular Metrics.

## Build
### hawkular-client-java
Since the version of the Hawkular Java Client i am using isn't currently released, you will need to build from source:
    
    git clone -b metrics-19 https://github.com/garethahealy/hawkular-client-java.git
    cd hawkular-client-java
    mvn clean install -DskipTests

You can then build the rest of the project:

    mvn clean install
    
## Running Outside of OpenShift
### Running example
The cdi-simple-jetty example is based on:

    https://github.com/fabric8io/ipaas-quickstarts/tree/cc3eb91c365936e40b759d321974329167073bc3/archetypes/cdi-camel-http-client-archetype 

Once it is built, you can run the example by:

    cd cdi-simple-jetty/target/hawt-app/bin
    ./run.sh

You can then go to:

    http://localhost:8080/camel/hello?name=bob

### Running Hawkular Metrics
To run hawkular metrics, you can use the following repo, which contains all the needed Dockerfiles:

    git clone https://github.com/garethahealy/hawkular-aio-docker.git
    docker-compose up

## Running Inside of OpenShift
### Running example
I am using the CDK for development. If you haven't used that before, check out the below:

    https://blog.openshift.com/how-to-install-red-hat-container-development-kit-cdk-in-minutes/

Once the CDK if up and running, you'll need to deploy a nexus as we need to deploy the hawkular-client
and agent as they are currently snapshots.

Deploy Nexus

    https://blog.openshift.com/improving-build-time-java-builds-openshift/

Build/Deploy Agent/Simple Jetty

    mvn clean install
    mvn deploy -DaltDeploymentRepository=local-nexus-snapshots::default::http://nexus.rhel-cdk.10.1.2.2.xip.io/content/repositories/snapshots/

Build/Deploy Hawkular Client

    git clone https://github.com/garethahealy/hawkular-client-java.git
    cd hawkular-client-java
    mvn clean install -DskipTests
    mvn deploy -DaltDeploymentRepository=local-nexus-snapshots::default::http://nexus.rhel-cdk.10.1.2.2.xip.io/content/repositories/snapshots/ -DskipTests

Build/Deploy cdi-simple-jetty to OSE

    oc project sample-project
    oc new-app registry.access.redhat.com/jboss-fuse-6/fis-java-openshift~https://github.com/garethahealy/jolokia-jvm-hawkular.git --context-dir=cdi-simple-jetty





