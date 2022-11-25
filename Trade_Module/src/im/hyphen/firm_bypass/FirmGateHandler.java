package im.hyphen.firm_bypass;


import java.io.*;
import java.net.*;

import im.hyphen.crypto.HyphenEncSocket;

import im.hyphen.util.*;

public class FirmGateHandler extends Thread
{
	private String msg_info[] = new String[10];
	Socket socket = null;

	public FirmGateHandler(String[] send_info)
	{	
		System.arraycopy(send_info, 0, this.msg_info, 0, send_info.length);
	}

	public void run()
	{
		DataOutputStream    serv_out = null;
		DataInputStream     serv_in = null;
		String              send_msg = null;
		String              recv_msg = null;
		int                 msg_len = 0;
		try {
			this.msg_info[0] = "9999";
			this.msg_info[9] = " ";
			send_msg = this.msg_info[8];
			
			//Socket Info and conncet
			String ksnet_ip = CUtil.get("KSNET_IP").trim();
			int ksnet_port = Integer.parseInt(CUtil.get("KSNET_PORT"));
			int sock_timeout = Integer.parseInt(CUtil.get("SND_TIMEOUT"));

			socket = new Socket();
			socket = HyphenEncSocket.connect_socket(ksnet_ip, ksnet_port);

			if(socket == null) {
				LUtil.println("CLIENT->KSNET=[Socket Connect Error]");
			}
			if (msg_info[2].equals("KEB"))
				msg_len = 2000;  /* KEB 2000bytes */
			else
				msg_len = 300;  /* 300bytes */

			byte[] sbuf = SUtil.ConvS2B(send_msg, msg_len, "ksc5601");
			//wbs_egate encrypt
			byte[] k_buf = HyphenEncSocket.GenerateKey();
			String svc_code = SUtil.GetSvcCode(SUtil.s2b(send_msg));

			String auth_key = CUtil.get("AUTH_KEY");
			byte[] snd_buf = SUtil.s2b(svc_code+auth_key+send_msg);

			byte[] e_sbuf = HyphenEncSocket.cms_encrypt(k_buf, snd_buf);


			HyphenEncSocket.snd_socket(socket, e_sbuf);
			byte[] e_rbuf = null;
			try{
				LUtil.println("CLIENT->KSNET=[" + send_msg + "][L:"+send_msg.length()+"]");
				e_rbuf = HyphenEncSocket.rcv_socket_len(socket, sock_timeout);
			}catch(Exception e) {
				e.printStackTrace();
			}

			if (e_rbuf == null) 
			{
				LUtil.println("ERROR rcv_socket REQ_MSG=["+ send_msg + "]" );
				return;
			}

			byte[] rpy_buf = HyphenEncSocket.cms_decrypt(k_buf, e_rbuf);

			if (rpy_buf == null) 
			{
				LUtil.println("ERROR kscms_decrypt REQ_MSG=["+ send_msg + "]" );
				return;
			}

			if (HyphenEncSocket.snd_socket(socket, rpy_buf))
			{
				LUtil.println("KSNET->CLIENT=[" + recv_msg + "][L:"+rpy_buf.length+"]");
			}			
			else
			{
				LUtil.println("SND", "ERROR send_socket RPY_MSG" + "=[" + SUtil.b2s(rpy_buf) + "] L:"+rpy_buf.length);		
			}

			LUtil.println("SND", "RPY_MSG" + "=[" + SUtil.b2s(rpy_buf) + "] L:"+rpy_buf.length);		


			recv_msg = SUtil.ConvB2S(rpy_buf, 0, msg_len, "ksc5601");

			this.msg_info[0] = "0000";
			this.msg_info[9] = recv_msg;

			DUtil.Update_RecvData(this.msg_info);   /* update recv_msg  */

		}catch(SocketTimeoutException e) {
			this.msg_info[0] = "TIME";
			DUtil.Update_RecvData(this.msg_info);
			LUtil.println("ERROR:"+e.getMessage()+" ["+send_msg+"]");
		}catch(ConnectException e) {
			this.msg_info[0] = "CONE";
			DUtil.Update_RecvData(this.msg_info);
			LUtil.println("ERROR:"+e.getMessage()+" ["+send_msg+"]");
		}catch (Exception e) {
			this.msg_info[0] = "9999";
			DUtil.Update_RecvData(this.msg_info);
			LUtil.println("ERROR:"+e.getMessage()+" ["+send_msg+"]");
		}finally {
			try{if (serv_in != null) serv_in.close();}catch(Exception e){};
			try{if (serv_out != null) serv_out.close();}catch(Exception e){};
			try{if (socket != null) socket.close();}catch(Exception e){};
		}
	}
}
