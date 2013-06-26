package com.workshare.micro.api.tokens.model;

import java.util.Date;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotBlank;

import com.wordnik.swagger.annotations.ApiClass;
import com.wordnik.swagger.annotations.ApiProperty;

@ApiClass
@XmlRootElement(name = "Token")
public class Token {

    @ApiProperty(required = true, value = "The unique id of this token")
    @NotNull
    private final String id;

    @ApiProperty(required = true, value = "The (application) type id of this token")
    @NotNull
    @NotBlank(message = "typeEmpty")
    @Size(max = 20, message = "typeOverflow")
    private final String type;

    @ApiProperty(required = true, value = "The (application) content of this token")
    @NotNull
    @NotBlank(message = "contentEmpty")
    @Size(max = 2000, message = "contentOverflow")
    private final String content;

    @ApiProperty(required = true, value = "The expiry date of this token")
    @NotNull
    @Future
    private final Date expiryDate;

    @ApiProperty(required = true, value = "The creation date of this token")
    private final Date createDate;

    @ApiProperty(required = true, value = "The id of the user who created date of this token")
    @NotNull
    @Size(max = 100, message = "createUserOverflow")
    private final String createUser;

    public Token(String id, String type, String content, Date expiryDate, Date createDate, String createUser) {
        super();
        this.id = id;
        this.type = type;
        this.content = content;
        this.expiryDate = expiryDate;
        this.createDate = createDate;
        this.createUser = createUser;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder("Token [id=").append(id).append(", type=").append(type).append(", content=").append(content).append(", expiryDate=")
                .append(expiryDate).append(", createDate=").append(createDate).append(", createUser=").append(createUser).append("]");
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Token) {
            return getId().equals(((Token) o).getId());
        } else {
            return false;
        }
    }
}