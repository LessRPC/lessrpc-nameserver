package org.mouji.ns.core.rpc;

import java.sql.SQLException;

import org.mouji.common.errors.ApplicationSpecificErrorException;
import org.mouji.common.errors.DatabaseNotSupported;
import org.mouji.common.errors.ExecuteInternalError;
import org.mouji.common.errors.InvalidArgsException;
import org.mouji.common.errors.ServiceNotSupportedException;
import org.mouji.common.info.SerializationFormat;
import org.mouji.common.info.SerializedObject;
import org.mouji.common.info.ServiceInfo;
import org.mouji.common.info.ServiceProviderInfo;
import org.mouji.common.info.ServiceRequest;
import org.mouji.common.info.ServiceSupportInfo;
import org.mouji.common.info.StubEnvInfo;
import org.mouji.common.info.responses.ServiceResponse;
import org.mouji.common.services.NameServer;
import org.mouji.common.services.NameServerServices;
import org.mouji.common.services.ServiceProvider;

public class NameServerServiceProvider implements ServiceProvider {

	private final NameServer nameServer;

	/**
	 * provider object for name server (current node)
	 */
	private final ServiceProviderInfo spInfo;

	public NameServerServiceProvider(NameServer nameServer) {
		this.nameServer = nameServer;
		this.spInfo = new ServiceProviderInfo(nameServer.getURL(), nameServer.getPort(), StubEnvInfo.currentEnvInfo());
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
			if (!(types[i].isAssignableFrom(request.getArgs()[0].getContent().getClass()))) {
				throw new InvalidArgsException("Argument number " + i + " expected to have type "
						+ types[i].getCanonicalName() + " but an object of type "
						+ request.getArgs()[i].getClass().getCanonicalName() + " was provided");
			}
		}

		return true;

	}

	private ServiceResponse<?> handleUnregister(ServiceRequest request)
			throws ClassNotFoundException, SQLException, DatabaseNotSupported, InvalidArgsException {
		checkArgs(2, request, new Class[] { ServiceInfo.class, ServiceProviderInfo.class });
		boolean flag = nameServer.unregister((ServiceInfo<?>) request.getArgs()[0].getContent(),
				(ServiceProviderInfo) request.getArgs()[1].getContent());

		return new ServiceResponse<>(request.getService(), new SerializedObject<>(new Boolean(flag)),
				request.getRequestId());
	}

	private ServiceResponse<?> handleRegister(ServiceRequest request)
			throws InvalidArgsException, ClassNotFoundException, SQLException, DatabaseNotSupported {
		checkArgs(1, request, new Class[] { ServiceSupportInfo.class });
		boolean flag = nameServer.register((ServiceSupportInfo) request.getArgs()[0].getContent());

		return new ServiceResponse<>(request.getService(), new SerializedObject<>(new Boolean(flag)),
				request.getRequestId());
	}

	private ServiceResponse<?> handleGetServiceInfoByName(ServiceRequest request)
			throws InvalidArgsException, ClassNotFoundException, SQLException, DatabaseNotSupported {
		checkArgs(1, request, new Class[] { String.class });
		ServiceInfo<?> info = nameServer.getServiceInfoByName((String) request.getArgs()[0].getContent());

		return new ServiceResponse<ServiceInfo<?>>(request.getService(), new SerializedObject<>(info),
				request.getRequestId());
	}

	private ServiceResponse<?> handleGetServiceInfoById(ServiceRequest request)
			throws InvalidArgsException, ClassNotFoundException, SQLException, DatabaseNotSupported {
		checkArgs(1, request, new Class[] { Integer.class });
		ServiceInfo<?> info = nameServer.getServiceInfoById((Integer) request.getArgs()[0].getContent());

		return new ServiceResponse<ServiceInfo<?>>(request.getService(), new SerializedObject<>(info),
				request.getRequestId());
	}

	private ServiceResponse<?> handleGetAllProvidersService(ServiceRequest request)
			throws InvalidArgsException, ClassNotFoundException, SQLException, DatabaseNotSupported {
		checkArgs(0, request, new Class[] {});
		ServiceSupportInfo[] info = nameServer.getAllProviders();

		return new ServiceResponse<ServiceSupportInfo[]>(request.getService(), new SerializedObject<>(info),
				request.getRequestId());
	}

	private ServiceResponse<?> handleGetProvidersService(ServiceRequest request)
			throws InvalidArgsException, ClassNotFoundException, SQLException, DatabaseNotSupported {
		checkArgs(1, request, new Class[] { ServiceInfo.class });
		ServiceSupportInfo[] info = nameServer.getProviders((ServiceInfo<?>) request.getArgs()[0].getContent());

		return new ServiceResponse<ServiceSupportInfo[]>(request.getService(), new SerializedObject<>(info),
				request.getRequestId());
	}

	private ServiceResponse<?> handleGetProviderService(ServiceRequest request)
			throws InvalidArgsException, ClassNotFoundException, SQLException, DatabaseNotSupported {
		checkArgs(1, request, new Class[] { ServiceInfo.class });
		ServiceSupportInfo info = nameServer.getProvider((ServiceInfo<?>) request.getArgs()[0].getContent());

		return new ServiceResponse<ServiceSupportInfo>(request.getService(), new SerializedObject<>(info),
				request.getRequestId());

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

}
