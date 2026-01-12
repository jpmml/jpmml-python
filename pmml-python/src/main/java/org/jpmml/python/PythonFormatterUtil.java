/*
 * Copyright (c) 2026 Villu Ruusmann
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
import java.util.Iterator;

import org.jpmml.converter.ConversionException;

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

		throw new ConversionException("Expected Java primitive wrapper class, got " + ClassDictUtil.formatClass(value));
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
	public String formatValues(Collection<?> values){
		return formatValues(values, "or");
	}

	static
	public String formatValues(Collection<?> values, String finalSeparator){
		StringBuilder sb = new StringBuilder();

		for(Iterator<?> it = values.iterator(); it.hasNext(); ){
			Object value = it.next();

			if(sb.length() > 0){
				boolean isFinal = !it.hasNext();

				if(isFinal){
					sb.append(" ").append(finalSeparator).append(" ");
				} else

				{
					sb.append(", ");
				}
			}

			sb.append(formatValue(value));
		}

		return sb.toString();
	}
}