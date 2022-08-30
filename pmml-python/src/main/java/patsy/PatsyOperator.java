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

public enum PatsyOperator {

	UNARY_TILDE(PythonParserConstants.TILDE, 1, -100),

	UNARY_PLUS(PythonParserConstants.PLUS, 1, 100),
	UNARY_MINUS(PythonParserConstants.MINUS, 1, 100),

	BINARY_TILDE(PythonParserConstants.TILDE, 2, -100),

	BINARY_ADD(PythonParserConstants.PLUS, 2, 100),
	BINARY_SUBTRACT(PythonParserConstants.MINUS, 2, 100),
	BINARY_DIVIDE(PythonParserConstants.DIVIDE, 2, 200),
	BINARY_MULTIPLY(PythonParserConstants.MULTIPLY, 2, 200),

	INTERACT(PythonParserConstants.COLON, 2, 300),

	OPEN_PAREN(PythonParserConstants.LPAREN, -1, -9999999)
	;

	private int kind = -1;

	private int arity = -1;

	private int precedence = -1;


	private PatsyOperator(int kind, int arity, int precedence){
		setKind(kind);
		setArity(arity);
		setPrecedence(precedence);
	}

	public int getKind(){
		return this.kind;
	}

	private void setKind(int kind){
		this.kind = kind;
	}

	public int getArity(){
		return this.arity;
	}

	private void setArity(int arity){
		this.arity = arity;
	}

	public int getPrecedence(){
		return this.precedence;
	}

	private void setPrecedence(int precedence){
		this.precedence = precedence;
	}
}