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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
		unpickle("python-2.7_joblib-0.9.4.pkl.z");

		unpickle("python-2.7_pickle-p2.pkl");

		unpickle("python-2.7_sklearn-joblib-0.9.4.pkl.z");
		unpickle("python-2.7_sklearn-joblib-0.10.2.pkl.z");
		unpickle("python-2.7_sklearn-joblib-0.13.0.pkl.z");

		unpickleNumPyArrays("python-2.7_numpy-1.11.2");
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

		unpickleNumPyArrays("python-3.4_numpy-1.13.3");
	}

	@Test
	public void python37() throws IOException {
		unpickle("python-3.7_joblib-1.0.1.pkl.z");
		unpickle("python-3.7_joblib-1.1.0.pkl.z");

		unpickle("python-3.7_pickle-p2.pkl");
		unpickle("python-3.7_pickle-p3.pkl");
		unpickle("python-3.7_pickle-p4.pkl");

		unpickleBuiltinDtypes("python-3.7");

		unpickleEnums("python-3.7");

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
		unpickle("python-3.9_dill-0.3.6.pkl");
		unpickle("python-3.9_dill-0.3.8.pkl");
		unpickle("python-3.9_dill-0.3.9.pkl");

		unpickle("python-3.9_joblib-1.0.1.pkl.z");
		unpickle("python-3.9_joblib-1.1.0.pkl.z");
		unpickle("python-3.9_joblib-1.2.0.pkl.z");
		unpickle("python-3.9_joblib-1.3.1.pkl.z");
		unpickle("python-3.9_joblib-1.3.2.pkl.z");
		unpickle("python-3.9_joblib-1.4.2.pkl.z");

		unpickle("python-3.9_pickle-p2.pkl");
		unpickle("python-3.9_pickle-p3.pkl");
		unpickle("python-3.9_pickle-p4.pkl");
		unpickle("python-3.9_pickle-p5.pkl");

		unpickleBuiltinDtypes("python-3.9");

		unpickleEnums("python-3.9");

		unpickleNumPyArrays("python-3.9_numpy-1.20.2");
		unpickleNumPyArrays("python-3.9_numpy-1.21.4");
		unpickleNumPyArrays("python-3.9_numpy-1.22.1");
		unpickleNumPyArrays("python-3.9_numpy-1.22.3");
		unpickleNumPyArrays("python-3.9_numpy-1.23.4");
		unpickleNumPyArrays("python-3.9_numpy-1.24.1");
		unpickleNumPyArrays("python-3.9_numpy-1.26.2");
		unpickleNumPyArrays("python-3.9_numpy-2.0.0");

		unpickleNumPyComplexArrays("python-3.9_numpy-1.26.2");
		unpickleNumPyComplexArrays("python-3.9_numpy-2.0.0");

		unpickleNumPyDatetimeArrays("python-3.9_numpy-1.26.2");
		unpickleNumPyDatetimeArrays("python-3.9_numpy-2.0.0");

		unpickleNumPyDtypes("python-3.9_numpy-1.22.3");
		unpickleNumPyDtypes("python-3.9_numpy-1.23.4");
		unpickleNumPyDtypes("python-3.9_numpy-1.24.1");
		unpickleNumPyDtypes("python-3.9_numpy-1.26.2");
		unpickleNumPyDtypes("python-3.9_numpy-2.0.0");

		unpickleNumPyDatetimeDtypes("python-3.9_numpy-1.24.1");
		unpickleNumPyDatetimeDtypes("python-3.9_numpy-1.26.2");
		unpickleNumPyDatetimeDtypes("python-3.9_numpy-2.0.0");

		unpickleNumPyRNGs("python-3.9_numpy-1.23.4");
		unpickleNumPyRNGs("python-3.9_numpy-1.24.1");
		unpickleNumPyRNGs("python-3.9_numpy-1.26.2");
		unpickleNumPyRNGs("python-3.9_numpy-2.0.0");

		unpicklePandasSeries("python-3.9_pandas-1.2.3");
		unpicklePandasSeries("python-3.9_pandas-1.3.1");
		unpicklePandasSeries("python-3.9_pandas-1.3.4");
		unpicklePandasSeries("python-3.9_pandas-1.4.1");
		unpicklePandasSeries("python-3.9_pandas-1.4.3");
		unpicklePandasSeries("python-3.9_pandas-1.5.1");
		unpicklePandasSeries("python-3.9_pandas-1.5.2");
		unpicklePandasSeries("python-3.9_pandas-2.0.2");
		unpicklePandasSeries("python-3.9_pandas-2.1.3");
		unpicklePandasSeries("python-3.9_pandas-2.2.0");

		unpicklePandasSeriesNA("python-3.9_pandas-1.4.1");
		unpicklePandasSeriesNA("python-3.9_pandas-1.4.3");
		unpicklePandasSeriesNA("python-3.9_pandas-1.5.1");
		unpicklePandasSeriesNA("python-3.9_pandas-1.5.2");
		unpicklePandasSeriesNA("python-3.9_pandas-2.0.2");
		unpicklePandasSeriesNA("python-3.9_pandas-2.1.3");
		unpicklePandasSeriesNA("python-3.9_pandas-2.2.0");

		unpicklePandasCategorical("python-3.9_pandas-1.4.1");
		unpicklePandasCategorical("python-3.9_pandas-1.4.3");
		unpicklePandasCategorical("python-3.9_pandas-1.5.1");
		unpicklePandasCategorical("python-3.9_pandas-1.5.2");
		unpicklePandasCategorical("python-3.9_pandas-2.0.2");
		unpicklePandasCategorical("python-3.9_pandas-2.1.3");
		unpicklePandasCategorical("python-3.9_pandas-2.2.0");

		unpicklePandasDataFrame("python-3.9_pandas-1.1.3");
		unpicklePandasDataFrame("python-3.9_pandas-1.2.3");
		unpicklePandasDataFrame("python-3.9_pandas-1.3.1");
		unpicklePandasDataFrame("python-3.9_pandas-1.3.4");
		unpicklePandasDataFrame("python-3.9_pandas-1.4.1");
		unpicklePandasDataFrame("python-3.9_pandas-1.4.3");
		unpicklePandasDataFrame("python-3.9_pandas-1.5.1");
		unpicklePandasDataFrame("python-3.9_pandas-1.5.2");
		unpicklePandasDataFrame("python-3.9_pandas-2.0.2");
		unpicklePandasDataFrame("python-3.9_pandas-2.1.3");
		unpicklePandasDataFrame("python-3.9_pandas-2.2.0");

		unpicklePandasDtypes("python-3.9_pandas-1.4.1");
		unpicklePandasDtypes("python-3.9_pandas-1.4.3");
		unpicklePandasDtypes("python-3.9_pandas-1.5.1");
		unpicklePandasDtypes("python-3.9_pandas-1.5.2");
		unpicklePandasDtypes("python-3.9_pandas-2.0.2");
		unpicklePandasDtypes("python-3.9_pandas-2.1.3");
		unpicklePandasDtypes("python-3.9_pandas-2.2.0");
	}

	@Test
	public void python311() throws Exception {
		unpickle("python-3.11_dill-0.3.6.pkl");
		unpickle("python-3.11_dill-0.3.8.pkl");
		unpickle("python-3.11_dill-0.3.9.pkl");

		unpickle("python-3.11_joblib-1.2.0.pkl.z");
		unpickle("python-3.11_joblib-1.3.1.pkl.z");
		unpickle("python-3.11_joblib-1.3.2.pkl.z");
		unpickle("python-3.11_joblib-1.4.2.pkl.z");

		unpickle("python-3.11_pickle-p2.pkl");
		unpickle("python-3.11_pickle-p3.pkl");
		unpickle("python-3.11_pickle-p4.pkl");
		unpickle("python-3.11_pickle-p5.pkl");

		unpickleBuiltinDtypes("python-3.11");

		unpickleEnums("python-3.11");

		unpickleNumPyArrays("python-3.11_numpy-1.23.4");
		unpickleNumPyArrays("python-3.11_numpy-1.24.1");
		unpickleNumPyArrays("python-3.11_numpy-1.26.2");
		unpickleNumPyArrays("python-3.11_numpy-2.0.0");
		unpickleNumPyArrays("python-3.11_numpy-2.1.2");
		unpickleNumPyArrays("python-3.11_numpy-2.2.3");
		unpickleNumPyArrays("python-3.11_numpy-2.3.5");

		unpickleNumPyComplexArrays("python-3.11_numpy-1.26.2");
		unpickleNumPyComplexArrays("python-3.11_numpy-2.0.0");
		unpickleNumPyComplexArrays("python-3.11_numpy-2.1.2");
		unpickleNumPyComplexArrays("python-3.11_numpy-2.2.3");
		unpickleNumPyComplexArrays("python-3.11_numpy-2.3.5");

		unpickleNumPyDatetimeArrays("python-3.11_numpy-1.26.2");
		unpickleNumPyDatetimeArrays("python-3.11_numpy-2.0.0");
		unpickleNumPyDatetimeArrays("python-3.11_numpy-2.1.2");
		unpickleNumPyDatetimeArrays("python-3.11_numpy-2.2.3");
		unpickleNumPyDatetimeArrays("python-3.11_numpy-2.3.5");

		unpickleNumPyDtypes("python-3.11_numpy-1.23.4");
		unpickleNumPyDtypes("python-3.11_numpy-1.24.1");
		unpickleNumPyDtypes("python-3.11_numpy-1.26.2");
		unpickleNumPyDtypes("python-3.11_numpy-2.0.0");
		unpickleNumPyDtypes("python-3.11_numpy-2.1.2");
		unpickleNumPyDtypes("python-3.11_numpy-2.2.3");
		unpickleNumPyDtypes("python-3.11_numpy-2.3.5");

		unpickleNumPyDatetimeDtypes("python-3.11_numpy-1.24.1");
		unpickleNumPyDatetimeDtypes("python-3.11_numpy-1.26.2");
		unpickleNumPyDatetimeDtypes("python-3.11_numpy-2.0.0");
		unpickleNumPyDatetimeDtypes("python-3.11_numpy-2.1.2");
		unpickleNumPyDatetimeDtypes("python-3.11_numpy-2.2.3");
		unpickleNumPyDatetimeDtypes("python-3.11_numpy-2.3.5");

		unpickleNumPyRNGs("python-3.11_numpy-1.23.4");
		unpickleNumPyRNGs("python-3.11_numpy-1.24.1");
		unpickleNumPyRNGs("python-3.11_numpy-1.26.2");
		unpickleNumPyRNGs("python-3.11_numpy-2.0.0");
		unpickleNumPyRNGs("python-3.11_numpy-2.1.2");
		unpickleNumPyRNGs("python-3.11_numpy-2.2.3");
		unpickleNumPyRNGs("python-3.11_numpy-2.3.5");

		unpicklePandasSeries("python-3.11_pandas-1.5.1");
		unpicklePandasSeries("python-3.11_pandas-1.5.2");
		unpicklePandasSeries("python-3.11_pandas-2.0.2");
		unpicklePandasSeries("python-3.11_pandas-2.1.3");
		unpicklePandasSeries("python-3.11_pandas-2.2.3");

		unpicklePandasSeriesNA("python-3.11_pandas-1.5.1");
		unpicklePandasSeriesNA("python-3.11_pandas-1.5.2");
		unpicklePandasSeriesNA("python-3.11_pandas-2.0.2");
		unpicklePandasSeriesNA("python-3.11_pandas-2.1.3");
		unpicklePandasSeriesNA("python-3.11_pandas-2.2.3");

		unpicklePandasCategorical("python-3.11_pandas-1.5.1");
		unpicklePandasCategorical("python-3.11_pandas-1.5.2");
		unpicklePandasCategorical("python-3.11_pandas-2.0.2");
		unpicklePandasCategorical("python-3.11_pandas-2.1.3");
		unpicklePandasCategorical("python-3.11_pandas-2.2.3");

		unpicklePandasDataFrame("python-3.11_pandas-1.5.1");
		unpicklePandasDataFrame("python-3.11_pandas-1.5.2");
		unpicklePandasDataFrame("python-3.11_pandas-2.0.2");
		unpicklePandasDataFrame("python-3.11_pandas-2.1.3");
		unpicklePandasDataFrame("python-3.11_pandas-2.2.3");

		unpicklePandasDtypes("python-3.11_pandas-1.5.1");
		unpicklePandasDtypes("python-3.11_pandas-1.5.2");
		unpicklePandasDtypes("python-3.11_pandas-2.0.2");
		unpicklePandasDtypes("python-3.11_pandas-2.1.3");
		unpicklePandasDtypes("python-3.11_pandas-2.2.3");
	}

	@Test
	public void python312() throws Exception {
		unpickle("python-3.12_dill-0.3.6.pkl");
		unpickle("python-3.12_dill-0.3.8.pkl");
		unpickle("python-3.12_dill-0.3.9.pkl");

		unpickle("python-3.12_joblib-1.3.2.pkl.z");
		unpickle("python-3.12_joblib-1.4.2.pkl.z");

		unpickle("python-3.12_pickle-p2.pkl");
		unpickle("python-3.12_pickle-p3.pkl");
		unpickle("python-3.12_pickle-p4.pkl");
		unpickle("python-3.12_pickle-p5.pkl");

		unpickleBuiltinDtypes("python-3.12");

		unpickleEnums("python-3.12");

		unpickleNumPyArrays("python-3.12_numpy-1.26.2");
		unpickleNumPyArrays("python-3.12_numpy-2.0.0");
		unpickleNumPyArrays("python-3.12_numpy-2.1.2");
		unpickleNumPyArrays("python-3.12_numpy-2.2.3");
		unpickleNumPyArrays("python-3.12_numpy-2.3.5");

		unpickleNumPyComplexArrays("python-3.12_numpy-1.26.2");
		unpickleNumPyComplexArrays("python-3.12_numpy-2.0.0");
		unpickleNumPyComplexArrays("python-3.12_numpy-2.1.2");
		unpickleNumPyComplexArrays("python-3.12_numpy-2.2.3");
		unpickleNumPyComplexArrays("python-3.12_numpy-2.3.5");

		unpickleNumPyDatetimeArrays("python-3.12_numpy-1.26.2");
		unpickleNumPyDatetimeArrays("python-3.12_numpy-2.0.0");
		unpickleNumPyDatetimeArrays("python-3.12_numpy-2.1.2");
		unpickleNumPyDatetimeArrays("python-3.12_numpy-2.2.3");
		unpickleNumPyDatetimeArrays("python-3.12_numpy-2.3.5");

		unpickleNumPyDtypes("python-3.12_numpy-1.26.2");
		unpickleNumPyDtypes("python-3.12_numpy-2.0.0");
		unpickleNumPyDtypes("python-3.12_numpy-2.1.2");
		unpickleNumPyDtypes("python-3.12_numpy-2.2.3");
		unpickleNumPyDtypes("python-3.12_numpy-2.3.5");

		unpickleNumPyDatetimeDtypes("python-3.12_numpy-1.26.2");
		unpickleNumPyDatetimeDtypes("python-3.12_numpy-2.0.0");
		unpickleNumPyDatetimeDtypes("python-3.12_numpy-2.1.2");
		unpickleNumPyDatetimeDtypes("python-3.12_numpy-2.2.3");
		unpickleNumPyDatetimeDtypes("python-3.12_numpy-2.3.5");

		unpickleNumPyRNGs("python-3.12_numpy-1.26.2");
		unpickleNumPyRNGs("python-3.12_numpy-2.0.0");
		unpickleNumPyRNGs("python-3.12_numpy-2.1.2");
		unpickleNumPyRNGs("python-3.12_numpy-2.2.3");
		unpickleNumPyRNGs("python-3.12_numpy-2.3.5");

		unpicklePandasSeries("python-3.12_pandas-2.2.3");

		unpicklePandasSeriesNA("python-3.12_pandas-2.2.3");

		unpicklePandasCategorical("python-3.12_pandas-2.2.3");

		unpicklePandasDataFrame("python-3.12_pandas-2.2.3");

		unpicklePandasDtypes("python-3.12_pandas-2.2.3");
	}

	@Test
	public void python313() throws Exception {
		unpickle("python-3.13_dill-0.3.9.pkl");

		unpickle("python-3.13_joblib-1.4.2.pkl.z");

		unpickle("python-3.13_pickle-p2.pkl");
		unpickle("python-3.13_pickle-p3.pkl");
		unpickle("python-3.13_pickle-p4.pkl");
		unpickle("python-3.13_pickle-p5.pkl");

		unpickleBuiltinDtypes("python-3.13");

		unpickleEnums("python-3.13");

		unpickleNumPyArrays("python-3.13_numpy-1.26.2");
		unpickleNumPyArrays("python-3.13_numpy-2.0.0");
		unpickleNumPyArrays("python-3.13_numpy-2.1.2");
		unpickleNumPyArrays("python-3.13_numpy-2.2.3");
		unpickleNumPyArrays("python-3.13_numpy-2.3.5");

		unpickleNumPyComplexArrays("python-3.13_numpy-1.26.2");
		unpickleNumPyComplexArrays("python-3.13_numpy-2.0.0");
		unpickleNumPyComplexArrays("python-3.13_numpy-2.1.2");
		unpickleNumPyComplexArrays("python-3.13_numpy-2.2.3");
		unpickleNumPyComplexArrays("python-3.13_numpy-2.3.5");

		unpickleNumPyDatetimeArrays("python-3.13_numpy-1.26.2");
		unpickleNumPyDatetimeArrays("python-3.13_numpy-2.0.0");
		unpickleNumPyDatetimeArrays("python-3.13_numpy-2.1.2");
		unpickleNumPyDatetimeArrays("python-3.13_numpy-2.2.3");
		unpickleNumPyDatetimeArrays("python-3.13_numpy-2.3.5");

		unpickleNumPyDtypes("python-3.13_numpy-1.26.2");
		unpickleNumPyDtypes("python-3.13_numpy-2.0.0");
		unpickleNumPyDtypes("python-3.13_numpy-2.1.2");
		unpickleNumPyDtypes("python-3.13_numpy-2.2.3");
		unpickleNumPyDtypes("python-3.13_numpy-2.3.5");

		unpickleNumPyDatetimeDtypes("python-3.13_numpy-1.26.2");
		unpickleNumPyDatetimeDtypes("python-3.13_numpy-2.0.0");
		unpickleNumPyDatetimeDtypes("python-3.13_numpy-2.1.2");
		unpickleNumPyDatetimeDtypes("python-3.13_numpy-2.2.3");
		unpickleNumPyDatetimeDtypes("python-3.13_numpy-2.3.5");

		unpickleNumPyRNGs("python-3.13_numpy-1.26.2");
		unpickleNumPyRNGs("python-3.13_numpy-2.0.0");
		unpickleNumPyRNGs("python-3.13_numpy-2.1.2");
		unpickleNumPyRNGs("python-3.13_numpy-2.2.3");
		unpickleNumPyRNGs("python-3.13_numpy-2.3.5");

		unpicklePandasSeries("python-3.13_pandas-2.2.3");

		unpicklePandasSeriesNA("python-3.13_pandas-2.2.3");

		unpicklePandasCategorical("python-3.13_pandas-2.2.3");

		unpicklePandasDataFrame("python-3.13_pandas-2.2.3");

		unpicklePandasDtypes("python-3.13_pandas-2.2.3");
	}

	private void unpickleBuiltinDtypes(String prefix) throws IOException {
		List<?> dtypes = (List<?>)unpickle(prefix + "_dtypes.pkl");

		for(Object dtype : dtypes){
			Type type = (Type)dtype;

			assertNotNull(type.getDataType());
		}
	}

	private void unpickleEnums(String prefix) throws IOException {
		List<?> enums = (List<?>)unpickle(prefix + "_enums.pkl");

		for(Object _enum : enums){
			PythonEnum pythonEnum = (PythonEnum)_enum;

			assertNotNull(pythonEnum.getValue());
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

	private void unpicklePandasDataFrame(String prefix) throws IOException {
		DataFrame dataFrame = (DataFrame)unpickle(prefix + "_df.pkl");

		BlockManager data = dataFrame.getData();

		Index columnAxis = data.getColumnAxis();
		Index rowAxis = data.getRowAxis();

		assertEquals(Arrays.asList("bool", "int", "float", "str"), columnAxis.getValues());
		assertEquals(Arrays.asList(0, 1, 2), rowAxis.getValues());

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

		List<HasArray> blocks = data.getBlockValues();

		assertEquals(4, blocks.size());

		IntFunction<List<?>> blockValuesFunction = new IntFunction<List<?>>(){

			private List<?> columns = columnAxis.getValues();


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

	static {
		Unpickler.registerConstructor("__main__", "Color", new PythonEnumConstructor("__main__", "Color"));
		Unpickler.registerConstructor("__main__", "ColorCode", new PythonEnumConstructor("__main__", "ColorCode"));
	}
}