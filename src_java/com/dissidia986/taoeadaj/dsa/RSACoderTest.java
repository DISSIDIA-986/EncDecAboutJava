package com.dissidia986.taoeadaj.dsa;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class RSACoderTest {
	private byte[] publicKey;
	private byte[] privateKey;
	
	public void initKey()throws Exception{
		Map<String,Object> keyMap1 = RSACoder.initKey();
		publicKey = RSACoder.getPublicKey(keyMap1);
		privateKey = RSACoder.getPrivateKey(keyMap1);
		System.err.println("公钥:\n"+Base64.encodeBase64String(publicKey));
		System.err.println("私钥:\n"+Base64.encodeBase64String(privateKey));
		
	}
	
	public void testSign()throws Exception{
		String inputStr1 = "RSA数字签名";
		byte[] data1 = inputStr1.getBytes();
		System.err.println("原文:"+inputStr1);
		byte[] sign = RSACoder.sign(data1, privateKey);
		System.err.println("签名:\n"+Hex.encodeHexString(sign));
		//验证签名
		boolean status = RSACoder.verify(data1, publicKey, sign);
		
		System.err.println("状态:\n"+status);
		
	}
	public static void main(String[] args)throws Exception{
		RSACoderTest test = new RSACoderTest();
		test.initKey();
		test.testSign();
	}
}
