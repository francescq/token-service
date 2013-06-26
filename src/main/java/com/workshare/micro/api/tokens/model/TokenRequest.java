package com.workshare.micro.api.tokens.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotBlank;

import com.wordnik.swagger.annotations.ApiClass;
import com.wordnik.swagger.annotations.ApiProperty;

@ApiClass
@XmlRootElement(name = "TokenRequest")
public class TokenRequest {

    @ApiProperty(required = true, value = "The application type of this token")
    @NotNull
    @NotBlank
    public String type;
    @ApiProperty(required = true, value = "The content of this token")
    @NotNull
    @NotBlank
    public String content;
    @ApiProperty(required = true, value = "The age of this token in seconds")
    @Min(value = 1, message = "minValue")
    public int maxAge;
}