package com.workshare.micro.utils;

import java.util.UUID;

public class UUIDGenerator {
    public String generateString() {
	return UUID.randomUUID().toString();
    }
}
