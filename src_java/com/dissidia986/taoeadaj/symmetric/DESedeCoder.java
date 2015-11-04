package com.dissidia986.taoeadaj.symmetric;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public abstract class DESedeCoder {
	public static final String KEY_ALGORITHM = "DESede";
	public static String CIPHER_ALGORITHM = "DESede/ECB/PKCS5Padding";
	private static Key toKey(byte[] key)throws Exception{
		DESedeKeySpec dks = new DESedeKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
		return keyFactory.generateSecret(dks);
	}
	
	public static byte[] decrypt(byte[] data,byte[] key)throws Exception{
		Key k = toKey(key);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, k);
		return cipher.doFinal(data);
	}
	
	public static byte[] encrypt(byte[] data,byte[] key)throws Exception{
		Key k = toKey(key);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, k);
		return cipher.doFinal(data);
	}
	
	public static byte[] initKey()throws Exception{
		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
		kg.init(168);
		SecretKey secretKey = kg.generateKey();
		return secretKey.getEncoded();
	}
}
