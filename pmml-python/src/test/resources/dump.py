from common import _pickle, _platform_module

from sklearn.datasets import load_iris
#from sklearn.externals import joblib as sklearn_joblib
from sklearn.linear_model import LogisticRegressionCV

import joblib
import numpy
import pandas
import pickle

iris = load_iris()

iris_classifier = LogisticRegressionCV()
iris_classifier.fit(iris.data, iris.target)

#sklearn_joblib.dump(iris_classifier, "dump/" + _platform_module("sklearn-joblib", sklearn_joblib.__version__) + ".pkl.z", compress = True)
joblib.dump(iris_classifier, "dump/" + _platform_module("joblib", joblib.__version__) + ".pkl.z", compress = True)

for protocol in range(2, pickle.HIGHEST_PROTOCOL + 1):
	_pickle(iris_classifier, "dump/" + _platform_module("pickle", "p" + str(protocol)) + ".pkl")

def _pickle_numpy_array(values, dtype):
	_pickle(values.astype(dtype), "dump/" + _platform_module("numpy", numpy.__version__) + "_" + dtype.__name__ + ".pkl")

def _pickle_pandas_series(values, dtype):
	_pickle(values, "dump/" + _platform_module("pandas", pandas.__version__) + "_" + dtype.__name__ + ".pkl")

def _pickle_pandas_dataframe(df):
	_pickle(df, "dump/" + _platform_module("pandas", pandas.__version__) + "_df.pkl")

values = numpy.asarray([0, 1], dtype = numpy.int8)
_pickle_numpy_array(values, bool)

values = numpy.asarray([False, True], dtype = bool)
series = pandas.Series(values, name = "y", dtype = bool)
_pickle_pandas_series(series, bool)

values = numpy.asarray([x for x in range(-128, 127, 1)], dtype = numpy.int8)
_pickle_numpy_array(values, numpy.int8)

series = pandas.Series(values, name = "y", dtype = numpy.int8)
_pickle_pandas_series(series, numpy.int8)

values = numpy.asarray([x for x in range(0, 255, 1)], dtype = numpy.uint8)
_pickle_numpy_array(values, numpy.uint8)

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

df = pandas.DataFrame(data = {
	"bool" : [False, False, True],
	"int" : [0, 1, 2],
	"float" : [0.0, 1.0, 2.0],
	"str" : ["zero", "one", "two"]
})
_pickle_pandas_dataframe(df)