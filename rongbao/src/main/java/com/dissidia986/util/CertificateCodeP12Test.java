package com.dissidia986.util;

import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.util.encoders.Base64;

public class CertificateCodeP12Test {
	private String password = "clientok";
	private String alias = "clientok";
	//private String certificatePath = "D:\\morshare\\rongbao-public.cer";
	private String certificatePath = "D:\\100000000052211.cer";
	//private String keyStorePath = "D:\\morshare\\rongbao-private.p12";
	private String keyStorePath = "D:\\100000000052211.p12";
	
	public void test1()throws Exception{
		
		
		
	}
	
	public void test2()throws Exception{
		System.err.println("私钥加密-----公钥解密");
		String inputStr ="数字证书";
		byte[] data = inputStr.getBytes();
		byte[] encrypt = CertificateCoderP12.encryptByPrivateKey(data, keyStorePath, alias, password);
		byte[] decrypt = CertificateCoderP12.decryptByPublicKey(encrypt, certificatePath);
		String outputStr = new String(decrypt);
		System.err.println("加密前:\n"+inputStr);
		System.err.println("加密后:\n"+outputStr);
		
		System.err.println(Arrays.equals(data, decrypt));
	}
	public void testSign()throws Exception{
		String inputStr="签名";
		byte[] data = inputStr.getBytes();
		System.err.println("私钥签名-----公钥验证");
		byte[] sign = CertificateCoderP12.sign(data, keyStorePath, alias, password);
		System.err.println("签名:\n"+Hex.encodeHexString(sign));
		boolean status = CertificateCoderP12.verify(data, sign, certificatePath);
		System.out.println("状态:\n"+status);
	}
	public static void main(String[] args)throws Exception{
		CertificateCodeP12Test test = new CertificateCodeP12Test();
		test.test1();
		test.test2();
		test.testSign();
	}
}
