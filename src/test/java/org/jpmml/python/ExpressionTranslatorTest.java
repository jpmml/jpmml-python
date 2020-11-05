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

import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.PMMLFunctions;
import org.jpmml.converter.PMMLUtil;
import org.jpmml.model.ReflectionUtil;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ExpressionTranslatorTest extends TranslatorTest {

	@Test
	public void translateLogicalExpression(){
		Expression expected = PMMLUtil.createApply(PMMLFunctions.OR)
			.addExpressions(PMMLUtil.createApply(PMMLFunctions.AND)
				.addExpressions(new FieldRef(FieldName.create("a")), new FieldRef(FieldName.create("b")))
			)
			.addExpressions(new FieldRef(FieldName.create("c")));

		String string = "X[\"a\"] and X[\"b\"] or X[\"c\"]";

		checkExpression(expected, string, new DataFrameScope(booleanFeatures));

		string = "a and b or c";

		checkExpression(expected, string, new BlockScope(booleanFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.NOT)
			.addExpressions(new FieldRef(FieldName.create("a")));

		string = "not X[\"a\"]";

		checkExpression(expected, string, new DataFrameScope(booleanFeatures));

		string = "not a";

		checkExpression(expected, string, new BlockScope(booleanFeatures));
	}

	@Test
	public void translateIdentityComparisonExpression(){
		FieldRef first = new FieldRef(FieldName.create("a"));

		Expression expected = PMMLUtil.createApply(PMMLFunctions.ISMISSING)
			.addExpressions(first);

		String string = "X[0] is None";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "a is None";

		checkExpression(expected, string, new BlockScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.ISNOTMISSING)
			.addExpressions(PMMLUtil.createApply(PMMLFunctions.ADD)
				.addExpressions(first, PMMLUtil.createConstant("1", DataType.INTEGER))
			);

		string = "(X['a'] + 1) is not None";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "(a + 1) is not None";

		checkExpression(expected, string, new BlockScope(doubleFeatures));
	}

	@Test
	public void translateComparisonExpression(){
		FieldRef first = new FieldRef(FieldName.create("a"));
		FieldRef second = new FieldRef(FieldName.create("b"));

		Expression expected = PMMLUtil.createApply(PMMLFunctions.AND)
			.addExpressions(first, second);

		String string = "X['a'] and X['b']";

		checkExpression(expected, string, new DataFrameScope(booleanFeatures));

		string = "a and b";

		checkExpression(expected, string, new BlockScope(booleanFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.AND)
			.addExpressions(PMMLUtil.createApply(PMMLFunctions.EQUAL)
				.addExpressions(first, PMMLUtil.createConstant("true", DataType.BOOLEAN))
			)
			.addExpressions(PMMLUtil.createApply(PMMLFunctions.EQUAL)
				.addExpressions(second, PMMLUtil.createConstant("false", DataType.BOOLEAN))
			);

		string = "X['a'] == True and X['b'] == False";

		checkExpression(expected, string, new DataFrameScope(booleanFeatures));

		string = "a == True and b == False";

		checkExpression(expected, string, new BlockScope(booleanFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.ISIN)
			.addExpressions(first, PMMLUtil.createConstant("0.0", DataType.DOUBLE), PMMLUtil.createConstant("1.0", DataType.DOUBLE));

		string = "X[0] in [0.0, 1.0]";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "a in [0.0, 1.0]";

		checkExpression(expected, string, new BlockScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.ISNOTIN)
			.addExpressions(PMMLUtil.createApply(PMMLFunctions.ADD)
				.addExpressions(first, PMMLUtil.createConstant("1.0", DataType.DOUBLE))
			)
			.addExpressions(second);

		string = "(X[0] + 1.0) not in [X[1]]";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "(a + 1.0) not in [b]";

		checkExpression(expected, string, new BlockScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.GREATERTHAN)
			.addExpressions(first, second);

		string = "X[\"a\"] > X[\"b\"]";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "a > b";

		checkExpression(expected, string, new BlockScope(doubleFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.NOT)
			.addExpressions(PMMLUtil.createApply(PMMLFunctions.LESSTHAN)
				.addExpressions(first, PMMLUtil.createConstant("0.0", DataType.DOUBLE))
			);

		string = "not X[\"a\"] < 0.0";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "not a < 0.0";

		checkExpression(expected, string, new BlockScope(doubleFeatures));
	}

	@Test
	public void translateArithmeticExpression(){
		Expression expected = PMMLUtil.createApply(PMMLFunctions.MULTIPLY)
			.addExpressions(PMMLUtil.createApply(PMMLFunctions.DIVIDE)
				.addExpressions(PMMLUtil.createApply(PMMLFunctions.SUBTRACT)
					.addExpressions(PMMLUtil.createApply(PMMLFunctions.ADD)
						.addExpressions(new FieldRef(FieldName.create("a")), new FieldRef(FieldName.create("b")))
					)
					.addExpressions(PMMLUtil.createConstant("1.0", DataType.DOUBLE))
				)
				.addExpressions(new FieldRef(FieldName.create("c")))
			)
			.addExpressions(PMMLUtil.createConstant("-2", DataType.INTEGER));

		String string = "(X[0] + X[1] - 1.0) / X[2] * -2";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "(X[\"a\"] + X[\"b\"] - 1.0) / X['c'] * -2";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "(a + b - 1.0) / c * -2";

		checkExpression(expected, string, new BlockScope(doubleFeatures));
	}

	@Test
	public void translateStringConcatenationExpression(){
		Expression expected = PMMLUtil.createApply(PMMLFunctions.CONCAT)
			.addExpressions(PMMLUtil.createApply(PMMLFunctions.CONCAT)
				.addExpressions(PMMLUtil.createConstant("19", DataType.STRING), new FieldRef(FieldName.create("a")))
			)
			.addExpressions(PMMLUtil.createConstant("-01-01", DataType.STRING));

		String string = "\'19\' + X[0] + \'-01-01\'";

		checkExpression(expected, string, new DataFrameScope(stringFeatures));

		string = "\"19\" + X[\'a\'] + \"-01-01\"";

		checkExpression(expected, string, new DataFrameScope(stringFeatures));

		string = "\"19\" + a + \"-01-01\"";

		checkExpression(expected, string, new BlockScope(stringFeatures));
	}

	@Test
	public void translateStringIfElseExpression(){
		Expression expected = PMMLUtil.createApply(PMMLFunctions.IF)
			.addExpressions(PMMLUtil.createApply(PMMLFunctions.EQUAL)
				.addExpressions(PMMLUtil.createApply(PMMLFunctions.TRIMBLANKS)
					.addExpressions(PMMLUtil.createApply(PMMLFunctions.SUBSTRING)
						.addExpressions(new FieldRef(FieldName.create("b")))
						.addExpressions(PMMLUtil.createConstant(1, DataType.INTEGER), PMMLUtil.createConstant(1, DataType.INTEGER))
					)
				)
				.addExpressions(PMMLUtil.createConstant("low", DataType.STRING))
			)
			.addExpressions(PMMLUtil.createApply(PMMLFunctions.LOWERCASE)
				.addExpressions(new FieldRef(FieldName.create("a")))
			)
			.addExpressions(PMMLUtil.createApply(PMMLFunctions.UPPERCASE)
				.addExpressions(new FieldRef(FieldName.create("a")))
			);

		String string = "X[0].lower() if (X[1][0:1].strip()) == \'low\' else X[0].upper()";

		checkExpression(expected, string, new DataFrameScope(stringFeatures));

		string = "a.lower() if (b[0:1].strip()) == \'low\' else a.upper()";

		checkExpression(expected, string, new BlockScope(stringFeatures));

		expected = PMMLUtil.createApply(PMMLFunctions.IF)
			.addExpressions(PMMLUtil.createApply(PMMLFunctions.GREATERTHAN)
				.addExpressions(PMMLUtil.createApply(PMMLFunctions.STRINGLENGTH)
					.addExpressions(new FieldRef(FieldName.create("a")))
				)
				.addExpressions(PMMLUtil.createConstant("0", DataType.INTEGER))
			)
			.addExpressions(PMMLUtil.createConstant("true", DataType.BOOLEAN))
			.addExpressions(PMMLUtil.createConstant("false", DataType.BOOLEAN));

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
		Expression expected = PMMLUtil.createApply(PMMLFunctions.IF)
			.addExpressions(PMMLUtil.createApply(PMMLFunctions.ISNOTMISSING)
				.addExpressions(new FieldRef(FieldName.create("a")))
			)
			.addExpressions(new FieldRef(FieldName.create("a")))
			.addExpressions(PMMLUtil.createApply(PMMLFunctions.ADD)
				.addExpressions(new FieldRef(FieldName.create("b")), new FieldRef(FieldName.create("c")))
			);

		String string = "X[\"a\"] if pandas.notnull(X[\"a\"]) else X[\"b\"] + X[\"c\"]";

		checkExpression(expected, string, new DataFrameScope(doubleFeatures));

		string = "a if pandas.notnull(a) else b + c";

		checkExpression(expected, string, new BlockScope(doubleFeatures));
	}

	static
	private void checkExpression(Expression expected, String string, Scope scope){
		Expression actual = ExpressionTranslator.translate(string, scope, false);

		assertTrue(ReflectionUtil.equals(expected, actual));
	}
}