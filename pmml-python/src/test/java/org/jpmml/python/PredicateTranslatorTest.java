/*
 * Copyright (c) 2018 Villu Ruusmann
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

import org.dmg.pmml.Array;
import org.dmg.pmml.ComplexArray;
import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.False;
import org.dmg.pmml.OpType;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.SimpleSetPredicate;
import org.dmg.pmml.True;
import org.jpmml.converter.ContinuousFeature;
import org.jpmml.converter.Feature;
import org.jpmml.converter.PMMLEncoder;
import org.jpmml.model.ReflectionUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PredicateTranslatorTest extends TranslatorTest {

	@Test
	public void translateLogicalPredicate(){
		PredicateTranslator predicateTranslator = new PredicateTranslator(new DataFrameScope(doubleFeatures));

		Predicate first = new SimplePredicate("a", SimplePredicate.Operator.GREATER_THAN, 0d);
		Predicate second = new SimplePredicate("b", SimplePredicate.Operator.GREATER_THAN, 0);
		Predicate third = new SimplePredicate("c", SimplePredicate.Operator.GREATER_THAN, 0);

		Predicate expected = new CompoundPredicate(CompoundPredicate.BooleanOperator.OR, null)
			.addPredicates(new CompoundPredicate(CompoundPredicate.BooleanOperator.AND, null)
				.addPredicates(first, second)
			)
			.addPredicates(third);

		String string = "X[0] > 0.0 and X[1] > 0 or X[2] > 0";

		checkPredicate(expected, translatePredicate(predicateTranslator, string));

		string = "X[:, 0] > 0.0 and X[:, [1]] > 0 or X[:, [2]] > 0";

		checkPredicate(expected, translatePredicate(predicateTranslator, string));

		expected = new CompoundPredicate(CompoundPredicate.BooleanOperator.AND, null)
			.addPredicates(first)
			.addPredicates(new CompoundPredicate(CompoundPredicate.BooleanOperator.OR, null)
				.addPredicates(second, third)
			);

		string = "(X[\"a\"] > 0.0) and ((X[\"b\"] > 0) or (X[\"c\"] > 0))";

		checkPredicate(expected, translatePredicate(predicateTranslator, string));
	}

	@Test
	public void translateDoubleComparisonPredicate(){
		PredicateTranslator predicateTranslator = new PredicateTranslator(new DataFrameScope(doubleFeatures));

		PredicateTranslationException exception = assertThrows(PredicateTranslationException.class, () -> translatePredicate(predicateTranslator, "X['a'] > X['b']"));

		assertInstanceOf(OperationException.class, exception.getCause());

		Predicate expected = new SimplePredicate("a", SimplePredicate.Operator.GREATER_THAN, 0d);

		checkPredicate(expected, translatePredicate(predicateTranslator, "X['a'] > 0.0"));

		expected = new SimplePredicate("a", SimplePredicate.Operator.IS_MISSING, null);

		checkPredicate(expected, translatePredicate(predicateTranslator, "X[0] is None"));

		expected = new SimplePredicate("a", SimplePredicate.Operator.IS_NOT_MISSING, null);

		checkPredicate(expected, translatePredicate(predicateTranslator, "X[-3] is not None"));

		expected = createSimpleSetPredicate("a", SimpleSetPredicate.BooleanOperator.IS_IN, Array.Type.INT, Arrays.asList(1, 2, 3));

		checkPredicate(expected, translatePredicate(predicateTranslator, "X[0] in [1, 2, 3]"));

		expected = createSimpleSetPredicate("a", SimpleSetPredicate.BooleanOperator.IS_NOT_IN, Array.Type.REAL, Arrays.asList(-1.5d, -1d, -0.5d, 0d));

		checkPredicate(expected, translatePredicate(predicateTranslator, "X['a'] not in [-1.5, -1.0, -0.5, 0.0]"));
	}

	@Test
	public void translateStringComparisonPredicates(){
		PredicateTranslator predicateTranslator = new PredicateTranslator(new DataFrameScope(stringFeatures));

		Predicate expected = new SimplePredicate("a", SimplePredicate.Operator.EQUAL, "one");

		checkPredicate(expected, translatePredicate(predicateTranslator, "X[0] == \"one\""));

		expected = createSimpleSetPredicate("a", SimpleSetPredicate.BooleanOperator.IS_IN, Array.Type.STRING, Arrays.asList("one", "two", "three", "four"));

		checkPredicate(expected, translatePredicate(predicateTranslator, "X[0] in ['one', \"two\", '''three''', \"\"\"four\"\"\"]"));
	}

	@Test
	public void translateFunctionInvocationPredicate(){
		PMMLEncoder encoder = new PMMLEncoder();

		PredicateTranslator predicateTranslator = new PredicateTranslator(new DataFrameScope(doubleFeatures, encoder));

		Predicate expected = new SimplePredicate("a", SimplePredicate.Operator.IS_MISSING, null);

		checkPredicate(expected, translatePredicate(predicateTranslator, "math.isnan(X[0])"));
		checkPredicate(expected, translatePredicate(predicateTranslator, "numpy.isnan(X[0])"));
		checkPredicate(expected, translatePredicate(predicateTranslator, "pandas.isnull(X[0])"));

		checkPredicate(expected, translatePredicate(predicateTranslator, "pandas.isnull(X[:, 0])"));

		expected = new SimplePredicate("a", SimplePredicate.Operator.IS_NOT_MISSING, null);

		checkPredicate(expected, translatePredicate(predicateTranslator, "pandas.notnull(X[0])"));

		checkPredicate(expected, translatePredicate(predicateTranslator, "pandas.notnull(X[:, 0])"));

		expected = new CompoundPredicate(CompoundPredicate.BooleanOperator.AND, null)
			.addPredicates(new SimplePredicate("a", SimplePredicate.Operator.IS_NOT_MISSING, null))
			.addPredicates(new SimplePredicate("b", SimplePredicate.Operator.IS_NOT_MISSING, null))
			.addPredicates(new SimplePredicate("c", SimplePredicate.Operator.IS_NOT_MISSING, null));

		checkPredicate(expected, translatePredicate(predicateTranslator, "pandas.notnull(X[0]) and pandas.notnull(X[1]) and pandas.notnull(X[2])"));
	}

	@Test
	public void translateUDFPredicate(){
		PMMLEncoder encoder = new PMMLEncoder();

		List<Feature> features = Arrays.asList(
			createContinuousDoubleFeature(encoder, "a"),
			createContinuousDoubleFeature(encoder, "b"),
			createContinuousDoubleFeature(encoder, "c")
		);

		PredicateTranslator predicateTranslator = new PredicateTranslator(new DataFrameScope("X", features, encoder));

		String newline = "\n";

		String signumDef =
			"def signum(x):" + newline +
			"	if is_negative(x):" + newline +
			"		return -1" + newline +
			"	elif is_positive(x):" + newline +
			"		return 1" + newline +
			"	else:" + newline +
			"		return 0" + newline;

		predicateTranslator.addFunctionDef(signumDef);

		String isNegativeDef =
			"def is_negative(x):" + newline +
			"	return (x < 0)" + newline;

		String isPositiveDef =
			"def is_positive(x):" + newline +
			"	return (x > 0)" + newline;

		predicateTranslator.addFunctionDef(isNegativeDef);
		predicateTranslator.addFunctionDef(isPositiveDef);

		Predicate expected = new SimplePredicate("signum(a)", SimplePredicate.Operator.NOT_EQUAL, 0);

		checkPredicate(expected, translatePredicate(predicateTranslator, "signum(X[0]) != 0"));

		assertNotNull(encoder.getDerivedField("signum(a)"));
		assertNull(encoder.getDerivedField("signum(b)"));

		assertNotNull(encoder.getDefineFunction("is_negative"));
		assertNotNull(encoder.getDefineFunction("is_positive"));

		expected = new SimplePredicate("signum(b)", SimplePredicate.Operator.NOT_EQUAL, 0);

		checkPredicate(expected, translatePredicate(predicateTranslator, "signum(X['b']) != 0"));

		assertNotNull(encoder.getDerivedField("signum(a)"));
		assertNotNull(encoder.getDerivedField("signum(b)"));

		expected = new CompoundPredicate(CompoundPredicate.BooleanOperator.OR, null)
			.addPredicates(new SimplePredicate("is_positive(c)", SimplePredicate.Operator.EQUAL, true))
			.addPredicates(new SimplePredicate("is_negative(c)", SimplePredicate.Operator.EQUAL, true));

		checkPredicate(expected, translatePredicate(predicateTranslator, "(is_positive(X[2]) == True) or (is_negative(X['c']) == True)"));

		assertNull(encoder.getDerivedField("signum(c)"));

		assertNotNull(encoder.getDerivedField("is_negative(c)"));
		assertNotNull(encoder.getDerivedField("is_positive(c)"));
	}

	@Test
	public void translateBooleanPredicate(){
		PredicateTranslator predicateTranslator = new PredicateTranslator(new DataFrameScope(booleanFeatures));

		Predicate expected = new SimplePredicate("a", SimplePredicate.Operator.EQUAL, true);

		checkPredicate(expected, translatePredicate(predicateTranslator, "X[0]"));

		checkPredicate(expected, translatePredicate(predicateTranslator, "X[:, 0]"));

		checkPredicate(expected, translatePredicate(predicateTranslator, "X['a']"));

		predicateTranslator = new PredicateTranslator(new BlockScope(booleanFeatures));

		checkPredicate(expected, translatePredicate(predicateTranslator, "a"));
	}

	@Test
	public void translateLiteralPredicate(){
		PredicateTranslator predicateTranslator = new PredicateTranslator(BlockScope.EMPTY);

		checkPredicate(True.INSTANCE, translatePredicate(predicateTranslator, "True"));
		checkPredicate(False.INSTANCE, translatePredicate(predicateTranslator, "False"));
	}

	static
	private void checkPredicate(Predicate expected, Predicate actual){
		assertTrue(ReflectionUtil.equals(expected, actual));
	}

	static
	private Predicate translatePredicate(PredicateTranslator predicateTranslator, String string){
		return predicateTranslator.translatePredicate(string);
	}

	static
	private SimpleSetPredicate createSimpleSetPredicate(String fieldName, SimpleSetPredicate.BooleanOperator booleanOperator, Array.Type type, List<Object> values){
		Array array = new ComplexArray()
			.setType(type)
			.setValue(values);

		return new SimpleSetPredicate(fieldName, booleanOperator, array);
	}

	static
	private Feature createContinuousDoubleFeature(PMMLEncoder encoder, String name){
		DataField dataField = encoder.createDataField(name, OpType.CONTINUOUS, DataType.DOUBLE);

		return new ContinuousFeature(encoder, dataField);
	}
}