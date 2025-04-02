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
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.Expression;
import org.dmg.pmml.Extension;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.OpType;
import org.dmg.pmml.PMMLFunctions;
import org.jpmml.converter.ContinuousFeature;
import org.jpmml.converter.ExpressionUtil;
import org.jpmml.converter.Feature;
import org.jpmml.converter.PMMLEncoder;
import org.jpmml.converter.StringFeature;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.FieldValueUtil;
import org.jpmml.evaluator.VirtualEvaluationContext;
import org.jpmml.model.ReflectionUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
			"	# Determine the signum of ratio" + newline +
			"	if (x1 / x2) < 0.0: return np.ceil(-1.5)" + newline +
			"	elif (x1 / x2) > 0.0: return np.floor(1.5)" + newline +
			"	else: return 0" + newline;

		DefineFunction defineFunction = expressionTranslator.translateDef(string);

		assertEquals("ratio_signum", defineFunction.requireName());
		assertEquals(OpType.CONTINUOUS, defineFunction.requireOpType());
		assertEquals(DataType.INTEGER, defineFunction.requireDataType());

		Expression expected = ExpressionUtil.createApply(PMMLFunctions.IF,
			ExpressionUtil.createApply(PMMLFunctions.LESSTHAN,
				ExpressionUtil.createApply(PMMLFunctions.DIVIDE, new FieldRef("x1"), new FieldRef("x2")),
				ExpressionUtil.createConstant(DataType.DOUBLE, 0.0)
			),
			ExpressionUtil.createApply(PMMLFunctions.CEIL, ExpressionUtil.createConstant(DataType.DOUBLE, -1.5)),
			ExpressionUtil.createApply(PMMLFunctions.IF,
				ExpressionUtil.createApply(PMMLFunctions.GREATERTHAN,
					ExpressionUtil.createApply(PMMLFunctions.DIVIDE, new FieldRef("x1"), new FieldRef("x2")),
					ExpressionUtil.createConstant(DataType.DOUBLE, 0.0)
				),
				ExpressionUtil.createApply(PMMLFunctions.FLOOR, ExpressionUtil.createConstant(DataType.DOUBLE, 1.5)),
				ExpressionUtil.createConstant(DataType.INTEGER, 0)
			)
		);

		checkExpression(expected, defineFunction.getExpression());

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

		defineFunction = expressionTranslator.translateDef(string);

		checkExpression(expected, defineFunction.getExpression());
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
			"	# Right side" + newline +
			"	if x >= 0:" + newline +
			"		# Upper half" + newline +
			"		if y >= 0:" + newline +
			"			return 'I' # Upper right" + newline +
			"		# Lower half" + newline +
			"		else:" + newline +
			"			return 'IV' # Lower right" + newline +
			"	# Left side" + newline +
			"	else:" + newline +
			"		# Upper half" + newline +
			"		if y >= 0:" + newline +
			"			return 'II' # Upper left" + newline +
			"		# Lower half" + newline +
			"		else:" + newline +
			"			return 'III' # Lower left" + newline;

		DefineFunction defineFunction = expressionTranslator.translateDef(string);

		assertEquals("quadrant", defineFunction.requireName());
		assertEquals(OpType.CATEGORICAL, defineFunction.requireOpType());
		assertEquals(DataType.STRING, defineFunction.requireDataType());

		Constant zero = ExpressionUtil.createConstant(DataType.INTEGER, 0);

		Expression expected = ExpressionUtil.createApply(PMMLFunctions.IF,
			ExpressionUtil.createApply(PMMLFunctions.GREATEROREQUAL, new FieldRef("x"), zero),
			ExpressionUtil.createApply(PMMLFunctions.IF,
				ExpressionUtil.createApply(PMMLFunctions.GREATEROREQUAL, new FieldRef("y"), zero),
				ExpressionUtil.createConstant(DataType.STRING, "I"),
				ExpressionUtil.createConstant(DataType.STRING, "IV")
			),
			ExpressionUtil.createApply(PMMLFunctions.IF,
				ExpressionUtil.createApply(PMMLFunctions.GREATEROREQUAL, new FieldRef("y"), zero),
				ExpressionUtil.createConstant(DataType.STRING, "II"),
				ExpressionUtil.createConstant(DataType.STRING, "III")
			)
		);

		checkExpression(expected, defineFunction.getExpression());
	}

	@Test
	public void translateContainsDef(){
		PMMLEncoder encoder = new PMMLEncoder();

		List<Feature> variables = Arrays.asList(
			new StringFeature(encoder, "string"),
			new StringFeature(encoder, "substring")
		);

		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new BlockScope(variables, encoder));

		String newline = "\n";

		String string =
			"def contains(string, substring):" + newline +
			"	if re.search(substring, string):" + newline +
			"		return True" + newline +
			"	return False" + newline;

		try {
			expressionTranslator.translateDef(string);

			fail();
		} catch(TranslationException te){
			// Ignored
		}

		string = string.replace("\treturn False", "\telse:\n\t\treturn False");

		DefineFunction defineFunction = expressionTranslator.translateDef(string);

		assertEquals("contains", defineFunction.requireName());
		assertEquals(OpType.CATEGORICAL, defineFunction.requireOpType());
		assertEquals(DataType.BOOLEAN, defineFunction.requireDataType());

		Extension reFlavourExtension = new Extension()
			.setName("re_flavour")
			.setValue("re");

		Expression expected = ExpressionUtil.createApply(PMMLFunctions.IF,
			ExpressionUtil.createApply(PMMLFunctions.MATCHES, new FieldRef("string"), new FieldRef("substring"))
				.addExtensions(reFlavourExtension),
			ExpressionUtil.createConstant(DataType.BOOLEAN, Boolean.TRUE), ExpressionUtil.createConstant(DataType.BOOLEAN, Boolean.FALSE)
		);

		checkExpression(expected, defineFunction.getExpression());
	}

	@Test
	public void translateIfElseExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

		Expression expected = ExpressionUtil.createApply(PMMLFunctions.IF,
			ExpressionUtil.createApply(PMMLFunctions.GREATERTHAN,
				fieldRefs.get(0),
				ExpressionUtil.createConstant(DataType.DOUBLE, 0.0d)
			),
			ExpressionUtil.createApply(PMMLFunctions.LN,
				fieldRefs.get(0)
			),
			ExpressionUtil.createMissingConstant()
		);

		assertEquals(null, ExpressionUtil.getDataType(expected, expressionTranslator));

		String string = "numpy.log(X[0]) if X[0] > 0.0 else None";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		string = "numpy.where(X[0] > 0.0, numpy.log(X[0]), None)";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expected = ExpressionUtil.createApply(PMMLFunctions.IF,
			ExpressionUtil.createApply(PMMLFunctions.GREATERTHAN,
				fieldRefs.get(0),
				ExpressionUtil.createConstant(DataType.INTEGER, 0)
			),
			ExpressionUtil.createConstant(DataType.STRING, "positive"),
			ExpressionUtil.createApply(PMMLFunctions.IF,
				ExpressionUtil.createApply(PMMLFunctions.LESSTHAN,
					fieldRefs.get(0),
					ExpressionUtil.createConstant(DataType.INTEGER, 0)
				),
				ExpressionUtil.createConstant(DataType.STRING, "negative"),
				ExpressionUtil.createConstant(DataType.STRING, "zero")
			)
		);

		assertEquals(DataType.STRING, ExpressionUtil.getDataType(expected, expressionTranslator));

		string = "'positive' if X[0] > 0 else 'negative' if X[0] < 0 else 'zero'";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		string = "numpy.where(X[0] > 0, r\"positive\", numpy.where(X[0] < 0, r\"negative\", r\"zero\"))";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expected = ExpressionUtil.createApply(PMMLFunctions.IF,
			ExpressionUtil.createApply(PMMLFunctions.ISMISSING,
				fieldRefs.get(0)
			),
			ExpressionUtil.createMissingConstant(),
			ExpressionUtil.createApply(PMMLFunctions.DIVIDE,
				fieldRefs.get(0),
				fieldRefs.get(1)
			)
		);

		assertEquals(null, ExpressionUtil.getDataType(expected, expressionTranslator));

		string = "numpy.nan if numpy.isnan(X[0]) else X[0] / X[1]";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		string = "numpy.where(numpy.isnan(X[0]), numpy.nan, X[0] / X[1])";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new DataFrameScope(stringFeatures));

		expected = ExpressionUtil.createApply(PMMLFunctions.IF,
			ExpressionUtil.createApply(PMMLFunctions.ISNOTMISSING,
				fieldRefs.get(0)
			),
			fieldRefs.get(0),
			ExpressionUtil.createConstant(DataType.STRING, "missing")
		);

		assertEquals(DataType.STRING, ExpressionUtil.getDataType(expected, expressionTranslator));

		string = "X[0] if pandas.notna(X[0]) else 'missing'";

		checkExpression(expected, translateExpression(expressionTranslator, string));
	}

	@Test
	public void translateStringIfElseExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new DataFrameScope(stringFeatures));

		Expression expected = ExpressionUtil.createApply(PMMLFunctions.IF,
			ExpressionUtil.createApply(PMMLFunctions.EQUAL, ExpressionUtil.createApply(PMMLFunctions.TRIMBLANKS, ExpressionUtil.createApply(PMMLFunctions.SUBSTRING, fieldRefs.get(1), ExpressionUtil.createConstant(DataType.INTEGER, 1), ExpressionUtil.createConstant(DataType.INTEGER, 1))), ExpressionUtil.createConstant(DataType.STRING, "low")),
			ExpressionUtil.createApply(PMMLFunctions.LOWERCASE, fieldRefs.get(0)),
			ExpressionUtil.createApply(PMMLFunctions.UPPERCASE, fieldRefs.get(0))
		);

		assertEquals(DataType.STRING, ExpressionUtil.getDataType(expected, expressionTranslator));

		String string = "X[0].lower() if (X[1][0:1].strip()) == 'low' else X[0].upper()";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(stringFeatures));

		string = "a.lower() if (b[0:1].strip()) == r\"low\" else a.upper()";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new DataFrameScope(stringFeatures));

		expected = ExpressionUtil.createApply(PMMLFunctions.IF,
			ExpressionUtil.createApply(PMMLFunctions.GREATERTHAN, ExpressionUtil.createApply(PMMLFunctions.STRINGLENGTH, fieldRefs.get(0)), ExpressionUtil.createConstant(DataType.INTEGER, 0)),
			ExpressionUtil.createConstant(DataType.BOOLEAN, true),
			ExpressionUtil.createConstant(DataType.BOOLEAN, false)
		);

		assertEquals(DataType.BOOLEAN, ExpressionUtil.getDataType(expected, expressionTranslator));

		string = "True if len(X[0][:]) > 0 else False";

		checkExpression(expected, translateExpression(expressionTranslator, string));
	}

	@Test
	public void translateLogicalExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new DataFrameScope(booleanFeatures));

		Expression expected = ExpressionUtil.createApply(PMMLFunctions.OR,
			ExpressionUtil.createApply(PMMLFunctions.AND,
				fieldRefs.get("a"),
				fieldRefs.get("b")
			),
			fieldRefs.get("c")
		);

		assertEquals(DataType.BOOLEAN, ExpressionUtil.getDataType(expected, expressionTranslator));

		String string = "X[\"a\"] and X[\"b\"] or X[\"c\"]";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(booleanFeatures));

		string = "a and b or c";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		string = "numpy.logical_or(numpy.logical_and(a, b), c)";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expected = ExpressionUtil.createApply(PMMLFunctions.NOT, fieldRefs.get("a"));

		assertEquals(DataType.BOOLEAN, ExpressionUtil.getDataType(expected, expressionTranslator));

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

		Expression expected = ExpressionUtil.createApply(PMMLFunctions.AND, fieldRefs.get("a"), fieldRefs.get("b"));

		assertEquals(DataType.BOOLEAN, ExpressionUtil.getDataType(expected, expressionTranslator));

		String string = "X['a'] and X['b']";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(booleanFeatures));

		string = "a and b";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new DataFrameScope(booleanFeatures));

		expected = ExpressionUtil.createApply(PMMLFunctions.AND,
			ExpressionUtil.createApply(PMMLFunctions.EQUAL, fieldRefs.get("a"), ExpressionUtil.createConstant(DataType.BOOLEAN, true)),
			ExpressionUtil.createApply(PMMLFunctions.EQUAL, fieldRefs.get("b"), ExpressionUtil.createConstant(DataType.BOOLEAN, false))
		);

		assertEquals(DataType.BOOLEAN, ExpressionUtil.getDataType(expected, expressionTranslator));

		string = "X['a'] == True and X['b'] == False";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(booleanFeatures));

		string = "a == True and b == False";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

		expected = ExpressionUtil.createApply(PMMLFunctions.ISIN, fieldRefs.get(0), ExpressionUtil.createConstant(DataType.DOUBLE, 0.0d), ExpressionUtil.createConstant(DataType.DOUBLE, 1.0d));

		assertEquals(DataType.BOOLEAN, ExpressionUtil.getDataType(expected, expressionTranslator));

		string = "X[0] in [0.0, 1.0]";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(doubleFeatures));

		string = "a in [0.0, 1.0]";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

		expected = ExpressionUtil.createApply(PMMLFunctions.ISNOTIN, ExpressionUtil.createApply(PMMLFunctions.ADD, fieldRefs.get(0), ExpressionUtil.createConstant(DataType.DOUBLE, 1.0d)), fieldRefs.get(1));

		assertEquals(DataType.BOOLEAN, ExpressionUtil.getDataType(expected, expressionTranslator));

		string = "(X[0] + 1.0) not in [X[1]]";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(doubleFeatures));

		string = "(a + 1.0) not in [b]";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

		expected = ExpressionUtil.createApply(PMMLFunctions.GREATERTHAN, fieldRefs.get("a"), fieldRefs.get("b"));

		assertEquals(DataType.BOOLEAN, ExpressionUtil.getDataType(expected, expressionTranslator));

		string = "X[\"a\"] > X[\"b\"]";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(doubleFeatures));

		string = "a > b";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

		expected = ExpressionUtil.createApply(PMMLFunctions.NOT, ExpressionUtil.createApply(PMMLFunctions.LESSTHAN, fieldRefs.get("a"), ExpressionUtil.createConstant(DataType.DOUBLE, 0.0d)));

		assertEquals(DataType.BOOLEAN, ExpressionUtil.getDataType(expected, expressionTranslator));

		string = "not X[\"a\"] < 0.0";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(doubleFeatures));

		string = "not a < 0.0";

		checkExpression(expected, translateExpression(expressionTranslator, string));
	}

	@Test
	public void translateIdentityComparisonExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

		Expression expected = ExpressionUtil.createApply(PMMLFunctions.EQUAL,
			fieldRefs.get(0),
			ExpressionUtil.createMissingConstant()
		);

		assertEquals(DataType.BOOLEAN, ExpressionUtil.getDataType(expected, expressionTranslator));

		String string = "X[0] == None";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expected = ExpressionUtil.createApply(PMMLFunctions.ISMISSING, fieldRefs.get(0));

		assertEquals(DataType.BOOLEAN, ExpressionUtil.getDataType(expected, expressionTranslator));

		checkExpression(expected, translateExpression(expressionTranslator, string, true));

		expected = ExpressionUtil.createApply(PMMLFunctions.NOTEQUAL,
			fieldRefs.get(0),
			ExpressionUtil.createMissingConstant()
		);

		assertEquals(DataType.BOOLEAN, ExpressionUtil.getDataType(expected, expressionTranslator));

		string = "X[0] != None";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expected = ExpressionUtil.createApply(PMMLFunctions.ISNOTMISSING, fieldRefs.get(0));

		assertEquals(DataType.BOOLEAN, ExpressionUtil.getDataType(expected, expressionTranslator));

		checkExpression(expected, translateExpression(expressionTranslator, string, true));

		expected = ExpressionUtil.createApply(PMMLFunctions.ISMISSING, fieldRefs.get(0));

		assertEquals(DataType.BOOLEAN, ExpressionUtil.getDataType(expected, expressionTranslator));

		string = "X[0] is None";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(doubleFeatures));

		string = "a is None";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

		expected = ExpressionUtil.createApply(PMMLFunctions.ISNOTMISSING, ExpressionUtil.createApply(PMMLFunctions.ADD, fieldRefs.get("a"), ExpressionUtil.createConstant(DataType.INTEGER, 1)));

		assertEquals(DataType.BOOLEAN, ExpressionUtil.getDataType(expected, expressionTranslator));

		string = "(X['a'] + 1) is not None";

		checkExpression(expected, translateExpression(expressionTranslator, string));

		expressionTranslator = new ExpressionTranslator(new BlockScope(doubleFeatures));

		string = "(a + 1) is not None";

		checkExpression(expected, translateExpression(expressionTranslator, string));
	}

	@Test
	public void translateArithmeticExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

		Expression expected = ExpressionUtil.createApply(PMMLFunctions.MULTIPLY,
			ExpressionUtil.createApply(PMMLFunctions.DIVIDE,
				ExpressionUtil.createApply(PMMLFunctions.SUBTRACT,
					ExpressionUtil.createApply(PMMLFunctions.ADD,
						fieldRefs.get("a"),
						fieldRefs.get("b")
					),
					ExpressionUtil.createConstant(DataType.DOUBLE, 1.0d)
				),
				fieldRefs.get("c")
			),
			ExpressionUtil.createConstant(DataType.INTEGER, -2)
		);

		assertEquals(null, ExpressionUtil.getDataType(expected, expressionTranslator));

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

		Constant prefix = ExpressionUtil.createConstant(DataType.STRING, "19");
		FieldRef content = fieldRefs.get(0);
		Constant suffix = ExpressionUtil.createConstant(DataType.STRING, "-01-01");

		Expression expectedNonCompact = ExpressionUtil.createApply(PMMLFunctions.CONCAT, ExpressionUtil.createApply(PMMLFunctions.CONCAT, prefix, content), suffix);
		Expression expectedCompact = ExpressionUtil.createApply(PMMLFunctions.CONCAT, prefix, content, suffix);

		assertEquals(DataType.STRING, ExpressionUtil.getDataType(expectedNonCompact, expressionTranslator));
		assertEquals(DataType.STRING, ExpressionUtil.getDataType(expectedCompact, expressionTranslator));

		String string = "'19' + X[0] + '-01-01'";

		checkExpression(expectedNonCompact, translateExpression(expressionTranslator, string));
		checkExpression(expectedCompact, translateExpression(expressionTranslator, string, true));

		string = "\"19\" + X['a'] + \"-01-01\"";

		checkExpression(expectedNonCompact, translateExpression(expressionTranslator, string));
		checkExpression(expectedCompact, translateExpression(expressionTranslator, string, true));

		expressionTranslator = new ExpressionTranslator(new BlockScope(stringFeatures));

		string = "r\"19\" + a + r\"-01-01\"";

		checkExpression(expectedNonCompact, translateExpression(expressionTranslator, string));
		checkExpression(expectedCompact, translateExpression(expressionTranslator, string, true));
	}

	@Test
	public void translateUnaryExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(BlockScope.EMPTY);

		Constant minusOne = ExpressionUtil.createConstant(DataType.INTEGER, -1);
		Constant plusOne = ExpressionUtil.createConstant(DataType.INTEGER, 1);

		assertEquals(DataType.INTEGER, ExpressionUtil.getDataType(minusOne, expressionTranslator));
		assertEquals(DataType.INTEGER, ExpressionUtil.getDataType(plusOne, expressionTranslator));

		checkExpression(minusOne, translateExpression(expressionTranslator, "-1"));

		checkExpression(plusOne, translateExpression(expressionTranslator, "1"));
		checkExpression(plusOne, translateExpression(expressionTranslator, "+1"));

		checkExpression(minusOne, translateExpression(expressionTranslator, "-+1"));
		checkExpression(plusOne, translateExpression(expressionTranslator, "--1"));
		checkExpression(minusOne, translateExpression(expressionTranslator, "---1"));
	}

	@Test
	public void translatePowerExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(BlockScope.EMPTY);

		Map<String, ?> arguments = Collections.emptyMap();

		assertEquals(2, evaluateExpression(expressionTranslator, "2 ** 1 ** 3", arguments));
		assertEquals(8, evaluateExpression(expressionTranslator, "(2 ** 1) ** 3", arguments));

		assertEquals(0.5d, evaluateExpression(expressionTranslator, "2.0 ** -1", arguments));
	}

	@Test
	public void translateArrayIndexingExpression(){
		List<Feature> features = booleanFeatures;

		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new DataFrameScope(features));

		try {
			translateExpression(expressionTranslator, "X[[0]]");

			fail();
		} catch(TranslationException te){
			// Ignored
		}

		for(int i = 0; i < features.size(); i++){
			Feature feature = features.get(i);

			FieldRef fieldRef = feature.ref();

			checkExpression(fieldRef, translateExpression(expressionTranslator, "X[" + "+" + i + "]"));

			checkExpression(fieldRef, translateExpression(expressionTranslator, "X[:, " + "+" + i + "]"));
			checkExpression(fieldRef, translateExpression(expressionTranslator, "X[:, [" + "+" + i + "]]"));
		}

		try {
			translateExpression(expressionTranslator, "X[" + features.size() + "]");

			fail();
		} catch(TranslationException te){
			// Ignored
		}

		for(int i = 1; i <= features.size(); i++){
			Feature feature = features.get(features.size() - i);

			FieldRef fieldRef = feature.ref();

			checkExpression(fieldRef, translateExpression(expressionTranslator, "X[" + "-" + i +"]"));

			checkExpression(fieldRef, translateExpression(expressionTranslator, "X[:, " + "-" + i + "]"));
			checkExpression(fieldRef, translateExpression(expressionTranslator, "X[:, [" + "-" + i + "]]"));
		}

		try {
			translateExpression(expressionTranslator, "X[" + "-" + (features.size() + 1) + "]");

			fail();
		} catch(TranslationException te){
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

		Expression expected = ExpressionUtil.createMissingConstant();

		assertEquals(null, ExpressionUtil.getDataType(expected, expressionTranslator));

		String[] strings = {"math.nan", "numpy.nan", "numpy.NaN", "numpy.NAN", "pandas.NA", "pandas.NaT"};
		for(String string : strings){
			checkExpression(expected, translateExpression(expressionTranslator, string));
		}

		expected = ExpressionUtil.createConstant(DataType.DOUBLE, Math.E);

		strings = new String[]{"math.e", "numpy.e"};
		for(String string : strings){
			checkExpression(expected, translateExpression(expressionTranslator, string));
		}

		expected = ExpressionUtil.createConstant(DataType.DOUBLE, Math.PI);

		strings = new String[]{"math.pi", "numpy.pi"};
		for(String string : strings){
			checkExpression(expected, translateExpression(expressionTranslator, string));
		}

		expected = ExpressionUtil.createConstant(DataType.DOUBLE, 2 * Math.PI);

		checkExpression(expected, translateExpression(expressionTranslator, "math.tau"));
	}

	@Test
	public void translateFunctionInvocationExpression(){
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(new DataFrameScope(doubleFeatures));

		Expression expected = ExpressionUtil.createApply(PMMLFunctions.IF,
			ExpressionUtil.createApply(PMMLFunctions.ISNOTMISSING, fieldRefs.get("a")),
			fieldRefs.get("a"),
			ExpressionUtil.createApply(PMMLFunctions.ADD, fieldRefs.get("b"), fieldRefs.get("c"))
		);

		assertEquals(null, ExpressionUtil.getDataType(expected, expressionTranslator));

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

		assertEquals(true, evaluateExpression(expressionTranslator, "x.startswith('Hello')", arguments));
		assertEquals(true, evaluateExpression(expressionTranslator, "x.startswith(\"Hello\")", arguments));
		assertEquals(true, evaluateExpression(expressionTranslator, "x.startswith('''Hello''')", arguments));
		assertEquals(true, evaluateExpression(expressionTranslator, "x.startswith(\"\"\"Hello\"\"\")", arguments));

		assertEquals(false, evaluateExpression(expressionTranslator, "x.startswith('Hello!')", arguments));

		assertEquals(true, evaluateExpression(expressionTranslator, "x.endswith('World!')", arguments));
		assertEquals(true, evaluateExpression(expressionTranslator, "x.endswith(\"World!\")", arguments));
		assertEquals(true, evaluateExpression(expressionTranslator, "x.endswith('''World!''')", arguments));
		assertEquals(true, evaluateExpression(expressionTranslator, "x.endswith(\"\"\"World!\"\"\")", arguments));

		assertEquals(false, evaluateExpression(expressionTranslator, "x.endswith(\"World\")", arguments));
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

		VirtualEvaluationContext context = new VirtualEvaluationContext();
		context.declareAll(arguments);

		FieldValue value = org.jpmml.evaluator.ExpressionUtil.evaluate(expression, context);

		return FieldValueUtil.getValue(value);
	}
}