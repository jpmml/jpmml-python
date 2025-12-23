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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
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

		boolean correct = function.checkCall(expressions);
		if(!correct){
			List<String> parameters = function.getParameters();

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
		Function<List<FieldRef>, Apply> applyGenerator = (fieldRefs) -> {
			FieldRef fieldRef = Iterables.getOnlyElement(fieldRefs);

			return ExpressionUtil.createApply(PMMLFunctions.DIVIDE,
				ExpressionUtil.createConstant(1),
				ExpressionUtil.createApply(PMMLFunctions.ADD,
					ExpressionUtil.createConstant(1),
					ExpressionUtil.createApply(PMMLFunctions.EXP, ExpressionUtil.createApply(PMMLFunctions.MULTIPLY, ExpressionUtil.createConstant(-1), fieldRef))
				)
			);
		};

		return ensureApply("expit", OpType.CONTINUOUS, DataType.DOUBLE, applyGenerator, Arrays.asList("x"), expressions, encoder);
	}

	static
	public Apply logit(List<Expression> expressions, PMMLEncoder encoder){
		Function<List<FieldRef>, Apply> applyGenerator = (fieldRefs) -> {
			FieldRef fieldRef = Iterables.getOnlyElement(fieldRefs);

			return ExpressionUtil.createApply(PMMLFunctions.LN,
				ExpressionUtil.createApply(PMMLFunctions.DIVIDE,
					fieldRef,
					ExpressionUtil.createApply(PMMLFunctions.SUBTRACT, ExpressionUtil.createConstant(1), fieldRef)
				)
			);
		};

		return ensureApply("logit", OpType.CONTINUOUS, DataType.DOUBLE, applyGenerator, Arrays.asList("x"), expressions, encoder);
	}

	static
	public Apply roundToDecimals(List<Expression> expressions, PMMLEncoder encoder){
		Function<List<FieldRef>, Apply> applyGenerator = (fieldRefs) -> {
			return ExpressionUtil.createApply(PMMLFunctions.DIVIDE,
				ExpressionUtil.createApply(PMMLFunctions.ROUND,
					ExpressionUtil.createApply(PMMLFunctions.MULTIPLY,
						fieldRefs.get(0),
						ExpressionUtil.createApply(PMMLFunctions.POW,
							ExpressionUtil.createConstant(10d), fieldRefs.get(1)
						)
					)
				),
				ExpressionUtil.createApply(PMMLFunctions.POW,
					ExpressionUtil.createConstant(10d), fieldRefs.get(1)
				)
			);
		};

		return ensureApply("roundToDecimals", OpType.CONTINUOUS, DataType.DOUBLE, applyGenerator, Arrays.asList("x", "decimals"), expressions, encoder);
	}

	static
	public Apply sign(List<Expression> expressions, PMMLEncoder encoder){
		Function<List<FieldRef>, Apply> applyGenerator = (fieldRefs) -> {
			FieldRef fieldRef = Iterables.getOnlyElement(fieldRefs);

			return ExpressionUtil.createApply(PMMLFunctions.IF, ExpressionUtil.createApply(PMMLFunctions.LESSTHAN, fieldRef, ExpressionUtil.createConstant(0)),
				ExpressionUtil.createConstant(-1), // x < 0
				ExpressionUtil.createApply(PMMLFunctions.IF, ExpressionUtil.createApply(PMMLFunctions.GREATERTHAN, fieldRef, ExpressionUtil.createConstant(0)),
					ExpressionUtil.createConstant(+1), // x > 0
					ExpressionUtil.createConstant(0) // x == 0
				)
			);
		};

		return ensureApply("sign", OpType.CATEGORICAL, DataType.INTEGER, applyGenerator, Arrays.asList("x"), expressions, encoder);
	}

	static
	public Apply trunc(List<Expression> expressions, PMMLEncoder encoder){
		Function<List<FieldRef>, Apply> applyGenerator = (fieldRefs) -> {
			FieldRef fieldRef = Iterables.getOnlyElement(fieldRefs);

			return ExpressionUtil.createApply(PMMLFunctions.IF, ExpressionUtil.createApply(PMMLFunctions.LESSTHAN, fieldRef, ExpressionUtil.createConstant(0)),
				ExpressionUtil.createApply(PMMLFunctions.CEIL, fieldRef), // x < 0
				ExpressionUtil.createApply(PMMLFunctions.FLOOR, fieldRef) // x >= 0
			);
		};

		return ensureApply("trunc", OpType.CONTINUOUS, DataType.INTEGER, applyGenerator, Arrays.asList("x"), expressions, encoder);
	}

	static
	public Apply ensureApply(String name, OpType opType, DataType dataType, Function<List<FieldRef>, Apply> applyGenerator, List<String> parameters, List<Expression> expressions, PMMLEncoder encoder){

		if(allFieldRefs(expressions)){
			return applyGenerator.apply((List)expressions);
		}

		Function<List<ParameterField>, Expression> applySupplier = (parameterFields) -> {
			List<FieldRef> fieldRefs = parameterFields.stream()
				.map(parameterField -> new FieldRef(parameterField))
				.collect(Collectors.toList());

			return applyGenerator.apply(fieldRefs);
		};

		DefineFunction defineFunction = ensureDefineFunction(name, opType, dataType, applySupplier, parameters, encoder);

		return ExpressionUtil.createApply(defineFunction, expressions.toArray(new Expression[expressions.size()]));
	}

	static
	public DefineFunction ensureDefineFunction(String name, OpType opType, DataType dataType, Function<List<ParameterField>, ? extends Expression> expressionGenerator, List<String> parameters, PMMLEncoder encoder){
		DefineFunction defineFunction = encoder.getDefineFunction(name);

		if(defineFunction == null){
			defineFunction = new DefineFunction(name, opType, dataType, null, null);

			for(String parameter : parameters){
				ParameterField parameterField = new ParameterField(parameter);

				defineFunction.addParameterFields(parameterField);
			}

			Expression expression = expressionGenerator.apply(defineFunction.getParameterFields());

			defineFunction.setExpression(expression);

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

	static
	private boolean allFieldRefs(List<Expression> expressions){

		for(Expression expression : expressions){

			if(!(expression instanceof FieldRef)){
				return false;
			}
		}

		return true;
	}
}