/**
 *
 */
package com.github.publiclibs.continuousjazzer.config;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.publiclibs.continuousjazzer.t.FUZZ;

/**
 * @author freedom1b2830
 * @date 2023-января-22 15:52:10
 */
public class JazzerAgentConfig {
	public CopyOnWriteArrayList<String> targets;
	public int time = 30;
	public CopyOnWriteArrayList<String> cp;

	public String[] exclude = new String[] {

			"com.fasterxml.jackson.**",

			"org.yaml.snakeyaml.**",

	};
	public String[] ignoreExcepts = new String[] { // --autofuzz_ignore

			"java.lang.NullPointerException"

	};
	public int keep = 2000;
	public int detectLeak = 1;
	public CopyOnWriteArrayList<String> customHooks = new CopyOnWriteArrayList<>();

	/**
	 * @param class1
	 * @return
	 */
	public boolean addHook(final Class<?> class1) {
		return addHook(class1.getName());
	}

	/**
	 * @param name
	 * @return
	 */
	public boolean addHook(final String name) {
		return customHooks.addIfAbsent(name);
	}

	public void appendExclude(final StringBuilder excludeBuilder) {
		if (exclude != null) {
			final Iterator<String> excludeIter = Arrays.asList(exclude).iterator();
			if (excludeIter.hasNext()) {
				excludeBuilder.append(FUZZ.INSTRUMENTATION_EXCLUDES);
				while (excludeIter.hasNext()) {
					final String excludeStr = excludeIter.next();
					excludeBuilder.append(excludeStr);
					if (excludeIter.hasNext()) {
						excludeBuilder.append(':');
					}
				}
			}
		}
	}

	/**
	 * @param fuzzerCmdBuilder
	 */
	public void appendHooks(final StringBuilder fuzzerCmdBuilder) {
		if (customHooks == null || customHooks.isEmpty()) {
			return;
		}
		fuzzerCmdBuilder.append(' ');
		fuzzerCmdBuilder.append("--custom_hooks=");
		fuzzerCmdBuilder.append(getHooksString());
	}

	private String getHooksString() {
		final StringBuilder hooksBuilder = new StringBuilder();
		final Iterator<String> iterator = customHooks.iterator();
		if (iterator.hasNext()) {
			while (iterator.hasNext()) {
				hooksBuilder.append(iterator.next());
				if (iterator.hasNext()) {
					hooksBuilder.append(",");
				}
			}
		}
		return hooksBuilder.toString();
	}

}
