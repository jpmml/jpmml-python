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

public class PythonParserUtil {

	private PythonParserUtil(){
	}

	static
	public String translateBoolean(String image){
		return image.toLowerCase();
	}

	static
	public String translateInt(String image){

		if(image.endsWith("l") || image.endsWith("L")){
			return image.substring(0, image.length() - 1);
		}

		return image;
	}

	static
	public String translateString(String image){

		if((image.length() < 2) || !(image.charAt(0) == '\"' || image.charAt(0) == '\'') || (image.charAt(0) != image.charAt(image.length() - 1))){
			throw new IllegalArgumentException(image);
		}

		return image.substring(1, image.length() - 1);
	}

	static
	public String translateMultilineString(String image){

		if(image.length() < 6 || !image.startsWith("\"\"\"") || !image.endsWith("\"\"\"")){
			throw new IllegalArgumentException(image);
		}

		return image.substring(3, image.length() - 3);
	}

	static
	public Object parseValue(Token value) throws ParseException {

		switch(value.kind){
			case PythonParserConstants.NONE:
				return null;
			case PythonParserConstants.FALSE:
			case PythonParserConstants.TRUE:
				return PythonParserUtil.parseBoolean(value);
			case PythonParserConstants.INT:
				return PythonParserUtil.parseInt(value);
			case PythonParserConstants.FLOAT:
				return PythonParserUtil.parseFloat(value);
			case PythonParserConstants.STRING:
				return PythonParserUtil.parseString(value);
			case PythonParserConstants.MULTILINE_STRING:
				return PythonParserUtil.parseMultilineString(value);
			default:
				throw new ParseException();
		}
	}

	static
	public boolean parseBoolean(Token value){
		String image = translateBoolean(value.image);

		return Boolean.parseBoolean(image);
	}

	static
	public double parseFloat(Token value){
		return Double.parseDouble(value.image);
	}

	static
	public int parseInt(Token value){
		return parseInt(null, value);
	}

	static
	public int parseInt(Token sign, Token value){
		String image = translateInt(value.image);

		if(sign != null){
			image = (sign.image + image);
		}

		return Integer.parseInt(image);
	}

	static
	public String parseString(Token value){
		return translateString(value.image);
	}

	static
	public String parseMultilineString(Token value){
		return translateMultilineString(value.image);
	}
}