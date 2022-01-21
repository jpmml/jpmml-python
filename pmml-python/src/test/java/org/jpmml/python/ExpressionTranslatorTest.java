/*
 * Copyright (c) 2017 Villu Ruusmann
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.PMMLFunctions;
import org.jpmml.converter.Feature;
import org.jpmml.converter.PMMLUtil;
import org.jpmml.converter.StringFeature;
import org.jpmml.evaluator.EvaluationContext;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.FieldValueUtil;
import org.jpmml.evaluator.VirtualEvaluationContext;
import org.jpmml.model.ReflectionUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ExpressionTranslatorTest extends TranslatorTest {

	@Test
	public void translateIfElseExpression(){
		Expression expected = PMMLUtil.createApply(PMMLFunctions.IF,
			PMMLUtil.createApply(PMMLFunctions.GREATERTHAN,
				fieldRefs.get(0),
				PMMLUtil.createConstant(0.0d, DataType.DOUBLE)
			),
			PMMLUtil.createApply(PMMLFunctions.LN,
				fieldRefs.get(0)
			),
			PMMLUtil.createMissingConstant()
		);

		String string = "numpy.log(X[0]) if X[0] > 0.0 else None";

		checkExpression(expected, string, null, new DataFrameScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.IF,
			PMMLUtil.createApply(PMMLFunctions.GREATERTHAN,
				fieldRefs.get(0),
				PMMLUtil.createConstant(0, DataType.INTEGER)
			),
			PMMLUtil.createConstant("positive", DataType.STRING),
			PMMLUtil.createApply(PMMLFunctions.IF,
				PMMLUtil.createApply(PMMLFunctions.LESSTHAN,
					fieldRefs.get(0),
					PMMLUtil.createConstant(0, DataType.INTEGER)
				),
				PMMLUtil.createConstant("negative", DataType.STRING),
				PMMLUtil.createConstant("zero", DataType.STRING)
			)
		);

		string = "\"positive\" if X[0] > 0 else \"negative\" if X[0] < 0 else \"zero\"";

		checkExpression(expected, string, DataType.STRING, new DataFrameScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.IF,
			PMMLUtil.createApply(PMMLFunctions.ISMISSING,
				fieldRefs.get(0)
			),
			PMMLUtil.createMissingConstant(),
			PMMLUtil.createApply(PMMLFunctions.DIVIDE,
				fieldRefs.get(0),
				fieldRefs.get(1)
			)
		);

		string = "numpy.nan if numpy.isnan(X[0]) else X[0]/X[1]";

		checkExpression(expected, string, null, new DataFrameScope(doubleFeatures));
	}

	@Test
	public void translateLogicalExpression(){
		Expression expected = PMMLUtil.createApply(PMMLFunctions.OR,
			PMMLUtil.createApply(PMMLFunctions.AND,
				fieldRefs.get("a"),
				fieldRefs.get("b")
			),
			fieldRefs.get("c")
		);

		String string = "numpy.logical_or(numpy.logical_and(a, b), c)";

		checkExpression(expected, string, DataType.BOOLEAN, new BlockScope(booleanFeatures));

		string = "X[\"a\"] and X[\"b\"] or X[\"c\"]";

		checkExpression(expected, string, new DataFrameScope(booleanFeatures));

		string = "a and b or c";

		checkExpression(expected, string, new BlockScope(booleanFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.NOT, fieldRefs.get("a"));

		string = "numpy.logical_not(a)";

		checkExpression(expected, string, DataType.BOOLEAN, new BlockScope(booleanFeatures));

		string = "not X[\"a\"]";

		checkExpression(expected, string, new DataFrameScope(booleanFeatures));

		string = "not a";

		checkExpression(expected, string, new BlockScope(booleanFeatures));
	}

	@Test
	public void translateIdentityComparisonExpression(){
		Expression expected = PMMLUtil.createApply(PMMLFunctions.EQUAL,
			fieldRefs.get(0),
			PMMLUtil.createMissingConstant()
		);

		String string = "X[0] == None";

		checkExpression(expected, string, DataType.BOOLEAN, new DataFrameScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.ISMISSING, fieldRefs.get(0));

		checkExpressionCompact(expected, string, DataType.BOOLEAN, new DataFrameScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.NOTEQUAL,
			fieldRefs.get(0),
			PMMLUtil.createMissingConstant()
		);

		string = "X[0] != None";

		checkExpression(expected, string, DataType.BOOLEAN, new DataFrameScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.ISNOTMISSING, fieldRefs.get(0));

		checkExpressionCompact(expected, string, DataType.BOOLEAN, new DataFrameScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.ISMISSING, fieldRefs.get(0));

		string = "X[0] is None";

		checkExpression(expected, string, DataType.BOOLEAN, new DataFrameScope(doubleFeatures));

		string = "a is None";

		checkExpression(expected, string, new BlockScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.ISNOTMISSING, PMMLUtil.createApply(PMMLFunctions.ADD, fieldRefs.get("a"), PMMLUtil.createConstant(1, DataType.INTEGER)));

		string = "(X['a'] + 1) is not None";

		checkExpression(expected, string, DataType.BOOLEAN, new DataFrameScope(doubleFeatures));

		string = "(a + 1) is not None";

		checkExpression(expected, string, new BlockScope(doubleFeatures));
	}

	@Test
	public void translateComparisonExpression(){
		Expression expected = PMMLUtil.createApply(PMMLFunctions.AND, fieldRefs.get("a"), fieldRefs.get("b"));

		String string = "X['a'] and X['b']";

		checkExpression(expected, string, DataType.BOOLEAN, new DataFrameScope(booleanFeatures));

		string = "a and b";

		checkExpression(expected, string, new BlockScope(booleanFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.AND,
			PMMLUtil.createApply(PMMLFunctions.EQUAL, fieldRefs.get("a"), PMMLUtil.createConstant(true, DataType.BOOLEAN)),
			PMMLUtil.createApply(PMMLFunctions.EQUAL, fieldRefs.get("b"), PMMLUtil.createConstant(false, DataType.BOOLEAN))
		);

		string = "X['a'] == True and X['b'] == False";

		checkExpression(expected, string, DataType.BOOLEAN, new DataFrameScope(booleanFeatures));

		string = "a == True and b == False";

		checkExpression(expected, string, new BlockScope(booleanFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.ISIN, fieldRefs.get(0), PMMLUtil.createConstant(0.0d, DataType.DOUBLE), PMMLUtil.createConstant(1.0d, DataType.DOUBLE));

		string = "X[0] in [0.0, 1.0]";

		checkExpression(expected, string, DataType.BOOLEAN, new DataFrameScope(doubleFeatures));

		string = "a in [0.0, 1.0]";

		checkExpression(expected, string, new BlockScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.ISNOTIN, PMMLUtil.createApply(PMMLFunctions.ADD, fieldRefs.get(0), PMMLUtil.createConstant(1.0d, DataType.DOUBLE)), fieldRefs.get(1));

		string = "(X[0] + 1.0) not in [X[1]]";

		checkExpression(expected, string, DataType.BOOLEAN, new DataFrameScope(doubleFeatures));

		string = "(a + 1.0) not in [b]";

		checkExpression(expected, string, new BlockScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.GREATERTHAN, fieldRefs.get("a"), fieldRefs.get("b"));

		string = "X[\"a\"] > X[\"b\"]";

		checkExpression(expected, string, DataType.BOOLEAN, new DataFrameScope(doubleFeatures));

		string = "a > b";

		checkExpression(expected, string, new BlockScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.NOT, PMMLUtil.createApply(PMMLFunctions.LESSTHAN, fieldRefs.get("a"), PMMLUtil.createConstant(0.0d, DataType.DOUBLE)));

		string = "not X[\"a\"] < 0.0";

		checkExpression(expected, string, DataType.BOOLEAN, new DataFrameScope(doubleFeatures));

		string = "not a < 0.0";

		checkExpression(expected, string, new BlockScope(doubleFeatures));
	}

	@Test
	public void translateArithmeticExpression(){
		Expression expected = PMMLUtil.createApply(PMMLFunctions.MULTIPLY,
			PMMLUtil.createApply(PMMLFunctions.DIVIDE,
				PMMLUtil.createApply(PMMLFunctions.SUBTRACT,
					PMMLUtil.createApply(PMMLFunctions.ADD,
						fieldRefs.get("a"),
						fieldRefs.get("b")
					),
					PMMLUtil.createConstant(1.0d, DataType.DOUBLE)
				),
				fieldRefs.get("c")
			),
			PMMLUtil.createConstant(-2, DataType.INTEGER)
		);

		String string = "(X[0] + X[1] - 1.0) / X[2] * -2";

		checkExpression(expected, string, null, new DataFrameScope(doubleFeatures));

		string = "(X[\"a\"] + X[\"b\"] - 1.0) / X['c'] * -2";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "(a + b - 1.0) / c * -2";

		checkExpression(expected, string, new BlockScope(doubleFeatures));
	}

	@Test
	public void translateStringConcatenationExpression(){
		Constant prefix = PMMLUtil.createConstant("19", DataType.STRING);
		FieldRef content = fieldRefs.get(0);
		Constant suffix = PMMLUtil.createConstant("-01-01", DataType.STRING);

		Expression expected = PMMLUtil.createApply(PMMLFunctions.CONCAT, PMMLUtil.createApply(PMMLFunctions.CONCAT, prefix, content), suffix);
		Expression expectedCompact = PMMLUtil.createApply(PMMLFunctions.CONCAT, prefix, content, suffix);

		String string = "\'19\' + X[0] + \'-01-01\'";

		Scope scope = new DataFrameScope(stringFeatures);

		checkExpression(expected, string, DataType.STRING, scope);
		checkExpressionCompact(expectedCompact, string, DataType.STRING, scope);

		string = "\"19\" + X[\'a\'] + \"-01-01\"";

		checkExpression(expected, string, scope);
		checkExpressionCompact(expectedCompact, string, scope);

		string = "\"19\" + a + \"-01-01\"";

		scope = new BlockScope(stringFeatures);

		checkExpression(expected, string, scope);
		checkExpressionCompact(expectedCompact, string, scope);
	}

	@Test
	public void translateStringIfElseExpression(){
		Expression expected = PMMLUtil.createApply(PMMLFunctions.IF,
			PMMLUtil.createApply(PMMLFunctions.EQUAL, PMMLUtil.createApply(PMMLFunctions.TRIMBLANKS, PMMLUtil.createApply(PMMLFunctions.SUBSTRING, fieldRefs.get(1), PMMLUtil.createConstant(1, DataType.INTEGER), PMMLUtil.createConstant(1, DataType.INTEGER))), PMMLUtil.createConstant("low", DataType.STRING)),
			PMMLUtil.createApply(PMMLFunctions.LOWERCASE, fieldRefs.get(0)),
			PMMLUtil.createApply(PMMLFunctions.UPPERCASE, fieldRefs.get(0))
		);

		String string = "X[0].lower() if (X[1][0:1].strip()) == \'low\' else X[0].upper()";

		checkExpression(expected, string, DataType.STRING, new DataFrameScope(stringFeatures));

		string = "a.lower() if (b[0:1].strip()) == \'low\' else a.upper()";

		checkExpression(expected, string, new BlockScope(stringFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.IF,
			PMMLUtil.createApply(PMMLFunctions.GREATERTHAN, PMMLUtil.createApply(PMMLFunctions.STRINGLENGTH, fieldRefs.get(0)), PMMLUtil.createConstant(0, DataType.INTEGER)),
			PMMLUtil.createConstant(true, DataType.BOOLEAN),
			PMMLUtil.createConstant(false, DataType.BOOLEAN)
		);

		string = "True if len(X[0][:]) > 0 else False";

		checkExpression(expected, string, DataType.BOOLEAN, new DataFrameScope(stringFeatures));
	}

	@Test
	public void translateStringSlicingExpression(){
		Feature feature = new StringFeature(encoder, "x");

		Scope scope = new BlockScope(Collections.singletonList(feature));

		Map<String, Object> arguments = new HashMap<>();
		arguments.put(feature.getName(), "Hello World!");

		assertEquals("Hello World!", evaluateExpression("x", scope, arguments));
		assertEquals("Hello World!", evaluateExpression("x[:]", scope, arguments));

		assertEquals("Hello World!", evaluateExpression("x[0:]", scope, arguments));
		assertEquals("ello World!", evaluateExpression("x[1:]", scope, arguments));
		assertEquals("", evaluateExpression("x[13:]", scope, arguments));
		assertEquals(" World!", evaluateExpression("x[-7:]", scope, arguments));
		assertEquals("Hello World!", evaluateExpression("x[-13:]", scope, arguments));

		assertEquals("", evaluateExpression("x[:0]", scope, arguments));
		assertEquals("H", evaluateExpression("x[:1]", scope, arguments));
		assertEquals("Hello World!", evaluateExpression("x[:13]", scope, arguments));
		assertEquals("Hello", evaluateExpression("x[:-7]", scope, arguments));
		assertEquals("", evaluateExpression("x[:-13]", scope, arguments));

		assertEquals("", evaluateExpression("x[0:0]", scope, arguments));
		assertEquals("H", evaluateExpression("x[0:1]", scope, arguments));
		assertEquals("Hello World", evaluateExpression("x[0:-1]", scope, arguments));
		assertEquals("Hello", evaluateExpression("x[0:-7]", scope, arguments));
		assertEquals("", evaluateExpression("x[0:-13]", scope, arguments));

		assertEquals("", evaluateExpression("x[1:0]", scope, arguments));
		assertEquals("", evaluateExpression("x[1:1]", scope, arguments));
		assertEquals("ello World", evaluateExpression("x[1:-1]", scope, arguments));
		assertEquals("ello", evaluateExpression("x[1:-7]", scope, arguments));
		assertEquals("", evaluateExpression("x[1:-13]", scope, arguments));

		assertEquals("", evaluateExpression("x[-1:0]", scope, arguments));
		assertEquals("", evaluateExpression("x[-1:1]", scope, arguments));
		assertEquals("", evaluateExpression("x[-1:-1]", scope, arguments));
		assertEquals("", evaluateExpression("x[-1:-7]", scope, arguments));
		assertEquals("", evaluateExpression("x[-1:-13]", scope, arguments));

		assertEquals(true, evaluateExpression("x.startswith('Hello')", scope, arguments));
		assertEquals(false, evaluateExpression("x.startswith('Hello!')", scope, arguments));
		assertEquals(true, evaluateExpression("x.endswith('World!')", scope, arguments));
		assertEquals(false, evaluateExpression("x.endswith('World')", scope, arguments));
	}

	@Test
	public void translateUnaryExpression(){
		Constant minusOne = PMMLUtil.createConstant(-1, DataType.INTEGER);
		Constant plusOne = PMMLUtil.createConstant(1, DataType.INTEGER);

		Scope scope = new BlockScope(Collections.emptyList());

		checkExpression(minusOne, "-1", DataType.INTEGER, scope);

		checkExpression(plusOne, "1", DataType.INTEGER, scope);
		checkExpression(plusOne, "+1", DataType.INTEGER, scope);

		checkExpression(minusOne, "-+1", scope);
		checkExpression(plusOne, "--1", scope);
		checkExpression(minusOne, "---1", scope);
	}

	@Test
	public void translateConstantExpression(){
		Expression expected = PMMLUtil.createMissingConstant();

		String[] strings = {"numpy.nan", "numpy.NaN", "numpy.NAN"};
		for(String string : strings){
			checkExpression(expected, string, new DataFrameScope(doubleFeatures));
		}
	}

	@Test
	public void translateFunctionInvocationExpression(){
		Expression expected = PMMLUtil.createApply(PMMLFunctions.IF,
			PMMLUtil.createApply(PMMLFunctions.ISNOTMISSING, fieldRefs.get("a")),
			fieldRefs.get("a"),
			PMMLUtil.createApply(PMMLFunctions.ADD, fieldRefs.get("b"), fieldRefs.get("c"))
		);

		String string = "X[\"a\"] if pandas.notnull(X[\"a\"]) else X[\"b\"] + X[\"c\"]";

		checkExpression(expected, string, null, new DataFrameScope(doubleFeatures));

		string = "a if pandas.notnull(a) else b + c";

		checkExpression(expected, string, new BlockScope(doubleFeatures));
	}

	@Test
	public void translateArrayIndexingExpression(){
		Expression expected;

		List<Feature> features = booleanFeatures;

		Scope scope = new DataFrameScope(features);

		for(int i = 0; i < features.size(); i++){
			Feature feature = features.get(i);

			expected = feature.ref();

			checkExpression(expected, "X[" + "+" + i + "]", DataType.BOOLEAN, scope);
		}

		try {
			ExpressionTranslator.translate("X[" + features.size() + "]", scope);

			fail();
		} catch(IllegalArgumentException iae){
			// Ignored
		}

		for(int i = 1; i <= features.size(); i++){
			Feature feature = features.get(features.size() - i);

			expected = feature.ref();

			checkExpression(expected, "X[" + "-" + i +"]", DataType.BOOLEAN, scope);
		}

		try {
			ExpressionTranslator.translate("X[" + "-" + (features.size() + 1) + "]", scope);

			fail();
		} catch(IllegalArgumentException iae){
			// Ignored
		}
	}

	static
	private Object evaluateExpression(String string, Scope scope, Map<String, ?> arguments){
		Expression expression = ExpressionTranslator.translate(string, scope, false);

		EvaluationContext context = new VirtualEvaluationContext();
		context.declareAll(arguments);

		FieldValue value = org.jpmml.evaluator.ExpressionUtil.evaluate(expression, context);

		return FieldValueUtil.getValue(value);
	}

	static
	private Expression checkExpression(Expression expected, String string, Scope scope){
		Expression actual = ExpressionTranslator.translate(string, scope, false);

		assertTrue(ReflectionUtil.equals(expected, actual));

		return actual;
	}

	static
	private Expression checkExpression(Expression expected, String string, DataType dataType, Scope scope){
		Expression actual = checkExpression(expected, string, scope);

		assertEquals(dataType, ExpressionUtil.getDataType(actual, scope));

		return actual;
	}

	static
	private Expression checkExpressionCompact(Expression expected, String string, Scope scope){
		Expression actual = ExpressionTranslator.translate(string, scope, true);

		assertTrue(ReflectionUtil.equals(expected, actual));

		return actual;
	}

	static
	private Expression checkExpressionCompact(Expression expected, String string, DataType dataType, Scope scope){
		Expression actual = checkExpressionCompact(expected, string, scope);

		assertTrue(ReflectionUtil.equals(expected, actual));

		return actual;
	}
}