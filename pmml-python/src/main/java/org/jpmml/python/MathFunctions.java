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

public interface MathFunctions extends Functions {

	PythonFunction ACOS = new UnaryFunction(PMMLFunctions.ACOS);

	PythonFunction ASIN = new UnaryFunction(PMMLFunctions.ASIN);

	PythonFunction ATAN = new UnaryFunction(PMMLFunctions.ATAN);

	PythonFunction ATAN2 = new BinaryFunction(PMMLFunctions.ATAN2){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("y", "x");
		}
	};

	PythonFunction CEIL = new UnaryFunction(PMMLFunctions.CEIL);

	PythonFunction COS = new UnaryFunction(PMMLFunctions.COS);

	PythonFunction COSH = new UnaryFunction(PMMLFunctions.COSH);

	PythonFunction DEGREES = new PythonFunction(){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("x");
		}

		@Override
		public Apply encode(List<Expression> expressions, PMMLEncoder encoder){
			return ExpressionUtil.createApply(PMMLFunctions.MULTIPLY, expressions.get(0), ExpressionUtil.createConstant(180d / Math.PI));
		}
	};

	PythonFunction EXP = new UnaryFunction(PMMLFunctions.EXP);

	PythonFunction EXPM1 = new UnaryFunction(PMMLFunctions.EXPM1);

	PythonFunction FABS = new UnaryFunction(PMMLFunctions.ABS);

	PythonFunction FLOOR = new UnaryFunction(PMMLFunctions.FLOOR);

	PythonFunction HYPOT = new UnaryFunction(PMMLFunctions.HYPOT);

	PythonFunction ISNAN = new UnaryFunction(PMMLFunctions.ISMISSING);

	PythonFunction LOG = new UnaryFunction(PMMLFunctions.LN);

	PythonFunction LOG1P = new UnaryFunction(PMMLFunctions.LN1P);

	PythonFunction LOG10 = new UnaryFunction(PMMLFunctions.LOG10);

	PythonFunction POW = new BinaryFunction(PMMLFunctions.POW){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("x", "y");
		}
	};

	PythonFunction RADIANS = new PythonFunction(){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("x");
		}

		@Override
		public Apply encode(List<Expression> expressions, PMMLEncoder encoder){
			return ExpressionUtil.createApply(PMMLFunctions.MULTIPLY, expressions.get(0), ExpressionUtil.createConstant(Math.PI / 180d));
		}
	};

	PythonFunction SIN = new UnaryFunction(PMMLFunctions.SIN);

	PythonFunction SINH = new UnaryFunction(PMMLFunctions.SINH);

	PythonFunction SQRT = new UnaryFunction(PMMLFunctions.SQRT);

	PythonFunction TAN = new UnaryFunction(PMMLFunctions.TAN);

	PythonFunction TANH = new UnaryFunction(PMMLFunctions.TANH);

	PythonFunction TRUNC = new PythonFunction(){

		@Override
		public List<String> getParameters(){
			return Arrays.asList("x");
		}

		@Override
		public Apply encode(List<Expression> expressions, PMMLEncoder encoder){
			return FunctionUtil.trunc(expressions, encoder);
		}
	};

	Map<String, PythonFunction> REGISTRY = Map.ofEntries(
		Map.entry("acos", MathFunctions.ACOS),
		Map.entry("asin", MathFunctions.ASIN),
		Map.entry("atan", MathFunctions.ATAN),
		Map.entry("atan2", MathFunctions.ATAN2),
		Map.entry("ceil", MathFunctions.CEIL),
		Map.entry("cos", MathFunctions.COS),
		Map.entry("cosh", MathFunctions.COSH),
		Map.entry("degrees", MathFunctions.DEGREES),
		Map.entry("exp", MathFunctions.EXP),
		Map.entry("expm1", MathFunctions.EXPM1),
		Map.entry("fabs", MathFunctions.FABS),
		Map.entry("floor", MathFunctions.FLOOR),
		Map.entry("hypot", MathFunctions.HYPOT),
		Map.entry("isnan", MathFunctions.ISNAN),
		Map.entry("log", MathFunctions.LOG),
		Map.entry("log1p", MathFunctions.LOG1P),
		Map.entry("log10", MathFunctions.LOG10),
		Map.entry("pow", MathFunctions.POW),
		Map.entry("radians", MathFunctions.RADIANS),
		Map.entry("sin", MathFunctions.SIN),
		Map.entry("sinh", MathFunctions.SINH),
		Map.entry("sqrt", MathFunctions.SQRT),
		Map.entry("tan", MathFunctions.TAN),
		Map.entry("tanh", MathFunctions.TANH),
		Map.entry("trunc", MathFunctions.TRUNC)
	);
}