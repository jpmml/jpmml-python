/*
 * Copyright (c) 2021 Villu Ruusmann
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

import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.PMMLFunctions;
import org.jpmml.converter.Feature;

public class ExpressionUtil {

	private ExpressionUtil(){
	}

	static
	public boolean isString(Expression expression, Scope scope){
		DataType dataType = getDataType(expression, scope);

		return (DataType.STRING).equals(dataType);
	}

	static
	public DataType getDataType(Expression expression, Scope scope){

		if(expression instanceof Constant){
			Constant constant = (Constant)expression;

			return constant.getDataType();
		} else

		if(expression instanceof FieldRef){
			FieldRef fieldRef = (FieldRef)expression;

			Feature feature = scope.resolveFeature(fieldRef.getField());
			if(feature == null){
				return null;
			}

			return feature.getDataType();
		} else

		if(expression instanceof Apply){
			Apply apply = (Apply)expression;

			String function = apply.getFunction();
			switch(function){
				case PMMLFunctions.CONCAT:
				case PMMLFunctions.FORMATDATETIME:
				case PMMLFunctions.FORMATNUMBER:
				case PMMLFunctions.LOWERCASE:
				case PMMLFunctions.REPLACE:
				case PMMLFunctions.SUBSTRING:
				case PMMLFunctions.TRIMBLANKS:
				case PMMLFunctions.UPPERCASE:
					return DataType.STRING;
				case PMMLFunctions.IF:
					{
						List<Expression> expressions = apply.getExpressions();

						if(expressions.size() > 1){
							DataType trueDataType = getDataType(expressions.get(1), scope);

							if(expressions.size() > 2){
								DataType falseDataType = getDataType(expressions.get(2), scope);

								if(Objects.equals(trueDataType, falseDataType)){
									return trueDataType;
								}

								return null;
							}

							return trueDataType;
						}
					}
					return null;
				default:
					return null;
			}
		}

		return null;
	}
}