import pickle
import platform

def _platform():
	version = platform.python_version_tuple()
	return ("python-" + version[0] + "." + version[1])

def _module(name, version):
	return (name + "-" + version)

def _platform_module(name, version):
	return (_platform() + "_" + _module(name, version))

def _pickle(obj, path, protocol = pickle.HIGHEST_PROTOCOL):
	con = open(path, "wb")
	pickle.dump(obj, con, protocol = protocol)
	con.close()