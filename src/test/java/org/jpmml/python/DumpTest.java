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
package org.jpmml.python;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import numpy.DType;
import numpy.core.NDArray;
import org.junit.Test;
import pandas.core.Index;
import pandas.core.Series;
import pandas.core.SingleBlockManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DumpTest extends PickleUtilTest {

	@Test
	public void python27() throws IOException {
		unpickle("python-2.7_joblib-0.9.4.pkl.z");

		unpickle("python-2.7_pickle-p2.pkl");

		unpickle("python-2.7_sklearn-joblib-0.9.4.pkl.z");
		unpickle("python-2.7_sklearn-joblib-0.10.2.pkl.z");
		unpickle("python-2.7_sklearn-joblib-0.13.0.pkl.z");

		unpickleNumpyArrays("python-2.7_numpy-1.11.2");
	}

	@Test
	public void python34() throws IOException {
		unpickle("python-3.4_joblib-0.9.3.pkl.z");
		unpickle("python-3.4_joblib-0.9.4.pkl.z");
		unpickle("python-3.4_joblib-0.10.0.pkl.z");
		unpickle("python-3.4_joblib-0.13.2.pkl.z");

		unpickle("python-3.4_pickle-p2.pkl");
		unpickle("python-3.4_pickle-p3.pkl");
		unpickle("python-3.4_pickle-p4.pkl");

		unpickle("python-3.4_sklearn-joblib-0.9.4.pkl.z");
		unpickle("python-3.4_sklearn-joblib-0.11.pkl.z");
		unpickle("python-3.4_sklearn-joblib-0.13.0.pkl.z");

		unpickleNumpyArrays("python-3.4_numpy-1.13.3");
	}

	@Test
	public void python37() throws IOException {
		unpickle("python-3.7_joblib-1.0.1.pkl.z");

		unpickle("python-3.7_pickle-p2.pkl");
		unpickle("python-3.7_pickle-p3.pkl");
		unpickle("python-3.7_pickle-p4.pkl");

		unpickleNumpyArrays("python-3.7_numpy-1.20.0");

		unpicklePandasSeries("python-3.7_pandas-1.0.5");
		unpicklePandasSeries("python-3.7_pandas-1.1.3");
		unpicklePandasSeries("python-3.7_pandas-1.2.3");
	}

	@Test
	public void python39() throws Exception {
		unpickle("python-3.9_joblib-1.0.1.pkl.z");

		unpickle("python-3.9_pickle-p2.pkl");
		unpickle("python-3.9_pickle-p3.pkl");
		unpickle("python-3.9_pickle-p4.pkl");
		unpickle("python-3.9_pickle-p5.pkl");

		unpickleNumpyArrays("python-3.9_numpy-1.20.2");

		unpicklePandasSeries("python-3.9_pandas-1.2.3");
	}

	private void unpickleNumpyArrays(String prefix) throws IOException {
		unpickleNumpyArray(prefix + "_bool.pkl", Arrays.asList(false, true));

		unpickleNumpyArray(prefix + "_int8.pkl", Byte.MIN_VALUE, Byte.MAX_VALUE, 1);
		unpickleNumpyArray(prefix + "_int16.pkl", Short.MIN_VALUE, Short.MAX_VALUE, 127);

		unpickleNumpyArray(prefix + "_uint8.pkl", 0, 255, 1);
		unpickleNumpyArray(prefix + "_uint16.pkl", 0, 65535, 127);

		String[] dtypes = new String[]{"int", "int32", "int64", "float32", "float64"};
		for(String dtype : dtypes){
			unpickleNumpyArray(prefix + "_" + dtype + ".pkl", Integer.MIN_VALUE, Integer.MAX_VALUE, 64 * 32767);
		}

		dtypes = new String[]{"uint32", "uint64"};
		for(String dtype : dtypes){
			unpickleNumpyArray(prefix + "_" + dtype + ".pkl", 0L, 4294967295L, 64 * 32767);
		}
	}

	private void unpickleNumpyArray(String name, List<?> expectedValues) throws IOException {
		NDArray ndArray = (NDArray)unpickle(name);

		List<?> values = ndArray.getArrayContent();
		int[] shape = ndArray.getArrayShape();

		DType dtype = (DType)ndArray.getDescr();

		assertNotNull(dtype.getDataType());

		assertEquals(expectedValues, values);
	}

	private void unpickleNumpyArray(String name, long min, long max, long step) throws IOException {
		NDArray ndArray = (NDArray)unpickle(name);

		List<?> values = ndArray.getArrayContent();
		int[] shape = ndArray.getArrayShape();

		DType dtype = (DType)ndArray.getDescr();

		assertNotNull(dtype.getDataType());

		for(int i = 0; i < values.size(); i++){
			Number expectedValue = min + (i * step);
			Number value = (Number)values.get(i);

			if(value instanceof Float){
				assertEquals((Float)expectedValue.floatValue(), (Float)value);
			} else

			if(value instanceof Double){
				assertEquals((Double)expectedValue.doubleValue(), (Double)value);
			} else

			{
				assertEquals(expectedValue.longValue(), value.longValue());
			}
		}
	}

	private void unpicklePandasSeries(String prefix) throws IOException {
		unpicklePandasSeries(prefix + "_bool.pkl", Arrays.asList(false, true));
		unpicklePandasSeries(prefix + "_int8.pkl", Byte.MIN_VALUE, Byte.MAX_VALUE, 1);
		unpicklePandasSeries(prefix + "_int.pkl", Integer.MIN_VALUE, Integer.MAX_VALUE, 64 * 32767);
	}

	private void unpicklePandasSeries(String name, List<?> expectedValues) throws IOException {
		Series series = (Series)unpickle(name);

		List<?> data = getData(series);

		assertEquals(expectedValues, data);
	}

	private void unpicklePandasSeries(String name, long min, long max, long step) throws IOException {
		Series series = (Series)unpickle(name);

		List<?> data = getData(series);

		for(int i = 0; i < data.size(); i++){
			Number expectedValue = min + (i * step);
			Number value = (Number)data.get(i);

			assertEquals(expectedValue.longValue(), value.longValue());
		}
	}

	static
	private List<?> getData(Series series){
		SingleBlockManager data = series.getBlockManager();

		Index blockItem = data.getOnlyBlockItem();
		HasArray blockValue = data.getOnlyBlockValue();

		return blockValue.getArrayContent();
	}

	static
	protected Object unpickle(String name) throws IOException {
		return unpickle("dump", name);
	}
}