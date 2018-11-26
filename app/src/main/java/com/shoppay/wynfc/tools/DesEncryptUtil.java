package com.shoppay.wynfc.tools;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DesEncryptUtil {

	private static final String encoding = "utf-8";

	/**
	 * 解密
	 *
	 * @param encryptText
	 * @return
	 * @throws Exception
	 */
	public static String decode(String encryptText) {
		try {
			Key deskey = null;
			DESedeKeySpec spec = new DESedeKeySpec(
					AppConstant.secretKey.getBytes());
			SecretKeyFactory keyfactory = SecretKeyFactory
					.getInstance("desede");
			deskey = keyfactory.generateSecret(spec);
			Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
			IvParameterSpec ips = new IvParameterSpec(
					AppConstant.desIv.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

			byte[] decryptData = cipher.doFinal(Base64.decode(encryptText));
			return new String(decryptData, encoding);
		} catch (Exception e) {

		}
		return null;
	}
}
