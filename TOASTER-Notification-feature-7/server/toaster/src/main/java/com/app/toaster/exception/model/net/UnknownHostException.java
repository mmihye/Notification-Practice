package com.app.toaster.exception.model.net;

import com.app.toaster.exception.Error;

public class UnknownHostException extends CustomJavaNetException {
	public UnknownHostException(Error error, String message) {
		super(error, message);
	}

}
