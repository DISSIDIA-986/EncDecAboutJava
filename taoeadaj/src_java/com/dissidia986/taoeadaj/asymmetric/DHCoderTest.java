package com.dissidia986.taoeadaj.asymmetric;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

public class DHCoderTest {
	private byte[] publicKey1;
	private byte[] privateKey1;
	private byte[] key1;
	private byte[] publicKey2;
	private byte[] privateKey2;
	private byte[] key2;
	
	public void initKey()throws Exception{
		Map<String,Object> keyMap1 = DHCoder.initKey();
		publicKey1 = DHCoder.getPublicKey(keyMap1);
		privateKey1 = DHCoder.getPrivateKey(keyMap1);
		System.err.println("甲方公钥:\n"+Base64.encodeBase64String(publicKey1));
		System.err.println("甲方私钥:\n"+Base64.encodeBase64String(privateKey1));
		Map<String,Object> keyMap2 = DHCoder.initKey(publicKey1);
		publicKey2 = DHCoder.getPublicKey(keyMap2);
		privateKey2 = DHCoder.getPrivateKey(keyMap2);
		System.err.println("乙方公钥:\n"+Base64.encodeBase64String(publicKey2));
		System.err.println("乙方私钥:\n"+Base64.encodeBase64String(privateKey2));
		key1 = DHCoder.getSecretKey(publicKey2, privateKey1);
		System.err.println("甲方本地密钥:\n"+Base64.encodeBase64String(key1));
		key2 = DHCoder.getSecretKey(publicKey1, privateKey2);
		System.err.println("乙方本地密钥:\n"+Base64.encodeBase64String(key2));
		System.err.println(Arrays.equals(key1, key2));
	}
	
	public void test()throws Exception{
		System.err.println("\n======甲方向乙方发送机加密数据======");
		String input1 = "密码交换算法";
		System.err.println("原文:"+input1);
		System.err.println("\n======使用甲方密钥对数据加密======");
		byte[] code1 = DHCoder.encrypt(input1.getBytes(), key1);
		System.err.println("加密:"+Base64.encodeBase64String(code1));
		System.err.println("\n======使用乙方本地密钥对数据解密======");
		byte[] decode1 = DHCoder.decrypt(code1, key2);
		String output1 = (new String(decode1));
		System.err.println("解密:\n"+output1);
		System.err.println(input1.equals(output1));
		
		System.err.println("\n======乙方向甲方发送机加密数据======");
		String input2 = "DH";
		System.err.println("原文:"+input2);
		System.err.println("\n======使用乙方密钥对数据加密======");
		byte[] code2 = DHCoder.encrypt(input2.getBytes(), key2);
		System.err.println("加密:"+Base64.encodeBase64String(code2));
		System.err.println("\n======使用甲方本地密钥对数据解密======");
		byte[] decode2 = DHCoder.decrypt(code2, key1);
		String output2 = (new String(decode2));
		System.err.println("解密:\n"+output2);
		System.err.println(input2.equals(output2));
		
	}
	
	public static void main(String[] args)throws Exception{
		DHCoderTest test = new DHCoderTest();
		test.initKey();
		test.test();
	}
}
