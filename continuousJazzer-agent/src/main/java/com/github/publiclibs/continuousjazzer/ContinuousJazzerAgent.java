/**
 *
 */
package com.github.publiclibs.continuousjazzer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.publiclibs.continuousjazzer.t.FUZZ;
import com.github.publiclibs.continuousjazzer.t.LIST;
import com.github.publiclibs.continuousjazzer.t.RUN;

/**
 *
 * @author freedom1b2830
 * @date 2023-января-13 01:01:05
 */
public class ContinuousJazzerAgent {

	public static final String JAVA_CLASS_PATH = "java.class.path";
	public static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

	/**
	 * @param fuzzerCmdBuilder
	 * @param cp
	 * @throws IOException
	 */

	public static void main(String[] args) throws Exception {

		final boolean test = false;
		if (test) {
			//
			System.err.println();
			if (args.length == 0) {
				args = new String[] {

						"LIST",

						"/home/user_dev_new/eclipse-workspace/publicLibs/_t_de/target"

						/*
						 * "FUZZ",
						 *
						 * "/home/user_dev_new/eclipse-workspace/publicLibs/continuousJazzer/continuousJazzer-agent/config.yaml"
						 */
				};
			}
		}

		if (args.length < 2) {
			for (int i = 0; i < args.length; i++) {
				final String string = args[i];
				System.err.println("argId[" + i + "] arg[" + string + "]");
			}
			throw new IllegalArgumentException("need args");
		}

		// cmd
		final CMD cmd = CMD.valueOf(args[0]);
		final Path inputPath = Paths.get(args[1]);
		switch (cmd) {
		case LIST:
			LIST.list(inputPath);
			break;
		case FUZZ:
			FUZZ.fuzzIn(inputPath);
			break;
		case RUN:
			RUN.run(args, inputPath);
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + cmd);
		}

	}
}
