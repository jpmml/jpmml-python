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

import java.util.Objects;

import org.dmg.pmml.DataType;

public class TypeUtil {

	private TypeUtil(){
	}

	static
	public String getModule(String name){

		switch(name){
			case "bool":
			case "bytes":
			case "complex":
			case "float":
			case "int":
			case "long":
			case "object":
			case "str":
			case "unicode":
				return "builtins";
			case "boolean":
			case "Float32":
			case "Float64":
			case "Int8":
			case "Int16":
			case "Int32":
			case "Int64":
			case "string":
			case "string[pyarrow]":
			case "string[python]":
			case "UInt8":
			case "UInt16":
			case "UInt32":
			case "UInt64":
				return "pandas";
			default:
				return "numpy";
		}
	}

	static
	public boolean isObjectType(String module, String name){

		if(Objects.equals("__builtin__", module) || Objects.equals("builtins", module)){
			return Objects.equals("object", name);
		} else

		if(Objects.equals("numpy", module)){
			return Objects.equals("object_", canonicalizeNumPyType(name));
		}

		return false;
	}

	static
	public DataType parseDataType(Identifiable identifiabe){
		return parseDataType(identifiabe.getModule(), identifiabe.getName());
	}

	static
	public DataType parseDataType(String module, String name){

		if(Objects.equals("__builtin__", module) || Objects.equals("builtins", module)){
			return parseBuiltinType(module, name);
		} else

		if(Objects.equals("numpy", module)){
			return parseNumPyType(module, name);
		} else

		if(Objects.equals("pandas", module)){
			return parsePandasType(module, name);
		} else

		if(Objects.equals("pyarrow", module)){
			return parsePyArrowType(module, name);
		}

		throw new TypeResolutionException(module, name);
	}

	static
	private DataType parseBuiltinType(String module, String name){

		switch(name){
			case "bool":
				return DataType.BOOLEAN;
			case "bytes":
				return DataType.STRING;
			case "float":
				return DataType.DOUBLE;
			case "int":
			case "long":
				return DataType.INTEGER;
			case "str":
			case "unicode":
				return DataType.STRING;
			default:
				break;
		}

		throw new TypeResolutionException(module, name);
	}

	static
	private DataType parseNumPyType(String module, String name){
		String canonicalName = canonicalizeNumPyType(name);

		switch(canonicalName){
			case "bool_":
				return DataType.BOOLEAN;
			case "bytes_":
				return DataType.STRING;
			case "datetime64[D]":
				return DataType.DATE;
			case "datetime64[s]":
				return DataType.DATE_TIME;
			case "float16":
				throw new TypeMappingException(module, canonicalName);
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
			case "longdouble":
				throw new TypeMappingException(module, canonicalName);
			case "str_":
				return DataType.STRING;
			case "uint8":
			case "uint16":
			case "uint32":
			case "uint64":
				return DataType.INTEGER;
			case "void":
				throw new TypeMappingException(module, canonicalName);
			default:
				break;
		}

		throw new TypeResolutionException(module, canonicalName);
	}

	static
	private String canonicalizeNumPyType(String name){
		String code = name;

		if(!code.isEmpty()){

			switch(code.charAt(0)){
				case '=':
				case '>':
				case '<':
				case '|':
					code = code.substring(1);
					break;
				default:
					break;
			}
		}

		switch(code){
			case "?":
			case "b1":
				return "bool_";
			case "S":
				return "bytes_";
			case "G":
				return "clongdouble";
			case "F":
			case "c8":
				return "complex64";
			case "D":
			case "c16":
				return "complex128";
			case "M":
			case "M8":
				return "datetime64";
			case "e":
			case "f2":
				return "float16";
			case "f":
			case "f4":
				return "float32";
			case "d":
			case "f8":
				return "float64";
			case "b":
			case "i1":
				return "int8";
			case "h":
			case "i2":
				return "int16";
			case "i":
			case "i4":
				return "int32";
			case "l":
			case "q":
			case "i8":
				return "int64";
			case "g":
				return "longdouble";
			case "O":
				return "object_";
			case "U":
				return "str_";
			case "m":
			case "m8":
				return "timedelta64";
			case "B":
			case "u1":
				return "uint8";
			case "H":
			case "u2":
				return "uint16";
			case "I":
			case "u4":
				return "uint32";
			case "L":
			case "Q":
			case "u8":
				return "uint64";
			case "V":
				return "void";
			default:
				break;
		}

		if(code.matches("S\\d+")){
			return "bytes_";
		} else

		if(code.matches("M8\\[\\w+\\]")){
			return "datetime64" + code.substring("M8".length());
		} else

		if(code.matches("U\\d+")){
			return "str_";
		} else

		if(code.matches("m8\\[\\w+\\]")){
			return "timedelta64" + code.substring("m8".length());
		} else

		if(code.matches("V\\d+")){
			return "void";
		}

		return name;
	}

	static
	private DataType parsePandasType(String module, String name){

		switch(name){
			case "boolean":
				return DataType.BOOLEAN;
			case "Float32":
				return DataType.FLOAT;
			case "Float64":
				return DataType.DOUBLE;
			case "Int8":
			case "Int16":
			case "Int32":
			case "Int64":
				return DataType.INTEGER;
			case "string":
			case "string[pyarrow]":
			case "string[python]":
				return DataType.STRING;
			case "UInt8":
			case "UInt16":
			case "UInt32":
			case "UInt64":
				return DataType.INTEGER;
			default:
				break;
		}

		throw new TypeResolutionException(module, name);
	}

	static
	private DataType parsePyArrowType(String module, String name){

		switch(name){
			case "large_string":
				return DataType.STRING;
			default:
				break;
		}

		throw new TypeResolutionException(module, name);
	}
}