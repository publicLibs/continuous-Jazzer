/**
*
*/
package com.github.publiclibs.continuousjazzer.t;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.lang3.SystemUtils;

import com.github.publicLibs.freedom1b2830.awesomeio.ResourcesIoUtils;
import com.github.publiclibs.continuousjazzer.ContinuousJazzerAgent;
import com.github.publiclibs.continuousjazzer.config.JazzerAgentConfig;

/**
 * @author freedom1b2830
 * @date 2023-января-23 08:42:03
 */
public class FUZZ {
	public static final String INSTRUMENTATION_EXCLUDES = "--instrumentation_excludes=";

	private static void execExt(final String classPath, final StringBuilder fuzzerCmdBuilder) throws IOException {
		final String[] cmd = fuzzerCmdBuilder.toString().split(" ");
		for (int i = 0; i < cmd.length; i++) {
			final String string = cmd[i];
			System.err.println("cmd:" + i + " arg:" + string);
		}

		final ProcessBuilder processBuilder = new ProcessBuilder(cmd);
		processBuilder.environment().put("CLASSPATH", classPath);

		processBuilder.redirectErrorStream(true);
		final Process process = processBuilder.start();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
			reader.lines().forEachOrdered(System.out::println);
		}
	}

	/**
	 * @param cp
	 * @return
	 */
	private static Object fixCPPath(final String cp) {
		final StringBuilder retBuilder = new StringBuilder();
		for (final char cc : cp.toCharArray()) {
			if (cc == ' ') {
				retBuilder.append("\\");
			}
			retBuilder.append(cc);
		}
		/*
		 * if (cp.length() != retBuilder.length()) { System.err.println(cp + " -> " +
		 * retBuilder); }
		 */

		return retBuilder.toString();
	}

	/**
	 *
	 * @param inputPath
	 * @throws IOException
	 */
	public static void fuzzIn(final Path configFilePath) throws IOException {

		if (!Files.exists(configFilePath)) {
			throw new NoSuchFileException(configFilePath.toAbsolutePath().toString());
		}
		final JazzerAgentConfig config = ContinuousJazzerAgent.objectMapper.readValue(configFilePath.toFile(),
				JazzerAgentConfig.class);

		final Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"),
				FUZZ.class.getPackage().getName() + "-" + System.getProperty("user.name"));
		if (Files.notExists(tmpDir)) {
			Files.createDirectories(tmpDir);
		}

		Path exeJar;
		if (SystemUtils.IS_OS_WINDOWS) {
			final String name = "jazzer-windows.exe";
			exeJar = Paths.get(tmpDir.toAbsolutePath().toString(), name);
			unpackResource(name, exeJar);
		} else if (SystemUtils.IS_OS_LINUX) {
			final String name = "jazzer-linux";
			exeJar = Paths.get(tmpDir.toAbsolutePath().toString(), name);
			unpackResource(name, exeJar);

			final Set<PosixFilePermission> perm = Files.getPosixFilePermissions(exeJar);
			perm.add(PosixFilePermission.OWNER_EXECUTE);
			Files.setPosixFilePermissions(exeJar, perm);
		} else if (SystemUtils.IS_OS_MAC) {
			final String name = "jazzer-macos";
			exeJar = Paths.get(tmpDir.toAbsolutePath().toString(), name);
			unpackResource(name, exeJar);
			final Set<PosixFilePermission> perm = Files.getPosixFilePermissions(exeJar);
			perm.add(PosixFilePermission.OWNER_EXECUTE);
			Files.setPosixFilePermissions(exeJar, perm);
		} else {
			throw new UnsupportedOperationException("wtf OS");
		}

		final Path hooksJar = Files.createTempFile(tmpDir, "publicLibs", "fuzzer-hooks.jar");
		unpackResource("fuzzer-hooks-jar-with-dependencies.jar", hooksJar);

		//
		final String agentName = "jazzer_standalone.jar";
		final Path agentJar = Paths.get(tmpDir.toAbsolutePath().toString(), agentName);
		unpackResource("jazzer-linux.jar", agentJar);

		final StringBuilder exclude = new StringBuilder();
		config.appendExclude(exclude);

		final StringBuilder cpFullString = new StringBuilder();

		cpFullString.append('.').append(File.pathSeparatorChar);
		cpFullString.append(hooksJar).append(File.pathSeparatorChar);

		final Iterator<String> cpIter = config.cp.iterator();
		while (cpIter.hasNext()) {
			final String cp = cpIter.next();
			cpFullString.append(fixCPPath(cp));
			if (cpIter.hasNext()) {
				cpFullString.append(File.pathSeparatorChar);
			}
		}
		// java -cp $ ....Fuzzer RUN --autofuzz=$target

		final StringBuilder exceptionsExcl = new StringBuilder();
		if (config.ignoreExcepts != null && config.ignoreExcepts.length > 0) {
			exceptionsExcl.append("--autofuzz_ignore=");
			final Iterator<String> iter = Arrays.asList(config.ignoreExcepts).iterator();
			while (iter.hasNext()) {
				final String exc = iter.next();
				exceptionsExcl.append(exc);
				if (iter.hasNext()) {
					exceptionsExcl.append(',');
				}
			}
		}

		for (final String target : config.targets) {

			final StringBuilder fuzzerCmdBuilder = new StringBuilder();
			fuzzerCmdBuilder.append(exeJar);

			fuzzerCmdBuilder.append(' ').append("--agent_path");
			fuzzerCmdBuilder.append(' ').append(agentJar.getParent());
			fuzzerCmdBuilder.append(' ').append("--cp=").append(cpFullString);

			config.appendHooks(fuzzerCmdBuilder);

			if (config.keep > 1) {
				fuzzerCmdBuilder.append(' ').append("--keep_going=").append(config.keep);
			}
			if (config.detectLeak == 1) {
				fuzzerCmdBuilder.append(' ').append("-detect_leaks=").append(config.detectLeak);
			}

			fuzzerCmdBuilder.append(' ').append("-max_total_time=").append(config.time);

			if (exceptionsExcl.length() > 0) {
				fuzzerCmdBuilder.append(' ').append(exceptionsExcl);
			}

			fuzzerCmdBuilder.append(' ').append("--autofuzz=").append(target);

			if (exclude.length() > 0) {
				fuzzerCmdBuilder.append(' ').append(exclude);
			}

			System.out.println("FUZZ.fuzzIn(cp):" + cpFullString);
			System.out.println("FUZZ.fuzzIn(cmd):" + fuzzerCmdBuilder);
			execExt(cpFullString.toString(), fuzzerCmdBuilder);
		}
	}

	private static void unpackResource(final String name, final Path filePath)
			throws NoSuchElementException, IOException {

		if (Files.notExists(filePath) || Files.size(filePath) == 0) {
			if (Files.notExists(filePath)) {
				Files.createFile(filePath);
			}
			try (InputStream hooksStream = ResourcesIoUtils.readResource(name);
					OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.TRUNCATE_EXISTING)) {
				final byte[] buffer = new byte[8192];
				while (hooksStream.available() > 0) {
					final int len = hooksStream.read(buffer);
					outputStream.write(buffer, 0, len);
				}
			}
		}

	}

}
