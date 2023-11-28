package org.gips.examples.incrementalp2p.run;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.gips.examples.incrementalp2p.common.JsonConverter;
import org.gips.examples.incrementalp2p.common.JsonConverter.Network;

public class ConsoleApp {
	final static Logger logger = Logger.getLogger(ConsoleApp.class);
	protected static String jsonImportPath;
	protected static String jsonOutputPath;

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
		new RunModule().run(net, jsonOutputPath, true);
	}

	private static void setArgs(final String[] args) {
		final Options options = new Options();

		// JSON file to load
		final Option jsonImportFile = new Option("i", "input", true, "json file to load the initial network from");
		jsonImportFile.setRequired(true);
		options.addOption(jsonImportFile);

		// JSON file to save
		final Option jsonExportFile = new Option("o", "output", true, "json file to save the output to");
		jsonExportFile.setRequired(true);
		options.addOption(jsonExportFile);

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

		jsonImportPath = cmd.getOptionValue("input");
		jsonOutputPath = cmd.getOptionValue("output");
	}

}
