package org.obolibrary.cli;

import org.apache.log4j.Logger;
import org.obolibrary.cli.OBORunnerConfiguration.Variable;

public class OBORunnerConfigCLIReader {

	private static final Logger logger = Logger.getLogger(OBORunnerConfigCLIReader.class);
	
	public static OBORunnerConfiguration readConfig(String[] args)
	{
		OBORunnerConfiguration configuration = new OBORunnerConfiguration();
		Iterable<Variable<?>> variables = configuration.getVariables();
		int i=0;
		while (i < args.length) {
			String opt = args[i];
			i++;
outer:		for (Variable<?> variable : variables) {
				Iterable<String> parameters = variable.getParameters();
				if (parameters != null) {
					for (String parameter : parameters) {
						if (parameter.equals(opt)) {
							String value = null;
							if (variable.doesReadValue()) {
								value = args[i];
								i++;
							}
							boolean success = variable.setValue(value);
							if (!success) {
								logger.warn(variable.getSetValueFailure());
							}
							break outer;
						}
					}
				}
				else {
					String value = opt;
					if (variable.doesReadValue()) {
						value = args[i];
						i++;
					}
					boolean success = variable.setValue(value);
					if (!success) {
						logger.warn(variable.getSetValueFailure());
					}
					break outer;
				}
			}
		}
		return configuration;
	}
}
