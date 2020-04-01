package com.example.attest.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class ServiceException extends RuntimeException {

	public static final String ERROR_TRANSACTION_DUPLICATED = "ERROR_TRANSACTION_DUPLICATED";
	public static final String ERROR_TRANSACTION_NOT_FOUND = "ERROR_TRANSACTION_NOT_FOUND";


	private final String code;
	private final HttpStatus httpStatus;
	private final Map<String, String> params;

	private ServiceException(String code, HttpStatus httpStatus, String message, Throwable cause, Map<String, String> params) {

		super(message, cause);
		this.code = code;
		this.httpStatus = httpStatus;
		this.params = params;
	}

	public String getCode() {

		return this.code;
	}

	public HttpStatus getHttpStatus() {

		return this.httpStatus;
	}

	public Map<String, String> getParams() {

		return this.params;
	}

	public static class Builder {

		private String code;
		private String message;
		private HttpStatus httpStatus;
		private Throwable cause;
		private Map<String, String> params;

		public Builder(String code) {

			this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			this.code = code;
		}

		public ServiceException.Builder withMessage(String message) {

			this.message = message;
			return this;
		}

		public ServiceException.Builder withHttpStatus(HttpStatus httpStatus) {

			this.httpStatus = httpStatus;
			return this;
		}

		public ServiceException.Builder withCause(Throwable cause) {

			this.cause = cause;
			return this;
		}

		public ServiceException.Builder withParams(Map<String, String> params) {

			this.params = params;
			return this;
		}

		public ServiceException.Builder addParam(String key, String value) {

			if (this.params == null) {
				this.params = new HashMap();
			}

			this.params.put(key, value);
			return this;
		}

		public ServiceException build() {

			return new ServiceException(this.code, this.httpStatus, this.message, this.cause, this.params);
		}
	}
}

