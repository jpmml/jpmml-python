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
package org.jpmml.python;

import org.dmg.pmml.DataType;

public class TypeUtil {

	private TypeUtil(){
	}

	static
	public DataType parseDataType(String dottedName){
		int dot = dottedName.lastIndexOf('.');
		if(dot < 0){
			throw new IllegalArgumentException(dottedName);
		}

		String module = dottedName.substring(0, dot);
		String name = dottedName.substring(dot + 1);

		return parseDataType(module, name);
	}

	static
	public DataType parseDataType(String module, String name){

		if(("builtins").equals(module)){
			return parseBuiltinType(module, name);
		} else

		if(("numpy").equals(module)){
			return parseNumpyType(module, name);
		}

		throw new TypeResolutionException(module, name);
	}

	static
	private DataType parseBuiltinType(String module, String name){

		switch(name){
			case "bool":
				return DataType.BOOLEAN;
			case "float":
				return DataType.DOUBLE;
			case "int":
				return DataType.INTEGER;
			case "str":
				return DataType.STRING;
			default:
				break;
		}

		throw new TypeResolutionException(module, name);
	}

	static
	private DataType parseNumpyType(String module, String name){

		switch(name){
			case "bool_":
				return DataType.BOOLEAN;
			case "datetime64[D]":
				return DataType.DATE;
			case "datetime64[s]":
				return DataType.DATE_TIME;
			case "float32":
				return DataType.FLOAT;
			case "float_":
			case "float64":
				return DataType.DOUBLE;
			case "int_":
			case "int8":
			case "int16":
			case "int32":
			case "int64":
				return DataType.INTEGER;
			case "str_":
				return DataType.STRING;
			case "uint8":
			case "uint16":
			case "uint32":
			case "uint64":
				return DataType.INTEGER;
			default:
				break;
		}

		throw new TypeResolutionException(module, name);
	}
}