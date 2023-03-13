/**
 *
 */
package com.github.publiclibs.continuousjazzer.t;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.github.publiclibs.continuousjazzer.ContinuousJazzerAgent;
import com.github.publiclibs.continuousjazzer.config.JazzerAgentConfig;

/**
 * @author freedom1b2830
 * @date 2023-января-23 08:43:55
 */
public class RUN {

	/**
	 *
	 */
	private static final String INSTRUMENTATION_EXCLUDES = "--instrumentation_excludes=";
	/**
	 *
	 */
	private static final String AUTOFUZZ = "--autofuzz=";

	public static void run(final String[] args, final Path inputPath) throws IOException, InterruptedException {

		final JazzerAgentConfig config = ContinuousJazzerAgent.objectMapper.readValue(inputPath.toFile(),
				JazzerAgentConfig.class);

		String target = "";
		String exclude = "";

		for (final String arg : args) {
			if (arg.startsWith(AUTOFUZZ)) {
				target = arg.split(AUTOFUZZ)[1];
				continue;
			}
			if (arg.startsWith(INSTRUMENTATION_EXCLUDES)) {
				exclude = arg.split(INSTRUMENTATION_EXCLUDES)[1];
				continue;
			}
			System.out.println("RUN.run(args):" + arg);
		}

		final ArrayList<String> newArgs = new ArrayList<>();
		newArgs.add("" + config.time);
		newArgs.add(INSTRUMENTATION_EXCLUDES + exclude);
		newArgs.add(AUTOFUZZ + target);

		final StringBuilder exceptionsIgnoresBuilder = new StringBuilder();
		if (config.ignoreExcepts.length > 0) {
			exceptionsIgnoresBuilder.append("--autofuzz_ignore=");
			final Iterator<String> iter = Arrays.asList(config.ignoreExcepts).iterator();
			while (iter.hasNext()) {
				final String exc = iter.next();
				exceptionsIgnoresBuilder.append(exc);
				if (iter.hasNext()) {
					exceptionsIgnoresBuilder.append(',');
				}
			}
		}
		newArgs.add(exceptionsIgnoresBuilder.toString());

		final String[] completeArgs = newArgs.toArray(new String[newArgs.size()]);

		for (int i = 0; i < completeArgs.length; i++) {
			final String string = completeArgs[i];
			System.err.println("COMPLETE:" + string);
		}

		// Jazzer.main(completeArgs);

	}

}
