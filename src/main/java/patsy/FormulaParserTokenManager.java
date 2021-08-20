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

import java.util.ArrayDeque;
import java.util.Deque;

import org.jpmml.python.ExpressionTranslatorTokenManager;
import org.jpmml.python.SimpleCharStream;
import org.jpmml.python.Token;

public class FormulaParserTokenManager extends ExpressionTranslatorTokenManager implements FormulaParserConstants {

	private final Deque<Token> tokens = new ArrayDeque<>();


	public FormulaParserTokenManager(SimpleCharStream simpleCharStream){
		super(simpleCharStream);
	}

	@Override
	public Token getNextToken(){

		if(!this.tokens.isEmpty()){
			return this.tokens.pop();
		}

		return super.getNextToken();
	}

	@Override
	public void SwitchTo(int lexState){
		throw new UnsupportedOperationException();
	}

	public void pushBack(Token token){
		this.tokens.push(token);
	}
}