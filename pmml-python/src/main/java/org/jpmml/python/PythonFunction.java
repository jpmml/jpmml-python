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

import java.util.List;

import org.dmg.pmml.Apply;
import org.dmg.pmml.Expression;
import org.jpmml.converter.PMMLEncoder;

abstract
public class PythonFunction {

	abstract
	public List<String> getParameters();

	abstract
	public Apply encode(List<Expression> arguments, PMMLEncoder encoder);

	public boolean checkCall(List<Expression> expressions){
		List<String> parameters = getParameters();

		if(parameters != null && expressions.size() != parameters.size()){
			return false;
		}

		return true;
	}
}