from common import _pickle, _platform

funcs = ["acos", "asin", "atan", "atan2", "ceil", "cos", "cosh", "degrees", "exp", "expm1", "fabs", "floor", "hypot", "isnan", "log", "log1p", "log10", "pow", "radians", "sin", "sinh", "sqrt", "tan", "tanh", "trunc"]

math_module = __import__("math")

for func in funcs:
	math_func = getattr(math_module, func)
	_pickle(math_func, "func/" + _platform() + "_math_" + func + ".pkl")
