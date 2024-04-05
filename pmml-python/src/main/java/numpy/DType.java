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
package numpy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dmg.pmml.DataType;
import org.jpmml.python.ClassDictUtil;
import org.jpmml.python.CythonObject;
import org.jpmml.python.TypeInfo;

public class DType extends CythonObject implements TypeInfo {

	public DType(String module, String name){
		super(module, name);
	}

	@Override
	public void __init__(Object[] args){
		super.__setstate__(ClassDictUtil.createAttributeMap(INIT_ATTRIBUTES, args));
	}

	/**
	 * https://github.com/numpy/numpy/blob/master/numpy/core/src/multiarray/descriptor.c
	 */
	@Override
	public void __setstate__(Object[] args){

		// XXX
		if(args.length == (SETSTATE_ATTRIBUTES.length + 1)){
			Object[] datetime_data = (Object[])args[SETSTATE_ATTRIBUTES.length];

			args = ClassDictUtil.extractArgs(args, 0, SETSTATE_ATTRIBUTES.length);
		}

		super.__setstate__(ClassDictUtil.createAttributeMap(SETSTATE_ATTRIBUTES, args));
	}

	@Override
	public DataType getDataType(){
		String obj = getObj();
		String order = getOrder();

		String descr = formatDescr(obj, order);

		return DTypeUtil.getDataType(descr);
	}

	public Object toDescr(){
		Map<String, Object[]> values = getValues();

		if(values == null){
			String obj = getObj();
			String order = getOrder();

			return formatDescr(obj, order);
		}

		Set<String> valueKeys = values.keySet();

		List<String> definition = DType.definitions.get(valueKeys);
		if(definition != null){
			return formatDescr(definition, values);
		}

		throw new IllegalArgumentException();
	}

	public Map<String, Object[]> getValues(){
		return (Map)getOptionalDict("values");
	}

	public String getObj(){
		return getOptionalString("obj");
	}

	public String getOrder(){
		return getOptionalString("order");
	}

	public Integer getWSize(){
		return getOptionalInteger("w_size");
	}

	static
	public void addDefinition(List<String> definition){
		DType.definitions.put(new HashSet<>(definition), definition);
	}

	static
	public void removeDefinition(List<String> definition){
		DType.definitions.remove(new HashSet<>(definition));
	}

	static
	private List<Object[]> formatDescr(Collection<String> keys, Map<String, Object[]> values){
		List<Object[]> result = new ArrayList<>();

		for(String key : keys){
			Object[] value = values.get(key);

			DType dtype = (DType)value[0];

			result.add(new Object[]{key, dtype.toDescr()});
		}

		return result;
	}

	static
	private String formatDescr(String obj, String order){

		if(obj != null){
			return (order != null ? (order + obj) : obj);
		}

		throw new IllegalArgumentException();
	}

	private static final Map<Set<String>, List<String>> definitions = new HashMap<>();

	private static final String[] INIT_ATTRIBUTES = {
		"obj",
		"align",
		"copy"
	};

	private static final String[] SETSTATE_ATTRIBUTES = {
		"version",
		"order",
		"subdescr",
		"names",
		"values",
		"w_size",
		"alignment",
		"flags"
	};
}