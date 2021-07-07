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

import java.util.List;

public class PatsyOperation extends PatsyTerm {

	private PatsyOperator patsyOperator = null;

	private List<PatsyTerm> patsyTerms = null;


	public PatsyOperation(PatsyOperator patsyOperator, List<PatsyTerm> patsyTerms){
		setPatsyOperator(patsyOperator);
		setPatsyTerms(patsyTerms);
	}

	@Override
	public String toString(int indent){
		PatsyOperator patsyOperator = getPatsyOperator();
		List<PatsyTerm> patsyTerms = getPatsyTerms();

		StringBuilder sb = new StringBuilder();
		sb.append(formatIndent(indent) + patsyOperator);

		for(PatsyTerm patsyTerm : patsyTerms){
			sb.append("\n");

			sb.append(patsyTerm.toString(indent + 1));
		}

		return sb.toString();
	}

	public PatsyOperator getPatsyOperator(){
		return this.patsyOperator;
	}

	private void setPatsyOperator(PatsyOperator patsyOperator){
		this.patsyOperator = patsyOperator;
	}

	public List<PatsyTerm> getPatsyTerms(){
		return this.patsyTerms;
	}

	private void setPatsyTerms(List<PatsyTerm> patsyTerms){
		this.patsyTerms = patsyTerms;
	}
}