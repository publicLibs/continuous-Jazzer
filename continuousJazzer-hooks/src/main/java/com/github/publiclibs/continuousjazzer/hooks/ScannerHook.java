/**
 *
 */
package com.github.publiclibs.continuousjazzer.hooks;

import java.lang.invoke.MethodHandle;

import com.code_intelligence.jazzer.api.HookType;
import com.code_intelligence.jazzer.api.MethodHook;

/*
import java.lang.invoke.MethodHandle;
import java.net.URL;
import java.util.Arrays;

import com.code_intelligence.jazzer.api.FuzzerSecurityIssueCritical;
import com.code_intelligence.jazzer.api.HookType;
import com.code_intelligence.jazzer.api.Jazzer;
import com.code_intelligence.jazzer.api.MethodHook;
*/
/**
 * @author freedom1b2830
 * @date 2023-марта-13 23:02:07
 */
public class ScannerHook {
	@MethodHook(targetClassName = "java.util.Scanner", targetMethod = "nextLine", type = HookType.REPLACE)
	public static String awesomeHookScanner(final MethodHandle handle, final Object thisObject, final Object[] args,
			final int hookId) {
		new Exception().printStackTrace();
		return "aaa";
	}
	/*
	 * @MethodHook(type = HookType.BEFORE, // targetClassName =
	 * "java.net.URLClassLoader", // targetMethod = "<init>",
	 *
	 * additionalClassesToHook = { "java.lang.ClassLoader",
	 * "java.security.SecureClassLoader" }
	 *
	 * ) public static void awesomeScriptEngineFactoryHook(final MethodHandle
	 * handle, final Object thisObject, final Object[] args, final int hookId) {
	 * System.err.println("handle:" + handle); System.err.println("obj:" +
	 * thisObject); System.err.println("args:" + Arrays.toString(args));
	 * System.err.println("hookId:" + hookId); final URL[] urls = (URL[]) args; for
	 * (final URL url : urls) { if (url.toString().contains("127.0.0.1:8000")) {
	 * final FuzzerSecurityIssueCritical exc = new FuzzerSecurityIssueCritical(
	 * "AwesomeScriptEngineFactory " + Arrays.toString(args));
	 * Jazzer.reportFindingFromHook(exc); throw exc; } } }
	 */
}
