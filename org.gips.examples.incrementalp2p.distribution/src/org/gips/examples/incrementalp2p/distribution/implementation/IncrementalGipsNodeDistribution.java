package org.gips.examples.incrementalp2p.distribution.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gips.examples.incrementalp2p.common.CommonConstants;
import org.gips.examples.incrementalp2p.common.JsonConverter.Connection;
import org.gips.examples.incrementalp2p.common.JsonConverter.Network;
import org.gips.examples.incrementalp2p.common.JsonConverter.Peer;
import org.gips.examples.incrementalp2p.distribution.contracts.IncrementalNodeDistributionEngine;
import org.gips.examples.incrementalp2p.repository.contracts.P2PNetworkRepository;

import jakarta.inject.Inject;

public class IncrementalGipsNodeDistribution extends GipsNodeDistribution implements IncrementalNodeDistributionEngine {
	final static Logger logger = Logger.getLogger(IncrementalGipsNodeDistribution.class);

//	@Inject
//	private IncrementStrategy incrementStrategy;
	@Inject
	private P2PNetworkRepository repository;

	@Override
	public IncrementalNodeDistributionEngine distributeNodes(final Network net) {
//		var count = 0;
//
//		var clientsLeft = clients.stream().collect(Collectors.toList());
//
//		while (!clientsLeft.isEmpty()) {
//			logger.debug("Run: " + ++count);
//			addClients(clients, clientsLeft);
//		}
		addClients(net);
		return this;
	}

	private void addClients(final Network net) {
//		var addedClients = incrementStrategy.getClients(totalClients, clientsLeft);
//		clientsLeft.removeAll(addedClients);
//
//		logger.debug("All clients:     " + toIdList(totalClients));
//		logger.debug("Missing clients: " + toIdList(clientsLeft));
//		logger.debug("Added clients:   " + toIdList(addedClients));

//		final int increment = (int) Math.sqrt(net.peers().size());
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

			// Get all related connections
//			final List<Connection> currentConnections = findRelatedConnections(net.connections(), currentPeers);
//			final List<Connection> currentConnections = net.connections();

			// Add the peers + connections to the repository
			repository.addWaitingClients(currentPeers, net.connections());

			// Distribute nodes
			distributeNodes();
		}

//		repository.addWaitingClients(net);
//		distributeNodes();
//		if (logger.isDebugEnabled()) {
//			save("src-sim", "run" + toIdList(addedClients));
//		}
	}

	private List<Connection> findRelatedConnections(final List<Connection> connections, final List<Peer> currentPeers) {
		final List<Connection> relatedConnections = new LinkedList<Connection>();

		// Get all connections to and from the lectureStudioServer
		for (final Peer p : currentPeers) {
			if (p.name().equals(CommonConstants.RootName)) {
				continue;
			}
			final Connection o1 = getConnectionFromSrcToTrg(p.name(), CommonConstants.RootName, connections);
			final Connection o2 = getConnectionFromSrcToTrg(CommonConstants.RootName, p.name(), connections);
			if (!relatedConnections.contains(o1)) {
				relatedConnections.add(o1);
			}
			if (!relatedConnections.contains(o2)) {
				relatedConnections.add(o2);
			}
//			relatedConnections.add(getConnectionFromSrcToTrg(p.name(), CommonConstants.RootName, connections));
//			relatedConnections.add(getConnectionFromSrcToTrg(CommonConstants.RootName, p.name(), connections));
		}

		// Get all connections to and from the peers to each other
		for (final Peer p : currentPeers) {
			for (final Peer pi : currentPeers) {
				if (!p.equals(pi)) {
					final Connection o1 = getConnectionFromSrcToTrg(p.name(), pi.name(), connections);
					final Connection o2 = getConnectionFromSrcToTrg(pi.name(), p.name(), connections);
					if (!relatedConnections.contains(o1)) {
						relatedConnections.add(o1);
					}
					if (!relatedConnections.contains(o2)) {
						relatedConnections.add(o2);
					}
//					relatedConnections.add(getConnectionFromSrcToTrg(p.name(), pi.name(), connections));
//					relatedConnections.add(getConnectionFromSrcToTrg(pi.name(), p.name(), connections));
				}
			}
		}

		return relatedConnections;
	}

	private Connection getConnectionFromSrcToTrg(final String src, final String trg,
			final List<Connection> connections) {
		for (final Connection c : connections) {
			if (c.source().equals(src) && c.target().equals(trg)) {
				return c;
			}
		}

		throw new UnsupportedOperationException("Connection from " + src + " to " + trg + " could not be found.");
	}

//	private String toIdList(final List<WaitingClient> clients) {
//		return clients.stream().map(x -> x.id()).collect(Collectors.joining(","));
//	}

}
