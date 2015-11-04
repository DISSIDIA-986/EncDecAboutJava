package com.dissidia986.taoeadaj.cert;

import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;

/**
 * 证书组件
 * @author 梁栋
 *
 */
public abstract class CertificateCoder {
	public static final String CERT_TYPE="X.509";
	
	private static PrivateKey getPrivateKeyByKeyStore(String keyStorePath,String alias,String password)throws Exception{
		KeyStore ks = getKeyStore(keyStorePath,password);
		return (PrivateKey) ks.getKey(alias,password.toCharArray());
	}
	private static PublicKey getPublicKeyByKeyStore(String certificatePath)throws Exception{
		Certificate certificate = getCertificate(certificatePath);
		return certificate.getPublicKey();
	}
	
	private static Certificate getCertificate(String certificatePath)throws Exception {
		CertificateFactory certificateFactory = CertificateFactory.getInstance(CERT_TYPE);
		FileInputStream in = new FileInputStream(certificatePath);
		Certificate certificate = certificateFactory.generateCertificate(in);
		in.close();
		return certificate;
	}
	private static Certificate getCertificate(String keyStorePath,String alias,String password)throws Exception {
		KeyStore ks = getKeyStore(keyStorePath,password);
		return ks.getCertificate(alias);
	}
	private static KeyStore getKeyStore(String keyStorePath, String password)throws Exception {
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		FileInputStream is = new FileInputStream(keyStorePath);
		ks.load(is, password.toCharArray());
		is.close();
		return ks;
	}
	
	public static byte[] encryptByPrivateKey(byte[] data,String keyStorePath,String alias,String password)throws Exception {
		PrivateKey privateKey = getPrivateKeyByKeyStore(keyStorePath, alias, password);
		Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}
	
	public static byte[] decryptByPrivateKey(byte[] data,String keyStorePath,String alias,String password)throws Exception {
		PrivateKey privateKey = getPrivateKeyByKeyStore(keyStorePath, alias, password);
		Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}
	
	public static byte[] encryptByPublicKey(byte[] data,String certificatePath)throws Exception {
		PublicKey publicKey = getPublicKeyByKeyStore(certificatePath);
		Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(data);
	}
	
	public static byte[] decryptByPublicKey(byte[] data,String certificatePath)throws Exception {
		PublicKey publicKey = getPublicKeyByKeyStore(certificatePath);
		Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		return cipher.doFinal(data);
	}
	
	public static byte[] sign(byte[] sign,String keyStorePath,String alias,String password)throws Exception {
		X509Certificate x509Certificate = (X509Certificate) getCertificate(keyStorePath, alias, password);
		Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
		PrivateKey privateKey = getPrivateKeyByKeyStore(keyStorePath, alias, password);
		signature.initSign(privateKey);;
		signature.update(sign);
		return signature.sign();
	}
	
	public static boolean verify(byte[] data,byte[] sign,String certificatePath)throws Exception {
		X509Certificate x509Certificate = (X509Certificate) getCertificate(certificatePath);
		Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
		signature.initVerify(x509Certificate);
		signature.update(data);
		return signature.verify(sign);
	}
}
