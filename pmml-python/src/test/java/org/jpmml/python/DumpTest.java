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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;

import com.google.common.collect.Iterables;
import numpy.DType;
import numpy.core.NDArray;
import org.junit.Test;
import pandas.core.BlockManager;
import pandas.core.Categorical;
import pandas.core.CategoricalDtype;
import pandas.core.DataFrame;
import pandas.core.ExtensionDtype;
import pandas.core.Index;
import pandas.core.Series;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
		unpickle("python-3.7_joblib-1.1.0.pkl.z");

		unpickle("python-3.7_pickle-p2.pkl");
		unpickle("python-3.7_pickle-p3.pkl");
		unpickle("python-3.7_pickle-p4.pkl");

		unpickleNumpyArrays("python-3.7_numpy-1.19.5");
		unpickleNumpyArrays("python-3.7_numpy-1.20.0");
		unpickleNumpyArrays("python-3.7_numpy-1.21.4");
		unpickleNumpyArrays("python-3.7_numpy-1.21.5");

		unpicklePandasSeries("python-3.7_pandas-1.0.5");
		unpicklePandasSeries("python-3.7_pandas-1.1.3");
		unpicklePandasSeries("python-3.7_pandas-1.2.3");
		unpicklePandasSeries("python-3.7_pandas-1.3.1");
		unpicklePandasSeries("python-3.7_pandas-1.3.4");
		unpicklePandasSeries("python-3.7_pandas-1.3.5");

		unpicklePandasSeriesNA("python-3.7_pandas-1.3.5");

		unpicklePandasCategorical("python-3.7_pandas-1.3.5");

		unpicklePandasDataFrame("python-3.7_pandas-1.1.3");
		unpicklePandasDataFrame("python-3.7_pandas-1.2.3");
		unpicklePandasDataFrame("python-3.7_pandas-1.3.1");
		unpicklePandasDataFrame("python-3.7_pandas-1.3.4");
		unpicklePandasDataFrame("python-3.7_pandas-1.3.5");

		unpicklePandasDtypes("python-3.7_pandas-1.3.5");
	}

	@Test
	public void python39() throws Exception {
		unpickle("python-3.9_joblib-1.0.1.pkl.z");
		unpickle("python-3.9_joblib-1.1.0.pkl.z");

		unpickle("python-3.9_pickle-p2.pkl");
		unpickle("python-3.9_pickle-p3.pkl");
		unpickle("python-3.9_pickle-p4.pkl");
		unpickle("python-3.9_pickle-p5.pkl");

		unpickleNumpyArrays("python-3.9_numpy-1.20.2");
		unpickleNumpyArrays("python-3.9_numpy-1.21.4");
		unpickleNumpyArrays("python-3.9_numpy-1.22.1");
		unpickleNumpyArrays("python-3.9_numpy-1.22.3");

		unpicklePandasSeries("python-3.9_pandas-1.2.3");
		unpicklePandasSeries("python-3.9_pandas-1.3.1");
		unpicklePandasSeries("python-3.9_pandas-1.3.4");
		unpicklePandasSeries("python-3.9_pandas-1.4.1");

		unpicklePandasSeriesNA("python-3.9_pandas-1.4.1");

		unpicklePandasCategorical("python-3.9_pandas-1.4.1");

		unpicklePandasDataFrame("python-3.9_pandas-1.1.3");
		unpicklePandasDataFrame("python-3.9_pandas-1.2.3");
		unpicklePandasDataFrame("python-3.9_pandas-1.3.1");
		unpicklePandasDataFrame("python-3.9_pandas-1.3.4");
		unpicklePandasDataFrame("python-3.9_pandas-1.4.1");

		unpicklePandasDtypes("python-3.9_pandas-1.4.1");
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

		assertEquals(expectedValues, values);
		assertArrayEquals(new int[]{expectedValues.size()}, shape);

		assertNotNull(dtype.getDataType());
	}

	private void unpickleNumpyArray(String name, long min, long max, long step) throws IOException {
		NDArray ndArray = (NDArray)unpickle(name);

		List<?> values = ndArray.getArrayContent();
		int[] shape = ndArray.getArrayShape();
		DType dtype = (DType)ndArray.getArrayType();

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

		assertNotNull(dtype.getDataType());
	}

	private void unpicklePandasSeries(String prefix) throws IOException {
		unpicklePandasSeries(prefix + "_bool.pkl", Arrays.asList(false, true));
		unpicklePandasSeries(prefix + "_int8.pkl", Byte.MIN_VALUE, Byte.MAX_VALUE, 1);
		unpicklePandasSeries(prefix + "_int.pkl", Integer.MIN_VALUE, Integer.MAX_VALUE, 64 * 32767);
	}

	private void unpicklePandasSeries(String name, List<?> expectedValues) throws IOException {
		Series series = (Series)unpickle(name);

		List<?> values = series.getArrayContent();
		int[] shape = series.getArrayShape();
		Object dtype = series.getArrayType();

		assertEquals(expectedValues, values);
		assertArrayEquals(new int[]{expectedValues.size()}, shape);

		assertNotNull(dtype);
	}

	private void unpicklePandasSeries(String name, long min, long max, long step) throws IOException {
		Series series = (Series)unpickle(name);

		List<?> values = series.getArrayContent();
		int[] shape = series.getArrayShape();
		Object dtype = series.getArrayType();

		for(int i = 0; i < values.size(); i++){
			Number expectedValue = min + (i * step);
			Number value = (Number)values.get(i);

			assertEquals(expectedValue.longValue(), value.longValue());
		}

		assertNotNull(dtype);
	}

	private void unpicklePandasSeriesNA(String prefix) throws IOException {
		unpicklePandasSeriesNA(prefix + "_bool-na.pkl", 2);
		unpicklePandasSeriesNA(prefix + "_int8-na.pkl", 255);
		unpicklePandasSeriesNA(prefix + "_uint8-na.pkl", 255);
		unpicklePandasSeriesNA(prefix + "_str-na.pkl", 3);
	}

	private void unpicklePandasSeriesNA(String name, int expectedSize) throws IOException {
		Series series = (Series)unpickle(name);

		List<?> values = series.getArrayContent();
		int[] shape = series.getArrayShape();
		Object dtype = series.getArrayType();

		assertNotNull(values.get(0));
		assertNull(values.get(1));

		assertArrayEquals(new int[]{expectedSize}, shape);

		assertNotNull(dtype);
	}

	private void unpicklePandasCategorical(String prefix) throws IOException {
		unpicklePandasCategorical(prefix + "_categorical_bool.pkl", Arrays.asList(false, true), true);
		unpicklePandasCategorical(prefix + "_categorical_str.pkl", Arrays.asList("a", "e", "b", "d", "c"), true);
	}

	private void unpicklePandasCategorical(String name, List<?> expectedCategories, boolean expectedOrdered) throws IOException {
		Categorical categorical = (Categorical)unpickle(name);

		CategoricalDtype dtype = categorical.getDType();

		assertEquals(expectedCategories, dtype.getValues());
		assertEquals(expectedOrdered, dtype.getOrdered());

		assertNotNull(dtype.getDType());
	}

	private void unpicklePandasDataFrame(String prefix) throws IOException {
		DataFrame dataFrame = (DataFrame)unpickle(prefix + "_df.pkl");

		BlockManager data = dataFrame.getData();

		List<Index> axes = data.getAxesArray();

		assertEquals(2, axes.size());

		Index columnAxis = axes.get(0);
		Index rowAxis = axes.get(1);

		assertEquals(Arrays.asList("bool", "int", "float", "str"), columnAxis.getDataValues());
		assertEquals(Arrays.asList(0, 1, 2), rowAxis.getDataValues());

		final
		List<Object> columnIndex = new ArrayList<>();

		try {
			List<Index> blockItems = data.getBlockItems();

			for(int i = 0; i < blockItems.size(); i++){
				Index blockItem = blockItems.get(i);

				columnIndex.add(Iterables.getOnlyElement(blockItem.getDataValues()));
			}
		} catch(UnsupportedOperationException uoe){
			// Ignored
		}

		List<HasArray> blocks = data.getBlockValues();

		assertEquals(4, blocks.size());

		IntFunction<List<?>> blockValuesFunction = new IntFunction<List<?>>(){

			private List<?> columns = columnAxis.getDataValues();


			@Override
			public List<?> apply(int index){

				if(!columnIndex.isEmpty()){
					index = this.columns.indexOf(columnIndex.get(index));
				}

				return (blocks.get(index)).getArrayContent();
			}
		};

		assertEquals(Arrays.asList(false, false, true), blockValuesFunction.apply(0));
		assertEquals(Arrays.asList(0L, 1L, 2L), blockValuesFunction.apply(1));
		assertEquals(Arrays.asList(0d, 1d, 2d), blockValuesFunction.apply(2));
		assertEquals(Arrays.asList("zero", "one", "two"), blockValuesFunction.apply(3));
	}

	private void unpicklePandasDtypes(String prefix) throws IOException {
		List<?> dtypes = (List<?>)unpickle(prefix + "_dtypes.pkl");

		for(Object dtype : dtypes){
			ExtensionDtype extensionDtype = (ExtensionDtype)dtype;

			assertNotNull(extensionDtype.getDataType());
		}
	}

	static
	protected Object unpickle(String name) throws IOException {
		return unpickle("dump", name);
	}
}