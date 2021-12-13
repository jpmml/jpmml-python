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

import org.jpmml.python.ExpressionTranslatorConstants;

public interface FormulaParserConstants {

	int EOF = ExpressionTranslatorConstants.EOF;
	int LPAREN = ExpressionTranslatorConstants.LPAREN;
	int RPAREN = ExpressionTranslatorConstants.RPAREN;
	int PLUS = ExpressionTranslatorConstants.PLUS;
	int MINUS = ExpressionTranslatorConstants.MINUS;
	int DIVIDE = ExpressionTranslatorConstants.DIVIDE;
	int MULTIPLY = ExpressionTranslatorConstants.MULTIPLY;
	int TILDE = ExpressionTranslatorConstants.TILDE;
	int LBRACKET = ExpressionTranslatorConstants.LBRACKET;
	int RBRACKET = ExpressionTranslatorConstants.RBRACKET;
	int COLON = ExpressionTranslatorConstants.COLON;
	int INT = ExpressionTranslatorConstants.INT;
	int FLOAT = ExpressionTranslatorConstants.FLOAT;
	int NAME = ExpressionTranslatorConstants.NAME;
	int STRING = ExpressionTranslatorConstants.STRING;

	String[] tokenImage = ExpressionTranslatorConstants.tokenImage;
}