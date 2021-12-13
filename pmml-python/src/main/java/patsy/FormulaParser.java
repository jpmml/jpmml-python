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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jpmml.python.ParseException;
import org.jpmml.python.SimpleCharStream;
import org.jpmml.python.StringProvider;
import org.jpmml.python.Token;

public class FormulaParser {

	static
	public void main(String... args) throws Exception {
		PatsyTerm patsyTerm = parseFormula(args[0]);

		System.out.println(patsyTerm);
	}

	static
	public PatsyTerm parseFormula(String string) throws ParseException {
		SimpleCharStream simpleCharStream = new SimpleCharStream(new StringProvider(string));

		FormulaParserTokenManager tokenManager = new FormulaParserTokenManager(simpleCharStream);

		EnumSet<PatsyOperator> privatePatsyOperators = EnumSet.of(PatsyOperator.OPEN_PAREN);

		EnumSet<PatsyOperator> patsyOperators = EnumSet.complementOf(privatePatsyOperators);

		Set<Integer> patsyOperatorTokens = patsyOperators.stream()
			.map(patsyOperator -> patsyOperator.getKind())
			.collect(Collectors.toSet());

		List<PatsyToken> patsyTokens = tokenizeFormula(tokenManager, patsyOperatorTokens);

		return infixParse(patsyTokens, patsyOperators);
	}

	static
	private PatsyTerm infixParse(List<PatsyToken> patsyTokens, Set<PatsyOperator> patsyOperators) throws ParseException {
		Map<Integer, PatsyOperator> unaryPatsyOperators = new HashMap<>();
		Map<Integer, PatsyOperator> binaryPatsyOperators = new HashMap<>();

		for(PatsyOperator patsyOperator : patsyOperators){
			int kind = patsyOperator.getKind();
			int arity = patsyOperator.getArity();

			switch(arity){
				case 1:
					unaryPatsyOperators.put(kind, patsyOperator);
					break;
				case 2:
					binaryPatsyOperators.put(kind, patsyOperator);
					break;
				default:
					throw new IllegalArgumentException();
			}
		}

		boolean wantNoun = true;

		Deque<PatsyTerm> nounStack = new ArrayDeque<>();
		Deque<PatsyOperator> opStack = new ArrayDeque<>();

		for(PatsyToken patsyToken : patsyTokens){

			if(wantNoun){
				wantNoun = readNoun(patsyToken, nounStack, opStack, unaryPatsyOperators, binaryPatsyOperators);
			} else

			{
				wantNoun = readOp(patsyToken, nounStack, opStack, unaryPatsyOperators, binaryPatsyOperators);
			}
		}

		while(!opStack.isEmpty()){

			if((opStack.peek()).equals(PatsyOperator.OPEN_PAREN)){
				throw new ParseException();
			}

			runOp(nounStack, opStack);
		}

		if(nounStack.size() != 1){
			throw new ParseException();
		}

		return nounStack.pop();
	}

	static
	private boolean readNoun(PatsyToken patsyToken, Deque<PatsyTerm> nounStack, Deque<PatsyOperator> opStack, Map<Integer, PatsyOperator> unaryPatsyOperators, Map<Integer, PatsyOperator> binaryPatsyOperators) throws ParseException {
		int kind = patsyToken.getKind();

		if(kind == FormulaParserConstants.LPAREN){
			opStack.push(PatsyOperator.OPEN_PAREN);

			return true;
		}

		PatsyOperator unaryPatsyOperator = unaryPatsyOperators.get(kind);
		if(unaryPatsyOperator != null){
			opStack.push(unaryPatsyOperator);

			return true;
		} // End if

		if(kind == -1){
			nounStack.push(new PatsyFactor(patsyToken));

			return false;
		} else

		{
			throw new ParseException();
		}
	}

	static
	private boolean readOp(PatsyToken patsyToken, Deque<PatsyTerm> nounStack, Deque<PatsyOperator> opStack, Map<Integer, PatsyOperator> unaryPatsyOperators, Map<Integer, PatsyOperator> binaryPatsyOperators) throws ParseException {
		int kind = patsyToken.getKind();

		if(kind == FormulaParserConstants.RPAREN){

			while(!opStack.isEmpty() && !(opStack.peek()).equals(PatsyOperator.OPEN_PAREN)){
				runOp(nounStack, opStack);
			}

			if(opStack.isEmpty()){
				throw new ParseException();
			} // End if

			if(!(opStack.peek()).equals(PatsyOperator.OPEN_PAREN)){
				throw new ParseException();
			}

			opStack.pop();

			return false;
		}

		PatsyOperator binaryPatsyOperator = binaryPatsyOperators.get(kind);
		if(binaryPatsyOperator != null){

			while(!opStack.isEmpty() && Integer.compare(binaryPatsyOperator.getPrecedence(), (opStack.peek()).getPrecedence()) <= 0){
				runOp(nounStack, opStack);
			}

			opStack.push(binaryPatsyOperator);

			return true;
		} else

		{
			throw new ParseException();
		}
	}

	static
	private void runOp(Deque<PatsyTerm> nounStack, Deque<PatsyOperator> opStack){
		PatsyOperator patsyOperator = opStack.pop();

		List<PatsyTerm> patsyTerms = new ArrayList<>();

		for(int i = 0; i < patsyOperator.getArity(); i++){
			patsyTerms.add(nounStack.pop());
		}

		Collections.reverse(patsyTerms);

		nounStack.push(new PatsyOperation(patsyOperator, patsyTerms));
	}

	static
	private List<PatsyToken> tokenizeFormula(FormulaParserTokenManager tokenManager, Set<Integer> patsyOperatorTokens) throws ParseException {
		List<PatsyToken> result = new ArrayList<>();

		patsyOperatorTokens.add(FormulaParserConstants.LPAREN);
		patsyOperatorTokens.add(FormulaParserConstants.RPAREN);

		Set<Integer> pythonEndTokens = new HashSet<>(patsyOperatorTokens);
		pythonEndTokens.remove(FormulaParserConstants.LPAREN);

		tokens:
		while(true){
			Token token = tokenManager.getNextToken();

			if(token.kind == FormulaParserConstants.EOF){
				break tokens;
			} // End if

			if(patsyOperatorTokens.contains(token.kind)){
				result.add(new PatsyToken(token));
			} else

			{
				tokenManager.pushBack(token);

				result.add(new PatsyToken(readPythonExpr(tokenManager, pythonEndTokens)));
			}
		}

		return result;
	}

	static
	private List<Token> readPythonExpr(FormulaParserTokenManager tokenManager, Set<Integer> pythonEndTokens) throws ParseException {
		List<Token> result = new ArrayList<>();

		int bracketLevel = 0;

		tokens:
		while(true){
			Token token = tokenManager.getNextToken();

			if(token.kind == FormulaParserConstants.EOF){
				tokenManager.pushBack(token);

				break tokens;
			} // End if

			if(bracketLevel == 0){

				if(pythonEndTokens.contains(token.kind)){
					tokenManager.pushBack(token);

					break tokens;
				}
			}

			switch(token.kind){
				case FormulaParserConstants.LPAREN:
				case FormulaParserConstants.LBRACKET:
					{
						bracketLevel++;
					}
					break;
				case FormulaParserConstants.RPAREN:
				case FormulaParserConstants.RBRACKET:
					{
						bracketLevel--;

						if(bracketLevel < 0){
							throw new ParseException();
						}
					}
					break;
				default:
					break;
			}

			result.add(token);
		}

		if(bracketLevel != 0){
			throw new ParseException();
		}

		return result;
	}
}