/**
 *
 */
package com.github.publiclibs.continuousjazzer.classpath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * @author freedom1b2830
 * @date 2023-января-22 23:30:22
 */
public class ClassPathUtils {
	public static void findAllJars(final CopyOnWriteArrayList<Path> jars, final Path inputPath) throws IOException {
		if (Files.isDirectory(inputPath)) {
			try (Stream<Path> list = Files.list(inputPath)) {
				list.forEachOrdered(path -> {
					try {
						findAllJars(jars, path);
					} catch (final IOException e) {
						e.printStackTrace();
					}
				});
			}
		} else if (Files.isRegularFile(inputPath)) {
			final String fileName = inputPath.toFile().getName();
			if (fileName.endsWith(".jar")) {
				jars.addIfAbsent(inputPath);
			}
		}

	}

}
