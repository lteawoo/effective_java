package kr.taeu.effectiveJava.item9;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * try-with-resources에서도 catch 절을 함께 쓸 수 있다.
 * try-with-resources 구조를 사용하려면 자원이 AutoCloseable 인터페이스를 구현해야한다.
 */
public class TryWithResources1 {
	private final static int BUFFER_SIZE = 1024;
	
	static String firstLineOfFile(String path) throws IOException {
		try (BufferedReader br = new BufferedReader(
				new FileReader(path))) {
			return br.readLine();
		}
	}

	static void copy(String src, String dst) throws IOException {
		try (InputStream in = new FileInputStream(src);
				OutputStream out = new FileOutputStream(dst)) {
			byte[] buf = new byte[BUFFER_SIZE];
			int n;
			while ((n = in.read(buf)) >= 0) {
				out.write(buf, 0, n);
			}
		}
	}
}