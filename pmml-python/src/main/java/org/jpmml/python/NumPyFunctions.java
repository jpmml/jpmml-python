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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.Apply;
import org.dmg.pmml.Expression;
import org.dmg.pmml.PMMLFunctions;
import org.jpmml.converter.ExpressionUtil;
import org.jpmml.converter.PMMLEncoder;

public interface NumPyFunctions extends Functions {

	PythonFunction ABSOLUTE = new UnaryFunction(PMMLFunctions.ABS);

	PythonFunction ARCCOS = new UnaryFunction(PMMLFunctions.ACOS);

	PythonFunction ARCSIN = new UnaryFunction(PMMLFunctions.ASIN);

	PythonFunction ARCTAN = new UnaryFunction(PMMLFunctions.ATAN);

	PythonFunction ARCTAN2 = new BinaryFunction(PMMLFunctions.ATAN2);

	PythonFunction CEIL = new UnaryFunction(PMMLFunctions.CEIL);

	PythonFunction CLIP = new PythonFunction(){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("x", "min", "max");
		}

		@Override
		public Apply encode(List<Expression> expressions, PMMLEncoder encoder){
			return ExpressionUtil.createApply(PMMLFunctions.MIN,
				ExpressionUtil.createApply(PMMLFunctions.MAX,
					expressions.get(0),
					expressions.get(1)
				),
				expressions.get(2)
			);
		}
	};

	PythonFunction COS = new UnaryFunction(PMMLFunctions.COS);

	PythonFunction COSH = new UnaryFunction(PMMLFunctions.COSH);

	PythonFunction DEG2RAD = new PythonFunction(){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("x");
		}

		@Override
		public Apply encode(List<Expression> expressions, PMMLEncoder encoder){
			return ExpressionUtil.createApply(PMMLFunctions.MULTIPLY, expressions.get(0), ExpressionUtil.createConstant(Math.PI / 180d));
		}
	};

	PythonFunction EXP = new UnaryFunction(PMMLFunctions.EXP);

	PythonFunction EXPM1 = new UnaryFunction(PMMLFunctions.EXPM1);

	PythonFunction FLOOR = new UnaryFunction(PMMLFunctions.FLOOR);

	PythonFunction FMAX = new BinaryFunction(PMMLFunctions.MAX);

	PythonFunction FMIN = new BinaryFunction(PMMLFunctions.MIN);

	PythonFunction HYPOT = new UnaryFunction(PMMLFunctions.HYPOT);

	PythonFunction ISNAN = new UnaryFunction(PMMLFunctions.ISMISSING);

	PythonFunction LOG = new UnaryFunction(PMMLFunctions.LN);

	PythonFunction LOG1P = new UnaryFunction(PMMLFunctions.LN1P);

	PythonFunction LOG10 = new UnaryFunction(PMMLFunctions.LOG10);

	PythonFunction LOGICALAND = new BinaryFunction(PMMLFunctions.AND);

	PythonFunction LOGICALNOT = new UnaryFunction(PMMLFunctions.NOT);

	PythonFunction LOGICALOR = new BinaryFunction(PMMLFunctions.OR);

	PythonFunction NEGATIVE = new PythonFunction(){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("x");
		}

		@Override
		public Apply encode(List<Expression> expressions, PMMLEncoder encoder){
			return ExpressionUtil.createApply(PMMLFunctions.MULTIPLY, ExpressionUtil.createConstant(-1), expressions.get(0));
		}
	};

	PythonFunction POWER = new BinaryFunction(PMMLFunctions.POW);

	PythonFunction RAD2DEG = new PythonFunction(){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("x");
		}

		@Override
		public Apply encode(List<Expression> expressions, PMMLEncoder encoder){
			return ExpressionUtil.createApply(PMMLFunctions.MULTIPLY, expressions.get(0), ExpressionUtil.createConstant(180d / Math.PI));
		}
	};

	PythonFunction RECIPROCAL = new PythonFunction(){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("x");
		}

		@Override
		public Apply encode(List<Expression> expressions, PMMLEncoder encoder){
			return ExpressionUtil.createApply(PMMLFunctions.DIVIDE, ExpressionUtil.createConstant(1), expressions.get(0));
		}
	};

	PythonFunction RINT = new UnaryFunction(PMMLFunctions.RINT);

	PythonFunction SIGN = new PythonFunction(){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("x");
		}

		@Override
		public Apply encode(List<Expression> expressions, PMMLEncoder encoder){
			return FunctionUtil.sign(expressions, encoder);
		}
	};

	PythonFunction SIN = new UnaryFunction(PMMLFunctions.SIN);

	PythonFunction SINH = new UnaryFunction(PMMLFunctions.SINH);

	PythonFunction SQRT = new UnaryFunction(PMMLFunctions.SQRT);

	PythonFunction SQUARE = new PythonFunction(){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("x");
		}

		@Override
		public Apply encode(List<Expression> expressions, PMMLEncoder encoder){
			return ExpressionUtil.createApply(PMMLFunctions.POW, expressions.get(0), ExpressionUtil.createConstant(2));
		}
	};

	PythonFunction TAN = new UnaryFunction(PMMLFunctions.TAN);

	PythonFunction TANH = new UnaryFunction(PMMLFunctions.TANH);

	PythonFunction WHERE = new PythonFunction(){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("condition", "x1", "x2");
		}

		@Override
		public Apply encode(List<Expression> expressions, PMMLEncoder encoder){
			return ExpressionUtil.createApply(PMMLFunctions.IF, expressions.get(0),
				expressions.get(1),
				expressions.get(2)
			);
		}
	};

	Map<String, PythonFunction> REGISTRY = Map.ofEntries(
		Map.entry("absolute", NumPyFunctions.ABSOLUTE),
		Map.entry("arccos", NumPyFunctions.ARCCOS),
		Map.entry("arcsin", NumPyFunctions.ARCSIN),
		Map.entry("arctan", NumPyFunctions.ARCTAN),
		Map.entry("arctan2", NumPyFunctions.ARCTAN2),
		Map.entry("ceil", NumPyFunctions.CEIL),
		Map.entry("clip", NumPyFunctions.CLIP),
		Map.entry("cos", NumPyFunctions.COS),
		Map.entry("cosh", NumPyFunctions.COSH),
		Map.entry("degrees", NumPyFunctions.RAD2DEG),
		Map.entry("deg2rad", NumPyFunctions.DEG2RAD),
		Map.entry("exp", NumPyFunctions.EXP),
		Map.entry("expm1", NumPyFunctions.EXPM1),
		Map.entry("floor", NumPyFunctions.FLOOR),
		Map.entry("fmax", NumPyFunctions.FMAX),
		Map.entry("fmin", NumPyFunctions.FMIN),
		Map.entry("hypot", NumPyFunctions.HYPOT),
		Map.entry("isnan", NumPyFunctions.ISNAN),
		Map.entry("log", NumPyFunctions.LOG),
		Map.entry("log1p", NumPyFunctions.LOG1P),
		Map.entry("log10", NumPyFunctions.LOG10),
		Map.entry("logical_and", NumPyFunctions.LOGICALAND),
		Map.entry("logical_not", NumPyFunctions.LOGICALNOT),
		Map.entry("logical_or", NumPyFunctions.LOGICALOR),
		Map.entry("negative", NumPyFunctions.NEGATIVE),
		Map.entry("power", NumPyFunctions.POWER),
		Map.entry("radians", NumPyFunctions.DEG2RAD),
		Map.entry("rad2deg", NumPyFunctions.RAD2DEG),
		Map.entry("reciprocal", NumPyFunctions.RECIPROCAL),
		Map.entry("rint", NumPyFunctions.RINT),
		Map.entry("sign", NumPyFunctions.SIGN),
		Map.entry("sin", NumPyFunctions.SIN),
		Map.entry("sinh", NumPyFunctions.SINH),
		Map.entry("sqrt", NumPyFunctions.SQRT),
		Map.entry("square", NumPyFunctions.SQUARE),
		Map.entry("tan", NumPyFunctions.TAN),
		Map.entry("tanh", NumPyFunctions.TANH),
		Map.entry("where", NumPyFunctions.WHERE)
	);
}