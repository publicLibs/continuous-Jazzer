/**
 *
 */
package com.github.publiclibs.continuousjazzer.t;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.publiclibs.continuousjazzer.ContinuousJazzerAgent;
import com.github.publiclibs.continuousjazzer.classpath.ClassPathUtils;
import com.github.publiclibs.continuousjazzer.config.JazzerAgentConfig;
import com.github.publiclibs.listofsignatures.Listofsignatures;

/**
 * @author freedom1b2830
 * @date 2023-января-23 08:45:29
 */
public class LIST {

	/**
	 * @param inputPath
	 * @throws IOException
	 *
	 */
	public static void list(final Path inputPath) throws IOException {
		if (!Files.exists(inputPath)) {
			throw new NoSuchFileException(inputPath.toAbsolutePath().toString());
		}
		if (!Files.isDirectory(inputPath)) {
			throw new IllegalArgumentException(String.format("%s no dir", inputPath));
		}
		final JazzerAgentConfig config = new JazzerAgentConfig();
		// config.addHook(UrlClassLoaderHook.class);

		// получаем все библиотеки
		final CopyOnWriteArrayList<Path> jars = new CopyOnWriteArrayList<>();
		ClassPathUtils.findAllJars(jars, inputPath);
		for (final Path path : jars) {
			System.err.println("founded jars :" + path);
		}

		// аргументы для получения сигнатур
		final List<String> showOnly = Collections.emptyList();
		final CopyOnWriteArrayList<String> signs = new CopyOnWriteArrayList<>();
		final boolean full = true;
		// получение сигнатур
		final Stream<String> data1 = Listofsignatures.getForJars(full, showOnly, jars);
		signs.addAllAbsent(data1.collect(Collectors.toList()));
		Collections.sort(signs);

		config.targets = signs;

		final CopyOnWriteArrayList<String> cpStrings = new CopyOnWriteArrayList<>();
		for (final Path path : jars) {
			if (Files.isDirectory(path)) {
				cpStrings.addIfAbsent(String.format("%s/*", path.toAbsolutePath().toString()));
			} else if (Files.isRegularFile(path)) {

				/*
				 * final var par = path.getParent(); cpStrings.addIfAbsent(new
				 * File(par.toString(), "*").getAbsolutePath());
				 */

				cpStrings.addIfAbsent(path.toAbsolutePath().toString());
			}
		}
		for (final String string : cpStrings) {
			System.out.println("cpStrings:" + string);
		}
		config.cp = cpStrings;

		final Path confPath = Paths.get("config.yaml");
		if (Files.notExists(confPath)) {
			Files.createFile(confPath);
		}
		ContinuousJazzerAgent.objectMapper.writeValue(confPath.toFile(), config);
	}

}
