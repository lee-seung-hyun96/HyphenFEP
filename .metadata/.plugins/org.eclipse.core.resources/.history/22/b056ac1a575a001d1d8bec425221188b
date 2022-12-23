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
	    
	public static String fmt(String str, int len, char ctype)
	{
		return format(str,len,ctype);
	}

	public static String fmt(int no, int len, char ctype)
	{
		return format(String.valueOf(no),len,ctype);
	}
	
	public static String format(String str, int len, char ctype)
	{
        byte[] buff;
        int filllen = 0;

        String			trim_str = null;
        StringBuffer	sb = new StringBuffer();
        
        buff = (str == null) ? new byte[0] : s2b(str);
        
        filllen = len - buff.length;
        if (filllen < 0)
        {
			for(int i=0, j=0; j<len-4; i++)//적당히 여유를 두고 잘라버리자
			{
				j += (str.charAt(i) > 127) ? 2 : 1;
				sb.append(str.charAt(i));
			}

			trim_str = sb.toString();
			buff = s2b(trim_str);
			filllen = len - buff.length;
			
			if (filllen <= 0) return new String(buff, 0, len);//여기는 절대로 안타겠지...
			sb.setLength(0);
        }else
        {
        	trim_str = str;
        }
        
        if(ctype == '9')	// 숫자열인 경우
        {
            for(int i = 0; i<filllen;i++) sb.append('0');            
            sb.append(trim_str);
        }else				// 문자열인 경우
        {
            for(int i = 0; i<filllen;i++) sb.append(' ');
            sb.insert(0, trim_str);
        }
        return sb.toString();
    }
}
