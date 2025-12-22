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
package org.jpmml.python.functions;

import java.util.List;
import java.util.Objects;

import org.dmg.pmml.Apply;
import org.dmg.pmml.Expression;
import org.jpmml.converter.ExpressionUtil;
import org.jpmml.converter.PMMLEncoder;
import org.jpmml.python.PythonFunction;

public class AggregateFunction extends PythonFunction {

	private String function = null;


	public AggregateFunction(String function){
		setFunction(function);
	}

	@Override
	public List<String> getParameters(){
		return null;
	}

	@Override
	public boolean checkCall(List<Expression> expressions){
		return !expressions.isEmpty();
	}

	@Override
	public Apply encode(List<Expression> expressions, PMMLEncoder encoder){
		String function = getFunction();

		Apply apply = ExpressionUtil.createApply(function);

		for(Expression expression : expressions){
			apply.addExpressions(expression);
		}

		return apply;
	}

	public String getFunction(){
		return this.function;
	}

	private void setFunction(String function){
		this.function = Objects.requireNonNull(function);
	}
}