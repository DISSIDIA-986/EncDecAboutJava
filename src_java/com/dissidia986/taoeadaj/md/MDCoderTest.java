package com.dissidia986.taoeadaj.md;

import java.util.Arrays;

public class MDCoderTest {

	public static void main(String[] args) {

	}

	public final void testEncodeMD2() throws Exception {
		String str = "MD2消息摘要";
		byte[] data1 = MDCoder.encodeMD2(str.getBytes());
		byte[] data2 = MDCoder.encodeMD2(str.getBytes());
		System.out.println(Arrays.equals(data1, data2));
	}

	public final void testEncodeMD5() throws Exception {
		String str = "MD2消息摘要";
		byte[] data1 = MDCoder.encodeMD5(str.getBytes());
		byte[] data2 = MDCoder.encodeMD5(str.getBytes());
		System.out.println(Arrays.equals(data1, data2));
	}
}
