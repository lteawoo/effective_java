# try-finally보다는 try-with-resources를 사용하라
전통적으로 자원이 제대로 닫힘을 보장하는 수단으로 try-finally가 쓰였다. 예외가 발생하거나 메서드에서 반환되는 경우를 포함해서.

```JAVA
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
```
자원이 1개일 때는 비교적 나쁘지 않지만, 2개 이상으로 되면 중첩문이 등장하면서 복잡해진다, firstLineOfFile에서 readLine과 close에서 예외가 발생하면 메인로직인 readLine의 예외를 close의 예외가 삼켜 문제 진단에 어려움이 생긴다.

위의 문제들은 try-with-resources가 등장하면서(java7) 해결되었다. 이 구조를 사용하려면 자원이 AutoCloseable 인터페이스를 구현해야한다.

```java
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
```
복수의 자원을 처리하는 구조도 상당히 깔끔하며 자원들이 AutoCloseable 인터페이스를 구현해놓았기 때문에 close를 호출해줄 필요도 없다, 여기서 readLine과 close 양쪽에서 예외가 발생하면 readLine에서 발생한 예외가 기록되고 close는 숨겨진다. 스택 추적내역에 suppressed 꼬리표를 달고 있다.