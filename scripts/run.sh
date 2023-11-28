#!/bin/bash

set -e

source ./env.sh

function gips_setup {
    echo "=> Applying GIPS hipe-network.xmi workaround."

    unzip -q -o $JAR "org/gips/examples/incrementalp2p/gips/incrementaldistribution/api/ibex-patterns.xmi"
    unzip -q -o $JAR "org/gips/examples/incrementalp2p/gips/incrementaldistribution/api/gips/gips-model.xmi"
    unzip -q -o $JAR "org/gips/examples/incrementalp2p/gips/incrementaldistribution/hipe/engine/hipe-network.xmi"

    unzip -q -o $JAR "org/gips/examples/incrementalp2p/repository/api/ibex-patterns.xmi"
    unzip -q -o $JAR "org/gips/examples/incrementalp2p/repository/hipe/engine/hipe-network.xmi"

    mkdir -p ../org.gips.examples.incrementalp2p.repository/src-gen/
    mkdir -p ../org.gips.examples.incrementalp2p.gips.incrementaldistribution/src-gen/

    rsync -a ./org ./bin

    rsync -a ./org ../org.gips.examples.incrementalp2p.repository/src-gen/
    rsync -a ./org ../org.gips.examples.incrementalp2p.gips.incrementaldistribution/src-gen/

    # ws-related gips-model file
    mkdir -p /tmp/gips/
    cp org/gips/examples/incrementalp2p/gips/incrementaldistribution/api/gips/gips-model.xmi /tmp/gips/p2p-gips-model.xmi

    rm -r ./org
}

function clean {
    rm -r ./bin
    rm -r ../org.gips.examples.incrementalp2p.repository
    rm -r ../org.gips.examples.incrementalp2p.gips.incrementaldistribution
    rm -r ./src-sim
    rm -r /tmp/gips
}

# Config
export JAR="gips-*.jar"

gips_setup
java -Xmx4g -jar $JAR "$@"
clean

echo " => Done."
