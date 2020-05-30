package com.asl.pms.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestDev {

	public static void main(String[] args) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("2020-01-31 00:00:00.000", formatter);

		System.out.println(dateTime);
	}

}
