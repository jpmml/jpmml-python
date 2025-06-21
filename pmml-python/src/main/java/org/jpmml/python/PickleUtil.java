/*
 * Copyright (c) 2015 Villu Ruusmann
 *
 * This file is part of JPMML-Python
 *
 * JPMML-Python is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPMML-Python is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with JPMML-Python.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jpmml.python;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.razorvine.pickle.IObjectConstructor;
import net.razorvine.pickle.PickleException;
import net.razorvine.pickle.Unpickler;
import net.razorvine.pickle.objects.ClassDictConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pandas.NDArrayBacked;
import pandas.NDArrayBackedConstructor;

public class PickleUtil {

	private PickleUtil(){
	}

	static
	public Object unpickle(Storage storage) throws PickleException, IOException {
		JoblibUnpickler unpickler = new JoblibUnpickler();

		return unpickler.load(storage);
	}

	@SuppressWarnings("unchecked")
	static
	public Map<String, IObjectConstructor> getObjectConstructors(){

		try {
			Field objectConstructorsField = Unpickler.class.getDeclaredField("objectConstructors");
			if(!objectConstructorsField.isAccessible()){
				objectConstructorsField.setAccessible(true);
			}

			return (Map<String, IObjectConstructor>)objectConstructorsField.get(null);
		} catch(ReflectiveOperationException roe){
			throw new RuntimeException(roe);
		}
	}

	static
	public void init(ClassLoader classLoader, String name){
		Enumeration<URL> urls;

		try {
			urls = classLoader.getResources("META-INF/" + name);
		} catch(IOException ioe){
			logger.warn("Failed to find resources", ioe);

			return;
		}

		while(urls.hasMoreElements()){
			URL url = urls.nextElement();

			logger.debug("Loading resource {}", url);

			try(InputStream is = url.openStream()){
				Properties properties = new Properties();
				properties.load(is);

				init(classLoader, properties);
			} catch(IOException ioe){
				logger.warn("Failed to load resource", ioe);
			}
		}
	}

	static
	private void init(ClassLoader classLoader, Properties properties){

		if(properties.isEmpty()){
			return;
		}

		Set<String> keys = properties.stringPropertyNames();
		for(String key : keys){
			String value = properties.getProperty(key);

			Collection<String> simpleKeys = expandComplexKey(key);
			for(String simpleKey : simpleKeys){
				init(classLoader, simpleKey, value);
			}
		}
	}

	static
	private void init(ClassLoader classLoader, String key, String value){

		if(("null").equals(value)){
			registerNullConstructor(key);
		} else

		{
			registerClassDictConstructor(classLoader, key, value);
		}
	}

	static
	private void registerNullConstructor(String key){
		logger.debug("Mapping Python class {} to null");

		int dot = key.lastIndexOf('.');
		if(dot < 0){
			logger.warn("Failed to identify the module and name parts of Python class {}", key);

			return;
		}

		String module = key.substring(0, dot);
		String name = key.substring(dot + 1);

		Unpickler.registerConstructor(module, name, NullConstructor.INSTANCE);
	}

	static
	private void registerClassDictConstructor(ClassLoader classLoader, String key, String value){

		if(value == null || ("").equals(value)){
			value = key;
		}

		logger.debug("Mapping Python class {} to Java class {}", key, value);

		int dot = key.lastIndexOf('.');
		if(dot < 0){
			logger.warn("Failed to identify the module and name parts of Python class {}", key);

			return;
		}

		String module = key.substring(0, dot);
		String name = key.substring(dot + 1);

		Class<?> clazz;

		try {
			clazz = classLoader.loadClass(value);
		} catch(ClassNotFoundException cnfe){
			logger.warn("Failed to load Java class {}", value);

			return;
		}

		ClassDictConstructor dictConstructor;

		if((PythonObject.class).isAssignableFrom(clazz)){

			if((CythonObject.class).isAssignableFrom(clazz)){

				if((NDArrayBacked.class).isAssignableFrom(clazz)){
					dictConstructor = new NDArrayBackedConstructor(module, name, clazz.asSubclass(NDArrayBacked.class));
				} else

				{
					dictConstructor = new CythonObjectConstructor(module, name, clazz.asSubclass(CythonObject.class));
				}
			} else

			{
				if((NamedTuple.class).isAssignableFrom(clazz)){
					dictConstructor = new NamedTupleConstructor(module, name, clazz.asSubclass(NamedTuple.class));
				} else

				if((PythonEnum.class).isAssignableFrom(clazz)){
					dictConstructor = new PythonEnumConstructor(module, name, clazz.asSubclass(PythonEnum.class));
				} else

				{
					dictConstructor = new PythonObjectConstructor(module, name, clazz.asSubclass(PythonObject.class));
				}
			}
		} else

		if((ClassDictConstructor.class).isAssignableFrom(clazz)){

			try {
				Constructor<?> constructor = clazz.getDeclaredConstructor(String.class, String.class);

				dictConstructor = (ClassDictConstructor)constructor.newInstance(module, name);
			} catch(ReflectiveOperationException roe){
				logger.warn("Failed to instantiate Java constructor", roe);

				return;
			}
		} else

		{
			logger.warn("Failed to identify the type of Java class {}", value);

			return;
		}

		Unpickler.registerConstructor(module, name, dictConstructor);
	}

	static
	private Collection<String> expandComplexKey(String key){
		int begin = key.indexOf('(');
		int end = key.indexOf(')', begin + 1);

		if(begin < 0 || end < 0){
			return Collections.singletonList(key);
		}

		String prefix = key.substring(0, begin);
		String body = key.substring(begin + 1, end);
		String suffix = key.substring(end + 1);

		List<String> result = new ArrayList<>();

		String[] strings = body.split("\\|");
		for(String string : strings){
			result.addAll(expandComplexKey(prefix + string + suffix));
		}

		return result;
	}

	private static final Logger logger = LoggerFactory.getLogger(PickleUtil.class);
}