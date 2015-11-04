package com.dissidia986.taoeadaj.dsa;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;

/**
 * RSA签名算法组件
 * 
 * @author 梁栋
 *
 */
public abstract class RSACoder {
	// 非对称加密密钥算法
	public static final String KEY_ALGORITHM = "RSA";
	/**
	 * 数字签名
	 * 签名/验证算法
	 */
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
	/**
	 * 密钥长度 RSA算法默认密钥长度为1024位, 密钥长度必须是64的倍数，其范围在512到65536位之间
	 */
	private static final int KEY_SIZE = 512;
	// 公钥
	private static final String PUBLIC_KEY = "RSAPublicKey";
	// 私钥
	private static final String PRIVATE_KEY = "RSAPrivateKey";
	
	public static byte[] sign(byte[] data,byte[] privateKey)throws Exception{
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(priKey);
		signature.update(data);
		return signature.sign();
	}
	
	public static boolean verify(byte[] data,byte[] publicKey,byte[] sign)throws Exception{
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey pubKey = keyFactory.generatePublic(keySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(pubKey);
		signature.update(data);
		return signature.verify(sign);
	}
	
	public static byte[] getPrivateKey(Map<String,Object> keyMap)throws Exception{
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		return key.getEncoded();
	}
	
	public static byte[] getPublicKey(Map<String,Object> keyMap)throws Exception{
		Key key = (Key) keyMap.get(PUBLIC_KEY);
		return key.getEncoded();
	}
	
	public static Map<String,Object> initKey()throws Exception{
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(KEY_SIZE);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		Map<String,Object> keyMap = new HashMap<String,Object>();
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}
	/*
	public static String sign(byte[] data,String privateKey)throws Exception{
		byte[] sign = sign(data,getKey(privateKey));
		return Hex.encodeHexString(sign);
	}
	public static boolean verify(byte[] data,String publicKey,String sign)throws Exception{
		return verify(data,getKey(publicKey),Hex.decodeHex(sign.toCharArray()));
	}
	public static boolean encryptByPrivateKey(byte[] data,String key)throws Exception{
		return encryptByPrivateKey(data,getKey(key));
	}
	private static byte[] getKey(String key)throws Exception {
		return Hex.decodeHex(key.toCharArray());
	}
	*/
}
