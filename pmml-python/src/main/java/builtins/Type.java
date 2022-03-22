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
import org.jpmml.python.PythonObject;

public class Type extends PythonObject {

	public Type(String module, String name){
		super(module, name);
	}

	public DataType getDataType(){
		String className = getClassName();

		switch(className){
			case "builtins.bool":
				return DataType.BOOLEAN;
			case "builtins.float":
				return DataType.DOUBLE;
			case "builtins.int":
				return DataType.INTEGER;
			case "builtins.str":
				return DataType.STRING;
			case "numpy.bool_":
				return DataType.BOOLEAN;
			case "numpy.int_":
			case "numpy.int8":
			case "numpy.int16":
			case "numpy.int32":
			case "numpy.int64":
				return DataType.INTEGER;
			case "numpy.uint8":
			case "numpy.uint16":
			case "numpy.uint32":
			case "numpy.uint64":
				return DataType.INTEGER;
			case "numpy.float32":
				return DataType.FLOAT;
			case "numpy.float_":
			case "numpy.float64":
				return DataType.DOUBLE;
			default:
				throw new IllegalArgumentException("Python data type \'" + className + "\' is not supported");

		}
	}
}