package Crypto;

import java.io.*;
import java.net.*;
import java.util.*;

import Util.*;


public class KsnetSndEnc extends Thread
{
	public static void main(String[] args) throws Exception
	{
		String path = System.getProperty("user.dir");
		path = path + "\\conf\\config.ini";
		System.out.println(path);
		CUtil.setConfig(path);

		/* LISTEN PORT */
		String LISTEN_PORT = CUtil.get("SND_LISTEN_PORT");
		int port = Integer.parseInt(LISTEN_PORT);

		if (port == 0) throw new IllegalArgumentException("ERROR config LISTEN_PORT");

		TimerDeleteLog DeleteLog = new TimerDeleteLog(Integer.parseInt(CUtil.get("LOG_SAVE_DAYS")), CUtil.get("LOG_DIR_PATH"));
		DeleteLog.start();

		ServerSocket ss = new ServerSocket(port);
		
		LUtil.println("SND", "START");

		while(true)
		{
			Socket  cs = ss.accept();

			new KsnetSndEnc(cs).start();
		}
	}

	Socket cs;
	public KsnetSndEnc(Socket cs)
	{
		this.cs = cs;
	}

	public void run()
	{
		String              req_msg    = null;
		Socket sock = null;

		try {
			byte[]  req_buf = KSBankEncSocket.rcv_socket_rfb(cs, 2);

	      	if (req_buf == null) 
	      	{
	      		LUtil.println("SND", "ERROR rcv_socket_rfb");
	      		return;
	      	}

			req_msg = KSBankMsg.b2s(req_buf);
			String svc_code = SUtil.GetSvcCode(req_buf);

			LUtil.println("SND", "REQ_MSG=[" + KSBankMsg.b2s(req_buf) + "] L:"+req_buf.length+" SVC_CODE:"+svc_code);		
		
			byte[] k_buf = KSBankEncSocket.GenerateKey();
			
			String auth_key = CUtil.get("AUTH_KEY");
			byte[] snd_buf = KSBankMsg.s2b(svc_code+auth_key+req_msg);
			
      		byte[] e_sbuf = KSBankEncSocket.kscms_encrypt(k_buf, snd_buf);

			String ksnet_ip = CUtil.get("KSNET_IP").trim();
			int ksnet_port = Integer.parseInt(CUtil.get("KSNET_PORT"));
			int sock_timeout = Integer.parseInt(CUtil.get("SND_TIMEOUT"));

		  	sock = KSBankEncSocket.connect_socket(ksnet_ip, ksnet_port);

      		KSBankEncSocket.snd_socket(sock, e_sbuf);

      		byte[] e_rbuf = KSBankEncSocket.rcv_socket_len(sock, sock_timeout);
      
	      	if (e_rbuf == null) 
	      	{
	      		LUtil.println("SND", "ERROR rcv_socket REQ_MSG=["+ req_msg + "]" );
	      		return;
	      	}
	
	      	byte[] rpy_buf = KSBankEncSocket.kscms_decrypt(k_buf, e_rbuf);
	
	      	if (rpy_buf == null) 
	      	{
	      		LUtil.println("ERROR kscms_decrypt REQ_MSG=["+ req_msg + "]" );
	      		return;
      		}
      
			LUtil.println("SND", "RPY_MSG" + "=[" + KSBankMsg.b2s(rpy_buf) + "] L:"+rpy_buf.length);		


      		if (KSBankEncSocket.snd_socket(cs, rpy_buf))
			{
				LUtil.println("RPY_MSG" + "=[" + KSBankMsg.b2s(rpy_buf) + "] L:"+rpy_buf.length);		
			}			
			else
			{
				LUtil.println("SND", "ERROR send_socket RPY_MSG" + "=[" + KSBankMsg.b2s(rpy_buf) + "] L:"+rpy_buf.length);		
			}

		}catch (Exception e) {
			LUtil.println("ERROR: "+e.getMessage()+ "REQ_MSG=["+ req_msg + "]" );
			LUtil.println(e);
		}finally{
			try{if (cs != null) cs.close();}catch(Exception e){};
		}
	}
}
