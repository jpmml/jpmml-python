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
package joblib;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import numpy.core.NDArray;
import numpy.core.NDArrayUtil;
import org.jpmml.python.HasArray;
import org.jpmml.python.HasContent;
import org.jpmml.python.PythonObject;

abstract
public class NDArrayWrapper extends PythonObject implements HasArray, HasContent<NDArray> {

	private NDArray content = null;


	public NDArrayWrapper(String module, String name){
		super(module, name);
	}

	abstract
	public InputStream getInputStream() throws IOException;

	@Override
	public List<?> getArrayContent(){
		NDArray content = getContent();

		return content.getArrayContent();
	}

	@Override
	public int[] getArrayShape(){
		NDArray content = getContent();

		return content.getArrayShape();
	}

	@Override
	public Object getArrayType(){
		NDArray content = getContent();

		return content.getArrayType();
	}

	public String getFileName(){
		return getString("filename");
	}

	@Override
	public NDArray getContent(){

		if(this.content == null){
			this.content = loadContent();
		}

		return this.content;
	}

	@Override
	public void clearContent(){

		if(this.content != null){
			this.content.clearContent();
		}

		this.content = null;
	}

	private NDArray loadContent(){

		try(InputStream is = getInputStream()){
			return NDArrayUtil.parseNpy(is);
		} catch(IOException ioe){
			throw new RuntimeException(ioe);
		}
	}

	@Override
	public String toString(){
		return getFileName();
	}
}