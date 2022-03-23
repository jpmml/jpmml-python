/*
 * Copyright (c) 2022 Villu Ruusmann
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
package pandas.core;

import java.util.List;

import numpy.DType;
import numpy.core.TypeDescriptor;
import org.dmg.pmml.DataType;
import org.jpmml.python.ClassDictUtil;
import org.jpmml.python.PythonObject;
import org.jpmml.python.TypeInfo;

public class CategoricalDtype extends PythonObject implements TypeInfo {

	public CategoricalDtype(String module, String name){
		super(module, name);
	}

	@Override
	public DataType getDataType(){
		Object descr = getDescr();

		if(descr instanceof String){
			String string = (String)descr;

			TypeDescriptor descriptor = new TypeDescriptor(string);

			return descriptor.getDataType();
		} else

		if(descr instanceof DType){
			DType dtype = (DType)descr;

			return dtype.getDataType();
		} else

		{
			throw new IllegalArgumentException("The type descriptor object (" + ClassDictUtil.formatClass(descr) + ") is not supported");
		}
	}

	public Object getDescr(){
		Index categories = getCategories();

		return categories.getDescr();
	}

	public List<?> getValues(){
		Index categories = getCategories();

		return categories.getValues();
	}

	public Index getCategories(){
		return get("categories", Index.class);
	}

	public Boolean getOrdered(){
		return getBoolean("ordered");
	}
}