package com.dissidia986.util;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.util.encoders.Base64;

public class CertificateCodePfxTest {
	private String password = "wdwyfy";
	private String alias = "test-alias";
	private String certificatePath = "D:\\workspace\\appApi\\src\\itrus001.cer";
	private String keyStorePath = "D:\\workspace\\appApi\\src\\itrus001.pfx";
	
	public void test1()throws Exception{
		System.err.println("融宝响应-----解密过程");
		byte[] response_encryptkey = "JnqBAajTbjCoqpwtFGBGlzyURfxVRJ3vUeG187HW2mfrOmldx+d/le7IvSlSf3XuHGcFlwy7ERK76J1Yj0uzpNlMI0tjL0RpLlLbUgTNNc2Jq7hF4PCC9xk8ZGKchjiv6Clk9XAqymwr68lIsXwxkCnv7qRAb8fBmjpP3z/tG/jHsg8SbMWwcOo0XFcLuU+4quOPTONrpEn4LP1p5BxVRIKzJD8zWf8ciMtsfucFd+kCjiQHyrk/uQvzQt+bT+TQs68FnEaZE5iOpAftbpyYvHWh0blg2DUZvJxpYEfAB3brSQxYJyqbRcnQFfuY0deg9GV9gXEhZxdNbGbdJUw8mA==".getBytes();
		byte[] resposne_encryptData = "WuQ+DdEl2z2GFd0FOG4QADH/7P9MXWGKXcHmm861ProP/pKK026dQ90RK+S/oLgcTtQqL4E2XNWXsOsF1cKNG7fJ7v0xByWZ6KUnNd972fQ3JToCLOjlgq8dZo2wf5zncYFM55yg0U63UkDcdoiUzrWcYhw1FjiOT96thf5z+i/7gTy7taCz5dt+XCOykfmc".getBytes();
		byte[] decrypt_AES_secretkey = CertificateCoderPfx.decryptByPrivateKey(response_encryptkey, keyStorePath, alias, password);
		//byte[] decrypt_AES_secretkey = CertificateCoderPfx.decryptByPrivateKey(response_encryptkey, keyStorePath, alias, password);
		String AES_secretkey = new String(decrypt_AES_secretkey);
		System.err.println("ASE秘密密钥:\n"+AES_secretkey);
		byte[] outputData = AESCoder.decrypt(Base64.decode(resposne_encryptData), AES_secretkey.getBytes("UTF-8"));
		System.out.println(new String(outputData,"UTF-8"));
	}
	
	public void test2()throws Exception{
		System.err.println("融宝响应-----加密过程");
		String inputStr ="{\"bind_id\":\"123456\",\"body\":\"pay\",\"currency\":\"156\",\"member_id\":\"123456\",\"member_ip\":\"127.0.0.1\",\"merchant_id\":\"123456\",\"order_no\":\"10151111172401887268\",\"seller_email\":\"\",\"sign\":\"914d048b22a583709ee78a38e7d9f1bd\",\"terminal_info\":\"123456\",\"terminal_type\":\"mobile\",\"title\":\"pay\",\"total_fee\":\"100\",\"transtime\":\"2015-11-11 17:24:01\"}";
		System.out.println(inputStr);
		String AES_secretkey = "2Nb313o76r1v0xo8";
		byte[] aes_key_encrypted = CertificateCoderPfx.encryptByPublicKey(AES_secretkey.getBytes(), certificatePath);
		String aes_key_encrypted_str = new String(Base64.encode(aes_key_encrypted),"UTF-8");
		System.err.println("被公钥加密后的ASE秘密密钥:\n"+aes_key_encrypted_str);
		String aes_encrypted_data = new String(Base64.encode(AESCoder.encrypt(inputStr.getBytes("UTF-8"), AES_secretkey.getBytes("UTF-8"))));
		System.err.println("ASE加密后:\n"+aes_encrypted_data);
	}
	public void testSign()throws Exception{
		String inputStr="签名";
		byte[] data = inputStr.getBytes();
		System.err.println("私钥签名-----公钥验证");
		byte[] sign = CertificateCoderPfx.sign(data, keyStorePath, alias, password);
		System.err.println("签名:\n"+Hex.encodeHexString(sign));
		boolean status = CertificateCoderPfx.verify(data, sign, certificatePath);
		System.out.println("状态:\n"+status);
	}
	public static void main(String[] args)throws Exception{
		CertificateCodePfxTest test = new CertificateCodePfxTest();
		test.test1();
		//test.test2();
		//test.testSign();
	}
}
