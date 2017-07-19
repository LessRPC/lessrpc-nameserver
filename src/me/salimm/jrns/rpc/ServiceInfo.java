package me.salimm.jrns.rpc;


/**
 * service information
 * @author Salim
 *
 */
public class ServiceInfo {

	/**
	 * name of the service
	 */
	private String name;

	/**
	 * id of the service
	 */
	private int id;

	/**
	 * types of the input arguments
	 */

	private Class<?>[] inputTypes;

	/**
	 * type of the output object
	 */
	private Class<?> outputType;
	
	public ServiceInfo(String name, int id, Class<?>[] inputTypes, Class<?> outputType) {
		this.name = name;
		this.id = id;
		this.inputTypes = inputTypes;
		this.outputType = outputType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Class<?>[] getInputTypes() {
		return inputTypes;
	}

	public void setInputTypes(Class<?>[] inputTypes) {
		this.inputTypes = inputTypes;
	}

	public Class<?> getOutputType() {
		return outputType;
	}

	public void setOutputType(Class<?> outputType) {
		this.outputType = outputType;
	}

}
