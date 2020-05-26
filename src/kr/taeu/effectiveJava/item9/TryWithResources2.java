package kr.taeu.effectiveJava.item9;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * 짧고 매혹적인 자원을 회수하는 최선책 try-with-resources
 */
public class TryWithResources2 {
	private final static int BUFFER_SIZE = 1024;
	
	static String firstLineOfFile(String path, String defaultVal) throws IOException {
		try (BufferedReader br = new BufferedReader(
				new FileReader(path))) {
			return br.readLine();
		} catch (IOException e) {
			return defaultVal;
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