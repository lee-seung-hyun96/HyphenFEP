package Firm_Bypass;

import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import Util.*;
import java.sql.*;

public class VirtualAccountDB extends Thread
{
	public static String Encrypt_Key = "12345678abcdefgh12345678";
	public static String Encoding = null;
	public static String LineType = null;
	public static String VrUpdateType = null;
	public static int MSG_LEN = 300;

	static String LOG_D = null;
	
	
	public static void main(String[] args) throws Exception
	{
		CUtil.setConfig(args[0]);
		
		LOG_D     = CUtil.get("LOG_DIR_PATH");
		File mkdirPath = new File(LOG_D);
		if(!mkdirPath.exists()) mkdirPath.mkdirs();
		

		/* Line Type */                             
		LineType = CUtil.get("COMM_LINE_TYPE");		/* Private Line; Y , Internet LIne: N */			
		if (LineType == null || LineType.length() == 0) LineType = "Y";
		
		/* Vr Account Auto Update */
		VrUpdateType = CUtil.get("AUTO_VR_UPDATE");
		if (VrUpdateType == null || VrUpdateType.length() == 0) VrUpdateType = "N";
		
		/* LISTEN Port */
		String LISTEN_PORT = CUtil.get("LISTEN_PORT");
		int port = Integer.parseInt(LISTEN_PORT);

		if (port == 0) throw new IllegalArgumentException("rve-config.ini LISTEN_PORT Error");

		Encoding = CUtil.get("ENCODING");
		if (Encoding == null || Encoding.length() == 0) Encoding = "ksc5601";

		LUtil.println("Process Start COMM_LINE_TYPE["+LineType+"] ENCODING["+Encoding+"] LISTEN_PORT["+LISTEN_PORT+"]");
				
		ServerSocket ss = new ServerSocket(port);
	
		while(true)
		{
			Socket  cs = ss.accept();
			new VirtualAccountDB(cs).start();
		}
	}

	Socket cs;
	public VirtualAccountDB(Socket cs)
	{
		this.cs = cs;
	}

	public void run()
	{
		DataOutputStream    out         = null;
		DataInputStream     in          = null;
		String              read_msg    = null;
		
		
		int read_len = 0, rtn_len = 0;

		try {

			in = new DataInputStream(cs.getInputStream());

			/* Internet line */
			if (LineType.equals("N") || LineType.equals("n") )
			{
				byte[]	read_buf = new byte[2048];
				byte[]	dec_read_buf = new byte[2048];
				byte[]	enc_send_buf = new byte[2048];
				byte[]	dec_send_buf = new byte[2048];


				if (0 >= (rtn_len = in.read(read_buf, read_len, 2048)))
				{
					throw new IOException("Read error - " + rtn_len + "read Byte!!");
				}
	
				read_len = read_len + rtn_len;
	
				byte[]  enc_read_buf = new byte[read_len];
				System.arraycopy(read_buf,0,enc_read_buf,0,read_len);
	
				dec_read_buf = EUtil.udecode_3des(Encrypt_Key.getBytes(), enc_read_buf);
	
				LUtil.println("RCV_MSG=[" + SUtil.toHanE(dec_read_buf) + "]");
			
				dec_send_buf = parseMsg(dec_read_buf);

				enc_send_buf = EUtil.uencode_3des(Encrypt_Key.getBytes(), dec_send_buf);
	
				out = new DataOutputStream(cs.getOutputStream());
				out.write(enc_send_buf);
				out.flush();
	
				LUtil.println("SND_MSG=[" + SUtil.toHanE(dec_send_buf) + "]");
			}
			/* private line */
			else
			{
				byte[]	read_buf = new byte[MSG_LEN];
				if (MSG_LEN != (rtn_len = in.read(read_buf, read_len, MSG_LEN)))
				{
					throw new IOException("Read error - " + rtn_len + "read Byte!!");
				}
	
				read_len = read_len + rtn_len;
	
				LUtil.println("RCV_MSG=[" + SUtil.toHanE(read_buf) + "]");
	
				byte[] send_buf = parseMsg(read_buf);
	
				out = new DataOutputStream(cs.getOutputStream());
				out.write(send_buf);
				out.flush();
	
				LUtil.println("SND_MSG=[" + SUtil.toHanE(send_buf) + "]");
			}

			
		}catch (Exception e) {
			LUtil.println(e.getMessage());
		}finally{
			try{if (in != null) in.close();}catch(Exception e){};
			try{if (out != null) out.close();}catch(Exception e){};
			try{if (cs != null) cs.close();}catch(Exception e){};
		}
	}

	public byte[] parseMsg(byte[] byte_data) throws Exception
	{
		boolean     ret = false;
		int         ipos = 0;
		HashMap<String,String> dHash = new HashMap<String,String>();
		HashMap<String,String> rHash = new HashMap<String,String>();
		

		if (byte_data[19] == '0' && byte_data[20] == '9' && byte_data[21] == '0' && byte_data[22] == '0')
		{
			String  send_date  = SUtil.toHanX(byte_data, 33, 8     );
			String  bank_code   = SUtil.toHanX(byte_data, 84, 3     );
			String  vr_acct_no   = SUtil.toHanX(byte_data, 100, 16   );
			String  amt         = SUtil.toHanX(byte_data, 170, 13   );
			String  corp_name 	= "";
			String  error_code = "L099";
			int     len;
			byte[] read_buf = (byte[])byte_data.clone();

			try {
				dHash.put("send_date", send_date);
				dHash.put("bank_code", bank_code);
				dHash.put("vr_acct_no", vr_acct_no);
				dHash.put("amt", amt);
	
				rHash = DUtil.select0900_100(dHash);

				if (rHash.containsKey("corp_name"))  corp_name = rHash.get("corp_name");
				if (rHash.containsKey("error_code")) error_code = rHash.get("error_code");
	
				byte[] b_resp_code = error_code.getBytes();
				byte[] b_corp_name = corp_name.getBytes("ksc5601");
				
				read_buf[19+2] = '1';
	
				System.arraycopy(b_resp_code, 0, read_buf, 47, b_resp_code.length);
				System.arraycopy(b_resp_code, 0, read_buf, 51, b_resp_code.length);
				System.arraycopy(b_corp_name, 0, read_buf, 116, b_corp_name.length);

			}
			catch(java.io.UnsupportedEncodingException ue){ue.printStackTrace();}
			
			return read_buf;
		}
		/* (0200/300) */
		else if (byte_data[19] == '0' && byte_data[20] == '2' && byte_data[21] == '0' && byte_data[22] == '0')
		{
			
			String  tran_code       = SUtil.toHanX(byte_data, ipos, 9   ); ipos +=  9 ;
			String  comp_code       = SUtil.toHanX(byte_data, ipos, 8   ); ipos +=  8 ;
			String  bank_code       = SUtil.toHanX(byte_data, ipos, 2   ); ipos +=  2 ;
			String  mess_code       = SUtil.toHanX(byte_data, ipos, 4   ); ipos +=  4 ;
			String  mess_diff       = SUtil.toHanX(byte_data, ipos, 3   ); ipos +=  3 ;
			String  tran_cnt        = SUtil.toHanX(byte_data, ipos, 1   ); ipos +=  1 ;
			String  seq_no          = SUtil.toHanX(byte_data, ipos, 6   ); ipos +=  6 ;
			String  tran_date       = SUtil.toHanX(byte_data, ipos, 8   ); ipos +=  8 ;
			String  tran_time       = SUtil.toHanX(byte_data, ipos, 6   ); ipos +=  6 ;
			String  stan_resp_code  = SUtil.toHanX(byte_data, ipos, 4   ); ipos +=  4 ;
			String  bank_resp_code  = SUtil.toHanX(byte_data, ipos, 4   ); ipos +=  4 ;
			String  inqu_date       = SUtil.toHanX(byte_data, ipos, 8   ); ipos +=  8 ;
			String  inqu_no         = SUtil.toHanX(byte_data, ipos, 6   ); ipos +=  6 ;
			String  bank_seq_no     = SUtil.toHanX(byte_data, ipos, 15  ); ipos +=  15;
			String  bank_code_3     = SUtil.toHanX(byte_data, ipos, 3   ); ipos +=  3 ;
			String  filler_1        = SUtil.toHanX(byte_data, ipos, 13  ); ipos +=  13;
			String  corp_acct_no     = SUtil.toHanX(byte_data, ipos, 15  ); ipos +=  15;
			String  comp_cnt        = SUtil.toHanX(byte_data, ipos, 2   ); ipos +=  2 ;
			String  deal_sele       = SUtil.toHanX(byte_data, ipos, 2   ); ipos +=  2 ;
			String  in_bank_code    = SUtil.toHanX(byte_data, ipos, 2   ); ipos +=  2 ;
			String  total_amt       = SUtil.toHanX(byte_data, ipos, 13  ); ipos +=  13;
			String  balance         = SUtil.toHanX(byte_data, ipos, 13  ); ipos +=  13;
			String  bran_code       = SUtil.toHanX(byte_data, ipos, 6   ); ipos +=  6 ;
			String  cust_name       = SUtil.toHanX(byte_data, ipos, 14  ); ipos +=  14;
			String  check_no        = SUtil.toHanX(byte_data, ipos, 10  ); ipos +=  10;
			String  cash            = SUtil.toHanX(byte_data, ipos, 13  ); ipos +=  13;
			String  out_bank_check  = SUtil.toHanX(byte_data, ipos, 13  ); ipos +=  13;
			String  etc_check       = SUtil.toHanX(byte_data, ipos, 13  ); ipos +=  13;
			String  vr_acct_no       = SUtil.toHanX(byte_data, ipos, 16  ); ipos +=  16;
			String  deal_date       = SUtil.toHanX(byte_data, ipos, 8   ); ipos +=  8 ;
			String  deal_time       = SUtil.toHanX(byte_data, ipos, 6   ); ipos +=  6 ;
			String  serial_no       = SUtil.toHanX(byte_data, ipos, 6   ); ipos +=  6 ;
			String  in_bank_code_3  = SUtil.toHanX(byte_data, ipos, 3   ); ipos +=  3 ;
			String  bran_code_3     = SUtil.toHanX(byte_data, ipos, 7   ); ipos +=  7 ;
			String  filler_2        = SUtil.toHanX(byte_data, ipos, 38  ); ipos +=  38;

			String  error_code = "9999";
			ret = DUtil.insert0200_300(tran_code,comp_code,bank_code,mess_code,mess_diff, tran_cnt,seq_no,tran_date,tran_time,stan_resp_code,bank_resp_code,inqu_date,inqu_no,bank_seq_no,bank_code_3,filler_1,corp_acct_no,comp_cnt,deal_sele,in_bank_code,total_amt,balance,bran_code,cust_name,check_no,cash,out_bank_check, etc_check,vr_acct_no,deal_date,deal_time,serial_no,in_bank_code_3,bran_code_3,filler_2);

			byte[] read_buf = (byte[])byte_data.clone();

			read_buf[19+2] = '1';

			if(ret) error_code = "0000";				

			byte[] b_resp_code = error_code.getBytes();

			System.arraycopy(b_resp_code, 0, read_buf, 47, b_resp_code.length);
			System.arraycopy(b_resp_code, 0, read_buf, 51, b_resp_code.length);

			return read_buf;
		}
		else if (byte_data[19] == '0' && byte_data[20] == '4' && byte_data[21] == '0' && byte_data[22] == '0')
		{
			String  tran_code       = SUtil.toHanX(byte_data, ipos, 9   ); ipos +=  9 ; 
			String  comp_code       = SUtil.toHanX(byte_data, ipos, 8   ); ipos +=  8 ; 
			String  bank_code       = SUtil.toHanX(byte_data, ipos, 2   ); ipos +=  2 ; 
			String  mess_code       = SUtil.toHanX(byte_data, ipos, 4   ); ipos +=  4 ; 
			String  mess_diff       = SUtil.toHanX(byte_data, ipos, 3   ); ipos +=  3 ; 
			String  tran_cnt        = SUtil.toHanX(byte_data, ipos, 1   ); ipos +=  1 ; 
			String  seq_no          = SUtil.toHanX(byte_data, ipos, 6   ); ipos +=  6 ; 
			String  tran_date       = SUtil.toHanX(byte_data, ipos, 8   ); ipos +=  8 ; 
			String  tran_time       = SUtil.toHanX(byte_data, ipos, 6   ); ipos +=  6 ; 
			String  stan_resp_code  = SUtil.toHanX(byte_data, ipos, 4   ); ipos +=  4 ; 
			String  bank_resp_code  = SUtil.toHanX(byte_data, ipos, 4   ); ipos +=  4 ; 
			String  inqu_date       = SUtil.toHanX(byte_data, ipos, 8   ); ipos +=  8 ; 
			String  inqu_no         = SUtil.toHanX(byte_data, ipos, 6   ); ipos +=  6 ; 
			String  bank_seq_no     = SUtil.toHanX(byte_data, ipos, 15  ); ipos +=  15; 
			String  bank_code_3     = SUtil.toHanX(byte_data, ipos, 3   ); ipos +=  3 ; 
			String  filler_1        = SUtil.toHanX(byte_data, ipos, 13  ); ipos +=  13; 
			String  org_seq_no      = SUtil.toHanX(byte_data, ipos, 6   ); ipos +=  6 ; 
			String  out_account_no  = SUtil.toHanX(byte_data, ipos, 15  ); ipos +=  15; 
			String  in_account_no   = SUtil.toHanX(byte_data, ipos, 15  ); ipos +=  15; 
			String  in_money        = SUtil.toHanX(byte_data, ipos, 13  ); ipos +=  13; 
			String  in_bank_code    = SUtil.toHanX(byte_data, ipos, 2   ); ipos +=  2 ; 
			String  nor_money       = SUtil.toHanX(byte_data, ipos, 13  ); ipos +=  13; 
			String  abnor_money     = SUtil.toHanX(byte_data, ipos, 13  ); ipos +=  13; 
			String  div_proc_cnt    = SUtil.toHanX(byte_data, ipos, 2   ); ipos +=  2 ; 
			String  div_proc_no     = SUtil.toHanX(byte_data, ipos, 2   ); ipos +=  2 ; 
			String  ta_no           = SUtil.toHanX(byte_data, ipos, 6   ); ipos +=  6 ; 
			String  not_in_amt      = SUtil.toHanX(byte_data, ipos, 9   ); ipos +=  9 ; 
			String  err_code        = SUtil.toHanX(byte_data, ipos, 3   ); ipos +=  3 ; 
			String  in_bank_code_3  = SUtil.toHanX(byte_data, ipos, 3   ); ipos +=  3 ; 
			String  filler_2        = SUtil.toHanX(byte_data, ipos, 98  ); ipos +=  98; 

			ret = DUtil.insert0400_100(tran_code, comp_code, bank_code, mess_code, mess_diff, tran_cnt, seq_no, tran_date, tran_time, stan_resp_code, bank_resp_code, inqu_date, inqu_no, bank_seq_no, bank_code_3, filler_1, org_seq_no, out_account_no, in_account_no, in_money, in_bank_code, nor_money, abnor_money, div_proc_cnt, div_proc_no, ta_no, not_in_amt, err_code, in_bank_code_3, filler_2);

			String  error_code = "9999";
			byte[] read_buf = (byte[])byte_data.clone();

			read_buf[19+2] = '1';

			if(ret) error_code = "0000";				

			byte[] b_resp_code = error_code.getBytes();

			System.arraycopy(b_resp_code, 0, read_buf, 47, b_resp_code.length);
			System.arraycopy(b_resp_code, 0, read_buf, 51, b_resp_code.length);

			return read_buf;
		}
		else if (byte_data[19] == '0' && byte_data[20] == '8' )
		{
			String  error_code = "0000";
			byte[] read_buf = (byte[])byte_data.clone();

			read_buf[19+2] = '1';

			byte[] b_resp_code = error_code.getBytes();

			System.arraycopy(b_resp_code, 0, read_buf, 47, b_resp_code.length);
			System.arraycopy(b_resp_code, 0, read_buf, 51, b_resp_code.length);

			return read_buf;
		}
		else
		{
			throw new RuntimeException("ERROR undefined MSG["+new String(byte_data)+"]");
		}
	}
}
