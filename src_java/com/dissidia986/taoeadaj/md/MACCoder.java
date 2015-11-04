package com.dissidia986.taoeadaj.md;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public abstract class MACCoder {
	public static byte[] initHmacMD5Key()throws Exception{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacMD5");
		SecretKey secretKey = keyGenerator.generateKey();
		return secretKey.getEncoded();		
	}
	
	public static byte[] encodeHmacMD5(byte[] data,byte[] key)throws Exception{
		SecretKey secretKey = new SecretKeySpec(key,"HmacMD5");
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		return mac.doFinal(data);
	}
	
	public static byte[] initHmacSHAKey()throws Exception{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA1");
		SecretKey secretKey = keyGenerator.generateKey();
		return secretKey.getEncoded();		
	}
	
	public static byte[] encodeHmacSHA(byte[] data,byte[] key)throws Exception{
		SecretKey secretKey = new SecretKeySpec(key,"HmacSHA1");
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		return mac.doFinal(data);
	}
	
	public static byte[] initHmacSHA256Key()throws Exception{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
		SecretKey secretKey = keyGenerator.generateKey();
		return secretKey.getEncoded();		
	}
	
	public static byte[] encodeHmacSHA256(byte[] data,byte[] key)throws Exception{
		SecretKey secretKey = new SecretKeySpec(key,"HmacSHA256");
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		return mac.doFinal(data);
	}
	
	public static byte[] initHmacSHA384Key()throws Exception{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA384");
		SecretKey secretKey = keyGenerator.generateKey();
		return secretKey.getEncoded();		
	}
	
	public static byte[] encodeHmacSHA384(byte[] data,byte[] key)throws Exception{
		SecretKey secretKey = new SecretKeySpec(key,"HmacSHA384");
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		return mac.doFinal(data);
	}
	
	public static byte[] initHmacSHA512Key()throws Exception{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA512");
		SecretKey secretKey = keyGenerator.generateKey();
		return secretKey.getEncoded();		
	}
	
	public static byte[] encodeHmacSHA512(byte[] data,byte[] key)throws Exception{
		SecretKey secretKey = new SecretKeySpec(key,"HmacSHA512");
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		return mac.doFinal(data);
	}
}
