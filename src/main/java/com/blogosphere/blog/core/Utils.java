package com.blogosphere.blog.core;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.lang.model.type.ErrorType;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

public class Utils {
	public static final String DEFAULT_ZONE_ID = "UTC";
	public static final ZoneId DEFAULT_ZONE = ZoneId.of(DEFAULT_ZONE_ID);

	public static ZonedDateTime now() {
		return ZonedDateTime.now(Utils.DEFAULT_ZONE);
	}

	// http://stackoverflow.com/a/28565320/548473
	public static Throwable getRootCause(Throwable t) {
		Throwable result = t;
		Throwable cause;

		while (null != (cause = result.getCause()) && (result != cause)) {
			result = cause;
		}
		return result;
	}

	public static String getMessage(Throwable e) {
		return e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.getClass().getName();
	}

	public static Throwable logAndGetRootCause(Logger log, HttpServletRequest req, Exception e, boolean logException,
			HttpStatus status) {
		Throwable rootCause = getRootCause(e);
		if (logException) {
			log.error(status + " at request " + req.getRequestURL(), rootCause);
		} else {
			log.warn("{} at request  {}: {}", status, req.getRequestURL(), rootCause.toString());
		}
		return rootCause;
	}
}
