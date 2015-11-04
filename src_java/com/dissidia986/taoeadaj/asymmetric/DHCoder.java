package com.dissidia986.taoeadaj.asymmetric;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/**
 * DH安全编码组件
 * @author 梁栋
 */
public abstract class DHCoder {
	//非对称加密密钥算法
	public static final String KEY_ALGORITHM="DH";
	/**
	 * 本地密钥算法，即对称加密密钥算法，
	 * 可选DES，DESede和AES算法
	 */
	public static final String SECRET_ALGORITHM="AES";
	/**
	 * 密钥长度
	 * DH算法默认密钥长度为1024
	 * 密钥长度必须是64的倍数，其范围在512到1024位之间
	 */
	private static final int KEY_SIZE = 512;
	//公钥
	private static final String PUBLIC_KEY="DHPublicKey";
	//私钥
	private static final String PRIVATE_KEY="DHPrivateKey";
	/**
	 * 初始化甲方密钥
	 * @return Map 甲方密钥Map
	 * @throws Exception
	 */
	public static Map<String,Object> initKey()throws Exception{
		// 实例化密钥对生成器
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGenerator.initialize(KEY_SIZE);
		// 生成密钥对
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		//甲方公钥
		DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();
		//甲方私钥
		DHPrivateKey privateKey = (DHPrivateKey) keyPair.getPrivate();
		//将密钥对存储在Map中
		Map<String,Object> keyMap = new HashMap<String,Object>();
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}
	/**
	 * 初始化乙方密钥
	 * @param key 甲方公钥
	 * @return Map 乙方密钥Map
	 * @throws Exception
	 */
	public static Map<String,Object> initKey(byte[] key)throws Exception{
		//解析甲方公钥，转换公钥材料
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
		//实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		//产生公钥
		PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
		//由甲方公钥构建乙方密钥
		DHParameterSpec dhParamSpec = ( (DHPublicKey) pubKey).getParams();
		//实例化密钥对生成器
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyFactory.getAlgorithm());
		//初始化密钥对生成器
		keyPairGenerator.initialize(dhParamSpec);
		//产生密钥对
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		//乙方公钥
		DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();
		//乙方私钥
		DHPrivateKey privateKey = (DHPrivateKey) keyPair.getPrivate();
		//将密钥对存储在Map中
		Map<String,Object> keyMap = new HashMap<String,Object>();
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}
	/**
	 * 加密
	 * @param data 待加密数据
	 * @param key 密钥
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data,byte[] key)throws Exception{
		//生成本地密钥
		SecretKey secretKey = new SecretKeySpec(key,SECRET_ALGORITHM);
		Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
		//数据加密
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(data);
	}
	/**
	 * 解密<br/>
	 * @param data 待解密数据
	 * @param key 密钥
	 * @return byte[] 解密数据
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data,byte[] key)throws Exception{
		//生成本地密钥
		SecretKey secretKey = new SecretKeySpec(key,SECRET_ALGORITHM);
		Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
		//数据解密
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return cipher.doFinal(data);
	}
	/**
	 * 构建密钥
	 * @param publicKey
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] getSecretKey(byte[] publicKey,byte[] privateKey)throws Exception{
		//实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		//初始化公钥
		//密钥材料转换
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
		//产生公钥
		PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
		//初始化私钥
		//密钥材料转换
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
		//产生私钥
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
		//实例化
		KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory.getAlgorithm());
		keyAgree.init(priKey);
		keyAgree.doPhase(pubKey, true);
		//产生本地密钥
		SecretKey secretKey = keyAgree.generateSecret(SECRET_ALGORITHM);
		return secretKey.getEncoded();
	}
	/**
	 * 取得私钥
	 * @param keyMap 密钥Map
	 * @return byte[] 私钥
	 */
	public static byte[] getPrivateKey(Map<String,Object> keyMap){
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		return key.getEncoded();
	}
	/**
	 * 取得公钥
	 * @param keyMap 密钥Map
	 * @return byte[] 公钥
	 */
	public static byte[] getPublicKey(Map<String,Object> keyMap){
		Key key = (Key) keyMap.get(PUBLIC_KEY);
		return key.getEncoded();
	}
}
