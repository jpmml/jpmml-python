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

import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.PMMLFunctions;
import org.jpmml.converter.PMMLUtil;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExpressionUtilTest {

	@Test
	public void isString(){
		Scope scope = new BlockScope(TranslatorTest.stringFeatures);

		assertFalse(ExpressionUtil.isString(createConstant("Hello World!", null), scope));
		assertTrue(ExpressionUtil.isString(createConstant("Hello World!", DataType.STRING), scope));

		assertFalse(ExpressionUtil.isString(createFieldRef("x"), scope));

		assertTrue(ExpressionUtil.isString(createFieldRef("a"), scope));
		assertTrue(ExpressionUtil.isString(createFieldRef("b"), scope));

		Expression expression = PMMLUtil.createApply(PMMLFunctions.CONCAT, PMMLUtil.createConstant("Hello World!", null), new FieldRef("x"));

		assertTrue(ExpressionUtil.isString(expression, scope));

		assertFalse(ExpressionUtil.isString(createIfApply("x", null), scope));
		assertTrue(ExpressionUtil.isString(createIfApply("a", null), scope));
		assertFalse(ExpressionUtil.isString(createIfApply("a", "x"), scope));
		assertTrue(ExpressionUtil.isString(createIfApply("a", "b"), scope));
	}

	static
	private Constant createConstant(Object value, DataType dataType){
		return PMMLUtil.createConstant(value, dataType);
	}

	static
	private FieldRef createFieldRef(String fieldName){
		return new FieldRef(fieldName);
	}

	static
	private Apply createIfApply(String trueName, String falseName){
		Apply apply = PMMLUtil.createApply(PMMLFunctions.IF,
			createConstant(Boolean.TRUE, null),
			createFieldRef(trueName)
		);

		if(falseName != null){
			apply.addExpressions(createFieldRef(falseName));
		}

		return apply;
	}
}