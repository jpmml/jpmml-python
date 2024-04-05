/*
 * Copyright (c) 2015 Villu Ruusmann
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
package scipy.sparse;

import java.util.List;

import org.jpmml.python.HasArray;
import org.jpmml.python.PythonObject;

public class CSRMatrix extends PythonObject implements HasArray {

	public CSRMatrix(String module, String name){
		super(module, name);
	}

	@Override
	public List<?> getArrayContent(){
		return CSRMatrixUtil.getContent(this);
	}

	@Override
	public int[] getArrayShape(){
		return CSRMatrixUtil.getShape(this);
	}

	@Override
	public Object getArrayType(){
		throw new UnsupportedOperationException();
	}

	public List<?> getData(){
		return getObjectArray("data");
	}

	public List<Integer> getIndices(){
		return getIntegerArray("indices");
	}

	public List<Integer> getIndPtr(){
		return getIntegerArray("indptr");
	}

	public Object[] getShape(){
		return getTuple("_shape");
	}
}