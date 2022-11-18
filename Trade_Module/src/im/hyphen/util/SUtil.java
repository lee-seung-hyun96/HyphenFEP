package im.hyphen.util;

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

	static String toHanD(byte[] bsrc, int idx, int len)
	{
		try
		{
			String str = new String(bsrc, "ksc5601");
	
			return format(str, len, 'X');
		}catch(java.io.UnsupportedEncodingException ue){}
		return null;
	}
	
	//가상계좌
	public static String toHanX(byte[] bsrc, int idx, int len)
	{
		String str = toHan(bsrc, idx, len);
		if (str == null) return null;

		return str.trim();
	}

	public static String toHan(byte[] bsrc, int idx, int len)
	{
		try
		{
			return new String(bsrc, idx, len ,"ksc5601");
		}catch(java.io.UnsupportedEncodingException ue){}

		return null;
	}
	
	
	public static String toHanE(byte[] bsrc)
	{
		try
		{
			String buf = new String(bsrc, "ksc5601");
			byte[] buf_enc = buf.getBytes("ksc5601");
			
			return new String(bsrc, "ksc5601");
			
		}catch(java.io.UnsupportedEncodingException ue){}

		return null;
	}

	public static String toHanE(byte[] bsrc, String encoding_type)
	{
		try
		{
			String buf = new String(bsrc, "ksc5601");
			byte[] buf_enc = buf.getBytes(encoding_type);

			return new String(bsrc, encoding_type);

		}catch(java.io.UnsupportedEncodingException ue){}

		return null;
	}

	public static String toHanE(byte[] bsrc, int idx, int len, String encoding_type)
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
	


	public static byte[] ConvB2B(byte[] bsrc, int idx, int len, String src_encoding, String tgt_encoding)
	{
		try
		{
			String buf = new String(bsrc, idx, len, src_encoding);
			byte[] buf_enc = buf.getBytes(tgt_encoding);

			return buf_enc;
		}catch(java.io.UnsupportedEncodingException ue){}
		return null;
	}

	public static String ConvB2S(byte[] bsrc, int idx, int len, String src_encoding)
	{
		try
		{
			String buf = new String(bsrc, idx, len, src_encoding);
			return format(buf, len, 'X');

		}catch(java.io.UnsupportedEncodingException ue){}

		return null;
	}

	public static byte[] ConvS2B(String src, int tgt_len, String tgt_encoding)
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

	public static String GetSvcCode(byte[] msg)
	{
		if(msg.length == 2000) return "2000";   //KEB
		else if (SUtil.toHan(msg, 0, 9).equals("KSNETVR  ")) return "4000"; //VR
		else if (SUtil.toHan(msg, 9, 3).equals("FCS") && SUtil.toHan(msg, 19, 7).equals("0600400") ) return "5000"; // FCS
		else if (SUtil.toHan(msg, 0, 9).equals("KSFC     ")|| SUtil.toHan(msg, 17, 4).equals("    ")) return "3000"; //SDS EBOND
		else if (SUtil.toHan(msg, 0, 9).equals("HYPHEN  ")||SUtil.toHan(msg, 0, 9).equals("KSDEBIT  ")) return "6000"; //KSBPAY, KSDEBIT
		else    return "1000";  //WON
	}
	public static String GetSvcName(byte[] msg)
	{
		if(msg.length == 2000) return "KEB";   //KEB
		else if (SUtil.toHan(msg, 0, 9).equals("KSNETVR  ")) return "VAC"; //VR
		else if (SUtil.toHan(msg, 9, 3).equals("FCS") && SUtil.toHan(msg, 19, 7).equals("0600400") ) return "FCS"; // FCS
		else if (SUtil.toHan(msg, 0, 9).equals("KSFC     ")|| SUtil.toHan(msg, 17, 4).equals("    ")) return "EBD"; //SDS EBOND
    else if (SUtil.toHan(msg, 0, 9).equals("KSBPAY   ")||SUtil.toHan(msg, 0, 9).equals("KSDEBIT  ")) return "PAY"; //KSBPAY,KSDEBIT
		else    return "WON";  //WON
	}

	//KSBankMsg
	public static String fmt(String str, int len, char ctype)
	{
		return format(str,len,ctype);
	}

	public static String fmt(int no, int len, char ctype)
	{
		return format(String.valueOf(no),len,ctype);
	}
	
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

	public static char[] b2cs(byte[] ebytes)
	{
		char[] echars = new char[ebytes.length];
		for(int i=0; i<echars.length; i++) echars[i] = (char)ebytes[i];

		return echars;
	}

	public static byte[] c2bs(char[] echars)
	{
		byte[] ebytes = new byte[echars.length];
		for(int i=0; i<echars.length; i++) ebytes[i] = (byte)echars[i];

		return ebytes;
	}
	//ksdebit_gate
	
    static char HALF_CHARS[]    = null;
    static char FULL_CHARS[]    = null;
    static
    {
        char S_HALF_CHAR = '!'  ,E_HALF_CHAR = '~';
        char S_FULL_CHAR = '！' ,E_FULL_CHAR = '～';

        HALF_CHARS = new char[E_HALF_CHAR - S_HALF_CHAR + 1];
        FULL_CHARS = new char[HALF_CHARS.length];

        for(int i=0; i<HALF_CHARS.length; i++)
        {
            HALF_CHARS[i] = (char)(S_HALF_CHAR + i);
            FULL_CHARS[i] = (char)(S_FULL_CHAR + i);
        }
    }
    // MS워드같은 프로그램이 자동으로 바꿔버리는 도형문자 및 유사문자
	static char		SYMB_CHARS[]	= {'　' ,'“'  ,'”'  ,'’' ,'‘' ,'㈜'   ,'…'	};
	static String	HALF_CSTRS[]	= {" "  ,"\"" ,"\"" ,"'" ,"`" ,"(주)" ,"..."};

    // 전각문자를 반각문자로 변경한다.
    public static String full2half(String str)
    {
        if (str == null) return "";

        char[] carr = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        OUTER: for(int i=0; i<carr.length; i++)
        {
            INNER1: for(int j=0; j<HALF_CHARS.length; j++)
            {
                if (carr[i] == FULL_CHARS[j])
                {
                    sb.append(HALF_CHARS[j]); continue OUTER;
                }
            }
            INNER2: for(int j=0; j<SYMB_CHARS.length; j++)
            {
                if (carr[i] == SYMB_CHARS[j])
                {
                    sb.append(HALF_CSTRS[j]); continue OUTER;
                }
            }
            sb.append(carr[i]);
        }

        return sb.toString();
    }
    
	public static String hex_encode(byte[] sbuf)
	{
		if (null == sbuf) return null;
		
		return hex_encode(sbuf,0,sbuf.length);
	}

	public static String hex_encode(byte[] sbuf, int sidx ,int len)
	{
		if (null == sbuf) return null;
		
		int tidx = sidx + len;
		if (tidx > sbuf.length) tidx = sbuf.length;
			
		StringBuffer sb = new StringBuffer();
		for(int i=sidx; i<tidx; i++)
		{
			sb.append(Integer.toHexString((0xFF & sbuf[i]) | 0x0100).substring(1));
		}
		
		return sb.toString();
	}

	public static byte[] hex_decode(String sStr)
	{
	 	int slen = 0;
	 	if (null == sStr || 0 != ((slen = sStr.length()) & 0x01)) return null;

	 	byte[] oData = new byte[slen / 2];

	 	for (int i=0,j=0; i < slen - 1; i += 2, j++)
	 	{
	 		oData[j] = (byte) (0xff & Integer.parseInt(sStr.substring(i, i + 2), 16));
	 	}

	 	return oData;
	}	
	
}
