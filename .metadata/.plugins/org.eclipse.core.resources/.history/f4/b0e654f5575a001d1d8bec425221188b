package Util;

import java.util.Calendar;
import java.util.LinkedList;

public class SUtil
{
	static String getCurrDate() 
	{
		Calendar cal = Calendar.getInstance();
		StringBuffer sb = new StringBuffer();
		int li_yyyy,li_MM,li_dd,li_hour,li_min,li_sec;

		li_yyyy = cal.get(Calendar.YEAR); li_MM = cal.get(Calendar.MONTH); li_dd = cal.get(Calendar.DATE);
		li_hour = cal.get(Calendar.HOUR_OF_DAY); li_min = cal.get(Calendar.MINUTE); li_sec = cal.get(Calendar.SECOND);

		sb.append(li_yyyy).append(li_MM<9 ? "0" : "").append(li_MM + 1).append(li_dd<10 ? "0" : "").append(li_dd);
		sb.append(li_hour<10 ? "0" : "").append(li_hour).append(li_min<10 ? "0" : "").append(li_min).append(li_sec<10 ? "0" : "").append(li_sec);

		return sb.toString();
	}

	//추후 수정 예정
//	static String toHanX(byte[] bsrc, int idx, int len)
//	{
//		String str = toHan(bsrc, idx, len);
//		if (str == null) return null;
//
//		return str;
//	}
//
//	static String toHan(byte[] bsrc, int idx, int len)
//	{
//		try
//		{
//			String str = new String(bsrc, "ksc5601");
//			return format(str, len, 'X');
//		}catch(java.io.UnsupportedEncodingException ue){}
//
//		return null;
//	}
	
	static String toHanE(byte[] bsrc, String encoding_type)
	{
		try
		{
			String buf = new String(bsrc, "ksc5601");
			byte[] buf_enc = buf.getBytes(encoding_type);
			
			return new String(bsrc, encoding_type);
			
		}catch(java.io.UnsupportedEncodingException ue){}

		return null;
	}
	

	static String toHanE(byte[] bsrc, int idx, int len, String encoding_type)
	{
		try
		{
			String buf = new String(bsrc, idx, len, "ksc5601");
			byte[] buf_enc = buf.getBytes(encoding_type);
			
			return new String(bsrc, encoding_type);
			
		}catch(java.io.UnsupportedEncodingException ue){}

		return null;
	}

	public static String[] split(String srcStr, char c1)
	{
		return split(srcStr, String.valueOf(c1));
	}

	public static String[] split(String srcStr, String str1)
	{
		if (srcStr == null) return new String[0];

		String[] tokenArr = null;
		if (srcStr.indexOf(str1) == -1)
		{
			tokenArr = new String[1];
			tokenArr[0] = srcStr;

			return tokenArr;
		}

		LinkedList<String> linkedlist = new LinkedList<String>();

		int srcLength    = srcStr.length();
		int tockenLength = str1.length();

		int pos = 0, startPos = 0;
		while(startPos < srcLength)
		{
			pos = srcStr.indexOf(str1, startPos);

			if (-1 == pos) break;

			linkedlist.add(srcStr.substring(startPos, pos));
			startPos = pos + tockenLength;
		}

		if (startPos <= srcLength) linkedlist.add(srcStr.substring(startPos));

		return (String[])linkedlist.toArray(new String[0]);
	}//split

	
	static String format(String str, int len, char ctype)
	{
		byte[] buff;
		int filllen = 0;

		String          trim_str = null;
		StringBuffer    sb = new StringBuffer();

		buff = (str == null) ? new byte[0] : str.getBytes();

		filllen = len - buff.length;
		if (filllen < 0)
		{
			for(int i=0, j=0; j<len-4; i++)
			{
				j += (str.charAt(i) > 127) ? 2 : 1;
				sb.append(str.charAt(i));
			}
			trim_str = sb.toString();
			buff = trim_str.getBytes();
			filllen = len - buff.length;

			if (filllen <= 0) return new String(buff, 0, len);
			sb.setLength(0);
		}else
		{
			trim_str = str;
		}

		if(ctype == '9')	/* number */
		{
			for(int i = 0; i<filllen;i++) sb.append('0');
			sb.append(trim_str);
		}else		/* string */
		{
			for(int i = 0; i<filllen;i++) sb.append(' ');
			sb.insert(0, trim_str);
		}
		return sb.toString();
	}
	


	static byte[] ConvB2B(byte[] bsrc, int idx, int len, String src_encoding, String tgt_encoding)
	{
		try
		{
			String buf = new String(bsrc, idx, len, src_encoding);
			byte[] buf_enc = buf.getBytes(tgt_encoding);

			return buf_enc;
		}catch(java.io.UnsupportedEncodingException ue){}
		return null;
	}

	static String ConvB2S(byte[] bsrc, int idx, int len, String src_encoding)
	{
		try
		{
			String buf = new String(bsrc, idx, len, src_encoding);
			return format(buf, len, 'X');

		}catch(java.io.UnsupportedEncodingException ue){}

		return null;
	}

	static byte[] ConvS2B(String src, int tgt_len, String tgt_encoding)
	{
		try
		{
			byte[] nb = new byte[tgt_len];
			
			for (int i=0; i < tgt_len; i++)
			{
				nb[i]= 0x20;
			}

			byte[] buf = src.getBytes(tgt_encoding);

			int len = tgt_len;
			if (buf.length < tgt_len) len = buf.length; 	// over array size
			
			System.arraycopy(buf, 0, nb, 0, len);

			return nb;
		}catch(java.io.UnsupportedEncodingException ue){}

		return null;
	}

	//인터넷망
	static String toHanX(byte[] bsrc, int idx, int len)
	{
		String str = toHan(bsrc, idx, len);
		if (str == null) return null;

		return str.trim();
	}

	static String toHan(byte[] bsrc, int idx, int len)
	{
		try
		{
			return new String(bsrc, idx, len ,"ksc5601");
		}catch(java.io.UnsupportedEncodingException ue){}

		return null;
	}

	public static String GetSvcCode(byte[] msg)
	{
		return "6000";  //WON
	}
	public static String GetSvcName(byte[] msg)
	{
	    return "PAY";  //WON
	}

}
