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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.OpType;
import org.dmg.pmml.PMMLFunctions;
import org.jpmml.converter.ContinuousFeature;
import org.jpmml.converter.Feature;
import org.jpmml.converter.PMMLEncoder;
import org.jpmml.converter.PMMLUtil;
import org.jpmml.converter.StringFeature;
import org.jpmml.converter.TypeUtil;
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
	public void translateRatioSignumDef(){
		PMMLEncoder encoder = new PMMLEncoder();

		List<Feature> variables = Arrays.asList(
			new ContinuousFeature(encoder, "x1", DataType.DOUBLE),
			new ContinuousFeature(encoder, "x2", DataType.DOUBLE)
		);

		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new BlockScope(variables, encoder));

		String newline = "\n";

		String string =
			"def ratio_signum(x1, x2):" + newline +
			"	\"\"\"" + newline +
			"	" + newline +
			"	:param float x1: dividend" + newline +
			"	:param float x2: divisor" + newline +
			"	\"\"\"" + newline +
			"	import numpy as np, pandas as pd" + newline +
			"	# Calculate ratio" + newline +
			// Four spaces instead of a tab
			"    ratio = (x1 / x2)" + newline +
			"	# Determine the signum of ratio" + newline +
			"	if ratio < 0.0: return np.ceil(-1.5)" + newline +
			"	elif ratio > 0.0: return np.floor(1.5)" + newline +
			"	else: return 0" + newline;

		try {
			expressionTranslator.translateDef(string);

			fail();
		} catch(IllegalArgumentException iae){
			// Ignored
		}

		string = string.replace("    ", "\t");

		DerivedField derivedField = expressionTranslator.translateDef(string);

		assertEquals("ratio_signum", derivedField.getName());
		assertEquals(OpType.CONTINUOUS, derivedField.getOpType());
		assertEquals(DataType.INTEGER, derivedField.getDataType());

		DerivedField ratioDerivedField = encoder.getDerivedField("ratio");

		Expression expected = PMMLUtil.createApply(PMMLFunctions.IF,
			PMMLUtil.createApply(PMMLFunctions.LESSTHAN,
				new FieldRef(ratioDerivedField),
				PMMLUtil.createConstant(0.0, DataType.DOUBLE)
			),
			PMMLUtil.createApply(PMMLFunctions.CEIL, PMMLUtil.createConstant(-1.5, DataType.DOUBLE)),
			PMMLUtil.createApply(PMMLFunctions.IF,
				PMMLUtil.createApply(PMMLFunctions.GREATERTHAN,
					new FieldRef(ratioDerivedField),
					PMMLUtil.createConstant(0.0, DataType.DOUBLE)
				),
				PMMLUtil.createApply(PMMLFunctions.FLOOR, PMMLUtil.createConstant(1.5, DataType.DOUBLE)),
				PMMLUtil.createConstant(0, DataType.INTEGER)
			)
		);

		checkExpression(expected, derivedField.getExpression());

		encoder = new PMMLEncoder();

		encoder.createDataField("x1", OpType.CONTINUOUS, DataType.DOUBLE);
		encoder.createDataField("x2", OpType.CONTINUOUS, DataType.DOUBLE);

		variables = Collections.emptyList();

		expressionTranslator = new ExpressionTranslator(new BlockScope(variables, encoder));

		string = string
			.replace("ratio_signum(x1, x2)", "ratio_signum()")
			.replace(": return", ":" + newline + "\t\t" + "return");

		// XXX
		string += newline;
		string += newline;

		derivedField = expressionTranslator.translateDef(string);

		checkExpression(expected, derivedField.getExpression());
	}

	@Test
	public void translateQuadrantDef(){
		PMMLEncoder encoder = new PMMLEncoder();

		List<Feature> variables = Arrays.asList(
			new ContinuousFeature(encoder, "x", DataType.DOUBLE),
			new ContinuousFeature(encoder, "y", DataType.DOUBLE)
		);

		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new BlockScope(variables, encoder));

		String newline = "\n";

		String string =
			"def quadrant(x, y):" + newline +
			"	if x >= 0:" + newline +
			"		if y >= 0:" + newline +
			"			return 'I'" + newline +
			"		else:" + newline +
			"			return 'IV'" + newline +
			"	else:" + newline +
			"		if y >= 0:" + newline +
			"			return 'II'" + newline +
			"		else:" + newline +
			"			return 'III'" + newline;

		DerivedField derivedField = expressionTranslator.translateDef(string);

		assertEquals("quadrant", derivedField.getName());
		assertEquals(OpType.CATEGORICAL, derivedField.getOpType());
		assertEquals(DataType.STRING, derivedField.getDataType());

		Constant zero = PMMLUtil.createConstant(0, DataType.INTEGER);

		Expression expected = PMMLUtil.createApply(PMMLFunctions.IF,
			PMMLUtil.createApply(PMMLFunctions.GREATEROREQUAL, new FieldRef("x"), zero),
			PMMLUtil.createApply(PMMLFunctions.IF,
				PMMLUtil.createApply(PMMLFunctions.GREATEROREQUAL, new FieldRef("y"), zero),
				PMMLUtil.createConstant("I", DataType.STRING),
				PMMLUtil.createConstant("IV", DataType.STRING)
			),
			PMMLUtil.createApply(PMMLFunctions.IF,
				PMMLUtil.createApply(PMMLFunctions.GREATEROREQUAL, new FieldRef("y"), zero),
				PMMLUtil.createConstant("II", DataType.STRING),
				PMMLUtil.createConstant("III", DataType.STRING)
			)
		);

		checkExpression(expected, derivedField.getExpression());
	}

	@Test
	public void translateIfElseExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

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

		assertEquals(null, TypeUtil.getDataType(expected, expressionTranslator));

		String string = "numpy.log(X[0]) if X[0] > 0.0 else None";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		string = "numpy.where(X[0] > 0.0, numpy.log(X[0]), None)";

		checkExpression(expected, translateExpression(expressionTranslator, string));

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

		assertEquals(DataType.STRING, TypeUtil.getDataType(expected, expressionTranslator));

		string = "\"positive\" if X[0] > 0 else \"negative\" if X[0] < 0 else \"zero\"";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		string = "numpy.where(X[0] > 0, \"positive\", numpy.where(X[0] < 0, \"negative\", \"zero\"))";

		checkExpression(expected, translateExpression(expressionTranslator, string));

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

		assertEquals(null, TypeUtil.getDataType(expected, expressionTranslator));

		string = "numpy.nan if numpy.isnan(X[0]) else X[0] / X[1]";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		string = "numpy.where(numpy.isnan(X[0]), numpy.nan, X[0] / X[1])";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new DataFrameScope(stringFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.IF,
			PMMLUtil.createApply(PMMLFunctions.ISNOTMISSING,
				fieldRefs.get(0)
			),
			fieldRefs.get(0),
			PMMLUtil.createConstant("missing", DataType.STRING)
		);

		assertEquals(DataType.STRING, TypeUtil.getDataType(expected, expressionTranslator));

		string = "X[0] if pandas.notna(X[0]) else 'missing'";

		checkExpression(expected, translateExpression(expressionTranslator, string));
	}

	@Test
	public void translateStringIfElseExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new DataFrameScope(stringFeatures));

		Expression expected = PMMLUtil.createApply(PMMLFunctions.IF,
			PMMLUtil.createApply(PMMLFunctions.EQUAL, PMMLUtil.createApply(PMMLFunctions.TRIMBLANKS, PMMLUtil.createApply(PMMLFunctions.SUBSTRING, fieldRefs.get(1), PMMLUtil.createConstant(1, DataType.INTEGER), PMMLUtil.createConstant(1, DataType.INTEGER))), PMMLUtil.createConstant("low", DataType.STRING)),
			PMMLUtil.createApply(PMMLFunctions.LOWERCASE, fieldRefs.get(0)),
			PMMLUtil.createApply(PMMLFunctions.UPPERCASE, fieldRefs.get(0))
		);

		assertEquals(DataType.STRING, TypeUtil.getDataType(expected, expressionTranslator));

		String string = "X[0].lower() if (X[1][0:1].strip()) == \'low\' else X[0].upper()";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(stringFeatures));

		string = "a.lower() if (b[0:1].strip()) == \'low\' else a.upper()";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new DataFrameScope(stringFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.IF,
			PMMLUtil.createApply(PMMLFunctions.GREATERTHAN, PMMLUtil.createApply(PMMLFunctions.STRINGLENGTH, fieldRefs.get(0)), PMMLUtil.createConstant(0, DataType.INTEGER)),
			PMMLUtil.createConstant(true, DataType.BOOLEAN),
			PMMLUtil.createConstant(false, DataType.BOOLEAN)
		);

		assertEquals(DataType.BOOLEAN, TypeUtil.getDataType(expected, expressionTranslator));

		string = "True if len(X[0][:]) > 0 else False";

		checkExpression(expected, translateExpression(expressionTranslator, string));
	}

	@Test
	public void translateLogicalExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new DataFrameScope(booleanFeatures));

		Expression expected = PMMLUtil.createApply(PMMLFunctions.OR,
			PMMLUtil.createApply(PMMLFunctions.AND,
				fieldRefs.get("a"),
				fieldRefs.get("b")
			),
			fieldRefs.get("c")
		);

		assertEquals(DataType.BOOLEAN, TypeUtil.getDataType(expected, expressionTranslator));

		String string = "X[\"a\"] and X[\"b\"] or X[\"c\"]";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(booleanFeatures));

		string = "a and b or c";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		string = "numpy.logical_or(numpy.logical_and(a, b), c)";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expected = PMMLUtil.createApply(PMMLFunctions.NOT, fieldRefs.get("a"));

		assertEquals(DataType.BOOLEAN, TypeUtil.getDataType(expected, expressionTranslator));

		expressionTranslator = new ExpressionTranslator(new DataFrameScope(booleanFeatures));

		string = "not X[\"a\"]";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(booleanFeatures));

		string = "not a";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		string = "numpy.logical_not(a)";

		checkExpression(expected, translateExpression(expressionTranslator, string));

	}

	@Test
	public void translateComparisonExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new DataFrameScope(booleanFeatures));

		Expression expected = PMMLUtil.createApply(PMMLFunctions.AND, fieldRefs.get("a"), fieldRefs.get("b"));

		assertEquals(DataType.BOOLEAN, TypeUtil.getDataType(expected, expressionTranslator));

		String string = "X['a'] and X['b']";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(booleanFeatures));

		string = "a and b";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new DataFrameScope(booleanFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.AND,
			PMMLUtil.createApply(PMMLFunctions.EQUAL, fieldRefs.get("a"), PMMLUtil.createConstant(true, DataType.BOOLEAN)),
			PMMLUtil.createApply(PMMLFunctions.EQUAL, fieldRefs.get("b"), PMMLUtil.createConstant(false, DataType.BOOLEAN))
		);

		assertEquals(DataType.BOOLEAN, TypeUtil.getDataType(expected, expressionTranslator));

		string = "X['a'] == True and X['b'] == False";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(booleanFeatures));

		string = "a == True and b == False";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.ISIN, fieldRefs.get(0), PMMLUtil.createConstant(0.0d, DataType.DOUBLE), PMMLUtil.createConstant(1.0d, DataType.DOUBLE));

		assertEquals(DataType.BOOLEAN, TypeUtil.getDataType(expected, expressionTranslator));

		string = "X[0] in [0.0, 1.0]";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(doubleFeatures));

		string = "a in [0.0, 1.0]";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.ISNOTIN, PMMLUtil.createApply(PMMLFunctions.ADD, fieldRefs.get(0), PMMLUtil.createConstant(1.0d, DataType.DOUBLE)), fieldRefs.get(1));

		assertEquals(DataType.BOOLEAN, TypeUtil.getDataType(expected, expressionTranslator));

		string = "(X[0] + 1.0) not in [X[1]]";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(doubleFeatures));

		string = "(a + 1.0) not in [b]";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.GREATERTHAN, fieldRefs.get("a"), fieldRefs.get("b"));

		assertEquals(DataType.BOOLEAN, TypeUtil.getDataType(expected, expressionTranslator));

		string = "X[\"a\"] > X[\"b\"]";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(doubleFeatures));

		string = "a > b";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.NOT, PMMLUtil.createApply(PMMLFunctions.LESSTHAN, fieldRefs.get("a"), PMMLUtil.createConstant(0.0d, DataType.DOUBLE)));

		assertEquals(DataType.BOOLEAN, TypeUtil.getDataType(expected, expressionTranslator));

		string = "not X[\"a\"] < 0.0";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(doubleFeatures));

		string = "not a < 0.0";

		checkExpression(expected, translateExpression(expressionTranslator, string));
	}

	@Test
	public void translateIdentityComparisonExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

		Expression expected = PMMLUtil.createApply(PMMLFunctions.EQUAL,
			fieldRefs.get(0),
			PMMLUtil.createMissingConstant()
		);

		assertEquals(DataType.BOOLEAN, TypeUtil.getDataType(expected, expressionTranslator));

		String string = "X[0] == None";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expected = PMMLUtil.createApply(PMMLFunctions.ISMISSING, fieldRefs.get(0));

		assertEquals(DataType.BOOLEAN, TypeUtil.getDataType(expected, expressionTranslator));

		checkExpression(expected, translateExpression(expressionTranslator, string, true));

		expected = PMMLUtil.createApply(PMMLFunctions.NOTEQUAL,
			fieldRefs.get(0),
			PMMLUtil.createMissingConstant()
		);

		assertEquals(DataType.BOOLEAN, TypeUtil.getDataType(expected, expressionTranslator));

		string = "X[0] != None";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expected = PMMLUtil.createApply(PMMLFunctions.ISNOTMISSING, fieldRefs.get(0));

		assertEquals(DataType.BOOLEAN, TypeUtil.getDataType(expected, expressionTranslator));

		checkExpression(expected, translateExpression(expressionTranslator, string, true));

		expected = PMMLUtil.createApply(PMMLFunctions.ISMISSING, fieldRefs.get(0));

		assertEquals(DataType.BOOLEAN, TypeUtil.getDataType(expected, expressionTranslator));

		string = "X[0] is None";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(doubleFeatures));

		string = "a is None";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.ISNOTMISSING, PMMLUtil.createApply(PMMLFunctions.ADD, fieldRefs.get("a"), PMMLUtil.createConstant(1, DataType.INTEGER)));

		assertEquals(DataType.BOOLEAN, TypeUtil.getDataType(expected, expressionTranslator));

		string = "(X['a'] + 1) is not None";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(doubleFeatures));

		string = "(a + 1) is not None";

		checkExpression(expected, translateExpression(expressionTranslator, string));
	}

	@Test
	public void translateArithmeticExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

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

		assertEquals(null, TypeUtil.getDataType(expected, expressionTranslator));

		String string = "(X[0] + X[1] - 1.0) / X[2] * -2";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		string = "(X[\"a\"] + X[\"b\"] - 1.0) / X['c'] * -2";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(doubleFeatures));

		string = "(a + b - 1.0) / c * -2";

		checkExpression(expected, translateExpression(expressionTranslator, string));
	}

	@Test
	public void translateStringConcatenationExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new DataFrameScope(stringFeatures));

		Constant prefix = PMMLUtil.createConstant("19", DataType.STRING);
		FieldRef content = fieldRefs.get(0);
		Constant suffix = PMMLUtil.createConstant("-01-01", DataType.STRING);

		Expression expectedNonCompact = PMMLUtil.createApply(PMMLFunctions.CONCAT, PMMLUtil.createApply(PMMLFunctions.CONCAT, prefix, content), suffix);
		Expression expectedCompact = PMMLUtil.createApply(PMMLFunctions.CONCAT, prefix, content, suffix);

		assertEquals(DataType.STRING, TypeUtil.getDataType(expectedNonCompact, expressionTranslator));
		assertEquals(DataType.STRING, TypeUtil.getDataType(expectedCompact, expressionTranslator));

		String string = "\'19\' + X[0] + \'-01-01\'";

		checkExpression(expectedNonCompact, translateExpression(expressionTranslator, string));
		checkExpression(expectedCompact, translateExpression(expressionTranslator, string, true));

		string = "\"19\" + X[\'a\'] + \"-01-01\"";

		checkExpression(expectedNonCompact, translateExpression(expressionTranslator, string));
		checkExpression(expectedCompact, translateExpression(expressionTranslator, string, true));

		expressionTranslator = new ExpressionTranslator(new BlockScope(stringFeatures));

		string = "\"19\" + a + \"-01-01\"";

		checkExpression(expectedNonCompact, translateExpression(expressionTranslator, string));
		checkExpression(expectedCompact, translateExpression(expressionTranslator, string, true));
	}

	@Test
	public void translateUnaryExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(BlockScope.EMPTY);

		Constant minusOne = PMMLUtil.createConstant(-1, DataType.INTEGER);
		Constant plusOne = PMMLUtil.createConstant(1, DataType.INTEGER);

		assertEquals(DataType.INTEGER, TypeUtil.getDataType(minusOne, expressionTranslator));
		assertEquals(DataType.INTEGER, TypeUtil.getDataType(plusOne, expressionTranslator));

		checkExpression(minusOne, translateExpression(expressionTranslator, "-1"));

		checkExpression(plusOne, translateExpression(expressionTranslator, "1"));
		checkExpression(plusOne, translateExpression(expressionTranslator, "+1"));

		checkExpression(minusOne, translateExpression(expressionTranslator, "-+1"));
		checkExpression(plusOne, translateExpression(expressionTranslator, "--1"));
		checkExpression(minusOne, translateExpression(expressionTranslator, "---1"));
	}

	@Test
	public void translateArrayIndexingExpression(){
		List<Feature> features = booleanFeatures;

		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new DataFrameScope(features));

		for(int i = 0; i < features.size(); i++){
			Feature feature = features.get(i);

			checkExpression(feature.ref(), translateExpression(expressionTranslator, "X[" + "+" + i + "]"));
		}

		try {
			translateExpression(expressionTranslator, "X[" + features.size() + "]");

			fail();
		} catch(IllegalArgumentException iae){
			// Ignored
		}

		for(int i = 1; i <= features.size(); i++){
			Feature feature = features.get(features.size() - i);

			checkExpression(feature.ref(), translateExpression(expressionTranslator, "X[" + "-" + i +"]"));
		}

		try {
			translateExpression(expressionTranslator, "X[" + "-" + (features.size() + 1) + "]");

			fail();
		} catch(IllegalArgumentException iae){
			// Ignored
		}
	}

	@Test
	public void translateStringSlicingExpression(){
		Feature feature = new StringFeature(encoder, "x");

		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new BlockScope(Collections.singletonList(feature)));

		Map<String, Object> arguments = new HashMap<>();
		arguments.put(feature.getName(), "Hello World!");

		assertEquals("Hello World!", evaluateExpression(expressionTranslator, "x", arguments));
		assertEquals("Hello World!", evaluateExpression(expressionTranslator, "x[:]", arguments));

		assertEquals("Hello World!", evaluateExpression(expressionTranslator, "x[0:]", arguments));
		assertEquals("ello World!", evaluateExpression(expressionTranslator, "x[1:]", arguments));
		assertEquals("", evaluateExpression(expressionTranslator, "x[13:]", arguments));
		assertEquals(" World!", evaluateExpression(expressionTranslator, "x[-7:]", arguments));
		assertEquals("Hello World!", evaluateExpression(expressionTranslator, "x[-13:]", arguments));

		assertEquals("", evaluateExpression(expressionTranslator, "x[:0]", arguments));
		assertEquals("H", evaluateExpression(expressionTranslator, "x[:1]", arguments));
		assertEquals("Hello World!", evaluateExpression(expressionTranslator, "x[:13]", arguments));
		assertEquals("Hello", evaluateExpression(expressionTranslator, "x[:-7]", arguments));
		assertEquals("", evaluateExpression(expressionTranslator, "x[:-13]", arguments));

		assertEquals("", evaluateExpression(expressionTranslator, "x[0:0]", arguments));
		assertEquals("H", evaluateExpression(expressionTranslator, "x[0:1]", arguments));
		assertEquals("Hello World", evaluateExpression(expressionTranslator, "x[0:-1]", arguments));
		assertEquals("Hello", evaluateExpression(expressionTranslator, "x[0:-7]", arguments));
		assertEquals("", evaluateExpression(expressionTranslator, "x[0:-13]", arguments));

		assertEquals("", evaluateExpression(expressionTranslator, "x[1:0]", arguments));
		assertEquals("", evaluateExpression(expressionTranslator, "x[1:1]", arguments));
		assertEquals("ello World", evaluateExpression(expressionTranslator, "x[1:-1]", arguments));
		assertEquals("ello", evaluateExpression(expressionTranslator, "x[1:-7]", arguments));
		assertEquals("", evaluateExpression(expressionTranslator, "x[1:-13]", arguments));

		assertEquals("", evaluateExpression(expressionTranslator, "x[-1:0]", arguments));
		assertEquals("", evaluateExpression(expressionTranslator, "x[-1:1]", arguments));
		assertEquals("", evaluateExpression(expressionTranslator, "x[-1:-1]", arguments));
		assertEquals("", evaluateExpression(expressionTranslator, "x[-1:-7]", arguments));
		assertEquals("", evaluateExpression(expressionTranslator, "x[-1:-13]", arguments));
	}

	@Test
	public void translateConstantExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

		Expression expected = PMMLUtil.createMissingConstant();

		assertEquals(null, TypeUtil.getDataType(expected, expressionTranslator));

		String[] strings = {"math.nan", "numpy.nan", "numpy.NaN", "numpy.NAN", "pandas.NA", "pandas.NaT"};
		for(String string : strings){
			checkExpression(expected, translateExpression(expressionTranslator, string));
		}

		expected = PMMLUtil.createConstant(Math.E, DataType.DOUBLE);

		strings = new String[]{"math.e", "numpy.e"};
		for(String string : strings){
			checkExpression(expected, translateExpression(expressionTranslator, string));
		}

		expected = PMMLUtil.createConstant(Math.PI, DataType.DOUBLE);

		strings = new String[]{"math.pi", "numpy.pi"};
		for(String string : strings){
			checkExpression(expected, translateExpression(expressionTranslator, string));
		}
	}

	@Test
	public void translateFunctionInvocationExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

		Expression expected = PMMLUtil.createApply(PMMLFunctions.IF,
			PMMLUtil.createApply(PMMLFunctions.ISNOTMISSING, fieldRefs.get("a")),
			fieldRefs.get("a"),
			PMMLUtil.createApply(PMMLFunctions.ADD, fieldRefs.get("b"), fieldRefs.get("c"))
		);

		assertEquals(null, TypeUtil.getDataType(expected, expressionTranslator));

		String string = "X[\"a\"] if pandas.notnull(X[\"a\"]) else X[\"b\"] + X[\"c\"]";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(doubleFeatures));

		string = "a if pandas.notnull(a) else b + c";

		checkExpression(expected, translateExpression(expressionTranslator, string));
	}

	@Test
	public void translateStringTrailerFunctionExpression(){
		Feature feature = new StringFeature(encoder, "x");

		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new BlockScope(Collections.singletonList(feature)));

		Map<String, Object> arguments = new HashMap<>();
		arguments.put(feature.getName(), "Hello World!");

		assertEquals(true, evaluateExpression(expressionTranslator, "x.startswith(\"Hello\")", arguments));
		assertEquals(true, evaluateExpression(expressionTranslator, "x.startswith('Hello')", arguments));
		assertEquals(true, evaluateExpression(expressionTranslator, "x.startswith(\"\"\"Hello\"\"\")", arguments));

		assertEquals(false, evaluateExpression(expressionTranslator, "x.startswith(\"Hello!\")", arguments));

		assertEquals(true, evaluateExpression(expressionTranslator, "x.endswith(\"World!\")", arguments));
		assertEquals(true, evaluateExpression(expressionTranslator, "x.endswith('World!')", arguments));
		assertEquals(true, evaluateExpression(expressionTranslator, "x.endswith(\"\"\"World!\"\"\")", arguments));

		assertEquals(false, evaluateExpression(expressionTranslator, "x.endswith('World')", arguments));
	}

	static
	private void checkExpression(Expression expected, Expression actual){
		assertTrue(ReflectionUtil.equals(expected, actual));
	}

	static
	private Expression translateExpression(ExpressionTranslator expressionTranslator, String string){
		return translateExpression(expressionTranslator, string, false);
	}

	static
	private Expression translateExpression(ExpressionTranslator expressionTranslator, String string, boolean compact){
		return expressionTranslator.translateExpression(string, compact);
	}

	static
	private Object evaluateExpression(ExpressionTranslator expressionTranslator, String string, Map<String, ?> arguments){
		Expression expression = translateExpression(expressionTranslator, string);

		EvaluationContext context = new VirtualEvaluationContext();
		context.declareAll(arguments);

		FieldValue value = org.jpmml.evaluator.ExpressionUtil.evaluate(expression, context);

		return FieldValueUtil.getValue(value);
	}
}