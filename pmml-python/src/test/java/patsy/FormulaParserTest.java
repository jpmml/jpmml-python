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
package patsy;

import org.jpmml.python.ParseException;
import org.junit.jupiter.api.Test;

public class FormulaParserTest {

	@Test
	public void parseFormula() throws ParseException {
		FormulaParser.parseFormula("a ~ b");

		FormulaParser.parseFormula("(a ~ b)");
		FormulaParser.parseFormula("a ~ ((((b))))");
		FormulaParser.parseFormula("a ~ ((((+b))))");

		FormulaParser.parseFormula("a + b + c");
		FormulaParser.parseFormula("a + (b ~ c) + d");

		FormulaParser.parseFormula("a + numpy.log(a)");

		FormulaParser.parseFormula("a + b ~ c * d");
		FormulaParser.parseFormula("a + b * c");
		FormulaParser.parseFormula("-a:b");
		FormulaParser.parseFormula("a + b:c");
		FormulaParser.parseFormula("(a + b):c");
		FormulaParser.parseFormula("a*b:c");

		FormulaParser.parseFormula("a+b / c");
		FormulaParser.parseFormula("~ a");
	}
}