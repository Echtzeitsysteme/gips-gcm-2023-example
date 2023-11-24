package org.gips.examples.incrementalp2p.repository.implementation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.emoflon.ibex.gt.api.GraphTransformationAPI;
import org.gips.examples.incrementalp2p.common.CommonConstants;
import org.gips.examples.incrementalp2p.common.JsonConverter.Connection;
import org.gips.examples.incrementalp2p.common.JsonConverter.Network;
import org.gips.examples.incrementalp2p.common.JsonConverter.Peer;
import org.gips.examples.incrementalp2p.common.TimeAggregator;
import org.gips.examples.incrementalp2p.common.models.WaitingClient;
import org.gips.examples.incrementalp2p.repository.api.RepositoryAPI;
import org.gips.examples.incrementalp2p.repository.contracts.P2PNetworkRepository;
import org.gips.examples.incrementalp2p.repository.contracts.models.ClientModel;
import org.gips.examples.incrementalp2p.repository.contracts.models.ConnectionModel;

import com.google.inject.Inject;

public class GtClientRepository extends AApiSaver implements P2PNetworkRepository {
//	final String nameToSearch = "lectureStudioServer";
	final String nameToSearch = CommonConstants.RootName;

	final static Logger logger = Logger.getLogger(GtClientRepository.class);

	@Inject
	RepositoryAPI api;

	@Override
	public ClientModel getRootServer() {
		return api.findLectureStudioServer().matchStream().map(x -> x.getLectureStudioServer().getLsNode())
				.map(Mapper::toClientModel).findFirst().get();
	}

	@Override
	public List<ClientModel> getClients() {
		return api.findAnyClient().matchStream().map(x -> x.getNode()).map(Mapper::toClientModel)
				.collect(Collectors.toList());
	}

	@Override
	public List<ClientModel> getClients(final List<String> clientIds) {
		var clientIdSet = clientIds.stream().collect(Collectors.toSet());

		return api.findAnyClient().matchStream().map(x -> x.getNode()).filter(x -> clientIdSet.contains(x.getId()))
				.map(Mapper::toClientModel).collect(Collectors.toList());
	}

	@Override
	public List<ConnectionModel> getConnections() {
		return api.findAnyCurrentConnection().matchStream().map(x -> x.getConnection()).map(Mapper::toConnectionModel)
				.collect(Collectors.toList());
	}

	@Override
	public GtClientRepository init(final Network net) {
		api.root().apply();
		api.network().apply();
		final Peer lectureStudioServer = getLectureStudioServer(net);
		api.lectureStudioServer(CommonConstants.RootName, net.filesize(), lectureStudioServer.maxDownload(),
				lectureStudioServer.maxUpload()).apply();
		api.waitingClientQueue().apply();
		return this;
	}

	@Override
	public void addWaitingClients(final Network net) {
		var ls = api.findLectureStudioServer().matchStream().map(x -> x.getLsNode()).map(Mapper::toWaitingClient)
				.findFirst().get();

//		var clientModels = clients.stream().collect(Collectors.toList());
		final var clientModels = new LinkedList<WaitingClient>();
		final var pIt = net.peers().iterator();
		while (pIt.hasNext()) {
			final var peer = pIt.next();
			if (peer.name() != null && !peer.name().equals(nameToSearch)) {
				clientModels.add(new WaitingClient(peer.name(), peer.maxDownload(), peer.maxUpload()));
			}
		}

		var currentNodes = api.findAnyNode().matchStream().map(x -> x.getNode()).filter(x -> x.getId() != ls.id())
				.map(Mapper::toWaitingClient).collect(Collectors.toList());

		var possibleServerNodes = Stream.concat(clientModels.stream(), currentNodes.stream());

		var lectureStudioConnections = getConnectionsForServer(clientModels, ls, net.connections());
		var relayClientConnections = possibleServerNodes
				.flatMap(x -> getConnectionsForServer(clientModels, x, net.connections()));

		clientModels.forEach(this::addClient);

		Stream.concat(lectureStudioConnections, relayClientConnections).collect(Collectors.toList())
				.forEach(this::createConnection);

		TimeAggregator.gtTick();
		api.updateMatches();
		TimeAggregator.gtTock();
	}

	private Peer getLectureStudioServer(final Network net) {
		final var it = net.peers().iterator();
		while (it.hasNext()) {
			final var p = it.next();
			if (p.name() != null && p.name().equals(nameToSearch)) {
				return p;
			}
		}
		throw new InternalError("lectureStudioServer not found!");
	}

//	@Override
//	public List<ClientModel> removeRelayClientsAndAttachOrphansAsWaiting(final int count) {
//		var relayClients = api.findAnyClient().matchStream().filter(x -> x.getNode().isIsRelayClient()).limit(count)
//				.map(x -> x.getNode()).map(Mapper::toClientModel).collect(Collectors.toList());
//
//		relayClients.forEach(x -> removeRelayClientAndGetChildrenInternal(x));
//
//		TimeAggregator.gtTick();
//		api.updateMatches();
//		TimeAggregator.gtTock();
//
//		return relayClients;
//	}

//	@Override
//	public void removeAllPossibleConnections() {
//		var toRemove = api.removePossibleConnection().matchStream().collect(Collectors.toList());
//
//		toRemove.forEach(x -> api.removePossibleConnection().apply(x, false));
//
//		TimeAggregator.gtTick();
//		api.updateMatches();
//		TimeAggregator.gtTock();
//	}

	@Override
	protected GraphTransformationAPI saveApi() {
		return api;
	}

//	private void removeRelayClientAndGetChildrenInternal(final ClientModel relayClient) {
//		var relayClientId = relayClient.id();
//
//		logger.debug("Removed client " + relayClientId);
//
//		var leafConnections = api.removeConnectionFromServer(relayClientId).matchStream().collect(Collectors.toList());
//
//		var leafs = leafConnections.stream().map(x -> x.getConnection().getClient()).collect(Collectors.toList());
//
//		leafConnections.forEach(x -> api.removeConnectionFromServer(relayClientId).apply(x, false));
//
//		api.removeConnectedClient(relayClientId).apply(false);
//
//		leafs.forEach(x -> addWaitingClient(x.getId()));
//	}

	private void addClient(final WaitingClient x) {
		api.addClient(x.id(), x.maxDownload(), x.maxUpload()).apply(false);
		addWaitingClient(x.id());
	}

	private void addWaitingClient(final String clientId) {
		logger.debug("Try to add waiting Client: " + clientId);
		var update = api.addWaitingClient(clientId).apply(false);
		logger.debug("Added waiting Client: " + update.get().getNode().getId());
	}

	private void createConnection(final ClientServerPair conn) {
		api.possibleConnection(conn.server(), conn.client(), conn.bandwidth).apply(false);
	}

	private Stream<ClientServerPair> getConnectionsForServer(final List<WaitingClient> clients,
			final WaitingClient server, final Collection<Connection> connections) {
//		return clients.stream().filter(x -> x.id() != server.id())
//				.map(client -> new ClientServerPair(server.id(), client.id()));
		final List<ClientServerPair> pairs = new LinkedList<GtClientRepository.ClientServerPair>();
		clients.stream().filter(x -> x.id() != server.id()).forEach(client -> {
			Connection con = null;
			final var connIt = connections.iterator();
			while (connIt.hasNext()) {
				final Connection currentConnection = connIt.next();
				if (currentConnection.source().equals(server.id()) && currentConnection.target().equals(client.id())) {
					con = currentConnection;
				}
			}
			pairs.add(new ClientServerPair(server.id(), client.id(), con.bandwidth()));
		});
		return pairs.stream();
	}

	record ClientServerPair(String server, String client, int bandwidth) {
	}

}
