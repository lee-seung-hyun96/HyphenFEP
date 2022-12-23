package im.hyphen.firm_bypass;


import java.io.*;
import java.net.*;

import im.hyphen.crypto.HyphenEncSocket;
import im.hyphen.msgVO.HyphenTradeData;
import im.hyphen.util.*;

public class FirmGateHandler extends Thread
{
	Socket socket = null;
	HyphenTradeData htd = null;
	public FirmGateHandler(HyphenTradeData htd)
	{	
		this.htd = htd;
	}

	public void run()
	{
		DataOutputStream    serv_out = null;
		DataInputStream     serv_in = null;
		String              send_msg = null;
		String              recv_msg = null;
		int                 msg_len = 0, rtn_len = 0, rest_len = 0, read_len = 0;
		byte[]		    sbuf = null;
		try {
			this.htd.setErrCode("9999");
                        send_msg = htd.getSendMsg();
                        
                        String lineType = CUtil.get("COMM_LINE_TYPE");
                        int sock_timeout = Integer.parseInt(CUtil.get("SND_TIMEOUT"));

                        if (htd.getSvcType().equals("KEB"))
                                msg_len = 2000;  /* KEB 2000bytes */
                        else
                                msg_len = 300;  /* 300bytes */

                        sbuf = SUtil.ConvS2B(send_msg, msg_len, "ksc5601");

                        if(lineType.equals("N")){

                        String ksnet_ip = CUtil.get("KSNET_IP").trim();
                        int ksnet_port = Integer.parseInt(CUtil.get("KSNET_PORT"));
                        
                        socket = HyphenEncSocket.connect_socket(ksnet_ip, ksnet_port);
                        if(socket == null) {
                                LUtil.println("CLIENT->KSNET=[Socket Connect Error]");
                        }
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
                                if(send_msg.contains("5000002022")) {
                                	System.out.println("Debug IN1");
                                }
                                
                                
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
//                                LUtil.println("KSNET->CLIENT=[" + SUtil.b2s(rpy_buf)  + "][L:"+rpy_buf.length+"]");
                                LUtil.println("SND", "KSNET->CLIENT=[" + SUtil.b2s(rpy_buf) + "] L:"+rpy_buf.length);
                                if(SUtil.b2s(rpy_buf).contains("5000002022")) {
                                	System.out.println("Debug IN2");
                                }
                        }
                        else
                        {
                                LUtil.println("SND", "ERROR send_socket RPY_MSG" + "=[" + SUtil.b2s(rpy_buf) + "] L:"+rpy_buf.length);
                        }



                        } else {
                        //Nogate
                        String svc_ip = "127.0.0.1";
                        int svc_port = SUtil.GetSvcPort(sbuf);                  

                        socket = new Socket();
                        socket = HyphenEncSocket.connect_socket(svc_ip, svc_port);      

                        if(socket == null) {
                                LUtil.println("CLIENT->KSNET=[Socket Connect Error]");
                        }

                        serv_out = new DataOutputStream(socket.getOutputStream());
                        serv_out.write(sbuf,0,msg_len);
                        serv_out.flush();
                        LUtil.println("CLIENT->KSNET=[" + send_msg + "][L:"+send_msg.length()+"]");
                        serv_in = new DataInputStream(socket.getInputStream());
                        rest_len = msg_len;
                        byte[] rbuf = new byte[msg_len];
			while(true){

				if((rtn_len = serv_in.read(rbuf,0,rest_len)) <= 0) {
					throw new IOException("Read(recv) error - {"+ rtn_len + ")!!");
				}
				read_len = read_len + rtn_len;
				rest_len = rest_len - rtn_len;
				if(rest_len == 0) {break;}
			}	
			recv_msg = SUtil.ConvB2S(rbuf,0,msg_len, "ksc5601");
			LUtil.println("KSNET->CLIENT=[" + recv_msg + "][L:" + rbuf.length+"]");
			}
			this.htd.setErrCode("0000");
			this.htd.setRecvMsg(recv_msg);

			DUtil.Update_RecvData(this.htd);   /* update recv_msg  */

		}catch(SocketTimeoutException e) {
			this.htd.setErrCode("TIME");
			DUtil.Update_RecvData(this.htd);
			LUtil.println("ERROR:"+e.getMessage()+" ["+send_msg+"]");
		}catch(ConnectException e) {
			this.htd.setErrCode("CONE"); 
			DUtil.Update_RecvData(this.htd);
			LUtil.println("ERROR:"+e.getMessage()+" ["+send_msg+"]");
		}catch (Exception e) {
			this.htd.setErrCode("9999");
			DUtil.Update_RecvData(this.htd);
			LUtil.println("ERROR:"+e.getMessage()+" ["+send_msg+"]");
		}finally {
			try{if (serv_in != null) serv_in.close();}catch(Exception e){};
			try{if (serv_out != null) serv_out.close();}catch(Exception e){};
			try{if (socket != null) socket.close();}catch(Exception e){};
		}
	}
}
