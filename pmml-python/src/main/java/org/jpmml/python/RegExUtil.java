/*
 * Copyright (c) 2024 Villu Ruusmann
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

public class RegExUtil {

	private RegExUtil(){
	}

	static
	public String translatePattern(String pattern, String reFlavour){

		switch(reFlavour){
			case RegExFlavours.PCRE:
			case RegExFlavours.PCRE2:
			case RegExFlavours.RE:
				return pattern;
			default:
				throw new IllegalArgumentException(reFlavour);
		}
	}

	static
	public String translateReplacement(String replacement, String reFlavour){

		switch(reFlavour){
			case RegExFlavours.PCRE:
			case RegExFlavours.PCRE2:
				return replacement;
			case RegExFlavours.RE:
				return replacement
					.replaceAll("\\$", "\\$\\$")
					.replaceAll("\\\\(\\d)", "\\$$1");
			default:
				throw new IllegalArgumentException(reFlavour);
		}
	}
}