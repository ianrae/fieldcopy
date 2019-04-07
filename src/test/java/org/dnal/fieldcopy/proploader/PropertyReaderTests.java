package org.dnal.fieldcopy.proploader;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.dnal.fieldcopy.BaseTest;
import org.junit.Test;

/*
 * Loader.add(PropertiesFile)
 .add(SystemProperties)
 .add(EnvironmentVariables)

setLoaders(List<Loader>)
-can customize order
-can add custom loaders

load(name)
load subj.instance.name
load sub.name
load zzz.name
then apply defaultValue

resolver(name, ctx)
 ctx.load ...
 
 		private List<String> loaders = new ArrayList<>();
		
		public void setLoadOrder(String...loaders) {
			
		}


 */

public class PropertyReaderTests extends BaseTest {
	
	/**
	 * Attempts to read a property.
	 * 
	 * @author Ian Rae
	 */
	public interface PLoader {
		Object load(String propertyName);
	}
	
	/**
	 * A list of loaders. They are tried in order. 
	 * The first one that returns a non-null value is returned.
	 * 
	 * @author Ian Rae
	 *
	 */
	public static class MultiLoader implements PLoader {
		private List<PLoader> loaders = new ArrayList<>();
		
		public void setLoadOrder(List<PLoader> list) {
			this.loaders = list;
		}
		public void add(PLoader loader) {
			loaders.add(loader);
		}
		
		@Override
		public Object load(String name) {
			Object val = null;
			for(PLoader loader: loaders) {
				val = loader.load(name);
				if (val != null) {
					break;
				}
			}
			return val;
		}
	}

	/**
	 * Abstract class for applying some additional logic
	 * on top of a list of loaders.
	 * 
	 * @author Ian Rae
	 *
	 */
	public static abstract class WrapperLoader implements PLoader {
		protected MultiLoader loader;

		public WrapperLoader(MultiLoader loader) {
			this.loader = loader;
		}
	}
	
	/**
	 * Searches in the following order
	 *    prefix1.propertyName
	 *    prefix2.propertyName
	 *    propertyName
	 * 
	 * The first search that finds a non-null value is returned.
	 * 
	 * Prefix1 and Prefix2 are optional. For example if prefix2 is null then
	 * the search is:
	 *    prefix1.propertyName
	 *    propertyName
	 * 
	 * @author Ian Rae
	 *
	 */
	public static class OptionalPrefixLoader extends WrapperLoader {
		private String prefix1;
		private String prefix2;
		private boolean searchRawName;
		
		public OptionalPrefixLoader(MultiLoader loader) {
			super(loader);
			searchRawName = true;
		}

		@Override
		public Object load(String name) {
			Object val = null;
			if (prefix1 != null) {
				val = loader.load(makePropertyName(prefix1, name));
			}
			if (val == null && prefix2 != null) {
				val = loader.load(makePropertyName(prefix2, name));
			}
			
			if (val == null && searchRawName) {
				val = loader.load(name);
			}
			return val;
		}

		private String makePropertyName(String prefix2, String name) {
			String propName = String.format("%s.%s", prefix1, name);
			return propName;
		}

		public String getPrefix1() {
			return prefix1;
		}

		public void setPrefix1(String prefix) {
			this.prefix1 = prefix;
		}

		public String getPrefix2() {
			return prefix2;
		}

		public void setPrefix2(String prefix2) {
			this.prefix2 = prefix2;
		}

		public boolean isSearchRawName() {
			return searchRawName;
		}

		public void setSearchRawName(boolean searchRawName) {
			this.searchRawName = searchRawName;
		}
	}
	
	//xloaderbuilder.addLoader(..).addWrapperLoader..
	//..addPrefixLoader(prefix1, prefix2)
	//copier(loader,dest)....
	//copier(dest).withPrefixes(tenantName, catName)....
	
	
	public static class XLoader {
		private List<PLoader> loaders = new ArrayList<>();
		
		public void setLoaders(List<PLoader> list) {
			this.loaders = list;
		}
		public List<PLoader> getLoaders() {
			return this.loaders;
		}
		public void addLoader(PLoader loader) {
			loaders.add(loader);
		}
		public void clearLoaders() {
			loaders.clear();
		}
		
		public Object load(String propertyName, Object defaultValue) {
			Object val = null;
			for(PLoader loader: loaders) {
				val = loader.load(propertyName);
				if (val != null) {
					break;
				}
			}
			
			if (val == null) {
				val = defaultValue;
			}
			
			return val;
		}
	}
	
	public static class SysPropLoader implements PLoader {
		@Override
		public Object load(String propertyName) {
			return System.getProperty(propertyName);
		}
	}
	public static class EnvLoader implements PLoader {
		@Override
		public Object load(String propertyName) {
			return System.getenv(propertyName);
		}
	}
	
	public static class ZZZ {
		private XLoader xloader;
		private MultiLoader multiLoader;
		private String prefix1;

		public ZZZ() {
			this.xloader = new XLoader();
			this.multiLoader = new MultiLoader();
			multiLoader.add(new SysPropLoader());
			multiLoader.add(new EnvLoader());
			xloader.addLoader(multiLoader);
		}
		
		public void addPrefix(String prefix) {
			if (this.prefix1 == null) {
				this.prefix1 = prefix;
				OptionalPrefixLoader oploader = new OptionalPrefixLoader(multiLoader);
				oploader.setPrefix1(prefix);
				xloader.clearLoaders();
				xloader.addLoader(oploader);
			} else {
				OptionalPrefixLoader oploader = (OptionalPrefixLoader) xloader.getLoaders().get(0);
				oploader.setPrefix1(prefix);
			}
		}
		
		public Object getAsObj(String propertyName, Object defaultValue) {
			return xloader.load(propertyName, defaultValue);
		}
		public String getString(String propertyName, Object defaultValue) {
			String s = (String) getAsObj(propertyName, defaultValue);
			return s;
		}
	}
	
	@Test
	public void test() {
		String s = System.getProperty("java.runtime.name");
		log(s);
		assertNotNull(s);

		final Properties systemProperties = System.getProperties();
		final Set<String> keys = systemProperties.stringPropertyNames();

		for (final String key : keys) {
			final String value = systemProperties.getProperty(key);
			log(String.format("%s: %s", key, value));
		}		
	}	
	@Test
	public void test1() {
		ZZZ zz = new ZZZ();
		String s = zz.getString("java.specification.version", "A");
		assertEquals("1.8", s);
	}	
	@Test
	public void test2() {
		ZZZ zz = new ZZZ();
		zz.addPrefix("java");
		String s = zz.getString("specification.version", "A");
		assertEquals("1.8", s);
	}	
	
}
