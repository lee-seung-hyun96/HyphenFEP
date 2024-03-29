package im.hyphen.crypto;

import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;

import im.hyphen.util.*;

public class HyphenEncSocket
{
	final static int MSG_LEN = 300;
	final static int MAX_MSG_LEN = 5000;
	final static int MSG_LEN_KEB = 2000;

	public static byte[] GenerateKey()
	{
		return SUtil.s2b(new StringBuffer().append(System.currentTimeMillis()).append(Long.MAX_VALUE).substring(0,16));
	}

	public static byte[] cms_encrypt(byte[] p_kbuf, byte[] p_dbuf)
	{
		try
		{
			byte[] e_kbuf	= rsa_encrypt(p_kbuf);

			byte[] e_dbuf	= seed_encrypt(p_kbuf, p_dbuf);
			byte[] e_sbuf	= new byte[4+1+e_kbuf.length+4+e_dbuf.length];

			int midx = 0;
			System.arraycopy("01292".getBytes(), 0	,e_sbuf	,midx,  5); midx+= 5;
			System.arraycopy(e_kbuf, 0 , e_sbuf,midx, e_kbuf.length); midx+=e_kbuf.length	;

			System.arraycopy(("0000".substring(String.valueOf(e_dbuf.length).length(),4) + e_dbuf.length).getBytes()	,0	,e_sbuf	,midx,  4			); midx+= 4				;
			System.arraycopy(e_dbuf, 0, e_sbuf,midx, e_dbuf.length); midx+=e_dbuf.length	;

			return e_sbuf;
			
		}catch(Exception e)
		{
			System.out.println("ERROR: EncrpyMsg ("+p_dbuf==null?"null":SUtil.b2s(p_dbuf) +")");
			e.printStackTrace();
		}

		return null;
	}
	public static byte[] cms_decrypt(byte[] p_kbuf, byte[] e_rbuf)
	{
		try
		{
			return seed_decrypt(p_kbuf,e_rbuf);
		}catch(Exception e)
		{
			System.out.println("ERROR: kscms_decrypt ");
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] cms_sndrcv_socket(String ipaddr, int port, byte[] p_dbuf, int timeout)
	{
		try
		{
			byte[] p_kbuf	= SUtil.s2b(new StringBuffer().append(System.currentTimeMillis()).append(Long.MAX_VALUE).substring(0,16));
			byte[] e_kbuf	= rsa_encrypt(p_kbuf);

			byte[] e_dbuf	= seed_encrypt(p_kbuf, p_dbuf);
			byte[] e_sbuf	= new byte[4+1+e_kbuf.length+4+e_dbuf.length];

			int midx = 0;
			System.arraycopy("01292".getBytes()																			,0	,e_sbuf	,midx,  5			); midx+= 5				;
			System.arraycopy(e_kbuf																						,0	,e_sbuf	,midx, e_kbuf.length); midx+=e_kbuf.length	;

			System.arraycopy(("0000".substring(String.valueOf(e_dbuf.length).length(),4) + e_dbuf.length).getBytes()	,0	,e_sbuf	,midx,  4			); midx+= 4				;
			System.arraycopy(e_dbuf																						,0	,e_sbuf	,midx, e_dbuf.length); midx+=e_dbuf.length	;

			byte[] e_rbuf = sndrcv_socket(ipaddr,port,e_sbuf, timeout);

			return seed_decrypt(p_kbuf,e_rbuf);
		}catch(IOException ie)
		{
			System.out.println("ERROR: kscms_sndrcv_socket ��ſ����߻� ("+ipaddr+","+port+","+p_dbuf==null?"null":SUtil.b2s(p_dbuf) +")");
			ie.printStackTrace();
			return null;
		}catch(Exception e)
		{
			System.out.println("ERROR: kscms_sndrcv_socket ��Ÿ�����߻� ("+ipaddr+","+port+","+p_dbuf==null?"null":SUtil.b2s(p_dbuf) +")");
			e.printStackTrace();
			return null;

		}

	}

	public static byte[] sndrcv_socket(String ipaddr, int port, byte[] sbuf) throws IOException
	{
		Socket			sock	= null;
		DataOutputStream	dout	= null;
		DataInputStream		din	= null;

		try
		{
			sock	= new Socket(ipaddr, port);
			dout	= new DataOutputStream(sock.getOutputStream());
			din		= new DataInputStream(sock.getInputStream());

			dout.write(sbuf	,0	, sbuf.length);
			dout.flush();

			byte[] rbuf = new byte[8192];
			int rtn_len=0, read_len=0;
			while(0<(rtn_len=din.read(rbuf,read_len,rbuf.length-read_len)) && read_len<rbuf.length)
			{
				read_len+=rtn_len;
			}
			
			if (read_len == 0) return null; // socket closed
			

			/* System.out.println("INFO: sndrcv_socket ("+read_len+")byte ���� length_len=("+SUtil.b2s(rbuf,0,4) +")"); */
			
			byte[] tbuf = new byte[read_len-4];
			System.arraycopy(rbuf,4,tbuf,0,read_len-4);
			
			return tbuf;
		}finally
		{
			try{if (sock != null) sock.close(); sock = null;}catch(Exception e){}
		}
	}
	public static byte[] sndrcv_socket(String ipaddr, int port, byte[] sbuf, int timeout) throws IOException, SocketTimeoutException
	{
		Socket			sock	= null;
		DataOutputStream	dout	= null;
		DataInputStream		din	= null;

		try
		{
			sock	= new Socket(ipaddr, port);
			sock.setSoTimeout(timeout*1000);	/* read timeout */

			dout	= new DataOutputStream(sock.getOutputStream());
			din		= new DataInputStream(sock.getInputStream());


			dout.write(sbuf	,0	, sbuf.length);
			dout.flush();

			byte[] rbuf = new byte[8192];
			int rtn_len=0, read_len=0;
			while(0<=(rtn_len=din.read(rbuf,read_len,rbuf.length-read_len)) && read_len<rbuf.length)
			{
				read_len+=rtn_len;
			}

			/* System.out.println("INFO: sndrcv_socket ("+read_len+")byte ���� length_len=("+SUtil.b2s(rbuf,0,4) +")"); */
			
			byte[] tbuf = new byte[read_len-4];
			System.arraycopy(rbuf,4,tbuf,0,read_len-4);
			
			return tbuf;
		}finally
		{
			try{if (sock != null) sock.close(); sock = null;}catch(Exception e){}
		}
	}

/*
	public static Socket connect_socket(String ipaddr, int port) throws IOException
	{
		Socket			sock	= null;

		try
		{
			sock	= new Socket(ipaddr, port);
			return sock;
		}catch(IOException ie)
		{
			System.out.println("ERROR: connect_socket("+ipaddr+","+port+")");
			ie.printStackTrace();
		}catch(Exception e)
		{
			System.out.println("ERROR: connect_socket("+ipaddr+","+port+")");
			e.printStackTrace();
		}
		return null;
		
		
	}
*/
	public static Socket connect_socket(String ipaddr, int port) throws Exception
	{
		Socket			sock	= null;

		try
		{	
			sock	= new Socket(ipaddr, port);
			return sock;
		}
		finally
		{
		}
	}

	public static boolean snd_socket(Socket sock, byte[] sbuf) throws IOException
	{
		DataOutputStream	dout	= null;

		try
		{
			dout	= new DataOutputStream(sock.getOutputStream());

			dout.write(sbuf	,0	, sbuf.length);
			dout.flush();
			return true;

		}catch(Exception e)
		{
			System.out.println("ERROR: snd_socket() ");
			e.printStackTrace();
			return false;
		}
	}

	public static byte[] rcv_socket(Socket sock) throws IOException
	{
		DataInputStream		din	= null;

			din		= new DataInputStream(sock.getInputStream());

			byte[] rbuf = new byte[8192];
			int rtn_len=0, read_len=0;
			while(0 < (rtn_len=din.read(rbuf,read_len,rbuf.length-read_len)) && read_len<rbuf.length)
			{
				read_len+=rtn_len;
			}

			/* System.out.println("INFO: rcv_socket ("+read_len+")byte ���� length_len=("+SUtil.b2s(rbuf,0,4) +")"); */
			if (read_len == 0) return null; // socket closed
			
			byte[] tbuf = new byte[read_len-4];
			System.arraycopy(rbuf,4,tbuf,0,read_len-4);
			
			return tbuf;
	}
	public static byte[] rcv_socket(Socket sock, int timeout) throws IOException,  SocketTimeoutException
	{
		DataInputStream		din	= null;


			sock.setSoTimeout(timeout*1000);	/* read timeout */
			din		= new DataInputStream(sock.getInputStream());

			byte[] rbuf = new byte[8192];
			int rtn_len=0, read_len=0;
				LUtil.println("SND", "DEBUG: start rtn_len("+rtn_len+")");
			while(0 < (rtn_len=din.read(rbuf,read_len,rbuf.length-read_len)) && read_len<rbuf.length)
			{
				LUtil.println("SND", "DEBUG: rtn_len("+rtn_len+")");
				read_len+=rtn_len;
			}
				LUtil.println("SND", "DEBUG: rtn_len1("+rtn_len+")");
			
			if (read_len == 0) 
			{
				LUtil.println("SND", "ERROR: rcv_socket closed ("+read_len+")bytes recv");
				return null; // socket closed
			}
			
			/* System.out.println("INFO: rcv_socket ("+read_len+")byte ���� length_len=("+SUtil.b2s(rbuf,0,4) +")"); */
			
			byte[] tbuf = new byte[read_len-4];
			System.arraycopy(rbuf,4,tbuf,0,read_len-4);
			
			return tbuf;

	}

	public static byte[] rcv_socket_len(Socket sock, int timeout) throws IOException,  SocketTimeoutException
	{
		DataInputStream		din	= null;


			sock.setSoTimeout(timeout*1000);	/* read timeout */
			din		= new DataInputStream(sock.getInputStream());

			byte[] rlen = new byte[4];
			byte[] rbuf = new byte[8192];
			int rtn_len=0, read_len=0, rest_len = 0;
			
			if (0 >= (rtn_len=din.read(rlen,0,4)))
			{
				LUtil.println("SND", "ERROR: rcv_socket closed ("+rtn_len+")bytes recv");
				return null; // socket closed
			}

			rest_len = Integer.parseInt(new String(rlen));

			while(true)
			{
				if (0 >= (rtn_len = din.read(rbuf, read_len, rest_len)))
				{
					throw new IOException("Read error2 - rtn_len:" + rtn_len + " read_len:"+read_len + " rest_len:" + rest_len);
				}
				read_len = read_len + rtn_len;
				rest_len = rest_len - rtn_len;

				if (rest_len == 0) break;
			}

			/* System.out.println("INFO: rcv_socket ("+read_len+")byte ���� length_len=("+SUtil.b2s(rbuf,0,4) +")"); */
			
			byte[] tbuf = new byte[read_len];
			System.arraycopy(rbuf,0,tbuf,0,read_len);
			
			return tbuf;

	}
	/* RFB msg receive */
	public static byte[] rcv_socket_rfb(Socket sock, int timeout) throws IOException,  SocketTimeoutException
	{
		DataInputStream		in	= null;

		int     read_len = 0, rtn_len = 0, rest_len =0;

		sock.setSoTimeout(timeout*1000);	/* read timeout */
		in		= new DataInputStream(sock.getInputStream());



		byte[] tmp_buf = new byte[MAX_MSG_LEN];

		if (0 >= (rtn_len = in.read(tmp_buf, read_len, MAX_MSG_LEN)))
		{
			throw new IOException("Read error1 - " + rtn_len + "read Byte!!");
		}

		read_len = rtn_len;

		if ( (rtn_len > MSG_LEN && rtn_len < MSG_LEN_KEB) || rtn_len < MSG_LEN)
		{
			if (rtn_len < MSG_LEN) rest_len = MSG_LEN-rtn_len;  //WON 300byte
			else rest_len = MSG_LEN_KEB-rtn_len;//KEB 2000byte

			while(true)
			{
				if (0 >= (rtn_len = in.read(tmp_buf, read_len, rest_len)))
				{
					throw new IOException("Read error2 - rtn_len:" + rtn_len + " read_len:"+read_len + " rest_len:" + rest_len);
				}
				read_len = read_len + rtn_len;
				rest_len = rest_len - rtn_len;

				if (rest_len == 0) break;
			}
		}

		byte[]  rbuf = new byte[read_len];
		System.arraycopy(tmp_buf, 0, rbuf, 0, read_len);
			
		return rbuf;
	}

	public static byte[] seed_decrypt(byte[] kbuf, byte[] mbuf) throws java.security.NoSuchProviderException,javax.crypto.NoSuchPaddingException,java.security.InvalidAlgorithmParameterException,javax.crypto.BadPaddingException,java.security.NoSuchAlgorithmException,java.security.InvalidKeyException,javax.crypto.IllegalBlockSizeException
	{
		byte tdata[]	= new  HyphenBankSeed(kbuf).cbc_decrypt(mbuf) ;
		return tdata;
	}

	public static byte[] seed_encrypt(byte[] kbuf, byte[] mbuf) throws java.security.NoSuchProviderException,javax.crypto.BadPaddingException,javax.crypto.NoSuchPaddingException,java.security.InvalidAlgorithmParameterException,java.security.NoSuchAlgorithmException,java.security.InvalidKeyException,javax.crypto.IllegalBlockSizeException
	{
		byte tdata[]	= new  HyphenBankSeed(kbuf).cbc_encrypt(mbuf) ;
		return tdata;
	}

	public static byte[] rsa_encrypt(byte[] sbuf) throws java.security.NoSuchProviderException,javax.crypto.NoSuchPaddingException,javax.crypto.BadPaddingException,java.security.NoSuchAlgorithmException,java.security.spec.InvalidKeySpecException,java.security.InvalidKeyException,javax.crypto.IllegalBlockSizeException
	{
		BigInteger modulus			= new BigInteger("d4846c2b8228dddfab9e614da2a324c1cc7b29d848cc005624d3a09667a2aab9073290bace6aa536ddceb3c47ddda78d9954da06c83aa65b939c5ec773a3787e71bec5a1c077bb446c06b393d2537967645d386b4b0b4ec21372fdc728c56693028c1c3915c1c4279793eb3dccefd6bf49b86cc7d88a47b0d44aba9e73750fcd",16);
		BigInteger publicExponent	= new BigInteger("0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010001",16);

		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, publicExponent);

		// JDK 1.4 ���Ͽ����� BouncyCastleProvider�� ���(�Ʒ��ּ�)
		//KeyFactory keyfactory = KeyFactory.getInstance("RSA", CIPHER_PROVIDER);
		KeyFactory keyfactory = KeyFactory.getInstance("RSA");
		java.security.PublicKey publickey = keyfactory.generatePublic(pubKeySpec);

		// JDK 1.4 ���Ͽ����� BouncyCastleProvider�� ���(�Ʒ��ּ�)
		//Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding",CIPHER_PROVIDER);
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

		cipher.init(Cipher.ENCRYPT_MODE, publickey);//, fixedsecurerandom);

		byte[] rbuf = cipher.doFinal(sbuf);

		return rbuf;
	}
}
