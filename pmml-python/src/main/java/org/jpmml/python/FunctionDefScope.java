/*
 * Copyright (c) 2023 Villu Ruusmann
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
import java.util.Objects;

import org.jpmml.converter.Feature;
import org.jpmml.converter.PMMLEncoder;

public class FunctionDefScope extends BlockScope {

	private FunctionDef functionDef = null;


	public FunctionDefScope(FunctionDef functionDef, List<? extends Feature> variables){
		this(functionDef, variables, null);
	}

	public FunctionDefScope(FunctionDef functionDef, List<? extends Feature> variables, PMMLEncoder encoder){
		super(variables, encoder);

		setFunctionDef(functionDef);
	}

	@Override
	public Feature resolveFeature(String name){
		List<? extends Feature> variables = getVariables();

		int index = getParameterIndex(name);
		if(index > -1){
			return variables.get(index);
		}

		return null;
	}

	public int getParameterIndex(String name){
		FunctionDef functionDef = getFunctionDef();

		List<FunctionDef.Parameter> parameters = functionDef.getParameters();

		for(int i = 0; i < parameters.size(); i++){
			FunctionDef.Parameter parameter = parameters.get(i);

			if((parameter.getName()).equals(name)){
				return i;
			}
		}

		return -1;
	}

	public FunctionDef getFunctionDef(){
		return this.functionDef;
	}

	private void setFunctionDef(FunctionDef functionDef){
		this.functionDef = Objects.requireNonNull(functionDef);
	}
}