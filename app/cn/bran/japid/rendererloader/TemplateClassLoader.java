package cn.bran.japid.rendererloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.bran.japid.template.JapidRenderer;
import cn.bran.japid.template.JapidTemplateBaseWithoutPlay;
import cn.bran.japid.util.JapidFlags;

/**
 * The template class loader that detects changes and recompile on the fly.
 * 
 * 1. whenever changes detected, clear the global class cache. 2. Only redefine
 * the class to load, which will lead to define all the dependencies. All the
 * dependencies must be defined by the same class classloader or
 * InvalidAccessException. 3. The main program will call the loadClass once for
 * each of the classes defined in one classloader.
 * 
 * 
 * @author Bing Ran<bing_ran@hotmail.com>
 * 
 */
public class TemplateClassLoader extends ClassLoader {
	// the per classloader class cache
	private Map<String, Class<?>> localClasses = new ConcurrentHashMap<String, Class<?>>();

	private ClassLoader parentClassLoader;

	public TemplateClassLoader(ClassLoader parentClassLoader) {
		super(TemplateClassLoader.class.getClassLoader());
		this.parentClassLoader = parentClassLoader;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (!name.startsWith(JapidRenderer.JAPIDVIEWS)) {
			Class<?> cl = parentClassLoader.loadClass(name);
			if (cl != null)
				return cl;
			return super.loadClass(name);
		} else {
			String oid = "[TemplateClassLoader@" + Integer.toHexString(hashCode()) + "]";

			Class<?> cla = localClasses.get(name);
			if (cla != null) {
//				JapidFlags.log(oid + " loaded from local cache : " + name);
				return cla;
			}

			RendererClass rc = JapidRenderer.japidClasses.get(name);
			if (rc == null)
				throw new ClassNotFoundException("Japid could not resolve class: " + name);

			if (!rc.getClassName().contains("$")) {
				// added just in time compiling
				JapidRenderer.recompile(rc);
			}
			
			byte[] bytecode = rc.bytecode;

			if (bytecode == null) {
				throw new RuntimeException(oid + " could not find the bytecode for: " + name);
			}

			// the defineClass method will load the classes of the dependency
			// classes.
			@SuppressWarnings("unchecked")
			Class<? extends JapidTemplateBaseWithoutPlay> cl = (Class<? extends JapidTemplateBaseWithoutPlay>) defineClass(
					name, bytecode, 0, bytecode.length);
			rc.setClz(cl);
			localClasses.put(name, cl);
			rc.setLastDefined(System.currentTimeMillis());
//			JapidFlags.log(oid + " defined: " + name);
			return cl;
		}
	}

	/**
	 * Search for the byte code of the given class.
	 */
	protected byte[] getClassDefinition(String name) {
		name = name.replace(".", "/") + ".class";
		InputStream is = getResourceAsStream(name);
		if (is == null) {
			return null;
		}
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream(8192);
			byte[] buffer = new byte[8192];
			int count;
			while ((count = is.read(buffer, 0, buffer.length)) > 0) {
				os.write(buffer, 0, count);
			}
			return os.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}