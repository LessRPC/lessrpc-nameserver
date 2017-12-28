package org.lessrpc.ns.core.rpc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.lessrpc.common.errors.ApplicationSpecificErrorException;
import org.lessrpc.common.errors.DatabaseNotSupported;
import org.lessrpc.common.errors.ExecuteInternalError;
import org.lessrpc.common.errors.InvalidArgsException;
import org.lessrpc.common.errors.ServiceNotSupportedException;
import org.lessrpc.common.info.EnvironmentInfo;
import org.lessrpc.common.info.SerializationFormat;
import org.lessrpc.common.info.SerializedObject;
import org.lessrpc.common.info.ServiceInfo;
import org.lessrpc.common.info.ServiceProviderInfo;
import org.lessrpc.common.info.ServiceRequest;
import org.lessrpc.common.info.ServiceSupportInfo;
import org.lessrpc.common.info.responses.ServiceResponse;
import org.lessrpc.common.services.NameServer;
import org.lessrpc.common.services.NameServerServices;
import org.lessrpc.common.services.ServiceProvider;

public class NameServerServiceProvider implements ServiceProvider {

	private final NameServer nameServer;

	/**
	 * provider object for name server (current node)
	 */
	private final ServiceProviderInfo spInfo;

	public NameServerServiceProvider(NameServer nameServer) {
		this.nameServer = nameServer;
		this.spInfo = new ServiceProviderInfo(nameServer.getURL(), nameServer.getPort(), EnvironmentInfo.currentEnvInfo());
	}

	@Override
	public boolean ping() {
		return nameServer.ping();
	}

	@Override
	public ServiceResponse<?> execute(ServiceRequest request)
			throws ApplicationSpecificErrorException, ExecuteInternalError, InvalidArgsException,
			ServiceNotSupportedException, ClassNotFoundException, SQLException, DatabaseNotSupported {
		if (request.getService().equals(NameServerServices.GET_PROVIDER)) {
			return handleGetProviderService(request);
		} else if (request.getService().equals(NameServerServices.GET_PROVIDERS)) {
			return handleGetProvidersService(request);
		} else if (request.getService().equals(NameServerServices.GET_ALL_PROVIDERS)) {
			return handleGetAllProvidersService(request);
		} else if (request.getService().equals(NameServerServices.GET_SERVICE_INFO_BY_ID)) {
			return handleGetServiceInfoById(request);
		} else if (request.getService().equals(NameServerServices.GET_SERVICE_INFO_BY_NAME)) {
			return handleGetServiceInfoByName(request);
		} else if (request.getService().equals(NameServerServices.REGISTER)) {
			return handleRegister(request);
		} else if (request.getService().equals(NameServerServices.UNREGISTER)) {
			return handleUnregister(request);
		} else if (request.getService().equals(NameServerServices.UNREGISTER_ALL)) {
			return handleUnregisterAll(request);
		} else if (request.getService().equals(NameServerServices.CHECK_PROVIDER_STATUS)) {
			return handleCheckProviderStatus(request);
		}
		throw new ServiceNotSupportedException(request.getService());
	}

	private boolean checkArgs(int requiredArgs, ServiceRequest request, Class<?>[] types) throws InvalidArgsException {
		if (request.getArgs() == null) {
			throw new InvalidArgsException(requiredArgs
					+ " argument(s) of type 'ServiceProviderInfo' was required but null args list was recieved");
		}

		if (request.getArgs().length != requiredArgs) {
			throw new InvalidArgsException("Exactly " + requiredArgs + " argument(s) was requried but "
					+ request.getArgs().length + " was received");
		}

		for (int i = 0; i < request.getArgs().length; i++) {
			// checks to make sure the provided object is not null
			if (request.getArgs()[i] == null) {
				throw new InvalidArgsException(
						"Required number of arguemnts was provided but the argument number " + i + " was null");
			}
		}
		for (int i = 0; i < request.getArgs().length; i++) {
			// checks to make sure the provided object is not null
			if (request.getArgs()[i].getContent() == null) {
				throw new InvalidArgsException(
						"Required number of arguemnts was provided but the content of argument number " + i
								+ " object was null");
			}
		}

		for (int i = 0; i < request.getArgs().length; i++) {
			// checking if the provided object is is instance of required type
			if (!(types[i].isAssignableFrom(request.getArgs()[i].getContent().getClass()))) {
				throw new InvalidArgsException("Argument number " + i + " expected to have type "
						+ types[i].getCanonicalName() + " but an object of type "
						+ request.getArgs()[i].getClass().getCanonicalName() + " was provided");
			}
		}

		return true;

	}

	private ServiceResponse<?> handleUnregister(ServiceRequest request)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported, InvalidArgsException, ApplicationSpecificErrorException {
		checkArgs(2, request, new Class[] { ServiceInfo.class, ServiceProviderInfo.class });
		boolean flag = nameServer.unregister((ServiceInfo<?>) request.getArgs()[0].getContent(),
				(ServiceProviderInfo) request.getArgs()[1].getContent());

		return new ServiceResponse<>(request.getService(), new SerializedObject<>(new Boolean(flag)),
				request.getRequestId());
	}

	private ServiceResponse<?> handleCheckProviderStatus(ServiceRequest request)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported, InvalidArgsException, ApplicationSpecificErrorException {
		checkArgs(1, request, new Class[] { ServiceProviderInfo.class });
		boolean flag = nameServer.checkProviderStatus((ServiceProviderInfo) request.getArgs()[0].getContent());

		return new ServiceResponse<>(request.getService(), new SerializedObject<>(new Boolean(flag)),
				request.getRequestId());
	}

	private ServiceResponse<?> handleUnregisterAll(ServiceRequest request)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported, InvalidArgsException, ApplicationSpecificErrorException {
		checkArgs(1, request, new Class[] { ServiceProviderInfo.class });
		boolean flag = nameServer.unregisterAll((ServiceProviderInfo) request.getArgs()[0].getContent());

		return new ServiceResponse<>(request.getService(), new SerializedObject<>(new Boolean(flag)),
				request.getRequestId());
	}

	private ServiceResponse<?> handleRegister(ServiceRequest request)
			throws InvalidArgsException, ClassNotFoundException, SQLException, DatabaseNotSupported, ApplicationSpecificErrorException {
		checkArgs(1, request, new Class[] { ServiceSupportInfo.class });
		boolean flag = nameServer.register((ServiceSupportInfo) request.getArgs()[0].getContent());

		return new ServiceResponse<>(request.getService(), new SerializedObject<>(new Boolean(flag)),
				request.getRequestId());
	}

	private ServiceResponse<?> handleGetServiceInfoByName(ServiceRequest request)
			throws InvalidArgsException, ClassNotFoundException, SQLException, DatabaseNotSupported, ApplicationSpecificErrorException {
		checkArgs(1, request, new Class[] { String.class });
		ServiceInfo<?> info = nameServer.getServiceInfoByName((String) request.getArgs()[0].getContent());

		return new ServiceResponse<ServiceInfo<?>>(request.getService(),
				getSerializedObject(info, ServiceInfo.class.getName()), request.getRequestId());
	}

	private ServiceResponse<?> handleGetServiceInfoById(ServiceRequest request)
			throws InvalidArgsException, ClassNotFoundException, SQLException, DatabaseNotSupported, ApplicationSpecificErrorException {
		checkArgs(1, request, new Class[] { Integer.class });
		ServiceInfo<?> info = nameServer.getServiceInfoById((Integer) request.getArgs()[0].getContent());

		return new ServiceResponse<ServiceInfo<?>>(request.getService(),
				getSerializedObject(info, ServiceInfo.class.getName()), request.getRequestId());
	}

	private ServiceResponse<?> handleGetAllProvidersService(ServiceRequest request)
			throws InvalidArgsException, ClassNotFoundException, SQLException, DatabaseNotSupported, ApplicationSpecificErrorException {
		checkArgs(0, request, new Class[] {});
		ServiceSupportInfo[] info = nameServer.getAllProviders();

		return new ServiceResponse<ServiceSupportInfo[]>(request.getService(),
				getSerializedObject(info, ServiceSupportInfo[].class.getName()), request.getRequestId());
	}

	private ServiceResponse<?> handleGetProvidersService(ServiceRequest request)
			throws InvalidArgsException, ClassNotFoundException, SQLException, DatabaseNotSupported, ApplicationSpecificErrorException {
		checkArgs(1, request, new Class[] { ServiceInfo.class });
		ServiceSupportInfo[] info = nameServer.getProviders((ServiceInfo<?>) request.getArgs()[0].getContent());

		return new ServiceResponse<ServiceSupportInfo[]>(request.getService(),
				getSerializedObject(info, ServiceSupportInfo[].class.getName()), request.getRequestId());
	}

	private ServiceResponse<?> handleGetProviderService(ServiceRequest request)
			throws InvalidArgsException, ClassNotFoundException, SQLException, DatabaseNotSupported, ApplicationSpecificErrorException {
		checkArgs(1, request, new Class[] { ServiceInfo.class });
		ServiceSupportInfo info = nameServer.getProvider((ServiceInfo<?>) request.getArgs()[0].getContent());

		return new ServiceResponse<ServiceSupportInfo>(request.getService(),
				getSerializedObject(info, ServiceSupportInfo.class.getName()), request.getRequestId());

	}

	private <T extends Object> SerializedObject<T> getSerializedObject(T content, String clsPath) {
		if (content == null) {
			return new SerializedObject<>();
		}
		return new SerializedObject<>(content);
	}

	@Override
	public ServiceProviderInfo info() {
		return spInfo;
	}

	@Override
	public ServiceSupportInfo service(ServiceInfo<?> info) throws ServiceNotSupportedException {
		return new ServiceSupportInfo(info, spInfo, new SerializationFormat[] { SerializationFormat.defaultFotmat() });
	}

	public NameServer getNameServer() {
		return nameServer;
	}

	@Override
	public List<ServiceSupportInfo> listSupport() {
		List<ServiceSupportInfo> list = new ArrayList<ServiceSupportInfo>();
		list.add(new ServiceSupportInfo(NameServerServices.CHECK_PROVIDER_STATUS, spInfo,
				new SerializationFormat[] { SerializationFormat.defaultFotmat() }));
		list.add(new ServiceSupportInfo(NameServerServices.GET_ALL_PROVIDERS, spInfo,
				new SerializationFormat[] { SerializationFormat.defaultFotmat() }));
		list.add(new ServiceSupportInfo(NameServerServices.GET_PROVIDER, spInfo,
				new SerializationFormat[] { SerializationFormat.defaultFotmat() }));
		list.add(new ServiceSupportInfo(NameServerServices.GET_PROVIDERS, spInfo,
				new SerializationFormat[] { SerializationFormat.defaultFotmat() }));
		list.add(new ServiceSupportInfo(NameServerServices.GET_SERVICE_INFO_BY_ID, spInfo,
				new SerializationFormat[] { SerializationFormat.defaultFotmat() }));
		list.add(new ServiceSupportInfo(NameServerServices.GET_SERVICE_INFO_BY_NAME, spInfo,
				new SerializationFormat[] { SerializationFormat.defaultFotmat() }));
		list.add(new ServiceSupportInfo(NameServerServices.REGISTER, spInfo,
				new SerializationFormat[] { SerializationFormat.defaultFotmat() }));
		list.add(new ServiceSupportInfo(NameServerServices.UNREGISTER, spInfo,
				new SerializationFormat[] { SerializationFormat.defaultFotmat() }));
		list.add(new ServiceSupportInfo(NameServerServices.UNREGISTER_ALL, spInfo,
				new SerializationFormat[] { SerializationFormat.defaultFotmat() }));
		return list;
	}

}
