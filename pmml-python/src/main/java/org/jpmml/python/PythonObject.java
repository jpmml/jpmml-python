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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import functools.Partial;
import joblib.NDArrayWrapper;
import net.razorvine.pickle.objects.ClassDict;
import numpy.core.NDArray;
import numpy.core.NDArrayUtil;
import numpy.core.Scalar;
import numpy.core.ScalarUtil;
import org.jpmml.converter.ValueUtil;
import org.jpmml.model.ReflectionUtil;

abstract
public class PythonObject extends ClassDict {

	public PythonObject(String module, String name){
		super(module, name);
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
			throw new AttributeException("Attribute \'" + ClassDictUtil.formatMember(this, name) + "\' not set");
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
		Object value = getattr(name);

		if(value == null){
			throw new AttributeException("Attribute \'" + ClassDictUtil.formatMember(this, name) + "\' has a missing (None/null) value");
		} // End if

		if(Objects.equals(Boolean.class, clazz) || (Number.class).isAssignableFrom(clazz) || Objects.equals(String.class, clazz)){

			if(value instanceof Scalar){
				Scalar scalar = (Scalar)value;

				value = scalar.getOnlyElement();
			}
		} else

		if(Objects.equals(Identifiable.class, clazz)){

			if(value instanceof Partial){
				Partial partial = (Partial)value;

				value = partial.toIdentifiable();
			} else

			if(value instanceof PythonObjectConstructor){
				PythonObjectConstructor dictConstructor = (PythonObjectConstructor)value;

				value = dictConstructor.newObject();
			}
		}

		CastFunction<E> castFunction = new AttributeCastFunction<E>(clazz){

			@Override
			protected String formatMessage(Object object){
				return "Attribute \'" + ClassDictUtil.formatMember(PythonObject.this, name) + "\' has an unsupported value (" + ClassDictUtil.formatClass(object) + ")";
			}
		};

		return castFunction.apply(value);
	}

	public <E> E getOptional(String name, Class<? extends E> clazz){
		Object value = getattr(name, null);

		if(value == null){
			return null;
		}

		return get(name, clazz);
	}

	public Object getObject(String name){
		return get(name, Object.class);
	}

	public Object getOptionalObject(String name){
		return getOptional(name, Object.class);
	}

	public Object getScalar(String name){
		Object object = getObject(name);

		return ScalarUtil.decode(object);
	}

	public Object getOptionalScalar(String name){
		Object object = getOptionalObject(name);

		return ScalarUtil.decode(object);
	}

	public Boolean getBoolean(String name){
		return get(name, Boolean.class);
	}

	public Boolean getOptionalBoolean(String name){
		return getOptional(name, Boolean.class);
	}

	public Boolean getOptionalBoolean(String name, Boolean defaultValue){
		Boolean value = getOptionalBoolean(name);

		if(value == null){
			return defaultValue;
		}

		return value;
	}

	public Number getNumber(String name){
		return get(name, Number.class);
	}

	public Number getOptionalNumber(String name){
		return getOptional(name, Number.class);
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
		return get(name, String.class);
	}

	public String getOptionalString(String name){
		return getOptional(name, String.class);
	}

	public Identifiable getIdentifiable(String name){
		return get(name, Identifiable.class);
	}

	public Identifiable getOptionalIdentifiable(String name){
		return getOptional(name, Identifiable.class);
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
		Object object = getObject(name);

		if(object instanceof HasArray){
			HasArray hasArray = (HasArray)object;

			return hasArray;
		}

		throw new AttributeException("The value of \'" + ClassDictUtil.formatMember(this, name) + "\' attribute (" + ClassDictUtil.formatClass(object) + ") is not a supported array type");
	}

	public <E> List<E> getArray(String name, Class<? extends E> clazz){
		HasArray hasArray = getArray(name);

		List<?> values = hasArray.getArrayContent();

		CastFunction<E> castFunction = new AttributeCastFunction<E>(clazz){

			@Override
			protected String formatMessage(Object object){
				return "Array attribute \'" + ClassDictUtil.formatMember(PythonObject.this, name) + "\' contains an unsupported value (" + ClassDictUtil.formatClass(object) + ")";
			}
		};

		return Lists.transform(values, castFunction);
	}

	public int[] getArrayShape(String name){
		Object object = getObject(name);

		if(object instanceof HasArray){
			HasArray hasArray = (HasArray)object;

			return hasArray.getArrayShape();
		} // End if

		if(object instanceof Number){
			return new int[]{1};
		}

		throw new AttributeException("The value of \'" + ClassDictUtil.formatMember(this, name) + "\' attribute (" + ClassDictUtil.formatClass(object) +") is not a supported array type");
	}

	public int[] getArrayShape(String name, int length){
		int[] shape = getArrayShape(name);

		if(shape.length != length){
			throw new AttributeException("Array attribute \'" + ClassDictUtil.formatMember(this, name) + "\' is mis-shaped");
		}

		return shape;
	}

	public List<Object> getObjectArray(String name){
		return getArray(name, Object.class);
	}

	public List<Boolean> getBooleanArray(String name){
		return getArray(name, Boolean.class);
	}

	public List<Number> getNumberArray(String name){
		Object object = getObject(name);

		if((Number.class).isInstance(object)){
			return Collections.singletonList((Number)object);
		}

		HasArray hasArray = getArray(name);

		List<?> values = hasArray.getArrayContent();

		CastFunction<Number> castFunction = new AttributeCastFunction<Number>(Number.class){

			@Override
			public Number apply(Object object){
				return super.apply(ScalarUtil.decode(object));
			}

			@Override
			protected String formatMessage(Object object){
				return "Array attribute \'" + ClassDictUtil.formatMember(PythonObject.this, name) + "\' contains an unsupported value (" + ClassDictUtil.formatClass(object) + ")";
			}
		};

		return Lists.transform(values, castFunction);
	}

	public List<Integer> getIntegerArray(String name){
		List<Number> values = getNumberArray(name);

		return ValueUtil.asIntegers(values);
	}

	public List<String> getStringArray(String name){
		return getArray(name, String.class);
	}

	public List<?> getArray(String name, String key){
		Object object = getObject(name);

		if(object instanceof NDArrayWrapper){
			NDArrayWrapper arrayWrapper = (NDArrayWrapper)object;

			object = arrayWrapper.getContent();
		} // End if

		if(object instanceof NDArray){
			NDArray array = (NDArray)object;

			return NDArrayUtil.getContent(array, key);
		}

		throw new AttributeException("The value of \'" + ClassDictUtil.formatMember(this, name) + "\' attribute (" + ClassDictUtil.formatClass(object) + ") is not a supported array type");
	}

	public List<?> getList(String name){
		return get(name, List.class);
	}

	public <E> List<E> getList(String name, Class<? extends E> clazz){
		List<?> values = getList(name);

		CastFunction<E> castFunction = new AttributeCastFunction<E>(clazz){

			@Override
			protected String formatMessage(Object object){
				return "List attribute \'" + ClassDictUtil.formatMember(PythonObject.this, name) + "\' contains an unsupported value (" + ClassDictUtil.formatClass(object) + ")";
			}
		};

		return Lists.transform(values, castFunction);
	}

	public List<Object> getObjectList(String name){
		return getList(name, Object.class);
	}

	public List<String> getStringList(String name){
		return getList(name, String.class);
	}

	public List<Object[]> getTupleList(String name){
		return getList(name, Object[].class);
	}

	public List<HasArray> getArrayList(String name){
		return getList(name, HasArray.class);
	}

	public <E> List<List<E>> getArrayList(String name, Class<? extends E> clazz){
		List<HasArray> values = getArrayList(name);

		CastFunction<E> castFunction = new AttributeCastFunction<E>(clazz){

			@Override
			protected String formatMessage(Object object){
				return "List of arrays attribute \'" + ClassDictUtil.formatMember(PythonObject.this, name) + "\' contains an unsupported value (" + ClassDictUtil.formatClass(object) + ")";
			}
		};

		Function<HasArray, List<E>> function = new Function<HasArray, List<E>>(){

			@Override
			public List<E> apply(HasArray hasArray){

				// A list may contain null elements
				if(hasArray == null){
					return null;
				}

				List<?> values = hasArray.getArrayContent();

				return Lists.transform(values, castFunction);
			}
		};

		return Lists.transform(values, function);
	}

	public List<?> getListLike(String name){
		Object object = getObject(name);

		if(object instanceof HasArray){
			HasArray hasArray = getArray(name);

			return hasArray.getArrayContent();
		} else

		{
			return getList(name);
		}
	}

	public <E> List<E> getListLike(String name, Class<? extends E> clazz){
		Object object = getObject(name);

		if(Objects.equals(Object.class, clazz)){
			throw new IllegalArgumentException();
		} // End if

		if(clazz.isInstance(object)){
			return Collections.singletonList(clazz.cast(object));
		}

		List<?> values = getListLike(name);

		CastFunction<E> castFunction = new AttributeCastFunction<E>(clazz){

			@Override
			protected String formatMessage(Object object){
				return "Array or list attribute \'" + ClassDictUtil.formatMember(PythonObject.this, name) + "\' contains an unsupported value (" + ClassDictUtil.formatClass(object) + ")";
			}
		};

		return Lists.transform(values, castFunction);
	}

	private static final Field FIELD_CLASSNAME = ReflectionUtil.getField(ClassDict.class, "classname");
}