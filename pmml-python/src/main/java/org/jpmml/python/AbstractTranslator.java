/*
 * Copyright (c) 2018 Villu Ruusmann
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import builtins.Type;
import org.dmg.pmml.Apply;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.Expression;
import org.dmg.pmml.Field;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.HasExpression;
import org.dmg.pmml.HasType;
import org.dmg.pmml.OpType;
import org.dmg.pmml.PMMLObject;
import org.dmg.pmml.ParameterField;
import org.jpmml.converter.ExpressionUtil;
import org.jpmml.converter.Feature;
import org.jpmml.converter.FeatureResolver;
import org.jpmml.converter.FeatureUtil;
import org.jpmml.converter.ObjectFeature;
import org.jpmml.converter.PMMLEncoder;
import org.jpmml.converter.TypeUtil;

abstract
public class AbstractTranslator implements FeatureResolver {

	private Scope scope = null;

	private Map<String, FunctionDef> functionDefs = new LinkedHashMap<>();

	private Map<String, String> moduleImports = new LinkedHashMap<>();


	public AbstractTranslator(){
	}

	@Override
	public Feature resolveFeature(String name){
		Scope scope = ensureScope();

		return scope.resolveFeature(name);
	}

	public void registerModuleAliases(){
		Map<String, String> imports = getModuleImports();

		imports.put("np", "numpy");
		imports.put("pd", "pandas");
	}

	public PMMLEncoder ensureEncoder(){
		Scope scope = ensureScope();

		PMMLEncoder encoder = scope.getEncoder();
		if(encoder == null){
			throw new IllegalStateException();
		}

		return encoder;
	}

	public Scope ensureScope(){
		Scope scope = getScope();

		if(scope == null){
			throw new IllegalStateException();
		}

		return scope;
	}

	public FunctionDef getFunctionDef(String name){
		Map<String, FunctionDef> functionDefs = getFunctionDefs();

		return functionDefs.get(name);
	}

	public void addFunctionDef(String string){
		Map<String, FunctionDef> functionDefs = getFunctionDefs();

		FunctionDef functionDef;

		try {
			FunctionDefParser functionDefParser = new FunctionDefParser();

			functionDef = functionDefParser.parseFunctionDef(string);
		} catch(Exception e){
			throw new FunctionDefTranslationException(string, e);
		}

		functionDefs.put(functionDef.getName(), functionDef);
	}

	public String canonicalizeDottedName(String dottedName){
		Map<String, String> imports = getModuleImports();

		int dot = dottedName.indexOf('.');
		if(dot > -1){
			String prefix = dottedName.substring(0, dot);
			prefix = imports.getOrDefault(prefix, prefix);

			String suffix = dottedName.substring(dot + 1);

			return prefix + "." + suffix;
		} else

		{
			return dottedName;
		}
	}

	public Expression encodeFunction(String dottedName, List<?> arguments){
		PMMLEncoder encoder = ensureEncoder();

		String module;
		String name;

		FunctionDef functionDef;

		int dot = dottedName.lastIndexOf('.');
		if(dot > -1){
			module = dottedName.substring(0, dot);
			name = dottedName.substring(dot + 1);

			functionDef = null;
		} else

		{
			module = "builtins";
			name = dottedName;

			functionDef = getFunctionDef(name);
		} // End if

		if(functionDef != null){
			List<FunctionDef.Parameter> parameters = functionDef.getParameters();
			if(arguments.size() != parameters.size()){
				String nameAndSignature = parameters.stream()
					.map(FunctionDef.Parameter::getName)
					.collect(Collectors.joining(", ", name + "(", ")"));

				throw new OperationException("Function \'" + nameAndSignature + "\' expects " + parameters.size() + " argument(s), got " + arguments.size() + " argument(s)");
			}

			List<Feature> features = arguments.stream()
				.map(argument -> {

					if(argument instanceof FieldRef){
						FieldRef fieldRef = (FieldRef)argument;

						Field field;

						try {
							field = encoder.getField(fieldRef.requireField());
						} catch(IllegalArgumentException iae){
							ParameterField parameterField = new ParameterField(fieldRef.requireField())
								.setOpType(null)
								// XXX: Since null is not allowed, use DataType#STRING as the next most versatile data type
								.setDataType(DataType.STRING);

							return new ObjectFeature(encoder, parameterField);
						}

						return FeatureUtil.createFeature(field, encoder);
					}

					return (Feature)argument;
				})
				.collect(Collectors.toList());

			DefineFunction defineFunction = encoder.getDefineFunction(name);
			if(defineFunction == null){
				List<Feature> parameterFeatures = new ArrayList<>();

				for(int i = 0; i < parameters.size(); i++){
					FunctionDef.Parameter parameter = parameters.get(i);
					Feature feature = features.get(i);

					ParameterField parameterField = new ParameterField(parameter.getName())
						.setOpType(null)
						.setDataType(feature.getDataType());

					Feature parameterFeature = new ObjectFeature(encoder, parameterField){

						@Override
						public Field<?> getField(){
							return parameterField;
						}
					};

					parameterFeatures.add(parameterFeature);
				}

				Scope scope = new FunctionDefScope(functionDef, parameterFeatures, encoder);

				ExpressionTranslator expressionTranslator = new ExpressionTranslator(scope){

					@Override
					public FunctionDef getFunctionDef(String name){
						return AbstractTranslator.this.getFunctionDef(name);
					}
				};

				defineFunction = expressionTranslator.translateDef(functionDef.getString());
			}

			Apply apply = ExpressionUtil.createApply(defineFunction);

			for(Feature feature : features){
				apply.addExpressions(feature.ref());
			}

			return apply;
		}

		List<Expression> expressions = arguments.stream()
			.map(argument -> {

				if(argument instanceof Feature){
					Feature feature = (Feature)argument;

					return feature.ref();
				}

				return (Expression)argument;
			})
			.collect(Collectors.toList());

		return FunctionUtil.encodeFunction(module, name, expressions, encoder);
	}

	protected DefineFunction createDefineFunction(String name, Type type, Expression expression){
		PMMLEncoder encoder = ensureEncoder();

		DefineFunction defineFunction = new DefineFunction(name, OpType.CONTINUOUS, DataType.DOUBLE, null, expression);

		updateType(defineFunction, type);

		encoder.addDefineFunction(defineFunction);

		return defineFunction;
	}

	protected DerivedField ensureDerivedField(String name, Type type, Expression expression){
		PMMLEncoder encoder = ensureEncoder();

		DerivedField derivedField = encoder.getDerivedField(name);
		if(derivedField != null){
			return derivedField;
		}

		derivedField = new DerivedField(name, OpType.CONTINUOUS, DataType.DOUBLE, expression);

		updateType(derivedField, type);

		encoder.addDerivedField(derivedField);

		return derivedField;
	}

	private <E extends PMMLObject & HasType<E> & HasExpression<E>> void updateType(E object, Type type){
		OpType opType;
		DataType dataType;

		if(type != null){
			dataType = type.getDataType();
		} else

		{
			Expression expression = object.getExpression();

			dataType = ExpressionUtil.getDataType(expression, this);
		} // End if

		if(dataType != null){
			opType = TypeUtil.getOpType(dataType);

			object
				.setOpType(opType)
				.setDataType(dataType);
		}
	}

	public Scope getScope(){
		return this.scope;
	}

	public void setScope(Scope scope){
		this.scope = Objects.requireNonNull(scope);
	}

	public Map<String, FunctionDef> getFunctionDefs(){
		return this.functionDefs;
	}

	public Map<String, String> getModuleImports(){
		return this.moduleImports;
	}

	static
	public String toSingleLine(String string){
		return string
			.replaceAll("\t", "\\\\t")
			.replaceAll("\n", "\\\\n");
	}
}