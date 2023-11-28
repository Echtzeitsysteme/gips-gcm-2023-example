package org.gips.examples.incrementalp2p.common;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonConverter {

	public static Network jsonToModel(final String path) {
		final JsonObject json = readFileToJson(path);
//		final JsonPrimitive jsonFilename = (JsonPrimitive) json.get("filename");
		final JsonPrimitive jsonFilesize = (JsonPrimitive) json.get("filesize");
		final JsonArray jsonPeers = (JsonArray) json.get("peers");
		final JsonArray jsonConnections = (JsonArray) json.get("connections");

		final Collection<Peer> peers = new HashSet<Peer>();
		final Collection<Connection> connections = new HashSet<Connection>();

		for (int i = 0; i < jsonPeers.size(); i++) {
			final JsonObject peer = jsonPeers.get(i).getAsJsonObject();
			peers.add(new Peer(peer.get("name").getAsString(), peer.get("maxDownload").getAsInt(),
					peer.get("maxUpload").getAsInt()));
		}

		for (int i = 0; i < jsonConnections.size(); i++) {
			final JsonObject connection = jsonConnections.get(i).getAsJsonObject();
			connections.add(new Connection(connection.get("sourceName").getAsString(),
					connection.get("targetName").getAsString(), connection.get("bandwidth").getAsInt()));
		}

		final Network net = new Network(jsonFilesize.getAsInt(), peers, connections);
		return net;
	}

	/**
	 * Reads a file from a given path to a JSON object.
	 *
	 * @param path Path for the file to read.
	 * @return JSON object read from file.
	 */
	protected static JsonObject readFileToJson(final String path) {
		return new Gson().fromJson(readFile(path), JsonObject.class);
	}

	/**
	 * Reads a file from a given path and returns its content as string.
	 *
	 * @param path Path to read file from.
	 * @return File content as string.
	 */
	public static String readFile(final String path) {
		String read = "";
		try {
			read = Files.readString(Path.of(path));
		} catch (final IOException e) {
			throw new IllegalArgumentException();
		}
		return read;
	}

	public record Peer(String name, int maxDownload, int maxUpload) {
	}

	public record Connection(String source, String target, int bandwidth) {
	}

	public record Network(int filesize, Collection<Peer> peers, Collection<Connection> connections) {
	}

	public record SrcTrgTuple(String source, String target) {
	}

	public static void convertOutputToJson(final List<String> superpeers, final List<SrcTrgTuple> p2pConnections,
			final String path) {
		// TODO Auto-generated method stub
		final JsonObject json = new JsonObject();

		// Superpeers
		final JsonArray superpeersJson = new JsonArray();
		for (final String s : superpeers) {
			final JsonObject sJson = new JsonObject();
			sJson.addProperty("name", s);
			superpeersJson.add(sJson);
		}
		json.add("superpeers", superpeersJson);

		// Peer2Peer connections
		final JsonArray p2pConnectionsJson = new JsonArray();
		for (final SrcTrgTuple p : p2pConnections) {
			final JsonObject pJson = new JsonObject();
			pJson.addProperty("sourceName", p.source);
			pJson.addProperty("targetName", p.target);
			p2pConnectionsJson.add(pJson);
		}	
		json.add("peer2peer", p2pConnectionsJson);
		writeFile(path, new GsonBuilder().setPrettyPrinting().create().toJson(json));
	}

	private static void writeFile(final String path, final String content) {
		FileWriter file = null;
		try {
			file = new FileWriter(path);
			file.write(content);
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			try {
				file.flush();
				file.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

}