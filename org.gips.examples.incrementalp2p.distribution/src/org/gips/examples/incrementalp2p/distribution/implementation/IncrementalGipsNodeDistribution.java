package org.gips.examples.incrementalp2p.distribution.implementation;

import org.apache.log4j.Logger;
import org.gips.examples.incrementalp2p.common.JsonConverter.Network;
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

		repository.addWaitingClients(net);
		distributeNodes();
//		if (logger.isDebugEnabled()) {
//			save("src-sim", "run" + toIdList(addedClients));
//		}
	}

//	private String toIdList(final List<WaitingClient> clients) {
//		return clients.stream().map(x -> x.id()).collect(Collectors.joining(","));
//	}

}
