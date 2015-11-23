package com.dissidia986.taoeadaj.cert;

import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;
/**
 * 证书校验
 * @author 梁栋
 *
 */
public class CertificateCodeTest {
	private String password = "123456";
	private String alias = "www.zlex.org";
	private String certificatePath = "d:/zlex.cer";
	private String keyStorePath = "d:/zlex.keystore";
	/**
	 * 公钥加密-私钥解密
	 * @throws Exception
	 */
	public void test1()throws Exception{
		System.err.println("公钥加密-----私钥解密");
		String inputStr ="数字签名";
		byte[] data = inputStr.getBytes();
		//公钥加密
		byte[] encrypt = CertificateCoder.encryptByPublicKey(data, certificatePath);
		//私钥解密
		byte[] decrypt = CertificateCoder.decryptByPrivateKey(encrypt, keyStorePath, alias, password);
		String outputStr = new String(decrypt);
		System.err.println("加密前:\n"+inputStr);
		System.err.println("加密后:\n"+outputStr);
		//验证数据一直
		System.err.println(Arrays.equals(data, decrypt));
	}
	/**
	 * 私钥加密-公钥解密
	 * @throws Exception
	 */
	public void test2()throws Exception{
		System.err.println("私钥加密-----公钥解密");
		String inputStr ="数字证书";
		byte[] data = inputStr.getBytes();
		//私钥加密
		byte[] encrypt = CertificateCoder.encryptByPrivateKey(data, keyStorePath, alias, password);
		//公钥解密
		byte[] decrypt = CertificateCoder.decryptByPublicKey(encrypt, certificatePath);
		String outputStr = new String(decrypt);
		System.err.println("加密前:\n"+inputStr);
		System.err.println("加密后:\n"+outputStr);
		
		System.err.println(Arrays.equals(data, decrypt));
	}
	/**
	 * 签名验证
	 * @throws Exception
	 */
	public void testSign()throws Exception{
		String inputStr="签名";
		byte[] data = inputStr.getBytes();
		System.err.println("私钥签名-----公钥验证");
		//产生签名
		byte[] sign = CertificateCoder.sign(data, keyStorePath, alias, password);
		System.err.println("签名:\n"+Hex.encodeHexString(sign));
		//验证签名
		boolean status = CertificateCoder.verify(data, sign, certificatePath);
		//校验
		System.out.println("状态:\n"+status);
	}
	public static void main(String[] args)throws Exception{
		CertificateCodeTest test = new CertificateCodeTest();
		test.test1();
		test.test2();
		test.testSign();
	}
}
