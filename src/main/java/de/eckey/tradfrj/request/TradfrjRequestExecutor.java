package de.eckey.tradfrj.request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import de.eckey.tradfrj.service.CoapRequest;
import de.eckey.tradfrj.service.CoapResponse;
import de.eckey.tradfrj.service.ServiceException;
import de.eckey.tradfrj.service.TradfrjService;

public class TradfrjRequestExecutor {

	private final TradfrjService service;

	public TradfrjRequestExecutor(TradfrjService service) {
		this.service = service;
	}

	public <T> T executeRequest(TradfrjRequest<T> request) throws ServiceException {
		final CoapRequest coapRequest = request.getRequest();
		final CoapResponse coapResponse = service.sendRequest(coapRequest);

		return request.handleResponse(coapResponse);
	}

	public <T> T executeRequest(TradfrjRequestBuilder<T> requestBuilder) throws ServiceException {
		final TradfrjRequest<T> request = requestBuilder.getRequest();
		final CoapRequest coapRequest = request.getRequest();
		final CoapResponse coapResponse = service.sendRequest(coapRequest);

		return request.handleResponse(coapResponse);
	}

	public <T> Collection<T> executeRequest(TradfrjRequestIterator<T> requestList) throws ServiceException {
		final Collection<T> result = new ArrayList<>();
		final AtomicReference<ServiceException> exception = new AtomicReference<ServiceException>(null);
		requestList.forEachRemaining(request -> {
			try {
				final Optional<T> optional = executeRequest(request);
				if (optional.isPresent()) {
					result.add(optional.get());
				}
			} catch (ServiceException e) {
				exception.set(e);
				return;
			}
		});
		if (exception.get() == null) {
			return result;
		} else {
			throw new ServiceException("exception occured while executing request list", exception.get());
		}
	}
}
