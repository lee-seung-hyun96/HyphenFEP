package im.hyphen.firm_bypass;


import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;
import java.math.BigInteger;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import im.hyphen.msgVO.HyphenTradeData;
import im.hyphen.util.*;

import java.security.spec.RSAPublicKeySpec;

public class DebitBinHandler extends Thread
{
	public static final byte C_STX = 0x02;
	public static final byte C_ETX = 0x03;
	public static final int READ_BLOCK_SIZE = 2000;
	public static final int READ_TIMEOUT = 40;

	private HyphenTradeData htd;
	DataInputStream         in         = null;
	MetaEn         hmsg;
	ByteBuffer      buff;
	Well512         ro;

	MsgEn          cmsg;
	MsgEn          smsg;

	Socket cs = null;
	Socket s_cs = null;


	public DebitBinHandler(HyphenTradeData htd) throws Exception {
		this.hmsg            = new MetaEn();
		this.ro              = new Well512();
		this.cmsg          = new MsgEn();
		this.smsg          = new MsgEn();
		this.cmsg.ctype = 'C';
		this.smsg.ctype = 'S';
		this.htd = htd;

	}

	public DataInputStream msgTodata() throws IOException {
		StringBuilder sb = new StringBuilder();
		String msgLen = String.format("%04d",this.htd.getSendMsg().length());
		sb.append(msgLen);
		sb.append(this.htd.getSendMsg());
		String send_msg = sb.toString();
		InputStream is = new ByteArrayInputStream(send_msg.getBytes());
		DataInputStream dis = new DataInputStream(is);

		return dis;
	}

	byte[] data = null;
	public void run()
	{
		//      DataInputStream         in         = null;
		try
		{
			hmsg.route_type    = "00".getBytes()    ;
			hmsg.enc_type      = "$".getBytes()     ;
			hmsg.m_key_type    = "0".getBytes()     ;
			DataInputStream bin_in = new DataInputStream(htd.getBin_in());
			DataInputStream msg_in = msgTodata();


			if ((data = checkMsgLen(msg_in, bin_in)) == null)
			{
				LUtil.println("DEBIT_BIN", "ERROR : storeMsg()");
				return;
			}
			processClientMsg(data);
			processServerMsg();

		}catch (Exception e) {
			LUtil.println("DEBIT_BIN", e.getMessage());
		}finally{
			try{

			}catch(Exception e){};
		}
	}

	static Random SO_RND = new Random();
	private byte[] make_session_key()
	{
		byte[] k16 = new byte[16];
		SO_RND.nextBytes(k16);
		k16[15] = 0;

		for(int i=0; i<15; i++)
		{
			k16[15] = (byte)(k16[15] ^ k16[i]);
		}

		return k16;
	}

	int processClientMsg(byte[] data) throws IOException, InterruptedException
	{
		BufferedInputStream fin;
		int rtn;
		int t_len;
		int read_len;
		int rest_len;

		/* ���ۼӵ� ���� �ʿ�  �ּ� 0.1�� => 0.05�ʷ� ����  */
		int sleep_time =(int) (Float.parseFloat(IUtil.get("fpeg.sleep"))*1000);
		if (sleep_time < 50) sleep_time = 50;

		/*
      LUtil.println("DEBIT_BIN", "DEBUG processClientMsg sleep_time : "+sleep_time);
		 */

		try {

			t_len = (int) data.length;
			InputStream in = new ByteArrayInputStream(data);
			fin  = new BufferedInputStream(in);
			byte buf[] = new byte[READ_BLOCK_SIZE];

			rest_len = t_len;

			while (true)
			{
				if (rest_len < READ_BLOCK_SIZE)  read_len = rest_len;
				else read_len = READ_BLOCK_SIZE;

				rtn = fin.read(buf, 0, read_len);   

				rest_len -= rtn;

				processClientData(buf ,rtn);

				if (rest_len == 0) break;

				Thread.sleep(sleep_time);

			}

			fin.close();
			return 1;

		} catch (IOException e) {
			e.printStackTrace(); 
			return -1;
		} finally{
			try {
				//if (in != null) in.close();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	int processClientData(byte[] mbuf ,int elen) throws IOException
	{
		if (null == s_cs)
		{
			processClientNewKey();
		}

		byte[] rnd_bytes   = mem_rnd_to_msg();
		byte[] enc_counter = aes_128_ecb_encrypt(hmsg.key ,rnd_bytes ,0 ,16);
		byte[] enc_data    = speed_ctr_encrypt(mbuf ,elen);

		int wlen    = write_van_msg("D1".getBytes() ,enc_counter ,enc_data);

		return wlen;
	}

	int write_van_msg(byte[] msg_type ,byte[] msg1 ,byte[] msg2) throws IOException
	{
		int    midx = 0, tlen = 0;
		byte[] sbuf = new byte[7+msg1.length+msg2.length+1];
		int    wlen = 0;

		tlen = sbuf.length - 4;

		sbuf[0] = C_STX;
		sbuf[2] = (byte)(tlen & 0xff);
		sbuf[1] = (byte)((tlen >>> 8) & 0xff);
		sbuf[3] = calculate_lrc(sbuf ,3);

		sbuf[4] = '$';

		midx = 5;
		System.arraycopy(msg_type ,0 ,sbuf ,midx , msg_type.length); midx = midx + msg_type.length;
		System.arraycopy(msg1     ,0 ,sbuf ,midx , msg1    .length); midx = midx + msg1    .length;
		System.arraycopy(msg2     ,0 ,sbuf ,midx , msg2    .length); midx = midx + msg2    .length;

		sbuf[midx] = C_ETX; midx++;

		DataOutputStream dout = new DataOutputStream(s_cs.getOutputStream());
		dout.write(sbuf,0,sbuf.length);
		dout.flush();

		return wlen;
	}

	String HYPHEN_ip = "";
	int HYPHEN_port = 0;

	
	int processClientNewKey() throws IOException
	{
		String lineType = CUtil.get("COMM_LINE_TYPE"); 
		String servType = CUtil.get("COMM_SERV_TYPE");
		String[] i_addrs = null;
		
		/*Internet*/	
		if(lineType.equals("I") || lineType.equals("i") ){
			/*DEV*/	
			if(servType.equals("D") || servType.equals("d")) {
				i_addrs = SUtil.split(IUtil.get("DEV_HYPHEN_BIN"),":");
				HYPHEN_ip = i_addrs[0];
				if(htd.getSvcType().equals("DBT")||htd.getSvcType().equals("PRD")) {
					HYPHEN_port=Integer.parseInt(i_addrs[2]);
				}else {
					HYPHEN_port=Integer.parseInt(i_addrs[1]);
				}
			/*REAL*/	
			}else {
				i_addrs = SUtil.split(IUtil.get("REAL_HYPHEN_BIN"),":");
				HYPHEN_ip = i_addrs[0];
				if(htd.getSvcType().equals("DBT")||htd.getSvcType().equals("PRD")) {
					HYPHEN_port=Integer.parseInt(i_addrs[2]);
				}else {
					HYPHEN_port=Integer.parseInt(i_addrs[1]);
				}
			}
			/*Private*/	
		}else {
			if(servType.equals("D") || servType.equals("d")) {
				if(htd.getSvcType().equals("DBT")) {
					i_addrs = SUtil.split(IUtil.get("DEV_HYPHEN_DBT"),":");
					HYPHEN_ip = i_addrs[0];
					HYPHEN_port=Integer.parseInt(i_addrs[1]);
				}else if(htd.getSvcType().equals("PRW")) {
					i_addrs = SUtil.split(IUtil.get("DEV_HYPHEN_BIN"),":");
					HYPHEN_ip = i_addrs[0];
					HYPHEN_port=Integer.parseInt(i_addrs[1]);
				}else if(htd.getSvcType().equals("PRD")) {
					i_addrs = SUtil.split(IUtil.get("DEV_HYPHEN_BIN"),":");
					HYPHEN_ip = i_addrs[0];
					HYPHEN_port=Integer.parseInt(i_addrs[2]);
				}
			}else {
				if(htd.getSvcType().equals("DBT")) {
					i_addrs = SUtil.split(IUtil.get("REAL_HYPHEN_DBT"),":");
					HYPHEN_ip = i_addrs[0];
					HYPHEN_port=Integer.parseInt(i_addrs[1]);
				}else if(htd.getSvcType().equals("PRW")) {
					i_addrs = SUtil.split(IUtil.get("REAL_HYPHEN_BIN"),":");
					HYPHEN_ip = i_addrs[0];
					HYPHEN_port=Integer.parseInt(i_addrs[1]);
				}else if(htd.getSvcType().equals("PRD")) {
					i_addrs = SUtil.split(IUtil.get("REAL_HYPHEN_BIN"),":");
					HYPHEN_ip = i_addrs[0];
					HYPHEN_port=Integer.parseInt(i_addrs[2]);
				}
			}

		}
		
		System.out.println("ip >  " + HYPHEN_ip);
		System.out.println("port >  " + HYPHEN_port);
		
		s_cs      =   new Socket();
		s_cs.setSoTimeout(READ_TIMEOUT*1000);    //read timeout             
		s_cs.connect(new InetSocketAddress(HYPHEN_ip, HYPHEN_port));

		byte[] sbuf = new byte[267];
		int    tlen = sbuf.length - 4;

		hmsg.key = make_session_key();
		byte[] kbuf = encrypt_rsa_2048(hmsg.key);

		System.arraycopy("0000$C1000".getBytes() ,0 ,sbuf ,0  ,10);
		System.arraycopy(kbuf                    ,0 ,sbuf ,10 ,kbuf.length);

		sbuf[0] = C_STX;
		sbuf[2] = (byte)(tlen & 0xff);
		sbuf[1] = (byte)((tlen >>> 8) & 0xff);
		sbuf[3] = calculate_lrc(sbuf ,3);

		sbuf[sbuf.length-1] = C_ETX;

		DataOutputStream dout = new DataOutputStream(s_cs.getOutputStream());
		dout.write(sbuf,0,sbuf.length);
		dout.flush();

		byte[] rbuf = new byte[7+34+1];
		DataInputStream din = new DataInputStream(s_cs.getInputStream());
		din.readFully(rbuf) ;

		int midx = 7;

		tlen =  2; String resp_code = new String(rbuf ,midx ,tlen ,"8859_1"); midx += tlen;
		tlen = 16; String trno      = new String(rbuf ,midx ,tlen ,"8859_1"); midx += tlen;
		tlen = 16; String enc_iv    = new String(rbuf ,midx ,tlen ,"8859_1"); midx += tlen;

		if (!resp_code.equals("00")) throw new IOException("new key not-ok-msg("+resp_code+")!!");

		storeSessionKey(enc_iv.getBytes("8859_1") ,trno.getBytes("8859_1"));

		return 1;
	}

	int processServerMsg() throws IOException
	{
		int  sidx = 0, clen = 0;
		DataInputStream in = null;


		int rtn_len = 0;

		smsg.clen = 0;
		smsg.elen = 0;

		in = new DataInputStream(s_cs.getInputStream());

		/* ����ɶ����� ���� */
		while (true)
		{
			rtn_len = in.read(smsg.mbuf, sidx, 3000);

			if (rtn_len <=0)
			{
				/*
            LUtil.println("DEBIT_BIN", "DEBUG processServerMsg:("+rtn_len+") ���� �����");
				 */
				break;
			}
			else smsg.clen += rtn_len;
		}

		int min_len = 7 + 1;/* 7 == sizeof(NEW_RSA_HEADER) */

		if (smsg.elen > min_len) min_len = smsg.elen;

		try
		{
			while(min_len <= smsg.clen)
			{
				if (0 == smsg.elen)
				{
					smsg.elen = ((smsg.mbuf[1] << 8) & 0x0000ff00) + (smsg.mbuf[2] & 0x000000ff) + 4;

					if (smsg.elen > 9999 || smsg.elen < 7)
					{
						LUtil.println("DEBIT_BIN", "ERROR:() format-error1!!");
						throw new IllegalStateException("ERROR:() format-error1!!");
					}
				}
				if (smsg.elen > smsg.clen) break;

				if (C_STX != smsg.mbuf[0] || C_ETX != smsg.mbuf[smsg.elen-1])
				{
					LUtil.println("DEBIT_BIN", "ERROR:() format-error2!!");
					throw new IllegalStateException("ERROR:() format-error2!!");
				}

				if ('D' != smsg.mbuf[5] || '1' != smsg.mbuf[6]) /* msg_type : D1 */
				{
					LUtil.println("DEBIT_BIN", "ERROR:() format-error3!!");
					throw new IllegalStateException("ERROR:() format-error3!!");
				}

				byte[] mbuf = (byte[])(smsg.mbuf).clone();

				processServerData(mbuf ,smsg.elen);

				sidx = smsg.elen;
				clen = smsg.clen - smsg.elen;
				System.arraycopy(mbuf,sidx ,smsg.mbuf,0,clen);

				min_len   = 4 + 1;
				smsg.clen = clen;
				smsg.elen = 0;
			}
		} catch (IOException e) {
			e.printStackTrace(); 
			return -1;
		} finally{
			try {
				if (in != null) in.close();
			} catch (Exception e) {
				System.out.println(e + "=> �ݱ� ����");
			}
		}

		return -1;
	}

	int processServerData(byte[] mbuf ,int elen) throws IOException
	{
		int midx = 0;
		byte[] enc_counter = new byte[16];
		byte[] enc_data    = new byte[elen-7-16-1];

		midx = 7;
		System.arraycopy(mbuf ,midx ,enc_counter ,0 ,enc_counter.length); midx = midx + enc_counter.length;
		System.arraycopy(mbuf ,midx ,enc_data    ,0 ,enc_data.length   );

		byte[] buf = aes_128_ecb_decrypt(hmsg.key ,enc_counter ,0 ,16);

		if (!msg_to_mem_rnd(buf)) /* ���� ��ġ ������ ������ ���Ű����� ���� ���� �� ���� ����. */
		{
			throw new IllegalStateException("ERROR:() ������ġ��������!!");
		}

		byte[] wbuf = speed_ctr_decrypt(enc_data, enc_data.length);

		LUtil.println("SND", "[DAEMON->CORP] rpy_msg["+new String (wbuf, 0, wbuf.length, "8859_1")+"] len["+wbuf.length+"]");


		return wbuf.length;
	}

	void storeSessionKey(byte[] enc_iv ,byte[] trno)
	{
		byte[] iv = aes_128_ecb_decrypt(hmsg.key ,enc_iv, 0, 16);

		/* IV�� ������ ä���. */
		int t1=0, t2=0;
		byte[] nonce = new byte[16];
		for(int i=0; i<7; i++)
		{
			t1 = i*2; t2 = t1+1;
			nonce[i] = (byte)(iv[t1] ^ iv[t2]);
		}

		byte uc1=0, uc2=0;
		if (iv[14] != (uc1 = calculate_lrc(iv ,14)) || iv[15] != (uc2 = (byte)(hmsg.key[13]^iv[13])))/* �������� �ٷ� ���ϸ� �ȵȴ�. */
		{
			throw new IllegalStateException("ERROR:() invalid IV");
		}

		nonce[7] = (byte)(iv[14] ^ iv[15]);

		hmsg.iv   = iv  ;
		hmsg.trno = trno;

		byte[] p_ctr_blocks = new byte[1024];
		for(int i=0; i<p_ctr_blocks.length; i += 16)
		{
			System.arraycopy(nonce ,0 ,p_ctr_blocks   ,i  ,8);

			p_ctr_blocks[i+15] = (byte)(i & 0xff);
		}

		hmsg.ctr_blocks = aes_128_cbc_encrypt(hmsg.key ,hmsg.iv ,p_ctr_blocks, 0 ,p_ctr_blocks.length);
	}


	byte calculate_lrc(byte[] src ,int slen)
	{
		byte c = 0x00;
		for(int i=0; i<slen; i++)
		{
			c ^= src[i];
		}
		return c;
	}

	boolean msg_to_mem_rnd(byte[] rnd_info)
	{
		int    xcnt = 0;
		byte[] tmp_bytes = new byte[16];
		byte crc1=0,crc2=0;

		if (0 != ((byte)0xff ^ rnd_info[0]) || '0' != rnd_info[6]) /* 0xff != rnd_info[0] */
		{
			LUtil.println("DEBIT_BIN", "ERROR:() msg_to_mem_rnd invalid_rcv_enc_counter(POS)!!");
			return false;
		}

		crc1 = rnd_info[5];
		System.arraycopy(rnd_info ,0 ,tmp_bytes ,0 , 5);
		System.arraycopy(rnd_info ,6 ,tmp_bytes ,5 ,10);

		crc2 = calculate_lrc(tmp_bytes ,15);
		if (0 != (crc1 ^ crc2)) /* if (crc1 != crc2) */
		{
			LUtil.println("DEBIT_BIN", "ERROR:() invalid_rcv_enc_counter(CRC)!!");
			return false;
		}

		int rnd_sidx = Integer.parseInt(new String(tmp_bytes,5,10));//&buf[5]);

		int cidx = ro.countWELL512();
		if (cidx != hmsg.rnd_idx || 0 > (xcnt = rnd_sidx - hmsg.rnd_idx))
		{
			LUtil.println("DEBIT_BIN", "ERROR:() rcv_enc_counter");     
			LUtil.println("DEBIT_BIN", "ERROR:() msg_to_mem_rnd cidx:"+cidx);   
			LUtil.println("DEBIT_BIN", "ERROR:() msg_to_mem_rnd hmsg.rnd_idx:"+hmsg.rnd_idx);  
			LUtil.println("DEBIT_BIN", "ERROR:() msg_to_mem_rnd rnd_sidx:"+rnd_sidx);  
			return false;
		}

		int[] wnos = null;
		for(int i=0; i<xcnt; i++) wnos = ro.getWELL512();/* skip */

		return true;
	}

	byte[] mem_rnd_to_msg()
	{
		byte[] tmp_bytes = new byte[16];

		String rnd_msg = String.valueOf(1000000000 + hmsg.rnd_idx);
		System.arraycopy(rnd_msg.getBytes() ,0 ,tmp_bytes ,5 ,10);
		tmp_bytes[5] = '0';

		/* ����1���� 80byte�̴ϱ� 9�ڸ��� �Ǿ 80�Ⱑ��  FF RN1 RN2 RN3 RN4 CRC 30 1~9 */
		tmp_bytes[0] = (byte)0xff;                        /* 0 - FF  */

		tmp_bytes[1] = (byte)(SO_RND.nextInt(255) & 0xff);               /* 1 - RN1 */
		tmp_bytes[2] = (byte)(SO_RND.nextInt(255) & 0xff);               /* 2 - RN2 */
		tmp_bytes[3] = (byte)(SO_RND.nextInt(255) & 0xff);               /* 3 - RN3 */
		tmp_bytes[4] = (byte)(SO_RND.nextInt(255) & 0xff);               /* 4 - RN4 */

		byte crc = calculate_lrc(tmp_bytes ,15);

		byte[] rnd_bytes = new byte[16];

		System.arraycopy(tmp_bytes,0,rnd_bytes,0,5);
		rnd_bytes[5] = crc;
		System.arraycopy(tmp_bytes,5,rnd_bytes,6,10);

		return rnd_bytes;
	}

	byte[] speed_ctr_encrypt(byte[] sbuf ,int slen)
	{
		return speed_ctr_decrypt(sbuf ,slen);
	}

	byte[] speed_ctr_decrypt(byte[] sbuf ,int slen)
	{
		int bidx=0,eidx=0;
		int rno=0 ,ridx=0;

		byte[] tbuf = new byte[slen];

		int tlen = 0;
		for(int i=0; i<slen && tlen<slen; i += 80)
		{
			int[] rinfos = ro.getWELL512();

			rno  = rinfos[0];
			ridx = rinfos[1];

			for(int j=0; j<5; j++)
			{
				eidx = rno & 0x3f;
				rno = rno>>>6;

		bidx = eidx * 16;
		for(int k=0; (k<16 && tlen<slen); k++,tlen++)
		{
			tbuf[tlen] = (byte)(hmsg.ctr_blocks[bidx+k] ^ sbuf[tlen]);
		}
			}
		}

		hmsg.rnd_idx = ridx;

		return tbuf;
	}

	byte[] encrypt_rsa_2048(byte[] aes_key)/* throws java.security.NoSuchProviderException,javax.crypto.NoSuchPaddingException,javax.crypto.BadPaddingException,java.security.NoSuchAlgorithmException,java.security.spec.InvalidKeySpecException,java.security.InvalidKeyException,javax.crypto.IllegalBlockSizeException */
	{
		try
		{
			BigInteger modulus          = new BigInteger("00ef5cd77cc2e16c7c86b216143ce973c05a1ab5717851250ac56cb1ca6bc450118b0e37939049c459bdb8a109b13101952025efb32646271b2616b7fe956ccd8792e60f57155d1ac9d36fa961f7b36776881334506039cca83e34a0e7a684639c6236d09c810cbedb950cdc9295ead4203381861c0eff68d12d193444991df1644f5f7ac4c5a20d3ef418448f238f255627633b4d3dfe0287ada528cf00c46ba452f93cbec551d8c388b32a222b36700c030aefedbb64e49073abe6bf23df66ddbb7a0aab63bcabf5c80b234113016098b5a008a141efa90fdebbddf5032019af3b943e436fa1e0a033d5bd5618c5d08e1f5b7968d55182d21cea8441ac3a75f1",16);
			BigInteger publicExponent   = new BigInteger("010001",16);

			RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, publicExponent);

			// JDK 1.4 ���Ͽ����� BouncyCastleProvider�� ���(�Ʒ��ּ�), JDK1.4�̻󿡼��� 2048bit����(US_export_policy.jar ,local_policy.jar)�� ���� policy���� �ʿ�
			//KeyFactory keyfactory = KeyFactory.getInstance("RSA", CIPHER_PROVIDER);
			KeyFactory keyfactory = KeyFactory.getInstance("RSA");
			java.security.PublicKey publickey = keyfactory.generatePublic(pubKeySpec);

			// JDK 1.4 ���Ͽ����� BouncyCastleProvider�� ���(�Ʒ��ּ�), JDK1.4�̻󿡼��� 2048bit����(US_export_policy.jar ,local_policy.jar)�� ���� policy���� �ʿ�
			//Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding",CIPHER_PROVIDER);
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

			cipher.init(Cipher.ENCRYPT_MODE, publickey);//, fixedsecurerandom);

			return cipher.doFinal(aes_key);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return (new byte[0]);
	}

	byte[] aes_128_ecb_decrypt(byte[] k16 ,byte[] pbuf ,int idx ,int len)
	{
		try
		{
			SecretKeySpec skeySpec = new SecretKeySpec(k16, "AES");//AES/ECB/NoPadding

			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");//"AES"

			cipher.init(Cipher.DECRYPT_MODE, skeySpec);

			return cipher.doFinal(pbuf,idx,len);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return (new byte[0]);
	}

	byte[] aes_128_ecb_encrypt(byte[] k16 ,byte[] pbuf ,int idx ,int len)
	{
		try
		{
			SecretKeySpec skeySpec = new SecretKeySpec(k16, "AES");//AES/ECB/NoPadding

			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");//"AES"

			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

			return cipher.doFinal(pbuf,idx,len);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return (new byte[0]);
	}

	byte[] aes_128_cbc_encrypt(byte[] k16 ,byte[] iv ,byte[] pbuf ,int idx ,int len)
	{
		try
		{
			SecretKeySpec skeySpec = new SecretKeySpec(k16, "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"AES"

			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(iv));

			return cipher.doFinal(pbuf,idx,len);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return (new byte[0]);
	}

	public byte[] ReadMsg(DataInputStream in) throws Exception
	{
		int msg_len = 4; /* ���� */
		int rtn_len = 0;

		byte[]   b_len = new byte[msg_len];
		if (msg_len != (rtn_len = in.read(b_len, 0, msg_len)))
		{
			throw new IOException("ReadMsg1 error - (" + rtn_len + ")����!!");
		}

		/*
      LUtil.println("DEBIT_BIN", "DEBUG ReadMsg1=[" + new String(b_len) + "]");
		 */

		msg_len = Integer.parseInt(new String(b_len));

		byte[]   rbuf = new byte[msg_len+4];

		System.arraycopy(b_len, 0, rbuf, 0, 4); /* ���̺��� */

		if (msg_len != (rtn_len = in.read(rbuf, 4, msg_len)))
		{
			throw new IOException("ReadMsg2 error - (" + rtn_len + ")����!!");
		}

		return rbuf;
	}
	public byte[] ReadLine(DataInputStream in, int msg_len) throws Exception
	{
		int rtn_len = 0;
		int read_len = 0;

		int rest_len = msg_len;

		byte[]   rbuf = new byte[msg_len];

		while (true)
		{
			if (rest_len != (rtn_len = in.read(rbuf, read_len, rest_len)))
			{
				/* �������̺��� ª�� */
				if (rtn_len > 0)
				{
					LUtil.println("DEBIT_BIN", "ReadLine �߰������� ���� - ���Ŵ��ũ��:"+ rest_len + " ����ũ��: "+ rtn_len );
					rest_len -= rtn_len;      
					read_len += rtn_len;
					continue;
				}
				throw new IOException("ERROR! ReadLine msg short - ���Ŵ��ũ��:"+ rest_len + " ����ũ��: "+ rtn_len );
			}

			break;
		}

		/*
      LUtil.println("DEBIT_BIN", "DEBUG ReadLine =[" + new String(rbuf) + "]");
		 */

		return rbuf;
	}

	public byte[] checkMsgLen(DataInputStream msg_in, DataInputStream bin_in) throws Exception
	{

		int    t_len = 0;
		int    rest_len = 0;
		int    read_len = 0;
		byte[] buf = null;
		byte[] block_buf = null;
		try
		{
			byte[]    msg_buf = ReadMsg(msg_in);
			String    MsgCode = new String(msg_buf, 4+9+8+2, 7, "8859_1");

			LUtil.println("SND","[CORP->DAEMON] req_msg["+new String (msg_buf, 0, msg_buf.length, "8859_1")+"] len["+msg_buf.length+"]");

			/* �����ڷ��  �����ڷ� �߰��� �����ؾߵȴ�. */
			if (MsgCode.equals("0600601"))
			{


				t_len = Integer.parseInt(new String(msg_buf, 4+244, 7, "8859_1"));

				rest_len = t_len;

				while(true)
				{
					if (rest_len > READ_BLOCK_SIZE) read_len = READ_BLOCK_SIZE;
					else read_len = rest_len;
					block_buf = ReadLine(bin_in, read_len);
					rest_len -= read_len;
					if (rest_len == 0) break;
				}

				LUtil.println("DEBIT_BIN", "�������� size[" + t_len + "]");


				buf = new byte[msg_buf.length + t_len];
				System.arraycopy(msg_buf, 0, buf, 0, msg_buf.length);
				System.arraycopy(block_buf, 0, buf, msg_buf.length, block_buf.length);
			}else {
				buf = new byte[msg_buf.length];
				System.arraycopy(msg_buf, 0, buf, 0, msg_buf.length);
			}

		}catch (Exception e) {
			LUtil.println("DEBIT_BIN", e.getMessage());
			return null;
		}
		return buf;
	}

}


class MetaEn
{
	byte[]    route_type ;/* [   2] */
	byte[]    enc_type   ;/* [   1] */
	byte[]    m_key_type ;/* [   1] */
	byte[]    trno       ;/* [  16] */
	byte[]    key        ;/* [  16] */
	byte[]    iv         ;/* [  16] */
	byte[]    ctr_blocks ;/* [1024] */
	int     rnd_idx      ;/*        */
}

class MsgEn
{
	char   ctype       ; /* connect type : 'C' ,'S' */

	byte[]   mbuf = new byte[20480];
	int      clen      ;
	int      elen      ;
}