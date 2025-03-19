package com.swd392.preOrderBlindBox.infrastructure.interceptor;

import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.exception.OTPException;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.common.exception.SignUpException;
import com.swd392.preOrderBlindBox.common.exception.UserException;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.ExceptionResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(UserException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<BaseResponse<ExceptionResponse>> handleUserException(
      UserException exception) {
    return buildErrorResponse(
        exception.getErrorCode(), exception.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(OTPException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<BaseResponse<ExceptionResponse>> handleOTPException(
      OTPException exception) {
    return buildErrorResponse(
        exception.getErrorCode(), exception.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(SignUpException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<BaseResponse<ExceptionResponse>> handleSignUpException(
      SignUpException exception) {
    return buildErrorResponse(
        exception.getErrorCode(), exception.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<BaseResponse<List<ExceptionResponse>>> handleValidationException(
      MethodArgumentNotValidException ex) {
    List<ExceptionResponse> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new ExceptionResponse(error.getField(), error.getDefaultMessage()))
            .collect(Collectors.toList());
    return ResponseEntity.badRequest().body(BaseResponse.build(errors, false));
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<BaseResponse<ExceptionResponse>> handleBadCredentialsException() {
    return buildErrorResponse(ErrorCode.BAD_CREDENTIAL_LOGIN, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<BaseResponse<ExceptionResponse>> handleResourceNotFoundException() {
    return buildErrorResponse(ErrorCode.RESOURCES_NOT_FOUND, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(SecurityException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<BaseResponse<ExceptionResponse>> handleSecurityException() {
    return buildErrorResponse(ErrorCode.UNAUTHORIZED_CART_ACCESS, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<BaseResponse<ExceptionResponse>> handleIllegalArgumentException(
      IllegalArgumentException ex) {
    return buildErrorResponse("VALIDATION_ERROR", ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<BaseResponse<ExceptionResponse>> handleIllegalStateException(
          IllegalArgumentException ex) {
    return buildErrorResponse("INVALID STATE", ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IOException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<BaseResponse<ExceptionResponse>> handleIOException(
          IOException ex) {
    return buildErrorResponse("Error with handling files: ", ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<BaseResponse<?>> handleGenericException(Exception ex) {
    var errorResponse = BaseResponse.build("Error: " + ex.getMessage(), false);
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<BaseResponse<ExceptionResponse>> buildErrorResponse(
      ErrorCode errorCode, HttpStatus status) {
    return buildErrorResponse(errorCode.getCode(), errorCode.getMessage(), status);
  }

  private ResponseEntity<BaseResponse<ExceptionResponse>> buildErrorResponse(
      String code, String message, HttpStatus status) {
    return ResponseEntity.status(status)
        .body(BaseResponse.build(new ExceptionResponse(code, message), false));
  }
}
