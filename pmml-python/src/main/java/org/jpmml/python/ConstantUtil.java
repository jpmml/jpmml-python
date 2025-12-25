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
package org.jpmml.python;

import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.jpmml.converter.ExpressionUtil;

public class ConstantUtil {

	private ConstantUtil(){
	}

	static
	public Constant encodeConstant(Identifiable identifiable){
		return encodeConstant(identifiable.getModule(), identifiable.getName());
	}

	static
	public Constant encodeConstant(String module, String name){

		if(("math").equals(module)){
			return encodeMathConstant(module, name);
		} else

		if(("numpy").equals(module)){
			return encodeNumPyConstant(module, name);
		} else

		if(("pandas").equals(module)){
			return encodePandasConstant(module, name);
		}

		throw new ConstantResolutionException(module, name);
	}

	static
	private Constant encodeMathConstant(String module, String name){

		switch(name){
			case "e":
				return ExpressionUtil.createConstant(DataType.DOUBLE, Math.E);
			case "nan":
				return ExpressionUtil.createMissingConstant();
			case "pi":
				return ExpressionUtil.createConstant(DataType.DOUBLE, Math.PI);
			case "tau":
				return ExpressionUtil.createConstant(DataType.DOUBLE, 2 * Math.PI);
			default:
				break;
		}

		throw new ConstantResolutionException(module, name);
	}

	static
	private Constant encodeNumPyConstant(String module, String name){

		switch(name){
			case "e":
				return ExpressionUtil.createConstant(DataType.DOUBLE, Math.E);
			case "nan":
			case "NaN":
			case "NAN":
				return ExpressionUtil.createMissingConstant();
			case "NZERO":
				return ExpressionUtil.createConstant(DataType.DOUBLE, -0.0d);
			case "pi":
				return ExpressionUtil.createConstant(DataType.DOUBLE, Math.PI);
			case "PZERO":
				return ExpressionUtil.createConstant(DataType.DOUBLE, 0.0);
			default:
				break;
		}

		throw new ConstantResolutionException(module, name);
	}

	static
	private Constant encodePandasConstant(String module, String name){

		switch(name){
			case "NA":
			case "NaT":
				return ExpressionUtil.createMissingConstant();
			default:
				break;
		}

		throw new ConstantResolutionException(module, name);
	}
}