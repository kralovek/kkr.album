package kkr.album.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import kkr.album.exception.BaseException;
import kkr.album.exception.TechnicalException;

import org.apache.log4j.Logger;

public class UtilsConsole {
	private static final Logger LOG = Logger.getLogger(UtilsConsole.class);

	public static boolean readAnswerYN(String question) throws BaseException {
		String answer = readAnswer(question);
		while (true) {
			if ("Y".equalsIgnoreCase(answer)) {
				return true;
			} else if ("N".equalsIgnoreCase(answer)) {
				return false;
			} else {
				System.out.print("I don't understand the answer (Y/N)");
			}
		}
	}

	public static String readAnswer(String question) throws BaseException {
		if (question != null) {
			System.out.println();
			System.out.println("####");
			System.out.println("####");
			System.out.println("####");
			System.out.println(question);
		}
		System.out.print("> ");

		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(
				System.in));

		try {
			String answer = bufferRead.readLine();
			return answer;
		} catch (IOException ex) {
			throw new TechnicalException(
					"Cannot read the anwer from the cosole input");
		}
	}
}
