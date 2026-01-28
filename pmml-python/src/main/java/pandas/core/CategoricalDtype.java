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

import org.dmg.pmml.DataType;
import org.jpmml.python.PythonObject;
import org.jpmml.python.TypeInfo;
import org.jpmml.python.TypeInfoUtil;

public class CategoricalDtype extends PythonObject implements TypeInfo {

	public CategoricalDtype(String module, String name){
		super(module, name);
	}

	@Override
	public DataType getDataType(){
		Object descr = getDescr();

		return TypeInfoUtil.getDataType(descr);
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