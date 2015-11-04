package com.dissidia986.taoeadaj.symmetric;


import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.util.encoders.Hex;

public class DESCoderTest {
	public static void main(String[] args) throws Exception {
		String inputStr = "DES";
		byte[] inputData = inputStr.getBytes();
		System.err.println("原文:\t"+inputStr);
		//初始化密钥
		byte[] key = DESCoder.initKey();
		//System.err.println("密钥:\t"+Base64.encodeBase64String(key));
		System.err.println("密钥:\t"+new String(Hex.encode(key)));
		//加密
		inputData = DESCoder.encrypt(inputData, key);
		System.err.println("加密后:\t"+Base64.encodeBase64String(inputData));
		//解密
		byte[] outputData = DESCoder.decrypt(inputData, key);
		String outputStr = new String(outputData);
		System.err.println("解密后:\t"+outputStr);
		//校验
		System.out.println(inputStr.equals(outputStr));
	}

}
