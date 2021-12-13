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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jpmml.python.Token;

public class PatsyToken {

	private List<Token> tokens = null;


	public PatsyToken(Token token){
		this(Collections.singletonList(token));
	}

	public PatsyToken(List<Token> tokens){
		setTokens(tokens);
	}

	@Override
	public String toString(){
		List<Token> tokens = getTokens();

		return tokens.stream()
			.map(token -> token.image + " -> " + FormulaParserConstants.tokenImage[token.kind] + " (" + token.kind + ")")
			.collect(Collectors.joining(", ", "[", "]"));
	}

	public int getKind(){
		List<Token> tokens = getTokens();

		if(tokens.size() == 1){
			Token token = tokens.get(0);

			switch(token.kind){
				case FormulaParserConstants.INT:
				case FormulaParserConstants.FLOAT:
				case FormulaParserConstants.NAME:
					return -1;
				default:
					return token.kind;
			}
		}

		return -1;
	}

	public List<Token> getTokens(){
		return this.tokens;
	}

	void setTokens(List<Token> tokens){

		if(tokens == null || tokens.isEmpty()){
			throw new IllegalArgumentException();
		}

		this.tokens = tokens;
	}
}