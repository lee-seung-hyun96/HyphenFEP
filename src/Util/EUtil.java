package Util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class EUtil
{
	public static byte[] udecode_3des(byte[] b_key, byte[] ebytes) throws NoSuchAlgorithmException, InvalidKeyException,
			IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, IOException
	{
		SecretKeySpec skeySpec = new SecretKeySpec(b_key, "DESede");
		Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");

		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] b_emsg = Base64.url64_decode(ebytes);
		byte[] b_dmsg = cipher.doFinal(b_emsg);

		return b_dmsg;
	}

	public static byte[] uencode_3des(byte[] b_key, byte[] pbytes) throws NoSuchAlgorithmException, InvalidKeyException,
			IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, IOException
	{
		SecretKeySpec skeySpec = new SecretKeySpec(b_key, "DESede");
		Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");

		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] b_emsg = cipher.doFinal(pbytes);
		byte[] b_bmsg = Base64.url64_encode(b_emsg);

		return b_bmsg;
	}
}

