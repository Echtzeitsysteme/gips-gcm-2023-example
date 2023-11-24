package org.gips.examples.incrementalp2p.run;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.gips.examples.incrementalp2p.common.JsonConverter;
import org.gips.examples.incrementalp2p.common.JsonConverter.Network;
import org.gips.examples.incrementalp2p.common.models.WaitingClient;

public class ConsoleApp {
	final static Logger logger = Logger.getLogger(ConsoleApp.class);
	protected static String jsonImportPath;

	public static void main(final String[] args) {
		Logger.getRootLogger().setLevel(Level.INFO);
		logger.info("Start");

		try {
			run(args);
		} catch (Exception e) {
			logger.error("Error: ", e);
			System.exit(1);
		}

		System.exit(0);

	}

	private static void run(final String[] args) {
		setArgs(args);
//		var clients = createClients();
		final Network net = JsonConverter.jsonToModel(jsonImportPath);
		new RunModule().run(net, false);
	}

	private static void setArgs(final String[] args) {
		final Options options = new Options();

		// JSON file to load
		final Option jsonImportFile = new Option("f", "file", true, "json file to load the initial network from");
		jsonImportFile.setRequired(true);
		options.addOption(jsonImportFile);

		final CommandLineParser parser = new DefaultParser();
		final HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (final ParseException ex) {
			logger.error("Argument parsing error: " + ex.getMessage());
			formatter.printHelp("cli parameters", options);
			System.exit(1);
		}

		jsonImportPath = cmd.getOptionValue("file");
	}

}
