from common import _pickle, _platform, _platform_module

from sklearn.datasets import load_iris
from enum import Enum
#from sklearn.externals import joblib as sklearn_joblib
from sklearn.linear_model import LogisticRegressionCV

import builtins
import joblib
import numpy
import pandas
import pickle

class Color(Enum):
	RED = 1
	GREEN = 2
	BLUE = 3

class ColorCode(Enum):
	RED = "r"
	GREEN = "g"
	BLUE = "b"

def _format_dtype(dtype):
	return (dtype if type(dtype) == str else dtype.__name__)

iris = load_iris()

iris_classifier = LogisticRegressionCV()
iris_classifier.fit(iris.data, iris.target)

#sklearn_joblib.dump(iris_classifier, "dump/" + _platform_module("sklearn-joblib", sklearn_joblib.__version__) + ".pkl.z", compress = True)
joblib.dump(iris_classifier, "dump/" + _platform_module("joblib", joblib.__version__) + ".pkl.z", compress = True)

for protocol in range(2, pickle.HIGHEST_PROTOCOL + 1):
	_pickle(iris_classifier, "dump/" + _platform_module("pickle", "p" + str(protocol)) + ".pkl")

def _pickle_builtin_dtypes(dtypes):
	_pickle(dtypes, "dump/" + _platform() + "_dtypes.pkl")

def _pickle_enums(enums):
	_pickle(enums, "dump/" + _platform() + "_enums.pkl")

def _pickle_numpy_array(values, dtype):
	_pickle(values.astype(dtype), "dump/" + _platform_module("numpy", numpy.__version__) + "_" + dtype.__name__ + ".pkl")

def _pickle_numpy_dtypes(dtypes):
	_pickle(dtypes, "dump/" + _platform_module("numpy", numpy.__version__) + "_dtypes.pkl")

def _pickle_pandas_series(values, dtype):
	_pickle(values, "dump/" + _platform_module("pandas", pandas.__version__) + "_" + _format_dtype(dtype) + ".pkl")

def _pickle_pandas_categorical(values, dtype):
	_pickle(values, "dump/" + _platform_module("pandas", pandas.__version__) + "_categorical_" + _format_dtype(dtype) + ".pkl")

def _pickle_pandas_dataframe(df):
	_pickle(df, "dump/" + _platform_module("pandas", pandas.__version__) + "_df.pkl")

def _pickle_pandas_dtypes(dtypes):
	_pickle(dtypes, "dump/" + _platform_module("pandas", pandas.__version__) + "_dtypes.pkl")

values = numpy.asarray([0, 1], dtype = numpy.int8)
_pickle_numpy_array(values, bool)

values = numpy.asarray([False, True], dtype = bool)
series = pandas.Series(values, name = "y", dtype = bool)
_pickle_pandas_series(series, bool)
categorical = pandas.Categorical(values, categories = [False, True], ordered = True)
_pickle_pandas_categorical(categorical, bool)
series = pandas.Series(values, name = "y", dtype = pandas.BooleanDtype())
series[1] = pandas.NA
_pickle_pandas_series(series, "bool-na")

values = numpy.asarray([x for x in range(-128, 127, 1)], dtype = numpy.int8)
_pickle_numpy_array(values, numpy.int8)

series = pandas.Series(values, name = "y", dtype = numpy.int8)
_pickle_pandas_series(series, numpy.int8)
series = pandas.Series(values, name = "y", dtype = pandas.Int8Dtype())
series[1] = pandas.NA
_pickle_pandas_series(series, "int8-na")

values = numpy.asarray([x for x in range(0, 255, 1)], dtype = numpy.uint8)
_pickle_numpy_array(values, numpy.uint8)

series = pandas.Series(values, name = "y", dtype = pandas.UInt8Dtype())
series[1] = pandas.NA
_pickle_pandas_series(series, "uint8-na")

values = numpy.asarray([x for x in range(-32768, 32767, 127)], dtype = numpy.int16)
_pickle_numpy_array(values, numpy.int16)

values = numpy.asarray([x for x in range(0, 65535, 127)], dtype = numpy.uint16)
_pickle_numpy_array(values, numpy.uint16)

values = numpy.asarray([x for x in range(-2147483648, 2147483647, 64 * 32767)], dtype = numpy.int32)
_pickle_numpy_array(values, int)
_pickle_numpy_array(values, numpy.int32)
_pickle_numpy_array(values, numpy.int64)
_pickle_numpy_array(values, numpy.float32)
_pickle_numpy_array(values, numpy.float64)

series = pandas.Series(values, name = "y", dtype = int)
_pickle_pandas_series(series, int)

values = numpy.asarray([x for x in range(0, 4294967295, 64 * 32767)], dtype = numpy.uint32)
_pickle_numpy_array(values, numpy.uint32)
_pickle_numpy_array(values, numpy.uint64)

values = numpy.asarray(["a", "b", "c"], dtype = str)
series = pandas.Series(values, name = "y", dtype = pandas.StringDtype())
series[1] = pandas.NA
_pickle_pandas_series(series, "str-na")

categorical = pandas.Categorical(values = ["a", "b", "c", "d", "e"], dtype = pandas.CategoricalDtype(categories = ["a", "e", "b", "d", "c"], ordered = True))
_pickle_pandas_categorical(categorical, str)

df = pandas.DataFrame(data = {
	"bool" : [False, False, True],
	"int" : [0, 1, 2],
	"float" : [0.0, 1.0, 2.0],
	"str" : ["zero", "one", "two"]
})
_pickle_pandas_dataframe(df)

dtypes = [
	builtins.bool,
	builtins.int,
	builtins.float,
	builtins.str
]

_pickle_builtin_dtypes(dtypes)

enums = [
	Color.RED, Color.GREEN, Color.BLUE,
	ColorCode.RED, ColorCode.GREEN, ColorCode.BLUE
]

_pickle_enums(enums)

dtypes = [
	numpy.bool,
	numpy.int, numpy.int8, numpy.int16, numpy.int32, numpy.int64,
	numpy.uint8, numpy.uint16, numpy.uint32, numpy.uint64,
	numpy.float, numpy.float32, numpy.float64,
	numpy.str, numpy.str_, numpy.unicode
]

_pickle_numpy_dtypes(dtypes)

dtypes = [
	pandas.BooleanDtype(),
	pandas.Int8Dtype(), pandas.Int16Dtype(), pandas.Int32Dtype(), pandas.Int64Dtype(),
	pandas.UInt8Dtype(), pandas.UInt16Dtype(), pandas.UInt32Dtype(), pandas.UInt64Dtype(),
	pandas.StringDtype()
]
_pickle_pandas_dtypes(dtypes)