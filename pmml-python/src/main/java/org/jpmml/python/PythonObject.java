/*
 * Copyright (c) 2017 Villu Ruusmann
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

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import joblib.NDArrayWrapper;
import net.razorvine.pickle.objects.ClassDict;
import numpy.core.NDArray;
import numpy.core.NDArrayUtil;
import org.jpmml.converter.ExceptionUtil;
import org.jpmml.converter.Formattable;
import org.jpmml.converter.ValueUtil;
import org.jpmml.model.ReflectionUtil;

abstract
public class PythonObject extends ClassDict implements Formattable {

	public PythonObject(String module, String name){
		super(module, name);
	}

	@Override
	public String format(){
		return getClassName();
	}

	public String getPythonModule(){
		String className = getClassName();

		int dot = className.lastIndexOf('.');
		if(dot > -1){
			return className.substring(0, dot);
		}

		return null;
	}

	public String getPythonName(){
		String className = getClassName();

		int dot = className.lastIndexOf('.');
		if(dot > -1){
			return className.substring(dot + 1);
		}

		return className;
	}

	@Override
	public String getClassName(){
		return super.getClassName();
	}

	public PythonObject setClassName(String className){
		ReflectionUtil.setFieldValue(PythonObject.FIELD_CLASSNAME, this, className);

		setattr("__class__", className);

		return this;
	}

	@Override
	public boolean containsKey(Object key){
		return super.containsKey(key);
	}

	@Override
	public Object get(Object key){
		return super.get(key);
	}

	@Override
	public Object put(String key, Object value){
		return super.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ?> map){
		super.putAll(map);
	}

	@Override
	public Object remove(Object key){
		return super.remove(key);
	}

	public boolean hasattr(String name){
		return containsKey(name);
	}

	public Object getattr(String name){

		if(!containsKey(name)){
			throw new MissingAttributeException(new Attribute(this, name));
		}

		return get(name);
	}

	public Object getattr(String name, Object defaultValue){

		if(!containsKey(name)){
			return defaultValue;
		}

		return get(name);
	}

	public void setattr(String name, Object value){
		put(name, value);
	}

	public void update(Map<String, ?> dict){
		putAll(dict);
	}

	public void delattr(String name){
		remove(name);
	}

	public <E> E get(String name, Class<? extends E> clazz){
		return get(name, clazz::cast);
	}

	public <E> E get(String name, java.util.function.Function<Object, E> castFunction){
		Object value = getattr(name);

		if(value == null){
			Attribute attribute = new Attribute(this, name);

			throw new InvalidAttributeException("Attribute " + ExceptionUtil.formatName(attribute.format()) + " has a missing (None) value", attribute);
		}

		try {
			return castFunction.apply(value);
		} catch(ClassCastException cce){
			Attribute attribute = new Attribute(this, name);

			throw new InvalidAttributeException("Attribute " + ExceptionUtil.formatName(attribute.format()) + " has an unsupported value (" + ClassDictUtil.formatClass(value) + ")", attribute, cce);
		}
	}

	public <E> E getOptional(String name, Class<? extends E> clazz){
		return getOptional(name, clazz::cast);
	}

	public <E> E getOptional(String name, java.util.function.Function<Object, E> castFunction){
		Object value = getattr(name, null);

		if(value == null){
			return null;
		}

		return get(name, castFunction);
	}

	public Object getObject(String name){
		return get(name, new ScalarCastFunction<>(Object.class));
	}

	public Object getOptionalObject(String name){
		return getOptional(name, new ScalarCastFunction<>(Object.class));
	}

	public Boolean getBoolean(String name){
		return get(name, new ScalarCastFunction<>(Boolean.class));
	}

	public Boolean getOptionalBoolean(String name){
		return getOptional(name, new ScalarCastFunction<>(Boolean.class));
	}

	public Boolean getOptionalBoolean(String name, Boolean defaultValue){
		Boolean value = getOptionalBoolean(name);

		if(value == null){
			return defaultValue;
		}

		return value;
	}

	public Number getNumber(String name){
		return get(name, new ScalarCastFunction<>(Number.class));
	}

	public Number getOptionalNumber(String name){
		return getOptional(name, new ScalarCastFunction<>(Number.class));
	}

	public Integer getInteger(String name){
		Number value = getNumber(name);

		return ValueUtil.asInteger(value);
	}

	public Integer getOptionalInteger(String name){
		Number value = getOptionalNumber(name);

		if(value == null){
			return null;
		}

		return ValueUtil.asInteger(value);
	}

	public String getString(String name){
		return get(name, new ScalarCastFunction<>(String.class));
	}

	public String getOptionalString(String name){
		return getOptional(name, new ScalarCastFunction<>(String.class));
	}

	public Identifiable getIdentifiable(String name){
		return get(name, new IdentifiableCastFunction<>(Identifiable.class));
	}

	public Identifiable getOptionalIdentifiable(String name){
		return getOptional(name, new IdentifiableCastFunction<>(Identifiable.class));
	}

	public <E> E getEnum(String name, java.util.function.Function<String, E> function, Collection<E> enumValues){
		E value = function.apply(name);

		if(!enumValues.contains(value)){
			Attribute attribute = new Attribute(this, name);

			throw new InvalidAttributeException("Attribute " + ExceptionUtil.formatName(attribute.format()) + " has an unsupported value " + PythonFormatterUtil.formatValue(value), attribute)
				.setSolution("Use one of the supported values " + PythonFormatterUtil.formatCollection(enumValues));
		}

		return value;
	}

	public <E> E getOptionalEnum(String name, java.util.function.Function< String, E> function, Collection<E> enumValues){
		E value = function.apply(name);

		if((value != null)  && (!enumValues.contains(value))){
			Attribute attribute = new Attribute(this, name);

			throw new InvalidAttributeException("Attribute " + ExceptionUtil.formatName(attribute.format()) + " has an unsupported value " + PythonFormatterUtil.formatValue(value), attribute)
				.setSolution("Use one of the supported values " + PythonFormatterUtil.formatCollection(enumValues));
		}

		return value;
	}

	public Object[] getTuple(String name){
		return get(name, Object[].class);
	}

	public Object[] getOptionalTuple(String name){
		return getOptional(name, Object[].class);
	}

	@SuppressWarnings("unchecked")
	public Map<String, ?> getDict(String name){
		return get(name, Map.class);
	}

	@SuppressWarnings("unchecked")
	public Map<String, ?> getOptionalDict(String name){
		return getOptional(name, Map.class);
	}

	public HasArray getArray(String name){
		Object object = get(name, Object.class);

		if(object instanceof HasArray){
			HasArray hasArray = (HasArray)object;

			return hasArray;
		}

		Attribute attribute = new Attribute(this, name);

		throw new InvalidAttributeException("Array attribute " + ExceptionUtil.formatName(attribute.format()) + " has an unsupported value (" + ClassDictUtil.formatClass(object) + ")", attribute);
	}

	public <E> List<E> getArray(String name, Class<? extends E> clazz){
		return getArray(name, clazz::cast);
	}

	public <E> List<E> getArray(String name, java.util.function.Function<Object, E> castFunction){
		HasArray hasArray = getArray(name);

		List<?> values = hasArray.getArrayContent();

		Attribute attribute = new Attribute(this, name);

		Function<Object, E> function = new Function<Object, E>(){

			@Override
			public E apply(Object object){

				try {
					return castFunction.apply(object);
				} catch(ClassCastException cce){
					throw new InvalidAttributeException("Array attribute " + ExceptionUtil.formatName(attribute.format()) + " contains an unsupported value (" + ClassDictUtil.formatClass(object) + ")", attribute, cce);
				}
			}
		};

		return Lists.transform(values, function);
	}

	public int[] getArrayShape(String name){
		Object object = getObject(name);

		if(object instanceof Number){
			return new int[]{1};
		} // End if

		if(object instanceof HasArray){
			HasArray hasArray = (HasArray)object;

			return hasArray.getArrayShape();
		}

		Attribute attribute = new Attribute(this, name);

		throw new InvalidAttributeException("Array attribute " + ExceptionUtil.formatName(attribute.format()) + " has an unsuppoted value (" + ClassDictUtil.formatClass(object) +")", attribute);
	}

	public int[] getArrayShape(String name, int length){
		int[] shape = getArrayShape(name);

		if(shape.length != length){
			Attribute attribute = new Attribute(this, name);

			throw new InvalidAttributeException("Array attribute " + ExceptionUtil.formatName(attribute.format()) + " is mis-shaped", attribute);
		}

		return shape;
	}

	public List<Object> getObjectArray(String name){
		return getArray(name, new ScalarCastFunction<>(Object.class));
	}

	public List<Boolean> getBooleanArray(String name){
		return getArray(name, new ScalarCastFunction<>(Boolean.class));
	}

	public List<Number> getNumberArray(String name){
		Object object = getObject(name);

		if((Number.class).isInstance(object)){
			return Collections.singletonList((Number)object);
		}

		return getArray(name, new ScalarCastFunction<>(Number.class));
	}

	public List<Integer> getIntegerArray(String name){
		List<Number> values = getNumberArray(name);

		return ValueUtil.asIntegers(values);
	}

	public List<String> getStringArray(String name){
		return getArray(name, new ScalarCastFunction<>(String.class));
	}

	public List<?> getArray(String name, String key){
		Object object = get(name, Object.class);

		if(object instanceof NDArrayWrapper){
			NDArrayWrapper arrayWrapper = (NDArrayWrapper)object;

			object = arrayWrapper.getContent();
		} // End if

		if(object instanceof NDArray){
			NDArray array = (NDArray)object;

			return NDArrayUtil.getContent(array, key);
		}

		Attribute attribute = new Attribute(this, name);

		throw new InvalidAttributeException("Array attribute " + ExceptionUtil.formatName(attribute.format()) + " has an unsupported value (" + ClassDictUtil.formatClass(object) + ")", attribute);
	}

	public List<?> getList(String name){
		return get(name, List.class);
	}

	public <E> List<E> getList(String name, Class<? extends E> clazz){
		return getList(name, clazz::cast);
	}

	public <E> List<E> getList(String name, java.util.function.Function<Object, E> castFunction){
		List<?> values = getList(name);

		Attribute attribute = new Attribute(this, name);

		Function<Object, E> function = new Function<Object, E>(){

			@Override
			public E apply(Object object){

				try {
					return castFunction.apply(object);
				} catch(ClassCastException cce){
					throw new InvalidAttributeException("List attribute " + ExceptionUtil.formatName(attribute.format()) + " contains an unsupported value (" + ClassDictUtil.formatClass(object) + ")", attribute, cce);
				}
			}
		};

		return Lists.transform(values, function);
	}

	public List<Object> getObjectList(String name){
		return getList(name, new ScalarCastFunction<>(Object.class));
	}

	public List<String> getStringList(String name){
		return getList(name, new ScalarCastFunction<>(String.class));
	}

	public <E> List<E> getEnumList(String name, java.util.function.Function<String, List<E>> function, Collection<E> enumValues){
		List<E> values = function.apply(name);

		Function<E, E> enumFunction = new Function<E, E>(){

			@Override
			public E apply(E value){

				if(!enumValues.contains(value)){
					Attribute attribute = new Attribute(PythonObject.this, name);

					throw new InvalidAttributeException("List attribute " + ExceptionUtil.formatName(attribute.format()) + " contains an unsupported value " + PythonFormatterUtil.formatValue(value), attribute)
						.setSolution("Use one of the supported values " + PythonFormatterUtil.formatCollection(enumValues));
				}

				return value;
			}
		};

		return Lists.transform(values, enumFunction);
	}

	public List<Object[]> getTupleList(String name){
		return getList(name, Object[].class);
	}

	public List<HasArray> getArrayList(String name){
		return getList(name, HasArray.class);
	}

	public <E> List<List<E>> getArrayList(String name, Class<? extends E> clazz){
		return getArrayList(name, clazz::cast);
	}

	public <E> List<List<E>> getArrayList(String name, java.util.function.Function<Object, E> castFunction){
		List<HasArray> values = getArrayList(name);

		Attribute attribute = new Attribute(this, name);

		Function<Object, E> arrayFunction = new Function<Object, E>(){

			@Override
			public E apply(Object object){

				try {
					return castFunction.apply(object);
				} catch(ClassCastException cce){
					throw new InvalidAttributeException("List of arrays attribute " + ExceptionUtil.formatName(attribute.format()) + " contains an unsupported value (" + ClassDictUtil.formatClass(object) + ")", attribute, cce);
				}
			}
		};

		Function<HasArray, List<E>> listFunction = new Function<HasArray, List<E>>(){

			@Override
			public List<E> apply(HasArray hasArray){

				// A list may contain null elements
				if(hasArray == null){
					return null;
				}

				List<?> values = hasArray.getArrayContent();

				return Lists.transform(values, arrayFunction);
			}
		};

		return Lists.transform(values, listFunction);
	}

	public List<?> getListLike(String name){
		Object object = get(name, Object.class);

		if(object != null && ScalarCastFunction.isScalar(object.getClass())){
			return Collections.singletonList(object);
		} // End if

		if(object instanceof HasArray){
			HasArray hasArray = getArray(name);

			return hasArray.getArrayContent();
		} else

		{
			return getList(name);
		}
	}

	public <E> List<E> getListLike(String name, Class<? extends E> clazz){
		return getListLike(name, clazz::cast);
	}

	public <E> List<E> getListLike(String name, java.util.function.Function<Object, E> castFunction){
		Object object = get(name, Object.class);

		if(object != null && ScalarCastFunction.isScalar(object.getClass())){
			return Collections.singletonList((E)object);
		} // End if

		if(object instanceof HasArray){
			HasArray hasArray = (HasArray)object;

			return getArray(name, castFunction);
		} else

		{
			return getList(name, castFunction);
		}
	}

	public List<String> getStringListLike(String name){
		return getListLike(name, new ScalarCastFunction<>(String.class));
	}

	public List<Number> getNumberListLike(String name){
		return getListLike(name, new ScalarCastFunction<>(Number.class));
	}

	private static final Field FIELD_CLASSNAME = ReflectionUtil.getField(ClassDict.class, "classname");
}