package org.gips.examples.incrementalp2p.distribution.contracts;

import org.gips.examples.incrementalp2p.common.JsonConverter.Network;

public interface IncrementalNodeDistributionEngine extends NodeDistributionEngine {
	IncrementalNodeDistributionEngine distributeNodes(Network net, int stepsize);
}
