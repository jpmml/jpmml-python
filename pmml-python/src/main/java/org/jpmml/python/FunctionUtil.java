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

public class FunctionUtil {

	private FunctionUtil(){
	}

	static
	public Apply encodeFunction(Identifiable identifiable, List<Expression> expressions, PMMLEncoder encoder){
		return encodeFunction(identifiable.getModule(), identifiable.getName(), expressions, null);
	}

	static
	public Apply encodeFunction(String module, String name, List<Expression> expressions, PMMLEncoder encoder){

		if((module).equals("builtins")){
			return encodeBuiltinFunction(module, name, expressions, encoder);
		} else

		if((module).equals("math")){
			return encodeMathFunction(module, name, expressions, encoder);
		} else

		if((module).equals("pcre") || (module).equals("pcre2") || (module).equals("re")){
			return encodeRegExFunction(module, name, expressions, encoder);
		} else

		if((module).equals("numpy") || (module).startsWith("numpy.")){
			return encodeNumpyFunction(module, name, expressions, encoder);
		} else

		if((module).equals("pandas") || (module).startsWith("pandas.")){
			return encodePandasFunction(module, name, expressions, encoder);
		} else

		if((module).equals("scipy") || (module).startsWith("scipy.")){
			return encodeScipyFunction(module, name, expressions, encoder);
		}

		throw new TranslationException("Function \'" + formatFunction(module, name) + "\' is not supported");
	}

	static
	public Apply encodeBuiltinFunction(String module, String name, List<Expression> expressions, PMMLEncoder encoder){

		if((module).equals("builtins")){

			switch(name){
				case "float":
					return tofloat(expressions, encoder);
				case "len":
					return encodeUnaryFunction(PMMLFunctions.STRINGLENGTH, expressions);
				case "str":
					return tostr(expressions, encoder);
				default:
					break;
			}
		}

		throw new TranslationException("Function \'" + formatFunction(module, name) + "\' is not supported");
	}

	static
	public Apply encodeMathFunction(String module, String name, List<Expression> expressions, PMMLEncoder encoder){

		if((module).equals("math")){

			switch(name){
				case "acos":
					return encodeUnaryFunction(PMMLFunctions.ACOS, expressions);
				case "asin":
					return encodeUnaryFunction(PMMLFunctions.ASIN, expressions);
				case "atan":
					return encodeUnaryFunction(PMMLFunctions.ATAN, expressions);
				case "atan2":
					return encodeBinaryFunction(PMMLFunctions.ATAN2, expressions);
				case "ceil":
					return encodeUnaryFunction(PMMLFunctions.CEIL, expressions);
				case "cos":
					return encodeUnaryFunction(PMMLFunctions.COS, expressions);
				case "cosh":
					return encodeUnaryFunction(PMMLFunctions.COSH, expressions);
				case "degrees":
					return rad2deg(expressions);
				case "exp":
					return encodeUnaryFunction(PMMLFunctions.EXP, expressions);
				case "expm1":
					return encodeUnaryFunction(PMMLFunctions.EXPM1, expressions);
				case "fabs":
					return encodeUnaryFunction(PMMLFunctions.ABS, expressions);
				case "floor":
					return encodeUnaryFunction(PMMLFunctions.FLOOR, expressions);
				case "hypot":
					return encodeUnaryFunction(PMMLFunctions.HYPOT, expressions);
				case "isnan":
					return encodeUnaryFunction(PMMLFunctions.ISMISSING, expressions);
				case "log":
					return encodeUnaryFunction(PMMLFunctions.LN, expressions);
				case "logp1":
					return encodeUnaryFunction(PMMLFunctions.LN1P, expressions);
				case "log10":
					return encodeUnaryFunction(PMMLFunctions.LOG10, expressions);
				case "pow":
					return encodeBinaryFunction(PMMLFunctions.POW, expressions);
				case "radians":
					return deg2rad(expressions);
				case "sin":
					return encodeUnaryFunction(PMMLFunctions.SIN, expressions);
				case "sinh":
					return encodeUnaryFunction(PMMLFunctions.SINH, expressions);
				case "sqrt":
					return encodeUnaryFunction(PMMLFunctions.SQRT, expressions);
				case "tan":
					return encodeUnaryFunction(PMMLFunctions.TAN, expressions);
				case "tanh":
					return encodeUnaryFunction(PMMLFunctions.TANH, expressions);
				case "trunc":
					return trunc(expressions, encoder);
				default:
					break;
			}
		}

		throw new TranslationException("Function \'" + formatFunction(module, name) +"\' is not supported");
	}

	static
	public Apply encodeRegExFunction(String module, String name, List<Expression> expressions, PMMLEncoder encoder){

		if((module).equals("pcre")){

			switch(name){
				case "search":
					return search(expressions, RegExFlavour.PCRE);
				case "sub":
					return sub(expressions, RegExFlavour.PCRE);
				default:
					break;
			}
		} else

		if((module).equals("pcre2")){

			switch(name){
				case "substitute":
					return sub(expressions, RegExFlavour.PCRE2);
				default:
					break;
			}
		} else

		if((module).equals("re")){

			switch(name){
				case "search":
					return search(expressions, RegExFlavour.RE);
				case "sub":
					return sub(expressions, RegExFlavour.RE);
				default:
					break;
			}
		}

		throw new TranslationException("Function \'" + formatFunction(module, name) +"\' is not supported");
	}

	static
	public Apply encodeNumpyFunction(String module, String name, List<Expression> expressions, PMMLEncoder encoder){

		// XXX
		if((module).equals("numpy") || (module).startsWith("numpy.")){

			switch(name){
				case "absolute":
					return encodeUnaryFunction(PMMLFunctions.ABS, expressions);
				case "arccos":
					return encodeUnaryFunction(PMMLFunctions.ACOS, expressions);
				case "arcsin":
					return encodeUnaryFunction(PMMLFunctions.ASIN, expressions);
				case "arctan":
					return encodeUnaryFunction(PMMLFunctions.ATAN, expressions);
				case "arctan2":
					return encodeBinaryFunction(PMMLFunctions.ATAN2, expressions);
				case "ceil":
					return encodeUnaryFunction(PMMLFunctions.CEIL, expressions);
				case "clip":
					return clip(expressions);
				case "cos":
					return encodeUnaryFunction(PMMLFunctions.COS, expressions);
				case "cosh":
					return encodeUnaryFunction(PMMLFunctions.COSH, expressions);
				case "degrees":
				case "rad2deg":
					return rad2deg(expressions);
				case "exp":
					return encodeUnaryFunction(PMMLFunctions.EXP, expressions);
				case "expm1":
					return encodeUnaryFunction(PMMLFunctions.EXPM1, expressions);
				case "floor":
					return encodeUnaryFunction(PMMLFunctions.FLOOR, expressions);
				case "fmax":
					return encodeBinaryFunction(PMMLFunctions.MAX, expressions);
				case "fmin":
					return encodeBinaryFunction(PMMLFunctions.MIN, expressions);
				case "hypot":
					return encodeUnaryFunction(PMMLFunctions.HYPOT, expressions);
				case "isnan":
					return encodeUnaryFunction(PMMLFunctions.ISMISSING, expressions);
				case "log":
					return encodeUnaryFunction(PMMLFunctions.LN, expressions);
				case "logical_and":
					return encodeBinaryFunction(PMMLFunctions.AND, expressions);
				case "logical_not":
					return encodeUnaryFunction(PMMLFunctions.NOT, expressions);
				case "logical_or":
					return encodeBinaryFunction(PMMLFunctions.OR, expressions);
				case "log1p":
					return encodeUnaryFunction(PMMLFunctions.LN1P, expressions);
				case "log10":
					return encodeUnaryFunction(PMMLFunctions.LOG10, expressions);
				case "negative":
					return negative(expressions);
				case "power":
					return encodeBinaryFunction(PMMLFunctions.POW, expressions);
				case "radians":
				case "deg2rad":
					return deg2rad(expressions);
				case "reciprocal":
					return reciprocal(expressions);
				case "rint":
					return encodeUnaryFunction(PMMLFunctions.RINT, expressions);
				case "sign":
					return sign(expressions, encoder);
				case "sin":
					return encodeUnaryFunction(PMMLFunctions.SIN, expressions);
				case "sinh":
					return encodeUnaryFunction(PMMLFunctions.SINH, expressions);
				case "sqrt":
					return encodeUnaryFunction(PMMLFunctions.SQRT, expressions);
				case "square":
					return square(expressions);
				case "tan":
					return encodeUnaryFunction(PMMLFunctions.TAN, expressions);
				case "tanh":
					return encodeUnaryFunction(PMMLFunctions.TANH, expressions);
				case "where":
					return where(expressions);
				default:
					break;
			}
		}

		throw new TranslationException("Function \'" + formatFunction(module, name) + "\' is not supported");
	}

	static
	public Apply encodePandasFunction(String module, String name, List<Expression> expressions, PMMLEncoder encoder){

		if((module).equals("pandas")){

			switch(name){
				case "isna":
				case "isnull":
					return encodeUnaryFunction(PMMLFunctions.ISMISSING, expressions);
				case "notna":
				case "notnull":
					return encodeUnaryFunction(PMMLFunctions.ISNOTMISSING, expressions);
				default:
					break;
			}
		}

		throw new TranslationException("Function \'" + formatFunction(module, name) + "\' is not supported");
	}

	static
	public Apply encodeScipyFunction(String module, String name, List<Expression> expressions, PMMLEncoder encoder){

		if((module).equals("scipy.special")){

			switch(name){
				case "expit":
					return expit(expressions);
				case "logit":
					return logit(expressions, encoder);
				default:
					break;
			}
		}

		throw new TranslationException("Function \'" + formatFunction(module, name) + "\' is not supported");
	}

	static
	public Apply encodeUnaryFunction(String function, List<Expression> expressions){
		return ExpressionUtil.createApply(function, getElement(expressions, 1, 0));
	}

	static
	public Apply encodeBinaryFunction(String function, List<Expression> expressions){
		return ExpressionUtil.createApply(function, getElement(expressions, 2, 0), getElement(expressions, 2, 1));
	}

	static
	private Apply clip(List<Expression> expressions){
		return ExpressionUtil.createApply(PMMLFunctions.MIN,
			ExpressionUtil.createApply(PMMLFunctions.MAX,
				getElement(expressions, 3, 0),
				getElement(expressions, 3, 1)
			),
			getElement(expressions, 3, 2)
		);
	}

	static
	private Apply deg2rad(List<Expression> expressions){
		return ExpressionUtil.createApply(PMMLFunctions.MULTIPLY, getOnlyElement(expressions), ExpressionUtil.createConstant(Math.PI / 180d));
	}

	static
	private Apply expit(List<Expression> expressions){
		return ExpressionUtil.createApply(PMMLFunctions.DIVIDE,
			ExpressionUtil.createConstant(1),
			ExpressionUtil.createApply(PMMLFunctions.ADD,
				ExpressionUtil.createConstant(1),
				ExpressionUtil.createApply(PMMLFunctions.EXP, ExpressionUtil.createApply(PMMLFunctions.MULTIPLY, ExpressionUtil.createConstant(-1), getOnlyElement(expressions)))
			)
		);
	}

	static
	private Apply logit(List<Expression> expressions, PMMLEncoder encoder){
		Function<Expression, Apply> applyGenerator = (expression) -> {
			return ExpressionUtil.createApply(PMMLFunctions.LN,
				ExpressionUtil.createApply(PMMLFunctions.DIVIDE,
					expression,
					ExpressionUtil.createApply(PMMLFunctions.SUBTRACT, ExpressionUtil.createConstant(1), expression)
				)
			);
		};

		return ensureApply("logit", OpType.CONTINUOUS, DataType.DOUBLE, getOnlyElement(expressions), applyGenerator, encoder);
	}

	static
	private Apply negative(List<Expression> expressions){
		return ExpressionUtil.createApply(PMMLFunctions.MULTIPLY, ExpressionUtil.createConstant(-1), getOnlyElement(expressions));
	}

	static
	private Apply rad2deg(List<Expression> expressions){
		return ExpressionUtil.createApply(PMMLFunctions.MULTIPLY, getOnlyElement(expressions), ExpressionUtil.createConstant(180d / Math.PI));
	}

	static
	private Apply reciprocal(List<Expression> expressions){
		return ExpressionUtil.createApply(PMMLFunctions.DIVIDE, ExpressionUtil.createConstant(1), getOnlyElement(expressions));
	}

	static
	private Apply search(List<Expression> expressions, RegExFlavour reFlavour){
		return ExpressionUtil.createApply(PMMLFunctions.MATCHES,
			getElement(expressions, 2, 1),
			updateConstant(getElement(expressions, 2, 0), reFlavour::translatePattern)
		)
			.addExtensions(reFlavour.createExtension());
	}

	static
	private Apply sign(List<Expression> expressions, PMMLEncoder encoder){
		Function<Expression, Apply> applyGenerator = (expression) -> {
			return ExpressionUtil.createApply(PMMLFunctions.IF, ExpressionUtil.createApply(PMMLFunctions.LESSTHAN, expression, ExpressionUtil.createConstant(0)),
				ExpressionUtil.createConstant(-1), // x < 0
				ExpressionUtil.createApply(PMMLFunctions.IF, ExpressionUtil.createApply(PMMLFunctions.GREATERTHAN, expression, ExpressionUtil.createConstant(0)),
					ExpressionUtil.createConstant(+1), // x > 0
					ExpressionUtil.createConstant(0) // x == 0
				)
			);
		};

		return ensureApply("sign", OpType.CATEGORICAL, DataType.INTEGER, getOnlyElement(expressions), applyGenerator, encoder);
	}

	static
	private Apply square(List<Expression> expressions){
		return ExpressionUtil.createApply(PMMLFunctions.POW, getOnlyElement(expressions), ExpressionUtil.createConstant(2));
	}

	static
	private Apply sub(List<Expression> expressions, RegExFlavour reFlavour){
		return ExpressionUtil.createApply(PMMLFunctions.REPLACE,
			getElement(expressions, 3, 2),
			updateConstant(getElement(expressions, 3, 0), reFlavour::translatePattern),
			updateConstant(getElement(expressions, 3, 1), reFlavour::translateReplacement)
		)
			.addExtensions(reFlavour.createExtension());
	}

	static
	private Apply tofloat(List<Expression> expressions, PMMLEncoder encoder){
		Function<ParameterField, FieldRef> expressionGenerator = (valueField) -> {
			return new FieldRef(valueField);
		};

		DefineFunction defineFunction = ensureDefineFunction("float", OpType.CONTINUOUS, DataType.DOUBLE, expressionGenerator, encoder);

		return ExpressionUtil.createApply(defineFunction, getOnlyElement(expressions));
	}

	static
	private Apply tostr(List<Expression> expressions, PMMLEncoder encoder){
		Function<ParameterField, FieldRef> expressionGenerator = (valueField) -> {
			return new FieldRef(valueField);
		};

		DefineFunction defineFunction = ensureDefineFunction("str", OpType.CATEGORICAL, DataType.STRING, expressionGenerator, encoder);

		return ExpressionUtil.createApply(defineFunction, getOnlyElement(expressions));
	}

	static
	private Apply trunc(List<Expression> expressions, PMMLEncoder encoder){
		Function<Expression, Apply> applyGenerator = (expression) -> {
			return ExpressionUtil.createApply(PMMLFunctions.IF, ExpressionUtil.createApply(PMMLFunctions.LESSTHAN, expression, ExpressionUtil.createConstant(0)),
				ExpressionUtil.createApply(PMMLFunctions.CEIL, expression), // x < 0
				ExpressionUtil.createApply(PMMLFunctions.FLOOR, expression) // x >= 0
			);
		};

		return ensureApply("trunc", OpType.CONTINUOUS, DataType.INTEGER, getOnlyElement(expressions), applyGenerator, encoder);
	}

	static
	private Apply where(List<Expression> expressions){
		return ExpressionUtil.createApply(PMMLFunctions.IF, getElement(expressions, 3, 0),
			getElement(expressions, 3, 1),
			getElement(expressions, 3, 2)
		);
	}

	static
	private Apply ensureApply(String name, OpType opType, DataType dataType, Expression expression, Function<Expression, Apply> applyGenerator, PMMLEncoder encoder){

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
	private DefineFunction ensureDefineFunction(String name, OpType opType, DataType dataType, Function<ParameterField, ? extends Expression> expressionGenerator, PMMLEncoder encoder){
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
	private String formatFunction(String module, String name){
		return module + "." + name;
	}

	static
	private Expression updateConstant(Expression expression, Function<String, String> function){

		if(expression instanceof Constant){
			Constant constant = (Constant)expression;

			constant.setValue(function.apply((String)constant.getValue()));

			return constant;
		}

		return expression;
	}

	static
	private Expression getOnlyElement(List<Expression> expressions){
		ClassDictUtil.checkSize(1, expressions);

		return expressions.get(0);
	}

	static
	private Expression getElement(List<Expression> expressions, int expectedSize, int index){
		ClassDictUtil.checkSize(expectedSize, expressions);

		return expressions.get(index);
	}
}