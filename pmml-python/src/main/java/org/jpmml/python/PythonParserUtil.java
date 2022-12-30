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
		return image.substring(1, image.length() - 1);
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
}