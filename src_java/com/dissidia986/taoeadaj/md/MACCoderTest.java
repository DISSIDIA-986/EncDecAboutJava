package com.dissidia986.taoeadaj.md;
import java.util.Arrays;

public class MACCoderTest {

	public static void main(String[] args) throws Exception {

		testEncodeHmacMD5();
		testEncodeHmacSHA256();
		testEncodeHmacSHA384();
		testEncodeHmacSHA512();

	}

	public final static void testEncodeHmacMD5() throws Exception {
		String str = "HmacMD5消息摘要";
		byte[] key = MACCoder.initHmacMD5Key();
		byte[] data1 = MACCoder.encodeHmacMD5(str.getBytes(), key);
		byte[] data2 = MACCoder.encodeHmacMD5(str.getBytes(), key);
		System.err.println(Arrays.equals(data1, data2));
	}

	public final static void testEncodeHmacSHA256() throws Exception {
		String str = "HmacSHA256消息摘要";
		byte[] key = MACCoder.initHmacSHA256Key();
		byte[] data1 = MACCoder.encodeHmacSHA256(str.getBytes(), key);
		byte[] data2 = MACCoder.encodeHmacSHA256(str.getBytes(), key);
		System.err.println(Arrays.equals(data1, data2));
	}

	public final static void testEncodeHmacSHA384() throws Exception {
		String str = "HmacSHA384消息摘要";
		byte[] key = MACCoder.initHmacSHA384Key();
		byte[] data1 = MACCoder.encodeHmacSHA384(str.getBytes(), key);
		byte[] data2 = MACCoder.encodeHmacSHA384(str.getBytes(), key);
		System.err.println(Arrays.equals(data1, data2));
	}

	public final static void testEncodeHmacSHA512() throws Exception {
		String str = "HmacSHA512消息摘要";
		byte[] key = MACCoder.initHmacSHA512Key();
		byte[] data1 = MACCoder.encodeHmacSHA512(str.getBytes(), key);
		byte[] data2 = MACCoder.encodeHmacSHA512(str.getBytes(), key);
		System.err.println(Arrays.equals(data1, data2));
	}
}
