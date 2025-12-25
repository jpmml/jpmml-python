/*
 * Copyright (c) 2025 Villu Ruusmann
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

import org.jpmml.converter.ResolutionException;

public class TypeResolutionException extends ResolutionException {

	public TypeResolutionException(String module, String name){
		super(formatMessage(module + "." + name));
	}

	public TypeResolutionException(String dottedName){
		super(formatMessage(dottedName));
	}

	static
	private String formatMessage(String dottedName){
		return "Type \'" + dottedName + "\' is not defined or not supported";
	}
}