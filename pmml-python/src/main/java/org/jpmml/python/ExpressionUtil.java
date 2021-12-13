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

		return (dataType == DataType.STRING);
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
				case PMMLFunctions.CEIL:
				case PMMLFunctions.FLOOR:
				case PMMLFunctions.ROUND:
					return DataType.INTEGER;
				case PMMLFunctions.ISMISSING:
				case PMMLFunctions.ISNOTMISSING:
				case PMMLFunctions.ISVALID:
				case PMMLFunctions.ISNOTVALID:
					return DataType.BOOLEAN;
				case PMMLFunctions.EQUAL:
				case PMMLFunctions.NOTEQUAL:
				case PMMLFunctions.LESSTHAN:
				case PMMLFunctions.LESSOREQUAL:
				case PMMLFunctions.GREATERTHAN:
				case PMMLFunctions.GREATEROREQUAL:
					return DataType.BOOLEAN;
				case PMMLFunctions.AND:
				case PMMLFunctions.OR:
					return DataType.BOOLEAN;
				case PMMLFunctions.NOT:
					return DataType.BOOLEAN;
				case PMMLFunctions.ISIN:
				case PMMLFunctions.ISNOTIN:
					return DataType.BOOLEAN;
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
				case PMMLFunctions.CONCAT:
				case PMMLFunctions.LOWERCASE:
				case PMMLFunctions.SUBSTRING:
				case PMMLFunctions.TRIMBLANKS:
				case PMMLFunctions.UPPERCASE:
					return DataType.STRING;
				case PMMLFunctions.STRINGLENGTH:
					return DataType.INTEGER;
				case PMMLFunctions.REPLACE:
					return DataType.STRING;
				case PMMLFunctions.MATCHES:
					return DataType.BOOLEAN;
				case PMMLFunctions.FORMATDATETIME:
				case PMMLFunctions.FORMATNUMBER:
					return DataType.STRING;
				case PMMLFunctions.DATEDAYSSINCEYEAR:
				case PMMLFunctions.DATESECONDSSINCEMIDNIGHT:
				case PMMLFunctions.DATESECONDSSINCEYEAR:
					return DataType.INTEGER;
				default:
					return null;
			}
		}

		return null;
	}
}