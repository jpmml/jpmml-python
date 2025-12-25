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
package builtins;

import org.dmg.pmml.DataType;
import org.jpmml.python.Identifiable;
import org.jpmml.python.IdentifiableUtil;
import org.jpmml.python.PythonObject;
import org.jpmml.python.TypeInfo;
import org.jpmml.python.TypeUtil;

public class Type extends PythonObject implements TypeInfo {

	public Type(String module, String name){
		super(module, name);
	}

	@Override
	public DataType getDataType(){
		Identifiable identifiable = IdentifiableUtil.toIdentifiable(this);

		return TypeUtil.parseDataType(identifiable);
	}

	static
	public Type forClassName(String className){
		String module;
		String name;

		int dot = className.lastIndexOf('.');
		if(dot > -1){
			module = className.substring(0, dot);
			name = className.substring(dot + 1);
		} else

		{
			module = "builtins";
			name = className;
		}

		return new Type(module, name);
	}
}