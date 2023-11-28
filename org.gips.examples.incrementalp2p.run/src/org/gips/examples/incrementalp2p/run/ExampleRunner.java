package org.gips.examples.incrementalp2p.run;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.gips.examples.incrementalp2p.common.JsonConverter;
import org.gips.examples.incrementalp2p.common.JsonConverter.Network;
import org.gips.examples.incrementalp2p.distribution.contracts.ConnectionLog;
import org.gips.examples.incrementalp2p.distribution.contracts.IncrementalNodeDistributionEngine;
import org.gips.examples.incrementalp2p.distribution.implementation.IncrementalGipsNodeDistribution;
import org.gips.examples.incrementalp2p.repository.contracts.P2PNetworkRepository;
import org.gips.examples.incrementalp2p.visualization.contracts.CachingVisualizationDataProvider;
import org.gips.examples.incrementalp2p.visualization.contracts.GraphVisualizer;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationUpdatesDataProvider;

import jakarta.inject.Inject;

public class ExampleRunner {
	final static Logger logger = Logger.getLogger(ExampleRunner.class);
	private static final String NodeName = "NodeDistribution";
	private static final String Folder = "src-sim";
	private static final String RelativeFolder = "." + File.separator + Folder;

	@Inject
	P2PNetworkRepository repository;
	@Inject
	IncrementalNodeDistributionEngine nodeDistributionEngine;
	@Inject
	GraphVisualizer visualizer;
	@Inject
	VisualizationUpdatesDataProvider visualizationUpdatesDataProvider;
	@Inject
	CachingVisualizationDataProvider cachingVisualizationDataProvider;
	@Inject
	ConnectionLog connectionLog;

	public void run(final Network net, final String jsonOutputPath, final boolean openBrowser) {
		repository.init(net).save(RelativeFolder, "Init");
		incrementalNodeDistribution(net, jsonOutputPath);
		if (openBrowser) {
			visualizer.createGraph(RelativeFolder, NodeName);
			openHtmlFileInBrowser();
		}
	}

	private void incrementalNodeDistribution(final Network net, final String jsonOutputPath) {
		nodeDistributionEngine.distributeNodes(net).save(RelativeFolder, "IncrementalNodeDistribution");

		var e = ((IncrementalGipsNodeDistribution) nodeDistributionEngine);
		JsonConverter.convertOutputToJson(e.getSuperpeers(), e.getP2pConnections(), jsonOutputPath);

		// For UI: Use First Incremental distr. as first view --> Cache network data
		cachingVisualizationDataProvider.cache();
	}

	private void openHtmlFileInBrowser() {
		try {
			var htmlFile = visualizer.getGraphFile(Folder, NodeName);
			Desktop.getDesktop().browse(htmlFile.toURI());
		} catch (final IOException e) {
			logger.error("Error while opening simulation", e);
			e.printStackTrace();
		}
	}

}
