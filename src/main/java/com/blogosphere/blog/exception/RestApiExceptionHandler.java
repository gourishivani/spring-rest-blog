package com.blogosphere.blog.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.blogosphere.blog.core.Utils;
import com.blogosphere.blog.vo.RestApiErrorVo;

@ControllerAdvice
@RestController
public class RestApiExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(RestApiExceptionHandler.class);

//	@ExceptionHandler(MethodArgumentNotValidException.class)
//	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
//	@ResponseBody
//	public RestApiErrorVo handleValidationError(MethodArgumentNotValidException ex) {
//		BindingResult bindingResult = ex.getBindingResult();
//		FieldError fieldError = bindingResult.getFieldError();
//		String defaultMessage = fieldError.getDefaultMessage();
//		return new RestApiErrorVo("VALIDATION_FAILED", defaultMessage, null);
//	}

	public static final String EXCEPTION_DUPLICATE_EMAIL = "Email";

	private static Map<String, String> exceptionMap = new HashMap<String, String>();
	static {
		exceptionMap.put("unique_users_email_idx", EXCEPTION_DUPLICATE_EMAIL);
	}

	@Autowired
	private MessageUtil messageUtil;

	@ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class,
			DataIntegrityViolationException.class })
	protected ResponseEntity<RestApiErrorVo> handleConflict(RuntimeException ex, WebRequest request) {
		if (ex instanceof DataIntegrityViolationException) {
			ex.printStackTrace();
		}
		String rootMsg = Utils.getRootCause(ex).getMessage();
		System.out.println(rootMsg); //Duplicate entry 'abc@example.com' for key 'unique_users_email_idx'
		String message = "Invalid";
		String lowerCaseMsg = rootMsg.toLowerCase();
		Optional<Map.Entry<String, String>> entry = exceptionMap.entrySet().stream()
				.filter(mapItem -> lowerCaseMsg.contains(mapItem.getKey())).findAny();
		if (entry.isPresent()) {
		  message = rootMsg.replaceAll(entry.get().getKey(), entry.get().getValue()); 
		}
		
		RestApiErrorVo errorDetails = new RestApiErrorVo(HttpStatus.CONFLICT.name(), message,
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
	}

//    @ResponseStatus(value = HttpStatus.CONFLICT)  // 409
//    @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class,
//			DataIntegrityViolationException.class })
//    public ResponseEntity<RestApiErrorVo> conflict(HttpServletRequest req, DataIntegrityViolationException e) {
//        String rootMsg = Utils.getRootCause(e).getMessage();
//        if (rootMsg != null) {
//            String lowerCaseMsg = rootMsg.toLowerCase();
//            Optional<Map.Entry<String, String>> entry = exceptionMap.entrySet().stream()
//                    .filter(it -> lowerCaseMsg.contains(it.getKey()))
//                    .findAny();
//            if (entry.isPresent()) {
//                return logAndGetErrorInfo(req, e, false, HttpStatus.CONFLICT, messageUtil.getMessage(entry.get().getValue()));
//            }
//        }
//        return logAndGetErrorInfo(req, e, true, HttpStatus.CONFLICT);
//    }

//    private ResponseEntity<RestApiErrorVo> logAndGetErrorInfo(HttpServletRequest request, Exception e, boolean logException, HttpStatus status, String... details) {
//        Throwable rootCause = Utils.logAndGetRootCause(log, request, e, logException, status);
//        RestApiErrorVo errorDetails = new RestApiErrorVo(HttpStatus.CONFLICT.name(), e.getMessage(),
//				request.getDescription(false));
//        return new ResponseEntity<>("Conflict", HttpStatus.CONFLICT);
//        return new ResponseEntity<RestApiErrorVo>(req.getRequestURL(), status, 
//                messageUtil.getMessage(status.name()),
//                details.length != 0 ? details : new String[]{Utils.getMessage(rootCause)});
//    }

//	@ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class,
//			DataIntegrityViolationException.class })
//	protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
//		String bodyOfResponse = "This should be application specific";
//		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
//	}

//	@ResponseBody
//	@ExceptionHandler(EntityNotFoundException.class)
//	@ResponseStatus(HttpStatus.NOT_FOUND)
//	String employeeNotFoundHandler(EntityNotFoundException ex) {
//		return ex.getMessage();
//	}

	@ExceptionHandler(EntityNotFoundException.class)
	public final ResponseEntity<RestApiErrorVo> handleEntityNotFoundException(EntityNotFoundException ex,
			WebRequest request) {
		RestApiErrorVo errorDetails = new RestApiErrorVo(HttpStatus.NOT_FOUND.name(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<RestApiErrorVo> handleAllExceptions(Exception ex, WebRequest request) {
		RestApiErrorVo errorDetails = new RestApiErrorVo(ex.getMessage(), request.getDescription(false), null);
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

//	 @ResponseStatus(value = HttpStatus.CONFLICT)  // 409
//	  @ExceptionHandler(DataIntegrityViolationException.class)
//	  @ResponseBody
//	  public RestApiErrorVo conflict(HttpServletRequest req, DataIntegrityViolationException e) {
//	    String rootMsg = e.getMessage();
//	    if (rootMsg != null) {
//	        Optional<Map.Entry<String, String>> entry = constraintCodeMap.entrySet().stream()
//	                .filter((it) -> rootMsg.contains(it.getKey()))
//	                .findAny();
//	        if (entry.isPresent()) {
//	            e=new DataIntegrityViolationException(
//	                  messageSource.getMessage(entry.get().getValue(), null, LocaleContextHolder.getLocale());
//	        }
//	    }
//	    return new RestApiErrorVo(req, e);
//	  }
}
