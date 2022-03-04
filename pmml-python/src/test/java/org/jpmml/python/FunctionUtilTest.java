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

import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldRef;
import org.jpmml.evaluator.EvaluationContext;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.FieldValueUtil;
import org.jpmml.evaluator.VirtualEvaluationContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FunctionUtilTest {

	@Test
	public void canonicalizeModule(){
		assertEquals("builtins", FunctionUtil.canonicalizeModule(""));
		assertEquals("builtins", FunctionUtil.canonicalizeModule("builtins"));

		assertEquals("prefix.suffix", FunctionUtil.canonicalizeModule("prefix.suffix"));

		assertEquals("numpy", FunctionUtil.canonicalizeModule("np"));
		assertEquals("numpy.suffix", FunctionUtil.canonicalizeModule("np.suffix"));
		assertEquals("numpy.suffix", FunctionUtil.canonicalizeModule("numpy.suffix"));

		assertEquals("prefix.np.suffix", FunctionUtil.canonicalizeModule("prefix.np.suffix"));

		assertEquals("pandas", FunctionUtil.canonicalizeModule("pd"));
		assertEquals("pandas.suffix", FunctionUtil.canonicalizeModule("pd.suffix"));
		assertEquals("pandas.suffix", FunctionUtil.canonicalizeModule("pandas.suffix"));

		assertEquals("prefix.pd.suffix", FunctionUtil.canonicalizeModule("prefix.pd.suffix"));
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

		assertEquals(Double.NaN, evaluateExpression("scipy.special", "logit", -5d));
		assertEquals(Double.NEGATIVE_INFINITY, evaluateExpression("scipy.special", "logit", 0d));
		assertEquals(-1.5d, (Double)evaluateExpression("scipy.special", "logit", 0.18242552d), 3e-8);
		assertEquals(0d, (Double)evaluateExpression("scipy.special", "logit", 0.5d), 1e-8);
		assertEquals(1.5d, (Double)evaluateExpression("scipy.special", "logit", 0.81757448d), 3e-8);
		assertEquals(Double.POSITIVE_INFINITY, evaluateExpression("scipy.special", "logit", 1d));
		assertEquals(Double.NaN, evaluateExpression("scipy.special", "logit", 5d));
	}

	static
	private Object evaluateExpression(String module, String name, Object argument){
		FieldRef fieldRef = new FieldRef("x");

		Expression expression = FunctionUtil.encodeFunction(module, name, Collections.singletonList(fieldRef));

		EvaluationContext context = new VirtualEvaluationContext();
		context.declare(fieldRef.requireField(), FieldValueUtil.create(argument));

		FieldValue value = org.jpmml.evaluator.ExpressionUtil.evaluate(expression, context);

		return FieldValueUtil.getValue(value);
	}
}