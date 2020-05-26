package kr.taeu.effectiveJava.item9;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * 자원이 둘 이상이면 try-finally 방식은 너무 지저분하다.
 */
public class TryFinally1 {
	private final static int BUFFER_SIZE = 1024;
	
	/*
	 * 한 개일때는 비교적 봐줄만함.
	 */
	static String firstLineOfFile(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		try {
			return br.readLine();
		} finally {
			br.close();
		}
	}
	/*
	 * 자원이 두개
	 */
	static void copy(String src, String dst) throws IOException {
		InputStream in = new FileInputStream(src);
		try {
			OutputStream out = new FileOutputStream(dst);
			try {
				byte[] buf = new byte[BUFFER_SIZE];
				int n;
				while ((n = in.read(buf)) >= 0) {
					out.write(buf, 0, n);
				}
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}
	}
}
