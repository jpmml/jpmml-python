/*
 * Copyright (c) 2025 Villu Ruusmann
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
package org.jpmml.python.functions;

import java.util.Arrays;
import java.util.List;

import org.dmg.pmml.Apply;
import org.dmg.pmml.Expression;
import org.dmg.pmml.PMMLFunctions;
import org.jpmml.converter.ExpressionUtil;
import org.jpmml.converter.PMMLEncoder;
import org.jpmml.python.FunctionUtil;
import org.jpmml.python.RegExFlavour;

public class SearchFunction extends RegExFunction {

	public SearchFunction(RegExFlavour reFlavour){
		super(reFlavour);
	}

	@Override
	public List<String> getParameters(){
		return Arrays.asList("pattern", "string");
	}

	@Override
	public Apply encode(List<Expression> expressions, PMMLEncoder encoder){
		RegExFlavour reFlavour = getFlavour();

		return ExpressionUtil.createApply(PMMLFunctions.MATCHES,
			expressions.get(1),
			FunctionUtil.updateConstant(expressions.get(0), reFlavour::translatePattern)
		)
			.addExtensions(reFlavour.createExtension());
	}
}