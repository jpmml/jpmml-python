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
package numpy.core;

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
	public void evaluateNumpyFunction(){
		assertEquals(3d, evaluateExpression("absolute", -3d));

		assertEquals(-2, evaluateExpression("ceil", -2.75d));
		assertEquals(3, evaluateExpression("ceil", 2.75d));

		assertEquals(-3, evaluateExpression("floor", -2.75d));
		assertEquals(2, evaluateExpression("floor", 2.75d));

		assertEquals(-3, evaluateExpression("negative", 3));
		assertEquals(-3f, evaluateExpression("negative", 3f));
		assertEquals(-3d, evaluateExpression("negative", 3d));

		assertEquals(1f / 3f, (Float)evaluateExpression("reciprocal", 3f), 1e-5);
		assertEquals(1d / 3d, (Double)evaluateExpression("reciprocal", 3d), 1e-8);

		assertEquals(-1, evaluateExpression("sign", -3d));
		assertEquals(0, evaluateExpression("sign", 0d));
		assertEquals(+1, evaluateExpression("sign", +3d));
	}

	static
	private Object evaluateExpression(String function, Object argument){
		FieldRef fieldRef = new FieldRef("x");

		Expression expression = FunctionUtil.encodeNumpyFunction(function, Collections.singletonList(fieldRef));

		EvaluationContext context = new VirtualEvaluationContext();
		context.declare(fieldRef.getField(), FieldValueUtil.create(argument));

		FieldValue value = org.jpmml.evaluator.ExpressionUtil.evaluate(expression, context);

		return FieldValueUtil.getValue(value);
	}
}