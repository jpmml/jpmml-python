/*
 * Copyright (c) 2027 Villu Ruusmann
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

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class PythonFormatterUtil {

	private PythonFormatterUtil(){
	}

	static
	public String formatValue(Object value){

		if(value == null){
			return "None";
		} // End if

		if(value instanceof Boolean){
			return formatBoolean((Boolean)value);
		} else

		if(value instanceof Number){
			return formatNumber((Number)value);
		} else

		if(value instanceof String){
			return formatString((String)value);
		}

		throw new IllegalArgumentException("Expected Java primitive wrapper class, got " + ClassDictUtil.formatClass(value));
	}

	static
	public String formatBoolean(Boolean value){
		return value ? "True" : "False";
	}

	static
	public String formatNumber(Number value){
		return value.toString();
	}

	static
	public String formatString(String value){

		if(value.contains("\n")){
			return "'''" + value + "'''";
		} else

		{
			return "'" + value + "'";
		}
	}

	static
	public String formatCollection(Collection<?> values){
		String startChar;
		String endChar;

		if(values instanceof Set){
			startChar = "(";
			endChar = ")";
		} else

		{
			startChar = "[";
			endChar = "]";
		}

		return values.stream()
			.map(value -> PythonFormatterUtil.formatValue(value))
			.collect(Collectors.joining(", ", startChar, endChar));
	}
}