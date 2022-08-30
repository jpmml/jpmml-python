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

import org.jpmml.python.PythonParserConstants;

public interface FormulaParserConstants {

	int EOF = PythonParserConstants.EOF;
	int LPAREN = PythonParserConstants.LPAREN;
	int RPAREN = PythonParserConstants.RPAREN;
	int PLUS = PythonParserConstants.PLUS;
	int MINUS = PythonParserConstants.MINUS;
	int DIVIDE = PythonParserConstants.DIVIDE;
	int MULTIPLY = PythonParserConstants.MULTIPLY;
	int TILDE = PythonParserConstants.TILDE;
	int LBRACKET = PythonParserConstants.LBRACKET;
	int RBRACKET = PythonParserConstants.RBRACKET;
	int COLON = PythonParserConstants.COLON;
	int INT = PythonParserConstants.INT;
	int FLOAT = PythonParserConstants.FLOAT;
	int NAME = PythonParserConstants.NAME;
	int STRING = PythonParserConstants.STRING;

	String[] tokenImage = PythonParserConstants.tokenImage;
}