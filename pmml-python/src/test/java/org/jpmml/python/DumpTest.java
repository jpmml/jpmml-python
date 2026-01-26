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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import builtins.Type;
import com.google.common.collect.Iterables;
import net.razorvine.pickle.Unpickler;
import numpy.DType;
import numpy.core.Complex;
import numpy.core.NDArray;
import numpy.random.Generator;
import numpy.random.LegacyRandomState;
import org.jpmml.converter.FortranMatrixUtil;
import org.junit.jupiter.api.Test;
import pandas.core.BlockManager;
import pandas.core.Categorical;
import pandas.core.CategoricalDtype;
import pandas.core.DataFrame;
import pandas.core.ExtensionDtype;
import pandas.core.Index;
import pandas.core.Series;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DumpTest extends UnpicklerTest {

	@Test
	public void python27() throws IOException {
		String[] pickleVersions = {
			"p2"
		};

		unpicklePickle("2.7", pickleVersions);

		String[] joblibVersions = {
			"0.9.4"
		};

		unpickleJoblib("2.7", joblibVersions);

		String[] sklearnJoblibVersions = {
			"0.9.4", "0.10.2", "0.13.0"
		};

		unpickleSkLearnJoblib("2.7", sklearnJoblibVersions);

		unpickleNumPyArrays("python-2.7_numpy-1.11.2");
	}

	@Test
	public void python34() throws IOException {
		String[] pickleVersions = {
			"p2", "p3", "p4"
		};

		unpicklePickle("3.4", pickleVersions);

		String[] joblibVersions = {
			"0.9.3", "0.9.4", "0.10.0", "0.13.2"
		};

		unpickleJoblib("3.4", joblibVersions);

		String[] sklearnJoblibVersions = {
			"0.9.4", "0.11", "0.13.0"
		};

		unpickleSkLearnJoblib("3.4", sklearnJoblibVersions);

		unpickleNumPyArrays("python-3.4_numpy-1.13.3");
	}

	@Test
	public void python37() throws IOException {
		unpickleBuiltinDtypes("3.7");

		unpickleEnums("3.7");

		String[] pickleVersions = {
			"p2", "p3", "p4"
		};

		unpicklePickle("3.7", pickleVersions);

		String[] joblibVersions = {
			"1.0.1", "1.1.0"
		};

		unpickleJoblib("3.7", joblibVersions);

		unpickleNumPyArrays("python-3.7_numpy-1.19.5");
		unpickleNumPyArrays("python-3.7_numpy-1.20.0");
		unpickleNumPyArrays("python-3.7_numpy-1.21.4");
		unpickleNumPyArrays("python-3.7_numpy-1.21.5");

		unpickleNumPyDtypes("python-3.7_numpy-1.21.5");

		unpicklePandasSeries("python-3.7_pandas-1.0.5");
		unpicklePandasSeries("python-3.7_pandas-1.1.3");
		unpicklePandasSeries("python-3.7_pandas-1.2.3");
		unpicklePandasSeries("python-3.7_pandas-1.3.1");
		unpicklePandasSeries("python-3.7_pandas-1.3.4");

		unpicklePandasDataFrame("python-3.7_pandas-1.1.3");
		unpicklePandasDataFrame("python-3.7_pandas-1.2.3");
		unpicklePandasDataFrame("python-3.7_pandas-1.3.1");
		unpicklePandasDataFrame("python-3.7_pandas-1.3.4");

		String[] pandasVersions = {
			"1.3.5"
		};

		unpicklePandasAll("3.7", pandasVersions);
	}

	@Test
	public void python39() throws Exception {
		unpickleBuiltinDtypes("3.9");

		unpickleEnums("3.9");

		String[] pickleVersions = {
			"p2", "p3", "p4", "p5"
		};

		unpicklePickle("3.9", pickleVersions);

		String[] dillVersions = {
			"0.3.6", "0.3.8", "0.3.9"
		};

		unpickleDill("3.9", dillVersions);

		String[] joblibVersions = {
			"1.0.1", "1.1.0", "1.2.0", "1.3.1", "1.3.2", "1.4.2"
		};

		unpickleJoblib("3.9", joblibVersions);

		unpickleNumPyArrays("python-3.9_numpy-1.20.2");
		unpickleNumPyArrays("python-3.9_numpy-1.21.4");
		unpickleNumPyArrays("python-3.9_numpy-1.22.1");
		unpickleNumPyArrays("python-3.9_numpy-1.22.3");
		unpickleNumPyArrays("python-3.9_numpy-1.23.4");
		unpickleNumPyArrays("python-3.9_numpy-1.24.1");

		unpickleNumPyDtypes("python-3.9_numpy-1.22.3");
		unpickleNumPyDtypes("python-3.9_numpy-1.23.4");
		unpickleNumPyDtypes("python-3.9_numpy-1.24.1");

		unpickleNumPyDatetimeDtypes("python-3.9_numpy-1.24.1");

		unpickleNumPyRNGs("python-3.9_numpy-1.23.4");
		unpickleNumPyRNGs("python-3.9_numpy-1.24.1");

		String[] numpyVersions = {
			"1.26.2", "2.0.0"
		};

		unpickleNumPyAll("3.9", numpyVersions);

		unpicklePandasSeries("python-3.9_pandas-1.2.3");
		unpicklePandasSeries("python-3.9_pandas-1.3.1");
		unpicklePandasSeries("python-3.9_pandas-1.3.4");

		unpicklePandasDataFrame("python-3.9_pandas-1.1.3");
		unpicklePandasDataFrame("python-3.9_pandas-1.2.3");
		unpicklePandasDataFrame("python-3.9_pandas-1.3.1");
		unpicklePandasDataFrame("python-3.9_pandas-1.3.4");

		String[] pandasVersions = {
			"1.4.1", "1.4.3",
			"1.5.1", "1.5.2",
			"2.0.2", "2.1.3",
			"2.2.0"
		};

		unpicklePandasAll("3.9", pandasVersions);
	}

	@Test
	public void python311() throws Exception {
		unpickleBuiltinDtypes("3.11");

		unpickleEnums("3.11");

		String[] pickleVersions = {
			"p2", "p3", "p4", "p5"
		};

		unpicklePickle("3.11", pickleVersions);

		String[] dillVersions = {
			"0.3.6", "0.3.8", "0.3.9", "0.4.0"
		};

		unpickleDill("3.11", dillVersions);

		String[] joblibVersions = {
			"1.2.0", "1.3.1", "1.3.2", "1.4.2", "1.5.3"
		};

		unpickleJoblib("3.11", joblibVersions);

		unpickleNumPyArrays("python-3.11_numpy-1.23.4");
		unpickleNumPyArrays("python-3.11_numpy-1.24.1");

		unpickleNumPyDtypes("python-3.11_numpy-1.23.4");
		unpickleNumPyDtypes("python-3.11_numpy-1.24.1");

		unpickleNumPyDatetimeDtypes("python-3.11_numpy-1.24.1");

		unpickleNumPyRNGs("python-3.11_numpy-1.23.4");
		unpickleNumPyRNGs("python-3.11_numpy-1.24.1");

		String[] numpyVersions = {
			"1.26.2", "2.0.0",
			"2.1.2", "2.2.3", "2.3.5", "2.4.1"
		};

		unpickleNumPyAll("3.11", numpyVersions);

		String[] pandasVersions = {
			"1.5.1", "1.5.2",
			"2.0.2", "2.1.3",
			"2.2.3", "2.3.3",
			"3.0.0"
		};

		unpicklePandasAll("3.11", pandasVersions);
	}

	@Test
	public void python312() throws Exception {
		unpickleBuiltinDtypes("3.12");

		unpickleEnums("3.12");

		String[] pickleVersions = {
			"p2", "p3", "p4", "p5"
		};

		unpicklePickle("3.12", pickleVersions);

		String[] dillVersions = {
			"0.3.6", "0.3.8", "0.3.9", "0.4.0"
		};

		unpickleDill("3.12", dillVersions);

		String[] joblibVersions = {
			"1.3.2", "1.4.2", "1.5.3"
		};

		unpickleJoblib("3.12", joblibVersions);

		String[] numpyVersions = {
			"1.26.2", "2.0.0",
			"2.1.2", "2.2.3", "2.3.5", "2.4.1"
		};

		unpickleNumPyAll("3.12", numpyVersions);

		String[] pandasVersions = {
			"2.2.3", "2.3.3",
			"3.0.0"
		};

		unpicklePandasAll("3.12", pandasVersions);
	}

	@Test
	public void python313() throws Exception {
		unpickleBuiltinDtypes("3.13");

		unpickleEnums("3.13");

		String[] pickleVersions = {
			"p2", "p3", "p4", "p5"
		};

		unpicklePickle("3.13", pickleVersions);

		String[] dillVersions = {
			"0.3.9", "0.4.0"
		};

		unpickleDill("3.13", dillVersions);

		String[] joblibVersions = {
			"1.4.2", "1.5.3"
		};

		unpickleJoblib("3.13", joblibVersions);

		String[] numpyVersions = {
			"1.26.2", "2.0.0",
			"2.1.2", "2.2.3", "2.3.5", "2.4.1"
		};

		unpickleNumPyAll("3.13", numpyVersions);

		String[] pandasVersions = {
			"2.2.3", "2.3.3",
			"3.0.0"
		};

		unpicklePandasAll("3.13", pandasVersions);
	}

	private void unpickleBuiltinDtypes(String pythonVersion) throws IOException {
		List<?> dtypes = (List<?>)unpickle("python-" + pythonVersion + "_dtypes.pkl");

		for(Object dtype : dtypes){
			Type type = (Type)dtype;

			assertNotNull(type.getDataType());
		}
	}

	private void unpickleEnums(String pythonVersion) throws IOException {
		List<?> enums = (List<?>)unpickle("python-" + pythonVersion + "_enums.pkl");

		for(Object _enum : enums){
			PythonEnum pythonEnum = (PythonEnum)_enum;

			assertNotNull(pythonEnum.getValue());
		}
	}

	private void unpicklePickle(String pythonVersion, String... pickleVersions) throws IOException {

		for(String pickleVersion : pickleVersions){
			String prefix = "python-" + pythonVersion + "_" + "pickle-" + pickleVersion;

			unpickle(prefix + ".pkl");
		}
	}

	private void unpickleDill(String pythonVersion, String... dillVersions) throws IOException {

		for(String dillVersion : dillVersions){
			String prefix = "python-" + pythonVersion + "_" + "dill-" + dillVersion;

			unpickle(prefix + ".pkl");
		}
	}

	private void unpickleJoblib(String pythonVersion, String... joblibVersions) throws IOException {

		for(String joblibVersion : joblibVersions){
			String prefix = "python-" + pythonVersion + "_" + "joblib-" + joblibVersion;

			unpickle(prefix + ".pkl.z");
		}
	}

	private void unpickleSkLearnJoblib(String pythonVersion, String... sklearnJoblibVersions) throws IOException {

		for(String sklearnJoblibVersion : sklearnJoblibVersions){
			String prefix = "python-" + pythonVersion + "_" + "sklearn-joblib-" + sklearnJoblibVersion;

			unpickle(prefix + ".pkl.z");
		}
	}

	private void unpickleNumPyAll(String pythonVersion, String... numpyVersions) throws IOException {

		for(String numpyVersion : numpyVersions){
			String prefix = "python-" + pythonVersion + "_" + "numpy-" + numpyVersion;

			unpickleNumPyArrays(prefix);
			unpickleNumPyComplexArrays(prefix);
			unpickleNumPyDatetimeArrays(prefix);
			unpickleNumPyDtypes(prefix);
			unpickleNumPyDatetimeDtypes(prefix);
			unpickleNumPyRNGs(prefix);
		}
	}

	private void unpickleNumPyArrays(String prefix) throws IOException {
		unpickleNumPyArray(prefix + "_bool.pkl", Arrays.asList(false, true));

		unpickleNumPyArray(prefix + "_int8.pkl", Byte.MIN_VALUE, Byte.MAX_VALUE, 1);
		unpickleNumPyArray(prefix + "_int16.pkl", Short.MIN_VALUE, Short.MAX_VALUE, 127);

		unpickleNumPyArray(prefix + "_uint8.pkl", 0, 255, 1);
		unpickleNumPyArray(prefix + "_uint16.pkl", 0, 65535, 127);

		String[] dtypes = new String[]{"int", "int32", "int64", "float32", "float64"};
		for(String dtype : dtypes){
			unpickleNumPyArray(prefix + "_" + dtype + ".pkl", Integer.MIN_VALUE, Integer.MAX_VALUE, 64 * 32767);
		}

		dtypes = new String[]{"uint32", "uint64"};
		for(String dtype : dtypes){
			unpickleNumPyArray(prefix + "_" + dtype + ".pkl", 0L, 4294967295L, 64 * 32767);
		}
	}

	private void unpickleNumPyComplexArrays(String prefix) throws IOException {
		List<Complex> complexes = Arrays.asList(
			new Complex(1f, 2f),
			new Complex(3f, 4f)
		);

		unpickleNumPyArray(prefix + "_complex64.pkl", complexes);
	}

	private void unpickleNumPyDatetimeArrays(String prefix) throws IOException {
		List<LocalDateTime> datetimes = Arrays.asList(
			LocalDateTime.of(1957, 10, 4, 19, 28, 34),
			LocalDateTime.of(1961, 4, 12, 6, 7, 0),
			LocalDateTime.of(1969, 7, 20, 20, 17, 0)
		);

		unpickle(prefix + "_datetime64Y.pkl");
		unpickle(prefix + "_datetime64M.pkl");

		List<LocalDate> dayDates = datetimes.stream()
			.map(datetime -> LocalDate.of(datetime.getYear(), datetime.getMonth(), datetime.getDayOfMonth()))
			.collect(Collectors.toList());

		unpickleNumPyArray(prefix + "_datetime64D.pkl", dayDates);

		List<LocalDateTime> hourDatetimes = datetimes.stream()
			.map(datetime-> datetime.truncatedTo(ChronoUnit.HOURS))
			.collect(Collectors.toList());

		unpickleNumPyArray(prefix + "_datetime64h.pkl", hourDatetimes);

		List<LocalDateTime> minuteDatetimes = datetimes.stream()
			.map(datetime -> datetime.truncatedTo(ChronoUnit.MINUTES))
			.collect(Collectors.toList());

		unpickleNumPyArray(prefix + "_datetime64m.pkl", minuteDatetimes);

		unpickleNumPyArray(prefix + "_datetime64s.pkl", datetimes);
	}

	private void unpickleNumPyArray(String name, List<?> expectedValues) throws IOException {
		NDArray ndArray = (NDArray)unpickle(name);

		List<?> values = ndArray.getArrayContent();
		int[] shape = ndArray.getArrayShape();
		DType dtype = (DType)ndArray.getDescr();

		assertEquals(expectedValues, values);
		assertArrayEquals(new int[]{expectedValues.size()}, shape);

		assertNotNull(dtype.getDataType());
	}

	private void unpickleNumPyArray(String name, long min, long max, long step) throws IOException {
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

	private void unpickleNumPyDtypes(String prefix) throws IOException {
		List<?> dtypes = (List<?>)unpickle(prefix + "_dtypes.pkl");

		for(Object dtype : dtypes){
			Type type = (Type)dtype;

			assertNotNull(type.getDataType());
		}
	}

	private void unpickleNumPyDatetimeDtypes(String prefix) throws IOException {
		List<?> dtypes = (List<?>)unpickle(prefix + "_datetime_dtypes.pkl");

		for(Object dtype : dtypes){
			DType type = (DType)dtype;

			assertNotNull(type.getDataType());
		}
	}

	private void unpickleNumPyRNGs(String prefix) throws IOException {
		List<?> rngs = (List<?>)unpickle(prefix + "_rngs.pkl");

		assertEquals(2, rngs.size());

		LegacyRandomState legacyRandomState = (LegacyRandomState)rngs.get(0);
		Generator generator = (Generator)rngs.get(1);

		assertNotNull(legacyRandomState);
		assertNotNull(generator);
	}

	private void unpicklePandasAll(String pythonVersion, String... pandasVersions) throws IOException {

		for(String pandasVersion : pandasVersions){
			String prefix = "python-" + pythonVersion + "_" + "pandas-" + pandasVersion;

			unpicklePandasSeries(prefix);
			unpicklePandasSeriesNA(prefix);
			unpicklePandasCategorical(prefix);
			unpicklePandasDateTimeDataFrame(prefix);
			unpicklePandasDataFrame(prefix);
			unpicklePandasDtypes(prefix);
		}
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

		assertNotNull(dtype.getDescr());
	}

	private void unpicklePandasDateTimeDataFrame(String prefix) throws IOException {
		DataFrame dataFrame;

		try {
			dataFrame = (DataFrame)unpickle(prefix + "_dt_df.pkl");
		} catch(FileNotFoundException fnfe){
			return;
		}

		BlockManager data = dataFrame.getData();

		List<LocalDateTime> rows = (Arrays.asList("1957-10-04T19:28:34Z", "1961-04-12T06:07:00Z", "1969-07-20T20:17:00Z")).stream()
			.map(rowIndex -> ZonedDateTime.parse((String)rowIndex, DateTimeFormatter.ISO_DATE_TIME).toLocalDateTime())
			.collect(Collectors.toList());

		List<String> columns = Arrays.asList("s", "m", "h", "D", "M", "Y");

		IntFunction<?> columnFunction = createColumnFunction(data, rows, columns, 1);

		assertEquals(truncateToUnit(rows, ChronoUnit.SECONDS), columnFunction.apply(0));
		assertEquals(truncateToUnit(rows, ChronoUnit.MINUTES), columnFunction.apply(1));
		assertEquals(truncateToUnit(rows, ChronoUnit.HOURS), columnFunction.apply(2));
		assertEquals(truncateToUnit(rows, ChronoUnit.DAYS), columnFunction.apply(3));
		assertEquals(truncateToMonth(rows), columnFunction.apply(4));
		assertEquals(truncateToYear(rows), columnFunction.apply(5));
	}

	private void unpicklePandasDataFrame(String prefix) throws IOException {
		DataFrame dataFrame = (DataFrame)unpickle(prefix + "_df.pkl");

		BlockManager data = dataFrame.getData();

		List<Integer> rows = Arrays.asList(0, 1, 2);
		List<String> columns = Arrays.asList("bool", "int", "float", "str");

		IntFunction<?> columnFunction = createColumnFunction(data, rows, columns, 4);

		assertEquals(Arrays.asList(false, false, true), columnFunction.apply(0));
		assertEquals(Arrays.asList(0L, 1L, 2L), columnFunction.apply(1));
		assertEquals(Arrays.asList(0d, 1d, 2d), columnFunction.apply(2));
		assertEquals(Arrays.asList("zero", "one", "two"), columnFunction.apply(3));
	}

	private void unpicklePandasDtypes(String prefix) throws IOException {
		List<?> dtypes = (List<?>)unpickle(prefix + "_dtypes.pkl");

		for(Object dtype : dtypes){
			ExtensionDtype extensionDtype = (ExtensionDtype)dtype;

			assertNotNull(extensionDtype.getDataType());
		}
	}

	static
	private IntFunction<List<?>> createColumnFunction(BlockManager data, List<?> rows, List<?> columns, int numberOfBlocks){
		Index rowAxis = data.getRowAxis();
		Index columnAxis = data.getColumnAxis();

		assertEquals(rows, rowAxis.getValues());
		assertEquals(columns, columnAxis.getValues());

		List<HasArray> blocks = data.getBlockValues();

		assertEquals(numberOfBlocks, blocks.size());

		final
		List<Object> columnIndex = new ArrayList<>();

		try {
			List<Index> blockItems = data.getBlockItems();

			for(int i = 0; i < blockItems.size(); i++){
				Index blockItem = blockItems.get(i);

				columnIndex.add(Iterables.getOnlyElement(blockItem.getValues()));
			}
		} catch(UnsupportedOperationException uoe){
			// Ignored
		}

		IntFunction<List<?>> function = new IntFunction<List<?>>(){

			@Override
			public List<?> apply(int index){

				if(!columnIndex.isEmpty()){
					index = columns.indexOf(columnIndex.get(index));
				} // End if

				if(numberOfBlocks == 1){
					HasArray block = blocks.get(0);

					return FortranMatrixUtil.getColumn(block.getArrayContent(), rows.size(), columns.size(), index);
				} else

				{
					HasArray block = blocks.get(index);

					return block.getArrayContent();
				}
			}
		};

		return function;
	}

	static
	private List<LocalDateTime> truncateToUnit(List<LocalDateTime> localDateTimes, ChronoUnit chronoUnit){
		return localDateTimes.stream()
			.map(localDateTime -> localDateTime.truncatedTo(chronoUnit))
			.collect(Collectors.toList());
	}

	static
	private List<LocalDateTime> truncateToMonth(List<LocalDateTime> localDateTimes){
		return localDateTimes.stream()
			.map(localDateTime -> localDateTime.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS))
			.collect(Collectors.toList());
	}

	static
	private List<LocalDateTime> truncateToYear(List<LocalDateTime> localDateTimes){
		return localDateTimes.stream()
			.map(localDateTime -> localDateTime.withDayOfYear(1).truncatedTo(ChronoUnit.DAYS))
			.collect(Collectors.toList());
	}

	static
	protected Object unpickle(String name) throws IOException {
		return unpickle("dump", name);
	}

	static {
		Unpickler.registerConstructor("__main__", "Color", new PythonEnumConstructor("__main__", "Color"));
		Unpickler.registerConstructor("__main__", "ColorCode", new PythonEnumConstructor("__main__", "ColorCode"));
	}
}