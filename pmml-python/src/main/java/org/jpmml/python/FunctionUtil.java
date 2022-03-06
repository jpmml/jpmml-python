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
import java.util.stream.Collectors;

import org.dmg.pmml.Expression;
import org.dmg.pmml.PMMLFunctions;
import org.jpmml.converter.PMMLUtil;

public class FunctionUtil {

	private FunctionUtil(){
	}

	static
	public String canonicalizeModule(String module){

		if(("").equals(module)){
			return "builtins";
		} else

		{
			String[] parts = module.split("\\.");

			switch(parts[0]){
				case "np":
					parts[0] = "numpy";
					break;
				case "pd":
					parts[0] = "pandas";
					break;
				case "sp":
					parts[0] = "scipy";
					break;
				default:
					return module;
			}

			return Arrays.stream(parts)
				.collect(Collectors.joining("."));
		}
	}

	static
	public Expression encodeFunction(Identifiable identifiable, List<Expression> expressions){
		return encodeFunction(identifiable.getModule(), identifiable.getName(), expressions);
	}

	static
	public Expression encodeFunction(String module, String name, List<Expression> expressions){

		if((module).equals("builtins")){
			return encodePythonFunction(module, name, expressions);
		} else

		if((module).equals("numpy") || (module).startsWith("numpy.")){
			return encodeNumpyFunction(module, name, expressions);
		} else

		if((module).equals("pandas") || (module).startsWith("pandas.")){
			return encodePandasFunction(module, name, expressions);
		} else

		if((module).equals("scipy") || (module).startsWith("scipy.")){
			return encodeScipyFunction(module, name, expressions);
		}

		throw new IllegalArgumentException("Function \'" + formatFunction(module, name) + "\' is not supported");
	}

	static
	public Expression encodePythonFunction(String module, String name, List<Expression> expressions){

		if((module).equals("builtins")){

			switch(name){
				case "len":
					return PMMLUtil.createApply(PMMLFunctions.STRINGLENGTH, getOnlyElement(expressions));
				default:
					break;
			}
		}

		throw new IllegalArgumentException("Function \'" + formatFunction(module, name) + "\' is not supported");
	}

	static
	public Expression encodeNumpyFunction(String module, String name, List<Expression> expressions){

		// XXX
		if((module).equals("numpy") || (module).startsWith("numpy.")){

			switch(name){
				case "absolute":
					return PMMLUtil.createApply(PMMLFunctions.ABS, getOnlyElement(expressions));
				case "arccos":
					return PMMLUtil.createApply(PMMLFunctions.ACOS, getOnlyElement(expressions));
				case "arcsin":
					return PMMLUtil.createApply(PMMLFunctions.ASIN, getOnlyElement(expressions));
				case "arctan":
					return PMMLUtil.createApply(PMMLFunctions.ATAN, getOnlyElement(expressions));
				case "arctan2":
					return PMMLUtil.createApply(PMMLFunctions.ATAN2, getElement(expressions, 2, 0), getElement(expressions, 2, 1));
				case "ceil":
					return PMMLUtil.createApply(PMMLFunctions.CEIL, getOnlyElement(expressions));
				case "clip":
					return PMMLUtil.createApply(PMMLFunctions.MIN,
						PMMLUtil.createApply(PMMLFunctions.MAX,
							getElement(expressions, 3, 0),
							getElement(expressions, 3, 1)
						),
						getElement(expressions, 3, 2)
					);
				case "cos":
					return PMMLUtil.createApply(PMMLFunctions.COS, getOnlyElement(expressions));
				case "cosh":
					return PMMLUtil.createApply(PMMLFunctions.COSH, getOnlyElement(expressions));
				case "degrees":
				case "rad2deg":
					return PMMLUtil.createApply(PMMLFunctions.MULTIPLY, getOnlyElement(expressions), PMMLUtil.createConstant(180d / Math.PI));
				case "exp":
					return PMMLUtil.createApply(PMMLFunctions.EXP, getOnlyElement(expressions));
				case "expm1":
					return PMMLUtil.createApply(PMMLFunctions.EXPM1, getOnlyElement(expressions));
				case "floor":
					return PMMLUtil.createApply(PMMLFunctions.FLOOR, getOnlyElement(expressions));
				case "fmax":
					return PMMLUtil.createApply(PMMLFunctions.MAX, getElement(expressions, 2, 0), getElement(expressions, 2, 1));
				case "fmin":
					return PMMLUtil.createApply(PMMLFunctions.MIN, getElement(expressions, 2, 0), getElement(expressions, 2, 1));
				case "hypot":
					return PMMLUtil.createApply(PMMLFunctions.HYPOT, getOnlyElement(expressions));
				case "isnan":
					return PMMLUtil.createApply(PMMLFunctions.ISMISSING, getOnlyElement(expressions));
				case "log":
					return PMMLUtil.createApply(PMMLFunctions.LN, getOnlyElement(expressions));
				case "logical_and":
					return PMMLUtil.createApply(PMMLFunctions.AND, getElement(expressions, 2, 0), getElement(expressions, 2, 1));
				case "logical_not":
					return PMMLUtil.createApply(PMMLFunctions.NOT, getOnlyElement(expressions));
				case "logical_or":
					return PMMLUtil.createApply(PMMLFunctions.OR, getElement(expressions, 2, 0), getElement(expressions, 2, 1));
				case "log1p":
					return PMMLUtil.createApply(PMMLFunctions.LN1P, getOnlyElement(expressions));
				case "log10":
					return PMMLUtil.createApply(PMMLFunctions.LOG10, getOnlyElement(expressions));
				case "negative":
					return PMMLUtil.createApply(PMMLFunctions.MULTIPLY, PMMLUtil.createConstant(-1), getOnlyElement(expressions));
				case "power":
					return PMMLUtil.createApply(PMMLFunctions.POW, getElement(expressions, 2, 0), getElement(expressions, 2, 1));
				case "radians":
				case "deg2rad":
					return PMMLUtil.createApply(PMMLFunctions.MULTIPLY, getOnlyElement(expressions), PMMLUtil.createConstant(Math.PI / 180d));
				case "reciprocal":
					return PMMLUtil.createApply(PMMLFunctions.DIVIDE, PMMLUtil.createConstant(1), getOnlyElement(expressions));
				case "rint":
					return PMMLUtil.createApply(PMMLFunctions.RINT, getOnlyElement(expressions));
				case "sign":
					return PMMLUtil.createApply(PMMLFunctions.IF, PMMLUtil.createApply(PMMLFunctions.LESSTHAN, getOnlyElement(expressions), PMMLUtil.createConstant(0)),
						PMMLUtil.createConstant(-1), // x < 0
						PMMLUtil.createApply(PMMLFunctions.IF, PMMLUtil.createApply(PMMLFunctions.GREATERTHAN, getOnlyElement(expressions), PMMLUtil.createConstant(0)),
							PMMLUtil.createConstant(+1), // x > 0
							PMMLUtil.createConstant(0) // x == 0
						)
					);
				case "sin":
					return PMMLUtil.createApply(PMMLFunctions.SIN, getOnlyElement(expressions));
				case "sinh":
					return PMMLUtil.createApply(PMMLFunctions.SINH, getOnlyElement(expressions));
				case "sqrt":
					return PMMLUtil.createApply(PMMLFunctions.SQRT, getOnlyElement(expressions));
				case "square":
					return PMMLUtil.createApply(PMMLFunctions.POW, getOnlyElement(expressions), PMMLUtil.createConstant(2));
				case "tan":
					return PMMLUtil.createApply(PMMLFunctions.TAN, getOnlyElement(expressions));
				case "tanh":
					return PMMLUtil.createApply(PMMLFunctions.TANH, getOnlyElement(expressions));
				case "where":
					return PMMLUtil.createApply(PMMLFunctions.IF, getElement(expressions, 3, 0), getElement(expressions, 3, 1), getElement(expressions, 3, 2));
				default:
					break;
			}
		}

		throw new IllegalArgumentException("Function \'" + formatFunction(module, name) + "\' is not supported");
	}

	static
	public Expression encodePandasFunction(String module, String name, List<Expression> expressions){

		if((module).equals("pandas")){

			switch(name){
				case "isnull":
					return PMMLUtil.createApply(PMMLFunctions.ISMISSING, getOnlyElement(expressions));
				case "notnull":
					return PMMLUtil.createApply(PMMLFunctions.ISNOTMISSING, getOnlyElement(expressions));
				default:
					break;
			}
		}

		throw new IllegalArgumentException("Function \'" + formatFunction(module, name) + "\' is not supported");
	}

	static
	public Expression encodeScipyFunction(String module, String name, List<Expression> expressions){

		if((module).equals("scipy.special")){

			switch(name){
				case "expit":
					return PMMLUtil.createApply(PMMLFunctions.DIVIDE,
						PMMLUtil.createConstant(1),
						PMMLUtil.createApply(PMMLFunctions.ADD,
							PMMLUtil.createConstant(1),
							PMMLUtil.createApply(PMMLFunctions.EXP, PMMLUtil.createApply(PMMLFunctions.MULTIPLY, PMMLUtil.createConstant(-1), getOnlyElement(expressions)))
						)
					);
				case "logit":
					return PMMLUtil.createApply(PMMLFunctions.LN,
						PMMLUtil.createApply(PMMLFunctions.DIVIDE,
							getOnlyElement(expressions),
							PMMLUtil.createApply(PMMLFunctions.SUBTRACT, PMMLUtil.createConstant(1), getOnlyElement(expressions))
						)
					);
				default:
					break;
			}
		}

		throw new IllegalArgumentException("Function \'" + formatFunction(module, name) + "\' is not supported");
	}

	static
	private String formatFunction(String module, String name){
		return module + "." + name;
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