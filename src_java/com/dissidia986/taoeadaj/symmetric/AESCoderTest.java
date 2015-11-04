package com.dissidia986.taoeadaj.symmetric;


import org.apache.commons.codec.binary.Hex;

public class AESCoderTest {
	static final String SECRET_KEY = "301e906e8e3e5533defd1cd8cf6d9906";
	public static void main(String[] args) throws Exception {
		String inputStr = "AES";
		byte[] inputData = inputStr.getBytes();
		System.err.println("原文:\t"+inputStr);
		//初始化密钥
		//byte[] key = AESCoder.initKey();
		byte[] key = Hex.decodeHex(SECRET_KEY.toCharArray());
		//System.err.println("密钥:\t"+Base64.encodeBase64String(key));
		System.err.println("密钥:\t"+Hex.encodeHexString(key));
		//加密
		inputData = AESCoder.encrypt(inputData, key);
		System.err.println("加密后:\t"+Hex.encodeHexString(inputData));
		//解密
		byte[] outputData = AESCoder.decrypt(inputData, key);
		String outputStr = new String(outputData);
		System.err.println("解密后:\t"+outputStr);
		//校验
		System.out.println(inputStr.equals(outputStr));
	}

}
