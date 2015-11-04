package com.dissidia986.taoeadaj.symmetric;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
/**
 * DES 安全编码组件
 * @author 梁栋
 *
 */
public abstract class DESCoder {
	/**
	 * 密钥算法 <br>
	 * Java 7 支持56位密钥<br>
	 * Bouncy Castle 支持64位密钥
	 */
	public static final String KEY_ALGORITHM = "DES";
	/**
	 * 加密/解密算法  /工作模式  / 填充方式
	 */
	public static String CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";
	/**
	 * 转换密钥
	 * @param key 二进制密钥
	 * @return  Key 密钥
	 * @throws Exception
	 */
	private static Key toKey(byte[] key)throws Exception{
		// 实例化DES密钥材料
		DESedeKeySpec dks = new DESedeKeySpec(key);
		// 实例化秘密密钥工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
		//生成秘密密钥
		return keyFactory.generateSecret(dks);
	}
	/**
	 * 解密
	 * @param data 待解密数据
	 * @param key 密钥
	 * @return byte[] 解密数据
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data,byte[] key)throws Exception{
		// 还原密钥
		Key k = toKey(key);
		// 实例化
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// 初始化、设置为解密模式
		cipher.init(Cipher.DECRYPT_MODE, k);
		//解密
		return cipher.doFinal(data);
	}
	/**
	 * 加密
	 * @param data 待加密数据
	 * @param key 密钥
	 * @return byte[] 加密数据
	 * @throws Exception
	 */	
	public static byte[] encrypt(byte[] data,byte[] key)throws Exception{
		//还原密钥
		Key k = toKey(key);
		// 实例化
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// 初始化、设置为加密模式
		cipher.init(Cipher.ENCRYPT_MODE, k);
		//加密
		return cipher.doFinal(data);
	}
	/**
	 * 生成密钥 <br>
	 * Java 7 支持56位密钥<br>
	 * Bouncy Castle 支持64位密钥<br>
	 * @return byte[] 二进制数组
	 * @throws Exception
	 */
	public static byte[] initKey()throws Exception{
		/*
		 * 实例化密钥生成器
		 * 若要使用64位密钥 ,注意替换
		 * 将下述代码中的
		 * KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
		 * 替换为
		 * KeyGenerator kg = KeyGenerator.getInstance(CIPHER_ALGORITHM,"BC");
		 * 
		 */
		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
		/*
		 * 实例化密钥生成器
		 * 若要使用64位密钥 ,注意替换
		 * 将下述代码中的
		 * kg.init(56);
		 * 替换为
		 * kg.init(64);
		 * 
		 */
		kg.init(56);
		//生成秘密密钥
		SecretKey secretKey = kg.generateKey();
		//获得密钥的二进制编码形式
		return secretKey.getEncoded();
	}
}
