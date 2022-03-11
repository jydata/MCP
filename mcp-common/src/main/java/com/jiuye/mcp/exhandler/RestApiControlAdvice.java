package com.jiuye.mcp.exhandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.jiuye.mcp.exception.*;
import com.jiuye.mcp.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;

/**
 * 全局rest异常处理
 * 
 * @author ningyu
 * @date 2017年2月15日 上午11:13:52
 */
@ControllerAdvice
public class RestApiControlAdvice extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestApiControlAdvice.class.getName());

    @ExceptionHandler(value = { 
            ForbiddenException.class,
            GoneException.class,
            InvalidRequestException.class,
            NotAcceptableException.class,
            NotFoundException.class,
            UnauthorizedException.class,
            UnprocesableEntityException.class })
    public final ResponseEntity<Object> handle(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Response<String> body = new Response<String>();
        body.setCode(((BizException) ex).getCode());
        body.setMessage(((BizException) ex).getMessage());
        if (ex instanceof ForbiddenException) {
            HttpStatus status = HttpStatus.FORBIDDEN;
            return handleExceptionInternal(ex, body, headers, status, request);
        } else if (ex instanceof InvalidRequestException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleExceptionInternal(ex, body, headers, status, request);
        } else if (ex instanceof NotAcceptableException) {
            HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
            return handleExceptionInternal(ex, body, headers, status, request);
        } else if (ex instanceof NotFoundException) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            return handleExceptionInternal(ex, body, headers, status, request);
        } else if (ex instanceof UnauthorizedException) {
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            return handleExceptionInternal(ex, body, headers, status, request);
        } else if (ex instanceof UnprocesableEntityException) {
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            return handleExceptionInternal(ex, body, headers, status, request);
        } else if (ex instanceof GoneException) {
            HttpStatus status = HttpStatus.GONE;
            return handleExceptionInternal(ex, body, headers, status, request);
        } else {
            logger.warn("handle(), Unknown exception type: {}", ex);
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            body.setCode("INTERNAL_SERVER_ERROR");
            body.setMessage("Server Error ！");
            body.setItems(getStackTraceToString(ex));
            return handleExceptionInternal(ex, body, headers, status, request);
        }
    }

    @ExceptionHandler(value = { 
            ConstraintViolationException.class,
            MaxUploadSizeExceededException.class, 
            MultipartException.class,
            SQLException.class,
            Exception.class })
    public final ResponseEntity<Object> otherExceptionHandle(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        if(ex instanceof MaxUploadSizeExceededException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            Response<String> body = new Response<String>();
            body.setCode("UPLOAD_SIZE_LIMIT");
            body.setMessage("Upload file size does not exceed："+((MaxUploadSizeExceededException) ex).getMaxUploadSize()+"/B");
            logger.warn("UPLOAD_SIZE_LIMIT: {}", ex);
            return handleExceptionInternal(ex, body, headers, status, request);
        } else if(ex instanceof MultipartException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            Response<String> body = new Response<String>();
            body.setCode("MULTIPART_RESOLUTION_ERROR");
            body.setMessage("The multipart request could not be parsed. Check whether the server configuration matches the request ！");
            logger.warn("MULTIPART_RESOLUTION_ERROR: {}", ex);
            return handleExceptionInternal(ex, body, headers, status, request);
        } else if(SQLException.class.isAssignableFrom(ex.getClass())) {
            //统一sqlexception处理
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            Response<String> body = new Response<String>();
            body.setCode("SQL_ERROR");
            body.setMessage("Server Error ！");
            body.setItems(getStackTraceToString(ex));
            logger.warn("SQL_ERROR: {}", ex);
            return handleExceptionInternal(ex, body, headers, status, request);
        } else if(ex instanceof ConstraintViolationException) {
            Response<String> body = new Response<String>();
            body.setCode("INVALID_ARGUMENT");
            body.setMessage("Server Error ！");
            logger.warn("INVALID_ARGUMENT: {}", ex);
            return new ResponseEntity<Object>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else {
            logger.warn("otherExceptionHandle(), Unknown exception type: {}", ex);
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            Response<String> body = new Response<String>();
            body.setCode("INTERNAL_SERVER_ERROR");
            body.setMessage("Server Error ！");
            body.setItems(getStackTraceToString(ex));
            return handleExceptionInternal(ex, body, headers, status, request);
        }
    }
    
    /**
     * 获取异常堆栈信息输出为字符串
     * 
     * @author ningyu
     * @date 2017年9月22日 上午11:00:00
     *
     * @param t Throwable
     * @return 异常堆栈字符串
     */
    protected String getStackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        Response<String> body = new Response<String>();
        body.setCode("INVALID_ARGUMENT");
        body.setMessage(formatErrorMessage(ex.getValue(), ex.getRequiredType()));
        status = HttpStatus.BAD_REQUEST;
        logger.warn("INVALID_ARGUMENT: {}", ex);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        Response<String> body = new Response<String>();
        body.setCode("METHOD_NOT_ALLOWED");
        body.setMessage("Method is not allowed ！");
        status = HttpStatus.METHOD_NOT_ALLOWED;
        logger.warn("METHOD_NOT_ALLOWED: {}", ex);
        return handleExceptionInternal(ex, body, headers, status, request);
    }
    
    private String formatErrorMessage(Object value, Class<?> type) {
        return "type mismatch：" + value + "Unable to convert to" + type.getSimpleName()  + "type";
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        Response<String> body = new Response<String>();
        String defaultMessage = "Read the request body error, please check the request body ！";
        if (ex.getCause() != null && ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException innerE = (InvalidFormatException) ex.getCause();
            defaultMessage = formatErrorMessage(innerE.getValue(), innerE.getTargetType());
        }
        body.setCode("MESSAGE_NOT_READABLE");
        body.setMessage(defaultMessage);
        status = HttpStatus.BAD_REQUEST;
        logger.warn("MESSAGE_NOT_READABLE: {}", ex);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoSuchRequestHandlingMethod(NoSuchRequestHandlingMethodException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        Response<String> body = new Response<String>();
        body.setCode("NOT_FOUND");
        body.setMessage("invalid request：" + ex.getMethodName());
        status = HttpStatus.NOT_FOUND;
        logger.warn("NOT_FOUND: {}", ex);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        Response<String> body = new Response<String>();
        body.setCode("UNSUPPORTED_MEDIA_TYPE");
        body.setMessage("Content-Type does not find the corresponding MessageConverter when reading the request body ！");
        status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        logger.warn("UNSUPPORTED_MEDIA_TYPE: {}", ex);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        Response<String> body = new Response<String>();
        body.setCode("NOT_ACCEPTABLE");
        body.setMessage("Unhandled content types, Accept generally does not find the corresponding MessageConverter when writing to the response body ！");
        status = HttpStatus.NOT_ACCEPTABLE;
        logger.warn("NOT_ACCEPTABLE: {}", ex);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        Response<String> body = new Response<String>();
        body.setCode("MESSING_ARGUMENT");
        body.setMessage("Missing parameters：" + ex.getParameterName());
        status = HttpStatus.BAD_REQUEST;
        logger.warn("MESSING_ARGUMENT: {}", ex);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        Response<String> body = new Response<String>();
        body.setCode("BIND_ERROR");
        body.setMessage("Parameter binding error："+ex.getMessage());
        status = HttpStatus.BAD_REQUEST;
        logger.warn("BIND_ERROR: {}", ex);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        Response<String> body = new Response<String>();
        body.setCode("CONVERSION_NOT_SUPPORTED");
        body.setMessage("Unsupported Conversion：" + ex.getPropertyName() + "=" + ex.getValue());
        status = HttpStatus.INTERNAL_SERVER_ERROR;
        logger.warn("CONVERSION_NOT_SUPPORTED: {}", ex);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        Response<String> body = new Response<String>();
        body.setCode("MESSAGE_NOT_WRITABLE");
        body.setMessage("Write response volume error, please check the background service ！");
        status = HttpStatus.INTERNAL_SERVER_ERROR;
        logger.warn("MESSAGE_NOT_WRITABLE: {}", ex);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        Response<String> body = new Response<String>();
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> errors = bindingResult.getFieldErrors();
        StringBuilder sb = new StringBuilder();
        for (FieldError err : errors) {
            sb.append(err.getField()).append(":").append(err.getDefaultMessage()).append(";");
        }
        body.setCode("INVALID_ARGUMENT");
        if (sb.length() > 0) {
            body.setMessage(sb.toString());
        } else {
            body.setMessage("invalid parameter：" + ex.getParameter().getParameterName());
        }
        status = HttpStatus.BAD_REQUEST;
        logger.warn("INVALID_ARGUMENT: {}", ex);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        Response<String> body = new Response<String>();
        body.setCode("MESSING_PART");
        body.setMessage("The current request is a Multipart request and the party name cannot be found：" + ex.getRequestPartName());
        status = HttpStatus.BAD_REQUEST;
        logger.warn("MESSING_PART: {}", ex);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        //TODO 错误再去细分处理
        Response<String> body = new Response<String>();
        body.setCode("BIND_ERROR");
        body.setMessage("Parameter binding error：" + ex.getObjectName());
        status = HttpStatus.BAD_REQUEST;
        logger.warn("BIND_ERROR: {}", ex);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        Response<String> body = new Response<String>();
        body.setCode("NOT_FOUND");
        body.setMessage("invalid request：" + ex.getRequestURL());
        status = HttpStatus.NOT_FOUND;
        logger.warn("NOT_FOUND: {}", ex);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

}
