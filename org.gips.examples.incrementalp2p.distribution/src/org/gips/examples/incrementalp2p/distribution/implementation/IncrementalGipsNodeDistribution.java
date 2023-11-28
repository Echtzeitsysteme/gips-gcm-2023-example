package org.gips.examples.incrementalp2p.distribution.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gips.examples.incrementalp2p.common.JsonConverter.Network;
import org.gips.examples.incrementalp2p.common.JsonConverter.Peer;
import org.gips.examples.incrementalp2p.distribution.contracts.IncrementalNodeDistributionEngine;
import org.gips.examples.incrementalp2p.repository.contracts.P2PNetworkRepository;

import jakarta.inject.Inject;

public class IncrementalGipsNodeDistribution extends GipsNodeDistribution implements IncrementalNodeDistributionEngine {
	final static Logger logger = Logger.getLogger(IncrementalGipsNodeDistribution.class);

	@Inject
	private P2PNetworkRepository repository;

	@Override
	public IncrementalNodeDistributionEngine distributeNodes(final Network net) {
		addClients(net);
		return this;
	}

	private void addClients(final Network net) {
		final int increment = 2;
		// Assumption: The first node is the lectureStudioServer
		for (int i = 0; i < net.peers().size(); i += increment) {
			final List<Peer> currentPeers = new LinkedList<Peer>();
			if (net.peers().size() > i + increment) {
				currentPeers.addAll(net.peers().subList(i + 1, i + 1 + increment));
			} else {
				// Latest index index is exclusive
				currentPeers.addAll(net.peers().subList(i + 1, net.peers().size()));
			}

			// Add the peers + connections to the repository
			repository.addWaitingClients(currentPeers, net.connections());

			// Distribute nodes
			distributeNodes();
		}
	}

}
