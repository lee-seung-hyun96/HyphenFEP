package im.hyphen.crypto;

import java.io.*;
import java.net.*;

import im.hyphen.util.*;

public class HyphenRcvEnc extends Thread
{
	final static String Encrypt_Key = "12345678abcdefgh12345678";
	final static int MSG_LEN = 300;
	final static int MAX_MSG_LEN = 2000;
	final static String VERIVFY_KEY = "                ";
  
	public static void main(String[] args) throws Exception
	{
		String path = System.getProperty("user.dir");
		path = path + "\\conf\\config.ini";
		System.out.println(path);
		CUtil.setConfig(path);

		/* LISTEN PORT */
		String LISTEN_PORT = CUtil.get("RCV_LISTEN_PORT");
		int port = Integer.parseInt(LISTEN_PORT);

		if (port == 0) throw new IllegalArgumentException("ERROR config RCV_LISTEN_PORT");

		TimerDeleteLog DeleteLog = new TimerDeleteLog(Integer.parseInt(CUtil.get("LOG_SAVE_DAYS")), CUtil.get("LOG_DIR_PATH"));
		DeleteLog.start();

		ServerSocket ss = new ServerSocket(port);
		
		LUtil.println("RCV", "START");

		while(true)
		{
			Socket  cs = ss.accept();

			new HyphenRcvEnc(cs).start();
		}
	}

	Socket cs;
	public HyphenRcvEnc(Socket cs)
	{
		this.cs = cs;
	}

	public void run()
	{
		DataOutputStream    out         = null;
		DataInputStream     in          = null;
		String              req_msg    = null;
		String              res_msg    = null;
		Socket sock = null;

		try {
			in = new DataInputStream(cs.getInputStream());

			byte[]  tmp_buf = new byte[4];
			int     read_len = 0, rtn_len = 0, rest_len =0;

			if (4 != (rtn_len = in.read(tmp_buf, 0, 4)))
			{
				throw new IOException("Read error1 - " + rtn_len + "read Byte!!");
			}


			read_len = 0;
			rest_len = Integer.parseInt(new String(tmp_buf));

			byte[] sebuf = new byte[rest_len];
			
			while(true)
			{
				if (0 >= (rtn_len = in.read(sebuf, read_len, rest_len)))
				{
					throw new IOException("Read error2 - rtn_len:" + rtn_len + " read_len:"+read_len + " rest_len:" + rest_len);
				}
				
				read_len = read_len + rtn_len;
				rest_len = rest_len - rtn_len;
					
				if (rest_len == 0) break;
			}

			/* decode */
			byte[] sbuf = EUtil.udecode_3des(Encrypt_Key.getBytes(), sebuf);

			req_msg = new String(sbuf, "ksc5601");

			String svc_name = SUtil.GetSvcName(sbuf);

			LUtil.println("RCV", "RCV_MSG=[" + req_msg + "] [L:"+sbuf.length+"] ["+svc_name+"]");

			String corp_ip = CUtil.get("COMPANY_IP_"+svc_name).trim();
			int corp_port = Integer.parseInt(CUtil.get("COMPANY_PORT_"+svc_name));
			int sock_timeout = Integer.parseInt(CUtil.get("RCV_TIMEOUT"));

			if (corp_ip == null)
			{
				LUtil.println("RCV", "ERROR config.ini undefined  parameter COMPANY_IP_"+svc_name+" REQ_MSG=["+ req_msg + "]");
			}

			LUtil.println("RCV", "DEBUG Connection infomation =["+corp_ip+"]["+corp_port+"] ["+sock_timeout+"]");

		  	sock = HyphenEncSocket.connect_socket(corp_ip, corp_port);


		  	HyphenEncSocket.snd_socket(sock, sbuf);


      		byte[] rbuf = HyphenEncSocket.rcv_socket_rfb(sock, sock_timeout);


	      	if (rbuf == null) 
	      	{
	      		LUtil.println("RCV", "ERROR rcv_socket REQ_MSG=["+ req_msg + "]" );
	      		return;
	      	}

			res_msg = new String(rbuf, "ksc5601");

			LUtil.println("RCV", "SND_MSG=[" + res_msg + "] [L:"+sbuf.length+"] ["+svc_name+"]");

			byte[] erbuf = EUtil.uencode_3des(Encrypt_Key.getBytes(), rbuf);


			out = new DataOutputStream(cs.getOutputStream());
			out.write(String.format("%04d", erbuf.length).getBytes());
			out.write(erbuf);
			out.flush();
      
		}catch (Exception e) {
			LUtil.println("RCV", "ERROR : "+e.getMessage()+ "REQ_MSG=["+ req_msg + "]" );
			LUtil.println("SND", e);
		}finally{
			try{if (in != null) in.close();}catch(Exception e){};
			try{if (out != null) out.close();}catch(Exception e){};
			try{if (cs != null) cs.close();}catch(Exception e){};
		}
	}
}
