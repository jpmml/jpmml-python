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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.dmg.pmml.Apply;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.OpType;
import org.dmg.pmml.PMMLFunctions;
import org.dmg.pmml.ParameterField;
import org.jpmml.converter.ExpressionUtil;
import org.jpmml.converter.FeatureResolver;
import org.jpmml.converter.PMMLEncoder;
import org.jpmml.python.FunctionUtil;
import org.jpmml.python.PythonFunction;

public interface BuiltinFunctions extends Functions {

	PythonFunction ABS = new UnaryFunction(PMMLFunctions.ABS);

	PythonFunction BOOL = new PythonFunction(){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("x");
		}

		@Override
		public Apply encode(List<Expression> expressions, PMMLEncoder encoder){
			Expression expression = expressions.get(0);

			DataType dataType = ExpressionUtil.getDataType(expression, (FeatureResolver)encoder);
			if(dataType != null){

				switch(dataType){
					case STRING:
						return ExpressionUtil.createApply(PMMLFunctions.GREATERTHAN,
							ExpressionUtil.createApply(PMMLFunctions.STRINGLENGTH, expression), ExpressionUtil.createConstant(0)
						);
					case INTEGER:
					case FLOAT:
					case DOUBLE:
						return ExpressionUtil.createApply(PMMLFunctions.NOTEQUAL,
							expression, ExpressionUtil.createConstant(dataType, 0)
						);
					default:
						break;
				}
			}

			Function<ParameterField, FieldRef> expressionGenerator = (valueField) -> {
				return new FieldRef(valueField);
			};

			DefineFunction defineFunction = FunctionUtil.ensureDefineFunction("bool", OpType.CATEGORICAL, DataType.BOOLEAN, expressionGenerator, encoder);

			return ExpressionUtil.createApply(defineFunction, expression);
		}
	};

	PythonFunction FLOAT = new PythonFunction(){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("x");
		}

		@Override
		public Apply encode(List<Expression> expressions, PMMLEncoder encoder){
			Function<ParameterField, FieldRef> expressionGenerator = (valueField) -> {
				return new FieldRef(valueField);
			};

			DefineFunction defineFunction = FunctionUtil.ensureDefineFunction("float", OpType.CONTINUOUS, DataType.DOUBLE, expressionGenerator, encoder);

			return ExpressionUtil.createApply(defineFunction, expressions.get(0));
		}
	};

	PythonFunction INT = new PythonFunction(){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("x");
		}

		@Override
		public Apply encode(List<Expression> expressions, PMMLEncoder encoder){
			Expression expression = expressions.get(0);

			DataType dataType = ExpressionUtil.getDataType(expression, (FeatureResolver)encoder);
			if(dataType != null){

				switch(dataType){
					case FLOAT:
					case DOUBLE:
						return FunctionUtil.trunc(expressions, encoder);
					default:
						break;
				}
			}

			Function<ParameterField, FieldRef> expressionGenerator = (valueField) -> {
				return new FieldRef(valueField);
			};

			DefineFunction defineFunction = FunctionUtil.ensureDefineFunction("int", OpType.CONTINUOUS, DataType.INTEGER, expressionGenerator, encoder);

			return ExpressionUtil.createApply(defineFunction, expression);
		}
	};

	PythonFunction LEN = new UnaryFunction(PMMLFunctions.STRINGLENGTH){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("obj");
		}
	};

	PythonFunction MAX = new AggregateFunction(PMMLFunctions.MAX);

	PythonFunction MIN = new AggregateFunction(PMMLFunctions.MIN);

	PythonFunction STR = new PythonFunction(){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("obj");
		}

		@Override
		public Apply encode(List<Expression> expressions, PMMLEncoder encoder){
			Function<ParameterField, FieldRef> expressionGenerator = (valueField) -> {
				return new FieldRef(valueField);
			};

			DefineFunction defineFunction = FunctionUtil.ensureDefineFunction("str", OpType.CATEGORICAL, DataType.STRING, expressionGenerator, encoder);

			return ExpressionUtil.createApply(defineFunction, expressions.get(0));
		}
	};

	Map<String, PythonFunction> REGISTRY = Map.ofEntries(
		Map.entry("abs", BuiltinFunctions.ABS),
		Map.entry("bool", BuiltinFunctions.BOOL),
		Map.entry("float", BuiltinFunctions.FLOAT),
		Map.entry("int", BuiltinFunctions.INT),
		Map.entry("len", BuiltinFunctions.LEN),
		Map.entry("max", BuiltinFunctions.MAX),
		Map.entry("min", BuiltinFunctions.MIN),
		Map.entry("str", BuiltinFunctions.STR)
	);
}