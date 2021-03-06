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
import java.util.List;

import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.PMMLFunctions;
import org.jpmml.converter.Feature;
import org.jpmml.converter.PMMLUtil;
import org.jpmml.model.ReflectionUtil;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ExpressionTranslatorTest extends TranslatorTest {

	@Test
	public void translateLogicalExpression(){
		Expression expected = PMMLUtil.createApply(PMMLFunctions.OR,
			PMMLUtil.createApply(PMMLFunctions.AND,
				new FieldRef(FieldName.create("a")),
				new FieldRef(FieldName.create("b"))
			),
			new FieldRef(FieldName.create("c"))
		);

		String string = "X[\"a\"] and X[\"b\"] or X[\"c\"]";

		checkExpression(expected, string, new DataFrameScope(booleanFeatures));

		string = "a and b or c";

		checkExpression(expected, string, new BlockScope(booleanFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.NOT, new FieldRef(FieldName.create("a")));

		string = "not X[\"a\"]";

		checkExpression(expected, string, new DataFrameScope(booleanFeatures));

		string = "not a";

		checkExpression(expected, string, new BlockScope(booleanFeatures));
	}

	@Test
	public void translateIdentityComparisonExpression(){
		FieldRef first = new FieldRef(FieldName.create("a"));

		Expression expected = PMMLUtil.createApply(PMMLFunctions.ISMISSING, first);

		String string = "X[0] is None";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "a is None";

		checkExpression(expected, string, new BlockScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.ISNOTMISSING, PMMLUtil.createApply(PMMLFunctions.ADD, first, PMMLUtil.createConstant("1", DataType.INTEGER)));

		string = "(X['a'] + 1) is not None";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "(a + 1) is not None";

		checkExpression(expected, string, new BlockScope(doubleFeatures));
	}

	@Test
	public void translateComparisonExpression(){
		FieldRef first = new FieldRef(FieldName.create("a"));
		FieldRef second = new FieldRef(FieldName.create("b"));

		Expression expected = PMMLUtil.createApply(PMMLFunctions.AND, first, second);

		String string = "X['a'] and X['b']";

		checkExpression(expected, string, new DataFrameScope(booleanFeatures));

		string = "a and b";

		checkExpression(expected, string, new BlockScope(booleanFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.AND,
			PMMLUtil.createApply(PMMLFunctions.EQUAL, first, PMMLUtil.createConstant("true", DataType.BOOLEAN)),
			PMMLUtil.createApply(PMMLFunctions.EQUAL, second, PMMLUtil.createConstant("false", DataType.BOOLEAN))
		);

		string = "X['a'] == True and X['b'] == False";

		checkExpression(expected, string, new DataFrameScope(booleanFeatures));

		string = "a == True and b == False";

		checkExpression(expected, string, new BlockScope(booleanFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.ISIN, first, PMMLUtil.createConstant("0.0", DataType.DOUBLE), PMMLUtil.createConstant("1.0", DataType.DOUBLE));

		string = "X[0] in [0.0, 1.0]";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "a in [0.0, 1.0]";

		checkExpression(expected, string, new BlockScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.ISNOTIN, PMMLUtil.createApply(PMMLFunctions.ADD, first, PMMLUtil.createConstant("1.0", DataType.DOUBLE)), second);

		string = "(X[0] + 1.0) not in [X[1]]";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "(a + 1.0) not in [b]";

		checkExpression(expected, string, new BlockScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.GREATERTHAN, first, second);

		string = "X[\"a\"] > X[\"b\"]";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "a > b";

		checkExpression(expected, string, new BlockScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.NOT, PMMLUtil.createApply(PMMLFunctions.LESSTHAN, first, PMMLUtil.createConstant("0.0", DataType.DOUBLE)));

		string = "not X[\"a\"] < 0.0";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "not a < 0.0";

		checkExpression(expected, string, new BlockScope(doubleFeatures));
	}

	@Test
	public void translateArithmeticExpression(){
		Expression expected = PMMLUtil.createApply(PMMLFunctions.MULTIPLY,
			PMMLUtil.createApply(PMMLFunctions.DIVIDE,
				PMMLUtil.createApply(PMMLFunctions.SUBTRACT,
					PMMLUtil.createApply(PMMLFunctions.ADD,
						new FieldRef(FieldName.create("a")),
						new FieldRef(FieldName.create("b"))
					),
					PMMLUtil.createConstant("1.0", DataType.DOUBLE)
				),
				new FieldRef(FieldName.create("c"))
			),
			PMMLUtil.createConstant("-2", DataType.INTEGER)
		);

		String string = "(X[0] + X[1] - 1.0) / X[2] * -2";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "(X[\"a\"] + X[\"b\"] - 1.0) / X['c'] * -2";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "(a + b - 1.0) / c * -2";

		checkExpression(expected, string, new BlockScope(doubleFeatures));
	}

	@Test
	public void translateStringConcatenationExpression(){
		Constant prefix = PMMLUtil.createConstant("19", DataType.STRING);
		FieldRef content = new FieldRef(FieldName.create("a"));
		Constant suffix = PMMLUtil.createConstant("-01-01", DataType.STRING);

		Expression expected = PMMLUtil.createApply(PMMLFunctions.CONCAT, PMMLUtil.createApply(PMMLFunctions.CONCAT, prefix, content), suffix);
		Expression expectedCompact = PMMLUtil.createApply(PMMLFunctions.CONCAT, prefix, content, suffix);

		String string = "\'19\' + X[0] + \'-01-01\'";

		Scope scope = new DataFrameScope(stringFeatures);

		checkExpression(expected, string, scope);
		checkExpressionCompact(expectedCompact, string, scope);

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
			PMMLUtil.createApply(PMMLFunctions.EQUAL, PMMLUtil.createApply(PMMLFunctions.TRIMBLANKS, PMMLUtil.createApply(PMMLFunctions.SUBSTRING, new FieldRef(FieldName.create("b")), PMMLUtil.createConstant(1, DataType.INTEGER), PMMLUtil.createConstant(1, DataType.INTEGER))), PMMLUtil.createConstant("low", DataType.STRING)),
			PMMLUtil.createApply(PMMLFunctions.LOWERCASE, new FieldRef(FieldName.create("a"))),
			PMMLUtil.createApply(PMMLFunctions.UPPERCASE, new FieldRef(FieldName.create("a")))
		);

		String string = "X[0].lower() if (X[1][0:1].strip()) == \'low\' else X[0].upper()";

		checkExpression(expected, string, new DataFrameScope(stringFeatures));

		string = "a.lower() if (b[0:1].strip()) == \'low\' else a.upper()";

		checkExpression(expected, string, new BlockScope(stringFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.IF,
			PMMLUtil.createApply(PMMLFunctions.GREATERTHAN, PMMLUtil.createApply(PMMLFunctions.STRINGLENGTH, new FieldRef(FieldName.create("a"))), PMMLUtil.createConstant("0", DataType.INTEGER)),
			PMMLUtil.createConstant("true", DataType.BOOLEAN),
			PMMLUtil.createConstant("false", DataType.BOOLEAN)
		);

		string = "True if len(X[0][:]) > 0 else False";

		checkExpression(expected, string, new DataFrameScope(stringFeatures));
	}

	@Test
	public void translateUnaryExpression(){
		Constant minusOne = PMMLUtil.createConstant("-1", DataType.INTEGER);
		Constant plusOne = PMMLUtil.createConstant("1", DataType.INTEGER);

		Scope scope = new BlockScope(Collections.emptyList());

		checkExpression(minusOne, "-1", scope);

		checkExpression(plusOne, "1", scope);
		checkExpression(plusOne, "+1", scope);

		checkExpression(minusOne, "-+1", scope);
		checkExpression(plusOne, "--1", scope);
		checkExpression(minusOne, "---1", scope);
	}

	@Test
	public void translateFunctionInvocationExpression(){
		Expression expected = PMMLUtil.createApply(PMMLFunctions.IF,
			PMMLUtil.createApply(PMMLFunctions.ISNOTMISSING, new FieldRef(FieldName.create("a"))),
			new FieldRef(FieldName.create("a")),
			PMMLUtil.createApply(PMMLFunctions.ADD, new FieldRef(FieldName.create("b")), new FieldRef(FieldName.create("c")))
		);

		String string = "X[\"a\"] if pandas.notnull(X[\"a\"]) else X[\"b\"] + X[\"c\"]";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

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

			checkExpression(expected, "X[" + "+" + i + "]", scope);
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

			checkExpression(expected, "X[" + "-" + i +"]", scope);
		}

		try {
			ExpressionTranslator.translate("X[" + "-" + (features.size() + 1) + "]", scope);

			fail();
		} catch(IllegalArgumentException iae){
			// Ignored
		}
	}

	static
	private void checkExpression(Expression expected, String string, Scope scope){
		Expression actual = ExpressionTranslator.translate(string, scope, false);

		assertTrue(ReflectionUtil.equals(expected, actual));
	}

	static
	private void checkExpressionCompact(Expression expected, String string, Scope scope){
		Expression actual = ExpressionTranslator.translate(string, scope, true);

		assertTrue(ReflectionUtil.equals(expected, actual));
	}
}