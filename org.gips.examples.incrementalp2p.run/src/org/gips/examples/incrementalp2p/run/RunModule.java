package org.gips.examples.incrementalp2p.run;

import org.eclipse.emf.common.util.URI;
import org.gips.examples.incrementalp2p.common.JsonConverter.Network;
import org.gips.examples.incrementalp2p.distribution.module.IncrementalGipsModule;
import org.gips.examples.incrementalp2p.repository.module.RepositoryModule;
import org.gips.examples.incrementalp2p.visualization.contracts.GraphType;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationConfiguration;
import org.gips.examples.incrementalp2p.visualization.module.VisualizationModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class RunModule extends AbstractModule {
	private static final URI Uri = URI.createFileURI("Model" + ".xmi");

	public void run(final Network net, final String jsonOutputPath, final boolean openBrowser) {
		createInjector().getInstance(ExampleRunner.class).run(net, jsonOutputPath, openBrowser);
	}

	@Override
	protected void configure() {
		super.configure();
		initWithGtModel();
		install(new VisualizationModule(new VisualizationConfiguration(false, GraphType.Classic, false, true)));
	}

	private void initWithGtModel() {
		var repository = new RepositoryModule(Uri);
		var incrementalGipsModule = new IncrementalGipsModule(repository.getModel());

		install(repository);
		install(incrementalGipsModule);
		bind(ExampleRunner.class);
	}

	private Injector createInjector() {
		return Guice.createInjector(this);
	}

}
