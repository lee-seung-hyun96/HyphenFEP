package Firm_Bypass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;

import Crypto.*;
import Util.*;

public class FirmBypass_bak extends Thread{
//	String RequestDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
//	String test_send = "         KONEPS01  08008001900002"+ RequestDate + "                                     089                                                                                                                                                                                                                     ";
//	//TestEnc Send
//	public static void main(String[] args) {
////		String curr_date_14 = DUtil.getCurrDate();
////		int midx = 1000000000 + Integer.parseInt(curr_date_14.substring(6)+"0");
////
////		CUtil.setConfig("C:\\stKim\\Transaction_v1.0\\Trade_Module\\conf\\config.ini");
////		new FirmBypass().start();
//
//	}
//
//	public void run()
//	{
//		DataOutputStream    cli_out = null;
//		DataInputStream     cli_in = null;
//		DataOutputStream    serv_out = null;
//		DataInputStream     serv_in = null;
//		String              send_msg = null;
//		String              recv_msg = null;
//		int                timeout;
//		int                 msg_len = 0;
//		int                 len;
//		Socket              socket = null;
//
//		int     read_len = 0, rest_len = 0, rtn_len = 0;
//		
//		try {
//			
//			//creat socket
//			String ksnet_ip = CUtil.get("KSNET_IP").trim();
//			int ksnet_port = Integer.parseInt(CUtil.get("KSNET_PORT"));
//			int sock_timeout = Integer.parseInt(CUtil.get("SND_TIMEOUT"));
//
////			LUtil.println("SND", "REQ_MSG=[" + SUtil.b2s(req_buf) + "] L:"+req_buf.length+" SVC_CODE:"+svc_code);		
//			String svc_code = SUtil.GetSvcCode(SUtil.s2b(test_send));
//			LUtil.println("SND", "REQ_MSG=[" + test_send + "] L:"+test_send.length()+" SVC_CODE:"+svc_code);		
//
//			if (ksnet_ip == null)
//			{
//				throw new IOException("config undefined - (" + ksnet_ip + ")!!");
//			}
//
//			
//			
//			
//			//wbs_egate encrypt
//			byte[] k_buf = KSBankEncSocket.GenerateKey();
//
//			String auth_key = CUtil.get("AUTH_KEY");
//			byte[] snd_buf = SUtil.s2b(svc_code+auth_key+test_send);
//
//      		byte[] e_sbuf = KSBankEncSocket.kscms_encrypt(k_buf, snd_buf);
//
//      		
//      		socket = KSBankEncSocket.connect_socket(ksnet_ip, ksnet_port);
//      		
//      		//common
//      		KSBankEncSocket.snd_socket(socket, e_sbuf);
//
//      		byte[] e_rbuf = KSBankEncSocket.rcv_socket_len(socket, sock_timeout);
//
//	      	if (e_rbuf == null) 
//	      	{
//	      		LUtil.println("SND", "ERROR rcv_socket REQ_MSG=["+ test_send + "]" );
//	      		return;
//	      	}
//	
//	      	byte[] rpy_buf = KSBankEncSocket.kscms_decrypt(k_buf, e_rbuf);
//	
//	      	if (rpy_buf == null) 
//	      	{
//	      		LUtil.println("ERROR kscms_decrypt REQ_MSG=["+ test_send + "]" );
//	      		return;
//      		}
//      
////			LUtil.println("SND", "RPY_MSG" + "=[" + SUtil.b2s(rpy_buf) + "] L:"+rpy_buf.length);		
//
//
//      		if (KSBankEncSocket.snd_socket(socket, rpy_buf))
//			{
//				LUtil.println("RPY_MSG" + "=[" + SUtil.b2s(rpy_buf) + "] L:"+rpy_buf.length);		
//			}			
//			else
//			{
//				LUtil.println("SND", "ERROR send_socket RPY_MSG" + "=[" + SUtil.b2s(rpy_buf) + "] L:"+rpy_buf.length);		
//			}
//
//
//
//
//
//
//
//
//
//
//      		//전용선 평문통신용
////			msg_len = 300;  /* 300bytes */
////
////			byte[] sbuf = SUtil.ConvS2B(send_msg, msg_len, "ksc5601");
////
////			LUtil.println("CLIENT->KSNET=[" + send_msg + "][L:" + sbuf.length + "] [" + ksnet_ip + ":"+ksnet_port+"]");
////
////			/* send to KSNET */
////			socket = new Socket();
////			socket.connect(new InetSocketAddress(ksnet_ip, ksnet_port),5*1000); /* connect timeout */
////			socket.setSoTimeout(sock_timeout*1000);	/* read timeout */
////
////			serv_out = new DataOutputStream(socket.getOutputStream());
////
////
////			serv_out.write(sbuf, 0, msg_len);
////			serv_out.flush();
////
////			/* receive from  KSNET */
////			serv_in = new DataInputStream(socket.getInputStream());
////
////
////			rest_len = msg_len;
////			read_len = 0;
////
////			byte[]  rbuf = new byte[msg_len];
////			while(true)
////			{
////				if((rtn_len = serv_in.read(rbuf, read_len, rest_len)) <= 0)
////				{
////					throw new IOException("Read(recv) error - (" + rtn_len + ")!!");
////				}
////
////				read_len = read_len + rtn_len;
////				rest_len = rest_len - rtn_len;
////
////				if( rest_len == 0 ) break;
////			}
////
////			recv_msg = SUtil.ConvB2S(rbuf, 0, msg_len, "ksc5601");
////
////			//			this.msg_info[0] = "0000";
////			//			this.msg_info[9] = recv_msg;
////
////			//			DUtil.Update_RecvData(this.msg_info);   /* update recv_msg  */
////
////			LUtil.println("KSNET->CLIENT=[" + recv_msg + "][L:"+rbuf.length+"]");
//
//		}catch(SocketTimeoutException e) {
//			//			this.msg_info[0] = "TIME";
//			//			DUtil.Update_RecvData(this.msg_info);
//			LUtil.println("ERROR:"+e.getMessage()+" ["+send_msg+"]");
//		}catch(ConnectException e) {
//			//			this.msg_info[0] = "CONE";
//			//			DUtil.Update_RecvData(this.msg_info);
//			LUtil.println("ERROR:"+e.getMessage()+" ["+send_msg+"]");
//		}catch (Exception e) {
//			//			this.msg_info[0] = "9999";
//			//			DUtil.Update_RecvData(this.msg_info);
//			LUtil.println("ERROR:"+e.getMessage()+" ["+send_msg+"]");
//		}finally {
//			try{if (serv_in != null) serv_in.close();}catch(Exception e){};
//			try{if (serv_out != null) serv_out.close();}catch(Exception e){};
//			try{if (socket != null) socket.close();}catch(Exception e){};
//		}
//	}
}
