package com.blogosphere.blog.exception;

import java.util.Map;
import java.util.Optional;

import javax.persistence.RollbackException;

import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.blogosphere.blog.core.Utils;
import com.blogosphere.blog.dto.RestApiErrorDto;

@ControllerAdvice
@RestController
public class RestApiExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(RestApiExceptionHandler.class);

	public static final String EXCEPTION_DUPLICATE_EMAIL = "Email";
	public static final String EXCEPTION_FOREIGN_KEY = "The resource does not exist";

	@Autowired
	private ModelConstraint modelConstraint;
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		BindingResult bindingResult = ex.getBindingResult();
		FieldError fieldError = bindingResult.getFieldError();
		String defaultMessage = fieldError.getDefaultMessage();
		RestApiErrorDto errorDetails = new RestApiErrorDto(HttpStatus.BAD_REQUEST, defaultMessage, null);
		
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
    @ExceptionHandler({ ConstraintViolationException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<RestApiErrorDto> handleConstraintViolationException(ConstraintViolationException e) {
        String fieldName = e.getConstraintName();
        RestApiErrorDto message = getResourceMessage(fieldName + ".alreadyExists", new RestApiErrorDto(HttpStatus.CONFLICT, "Already Exists"));
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }
    
    private RestApiErrorDto getResourceMessage(String key, RestApiErrorDto defaultMessage) {
    	RestApiErrorDto message = modelConstraint.getConstraintMap().get(key);
        if (StringUtils.isEmpty(message)) {
            return defaultMessage;
        }
        return message;
    }
	
    @ExceptionHandler(value = { RollbackException.class, TransactionSystemException.class, IllegalArgumentException.class, IllegalStateException.class,
			DataIntegrityViolationException.class, MappingException.class })
	protected ResponseEntity<RestApiErrorDto> handleConstraintExceptions(RuntimeException ex, WebRequest request) {
		if (ex instanceof DataIntegrityViolationException) {
			ex.printStackTrace();
		}
		String rootMsg = Utils.getRootCause(ex).getMessage();
		System.out.println("Root Cause "+ rootMsg);
		System.out.println("ROOT MESSAGE = === " + rootMsg); //Duplicate entry 'abc@example.com' for key 'unique_users_email_idx'
		
		String message = "Please check the body of your post request. It does not comply with the schema";
		RestApiErrorDto errorDetails = null;
		String lowerCaseMsg = rootMsg.toLowerCase();
		Optional<Map.Entry<String, RestApiErrorDto>> entry = modelConstraint.getConstraintMap().entrySet().stream()
				.filter(mapItem -> lowerCaseMsg.contains(mapItem.getKey())).findAny();
		if (entry.isPresent()) {
		  errorDetails = entry.get().getValue(); 
		  errorDetails.setDetails(request.getDescription(false));
		}
		else {
			errorDetails = new RestApiErrorDto(HttpStatus.CONFLICT, message,
					request.getDescription(false));
		}
		
		return new ResponseEntity<>(errorDetails, errorDetails.getErrorCode());
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public final ResponseEntity<RestApiErrorDto> handleEntityNotFoundException(EntityNotFoundException ex,
			WebRequest request) {
		RestApiErrorDto errorDetails = new RestApiErrorDto(HttpStatus.NOT_FOUND, ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<RestApiErrorDto> handleAllExceptions(Exception ex, WebRequest request) {
		RestApiErrorDto errorDetails = new RestApiErrorDto(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getDescription(false));
		log.error("There was an exception when executing request={}, errorDetails={}, ex={}", request.getDescription(false), errorDetails, ex);
		errorDetails = new RestApiErrorDto(HttpStatus.INTERNAL_SERVER_ERROR, request.getDescription(false), null);
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
