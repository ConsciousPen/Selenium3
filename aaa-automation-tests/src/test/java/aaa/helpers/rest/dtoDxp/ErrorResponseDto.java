package aaa.helpers.rest.dtoDxp;

import java.util.List;

public class ErrorResponseDto {
	public String errorCode;
	public String message;
	public String field;
	public List<ErrorResponseDto> errors;
}

