package Crypto;
import java.net.*;
import java.io.*;
import java.util.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.security.interfaces.RSAPublicKey;
import javax.crypto.*;
import javax.crypto.spec.*;


public class KSBankMsg
{
	public static byte[] s2b(String str)
	{
		byte[] buf = null;
		try
		{
			buf = str.getBytes("ksc5601");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return buf;
	}

	public static String b2s(byte[] buf)
	{
		return (null == buf) ? null : b2s(buf,0,buf.length);
	}

	public static String b2s(byte[] buf, int bidx, int blen)
	{
		String str = null;
		try
		{
			str = new String(buf,bidx,blen,"ksc5601");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return str;
	}
	    

	

}
