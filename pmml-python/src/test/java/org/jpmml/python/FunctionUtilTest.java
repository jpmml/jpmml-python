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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.OpType;
import org.jpmml.converter.PMMLEncoder;
import org.jpmml.converter.TypeUtil;
import org.jpmml.evaluator.EvaluationException;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.FieldValueUtil;
import org.jpmml.evaluator.VirtualEvaluationContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class FunctionUtilTest {

	@Test
	public void evaluateBuiltinFunction(){
		assertEquals(0.0d, evaluateExpression("builtins", "float", 0));
		assertEquals(3.14d, evaluateExpression("builtins", "float", "3.14"));
		assertEquals(0.0d, evaluateExpression("builtins", "float", false));
		assertEquals(1.0d, evaluateExpression("builtins", "float", true));

		assertEquals(-3, evaluateExpression("builtins", "int", -3.14d));
		assertEquals(3, evaluateExpression("builtins", "int", 3.14d));
		assertEquals(0, evaluateExpression("builtins", "int", "0"));
		assertEquals(0, evaluateExpression("builtins", "int", false));
		assertEquals(1, evaluateExpression("builtins", "int", true));

		assertEquals("0", evaluateExpression("builtins", "str", 0));
		assertEquals("3.14", evaluateExpression("builtins", "str", 3.14d));
	}

	@Test
	public void evaluateMathFunction(){
		assertEquals(-2, evaluateExpression("math", "trunc", -2.75d));
		assertEquals(2, evaluateExpression("math", "trunc", 2.75d));
	}

	@Test
	public void evaluatePCREFunction(){
		Map<String, String> arguments = new LinkedHashMap<>();
		arguments.put("pattern", "ar?y");

		assertEquals(true, evaluateExpression("pcre", "search", withString(arguments, "January")));
		assertEquals(true, evaluateExpression("pcre", "search", withString(arguments, "February")));
		assertEquals(false, evaluateExpression("pcre", "search", withString(arguments, "March")));

		assertEquals(false, evaluateExpression("re", "search", withString(arguments, "April")));
		assertEquals(true, evaluateExpression("re", "search", withString(arguments, "May")));
		assertEquals(false, evaluateExpression("re", "search", withString(arguments, "June")));

		arguments = new LinkedHashMap<>();
		arguments.put("pattern", "B+");
		arguments.put("repl", "c");

		assertEquals("AcA", evaluateExpression("pcre", "sub", withString(arguments, "ABBA")));

		assertEquals("AcA", evaluateExpression("pcre2", "substitute", withString(arguments, "ABBA")));

		assertEquals("AcA", evaluateExpression("re", "sub", withString(arguments, "ABBA")));
	}

	@Test
	public void evaluateNumpyFunction(){
		assertEquals(3d, evaluateExpression("numpy", "absolute", -3d));

		assertEquals(-2, evaluateExpression("numpy", "ceil", -2.75d));
		assertEquals(3, evaluateExpression("numpy", "ceil", 2.75d));

		assertEquals(-3, evaluateExpression("numpy", "floor", -2.75d));
		assertEquals(2, evaluateExpression("numpy", "floor", 2.75d));

		assertEquals(-3, evaluateExpression("numpy", "negative", 3));
		assertEquals(-3f, evaluateExpression("numpy", "negative", 3f));
		assertEquals(-3d, evaluateExpression("numpy", "negative", 3d));

		assertEquals(1f / 3f, (Float)evaluateExpression("numpy", "reciprocal", 3f), 1e-5);
		assertEquals(1d / 3d, (Double)evaluateExpression("numpy", "reciprocal", 3d), 1e-8);

		assertEquals(-1, evaluateExpression("numpy", "sign", -3d));
		assertEquals(0, evaluateExpression("numpy", "sign", 0d));
		assertEquals(+1, evaluateExpression("numpy", "sign", +3d));
	}

	@Test
	public void evaluateScipyFunction(){
		assertEquals(0.18242552d, (Double)evaluateExpression("scipy.special", "expit", -1.5d), 1e-8);
		assertEquals(0.5d, (Double)evaluateExpression("scipy.special", "expit", 0d), 1e-8);
		assertEquals(0.81757448d, (Double)evaluateExpression("scipy.special", "expit", 1.5d), 1e-8);

		try {
			evaluateExpression("scipy.special", "logit", -2d);

			fail();
		} catch(EvaluationException ee){
			// Ignored
		}

		assertEquals(Double.NEGATIVE_INFINITY, evaluateExpression("scipy.special", "logit", 0d));
		assertEquals(-1.5d, (Double)evaluateExpression("scipy.special", "logit", 0.18242552d), 3e-8);
		assertEquals(0d, (Double)evaluateExpression("scipy.special", "logit", 0.5d), 1e-8);
		assertEquals(1.5d, (Double)evaluateExpression("scipy.special", "logit", 0.81757448d), 3e-8);
		assertEquals(Double.POSITIVE_INFINITY, evaluateExpression("scipy.special", "logit", 1d));

		try {
			evaluateExpression("scipy.special", "logit", 2d);

			fail();
		} catch(EvaluationException ee){
			// Ignored
		}
	}

	static
	private Map<String, String> withString(Map<String, String> arguments, String string){
		arguments.put("string", string);

		return arguments;
	}

	static
	private Object evaluateExpression(String module, String name, Object argument){
		return evaluateExpression(module, name, Collections.singletonMap("x", argument));
	}

	static
	private Object evaluateExpression(String module, String name, Map<String, ?> arguments){
		PMMLEncoder encoder = new PythonEncoder(){
		};

		List<Expression> fieldRefs = (arguments.entrySet()).stream()
			.map((entry) -> {
				String key = entry.getKey();
				Object value = entry.getValue();

				DataType dataType = TypeUtil.getDataType(value);
				OpType opType = TypeUtil.getOpType(dataType);

				DataField dataField = new DataField(key, opType, dataType);

				encoder.addDataField(dataField);

				return new FieldRef(key);
			})
			.collect(Collectors.toList());

		Expression expression = FunctionUtil.encodeFunction(module, name, fieldRefs, encoder);

		VirtualEvaluationContext context = new VirtualEvaluationContext(){

			@Override
			public DefineFunction getDefineFunction(String name){
				return encoder.getDefineFunction(name);
			}
		};
		context.declareAll(arguments);

		FieldValue value = org.jpmml.evaluator.ExpressionUtil.evaluate(expression, context);

		return FieldValueUtil.getValue(value);
	}
}