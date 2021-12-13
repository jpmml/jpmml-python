from common import _pickle, _platform_module

import numpy
import pickle

ufuncs = ["absolute", "arccos", "arcsin", "arctan", "arctan2", "ceil", "clip", "cos", "cosh", "degrees", "rad2deg", "exp", "expm1", "floor", "fmax", "fmin", "hypot", "log", "log1p", "log10", "negative", "power", "radians", "deg2rad", "reciprocal", "rint", "sign", "sin", "sinh", "sqrt", "square", "tan", "tanh"]

numpy_module = __import__("numpy")

for ufunc in ufuncs:
	numpy_ufunc = getattr(numpy_module, ufunc)
	_pickle(numpy_ufunc, "ufunc/" + _platform_module("numpy", numpy.__version__) + "_" + ufunc + ".pkl")