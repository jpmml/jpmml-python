/*
 * Copyright (c) 2019 Villu Ruusmann
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
import java.util.Map;
import java.util.function.Function;

import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.OpType;
import org.dmg.pmml.PMMLFunctions;
import org.dmg.pmml.ParameterField;
import org.jpmml.converter.ExpressionUtil;
import org.jpmml.converter.PMMLEncoder;
import org.jpmml.python.functions.BuiltinFunctions;
import org.jpmml.python.functions.MathFunctions;
import org.jpmml.python.functions.NumPyFunctions;
import org.jpmml.python.functions.PCRE2Functions;
import org.jpmml.python.functions.PCREFunctions;
import org.jpmml.python.functions.PandasFunctions;
import org.jpmml.python.functions.REFunctions;
import org.jpmml.python.functions.SciPySpecialFunctions;

public class FunctionUtil {

	private FunctionUtil(){
	}

	static
	public Apply encodeFunction(Identifiable identifiable, List<Expression> expressions, PMMLEncoder encoder){
		return encodeFunction(identifiable.getModule(), identifiable.getName(), expressions, null);
	}

	static
	public Apply encodeFunction(String module, String name, List<Expression> expressions, PMMLEncoder encoder){
		PythonFunction function = resolveFunction(module, name);

		List<String> parameters = function.getParameters();
		if(parameters != null && expressions.size() != parameters.size()){
			throw new InvalidFunctionCallException(module, name, parameters, expressions);
		}

		return function.encode(expressions, encoder);
	}

	static
	private PythonFunction resolveFunction(String module, String name){
		Map<String, PythonFunction> registry = getRegistry(module);

		if(registry != null){
			PythonFunction function = registry.get(name);

			if(function != null){
				return function;
			}
		}

		throw new FunctionResolutionException(module + "." + name);
	}

	static
	private Map<String, PythonFunction> getRegistry(String module){

		if(checkModulePrefix(module, "builtins")){
			return BuiltinFunctions.REGISTRY;
		} else

		if(checkModulePrefix(module, "math")){
			return MathFunctions.REGISTRY;
		} else

		if(checkModulePrefix(module, "numpy")){
			return NumPyFunctions.REGISTRY;
		} else

		if(checkModulePrefix(module, "pandas")){
			return PandasFunctions.REGISTRY;
		} else

		if(checkModulePrefix(module, "pcre")){
			return PCREFunctions.REGISTRY;
		} else

		if(checkModulePrefix(module, "pcre2")){
			return PCRE2Functions.REGISTRY;
		} else

		if(checkModulePrefix(module, "re")){
			return REFunctions.REGISTRY;
		} else

		if(checkModulePrefix(module, "scipy.special")){
			return SciPySpecialFunctions.REGISTRY;
		}

		return null;
	}

	static
	public Apply expit(List<Expression> expressions, PMMLEncoder encoder){
		Function<Expression, Apply> applyGenerator = (expression) -> {
			return ExpressionUtil.createApply(PMMLFunctions.DIVIDE,
				ExpressionUtil.createConstant(1),
				ExpressionUtil.createApply(PMMLFunctions.ADD,
					ExpressionUtil.createConstant(1),
					ExpressionUtil.createApply(PMMLFunctions.EXP, ExpressionUtil.createApply(PMMLFunctions.MULTIPLY, ExpressionUtil.createConstant(-1), expression))
				)
			);
		};

		return ensureApply("expit", OpType.CONTINUOUS, DataType.DOUBLE, expressions.get(0), applyGenerator, encoder);
	}

	static
	public Apply logit(List<Expression> expressions, PMMLEncoder encoder){
		Function<Expression, Apply> applyGenerator = (expression) -> {
			return ExpressionUtil.createApply(PMMLFunctions.LN,
				ExpressionUtil.createApply(PMMLFunctions.DIVIDE,
					expression,
					ExpressionUtil.createApply(PMMLFunctions.SUBTRACT, ExpressionUtil.createConstant(1), expression)
				)
			);
		};

		return ensureApply("logit", OpType.CONTINUOUS, DataType.DOUBLE, expressions.get(0), applyGenerator, encoder);
	}

	static
	public Apply sign(List<Expression> expressions, PMMLEncoder encoder){
		Function<Expression, Apply> applyGenerator = (expression) -> {
			return ExpressionUtil.createApply(PMMLFunctions.IF, ExpressionUtil.createApply(PMMLFunctions.LESSTHAN, expression, ExpressionUtil.createConstant(0)),
				ExpressionUtil.createConstant(-1), // x < 0
				ExpressionUtil.createApply(PMMLFunctions.IF, ExpressionUtil.createApply(PMMLFunctions.GREATERTHAN, expression, ExpressionUtil.createConstant(0)),
					ExpressionUtil.createConstant(+1), // x > 0
					ExpressionUtil.createConstant(0) // x == 0
				)
			);
		};

		return ensureApply("sign", OpType.CATEGORICAL, DataType.INTEGER, expressions.get(0), applyGenerator, encoder);
	}

	static
	public Apply trunc(List<Expression> expressions, PMMLEncoder encoder){
		Function<Expression, Apply> applyGenerator = (expression) -> {
			return ExpressionUtil.createApply(PMMLFunctions.IF, ExpressionUtil.createApply(PMMLFunctions.LESSTHAN, expression, ExpressionUtil.createConstant(0)),
				ExpressionUtil.createApply(PMMLFunctions.CEIL, expression), // x < 0
				ExpressionUtil.createApply(PMMLFunctions.FLOOR, expression) // x >= 0
			);
		};

		return ensureApply("trunc", OpType.CONTINUOUS, DataType.INTEGER, expressions.get(0), applyGenerator, encoder);
	}

	static
	public Apply ensureApply(String name, OpType opType, DataType dataType, Expression expression, Function<Expression, Apply> applyGenerator, PMMLEncoder encoder){

		if(expression instanceof FieldRef){
			FieldRef fieldRef = (FieldRef)expression;

			return applyGenerator.apply(fieldRef);
		}

		Function<ParameterField, Expression> applySupplier = (valueField) -> {
			return applyGenerator.apply(new FieldRef(valueField));
		};

		DefineFunction defineFunction = ensureDefineFunction(name, opType, dataType, applySupplier, encoder);

		return ExpressionUtil.createApply(defineFunction, expression);
	}

	static
	public DefineFunction ensureDefineFunction(String name, OpType opType, DataType dataType, Function<ParameterField, ? extends Expression> expressionGenerator, PMMLEncoder encoder){
		DefineFunction defineFunction = encoder.getDefineFunction(name);

		if(defineFunction == null){
			ParameterField valueField = new ParameterField("x");

			Expression expression = expressionGenerator.apply(valueField);

			defineFunction = new DefineFunction(name, opType, dataType, null, expression)
				.addParameterFields(valueField);

			encoder.addDefineFunction(defineFunction);
		}

		return defineFunction;
	}

	static
	public Expression updateConstant(Expression expression, Function<String, String> function){

		if(expression instanceof Constant){
			Constant constant = (Constant)expression;

			constant.setValue(function.apply((String)constant.getValue()));

			return constant;
		}

		return expression;
	}

	static
	private boolean checkModulePrefix(String module, String prefix){
		return (prefix).equals(module) || (module != null && module.startsWith(prefix + "."));
	}
}