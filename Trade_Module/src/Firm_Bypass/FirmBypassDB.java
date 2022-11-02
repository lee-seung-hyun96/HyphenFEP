package Firm_Bypass;


import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.sql.*;
import Util.*;

public class FirmBypassDB extends Thread
{
	private String msg_info[] = new String[10];

	public static void main(String[] args) throws Exception
	{
		CUtil.setConfig(args[0]);

		long 	SLEEP_TIME = Long.parseLong(CUtil.get("SLEEP_TIME"));						/* request sleep time */
		int 	THREAD_DELAY_TIME = 1000/Integer.parseInt(CUtil.get("SEND_CNT_PER_SEC"));	/* db select sleep time */

		LUtil.println("START["+CUtil.get("SEND_CNT_PER_SEC")+"]");

		new LogDeleteTimer().start();

		while(true)
		{
			String send_info[] = DUtil.Select_SendData();

			if (send_info[0].equals("0000"))
			{
				new FirmBypassDB(send_info).start();
				Thread.sleep(THREAD_DELAY_TIME);
			}
			else	/*  db select not found */
			{
				Thread.sleep(SLEEP_TIME);
			}
		}
	}

	public FirmBypassDB(String[] send_info)
	{
		System.arraycopy(send_info, 0, this.msg_info, 0, send_info.length);
	}

	public void run()
	{
		DataOutputStream    cli_out = null;
		DataInputStream     cli_in = null;
		DataOutputStream    serv_out = null;
		DataInputStream     serv_in = null;
		String              send_msg = null;
		String              recv_msg = null;
		int                timeout;
		int                 msg_len = 0;
		int                 len;
		Socket              socket = null;

		int     read_len = 0, rest_len = 0, rtn_len = 0;
//		if(CUtil.get("COMM_LINE_TYPE").equals("N")) {
		try {

			String ksnet_ip = CUtil.get("KSNET_IP").trim();
			int ksnet_port = Integer.parseInt(CUtil.get("KSNET_PORT"));
			int sock_timeout = Integer.parseInt(CUtil.get("SND_TIMEOUT"));

			this.msg_info[0] = "9999";
			this.msg_info[9] = " ";
			send_msg = this.msg_info[8];

			if (ksnet_ip == null)
			{
				throw new IOException("config undefined - (" + ksnet_ip + ")!!");
			}


			if (msg_info[2].equals("KEB"))
				msg_len = 2000;  /* KEB 2000bytes */
			else
				msg_len = 300;  /* 300bytes */

			byte[] sbuf = SUtil.ConvS2B(send_msg, msg_len, "ksc5601");

			LUtil.println("CLIENT->KSNET=[" + send_msg + "][L:" + sbuf.length + "] [" + ksnet_ip + ":"+ksnet_port+"]");

			/* send to KSNET */
			socket = new Socket();
			socket.connect(new InetSocketAddress(ksnet_ip, ksnet_port),5*1000); /* connect timeout */
			socket.setSoTimeout(sock_timeout*1000);	/* read timeout */

			serv_out = new DataOutputStream(socket.getOutputStream());

			/* send include msg_len */
			if (msg_info[2].equals("DBT"))
			{
				serv_out.write(String.format("%04d", msg_len).getBytes(), 0, 4);
				serv_out.flush();
			}

			serv_out.write(sbuf, 0, msg_len);
			serv_out.flush();

			/* receive from  KSNET */
			serv_in = new DataInputStream(socket.getInputStream());

			/* recv include msg_len */
			if (msg_info[2].equals("DBT"))
			{
				byte[]  tbuf = new byte[4];
				if((rtn_len = serv_in.read(tbuf, 0, 4)) <= 0)
				{
					throw new IOException("Read(recv) error - (" + rtn_len + ")!!");
				}   

				rest_len = Integer.parseInt(SUtil.toHanX(tbuf, 0, rtn_len));
			}   
			else
			{
				rest_len = msg_len;
			}

			read_len = 0;

			byte[]  rbuf = new byte[msg_len];
			while(true)
			{
				if((rtn_len = serv_in.read(rbuf, read_len, rest_len)) <= 0)
				{
					throw new IOException("Read(recv) error - (" + rtn_len + ")!!");
				}

				read_len = read_len + rtn_len;
				rest_len = rest_len - rtn_len;

				if( rest_len == 0 ) break;
			}

			recv_msg = SUtil.ConvB2S(rbuf, 0, msg_len, "ksc5601");

			this.msg_info[0] = "0000";
			this.msg_info[9] = recv_msg;

			DUtil.Update_RecvData(this.msg_info);   /* update recv_msg  */

			LUtil.println("KSNET->CLIENT=[" + recv_msg + "][L:"+rbuf.length+"]");

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




class LogDeleteTimer extends Thread
{
	public void run()
	{
		try {
			while(true)
			{
				deleteLogFile();
				Thread.sleep(1000*60);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String SS_LOG_HOME  = null;
	static int    SI_LOG_SAVE_DAY = 0;

	private void deleteLogFile()
	{
		String fnm = "deleteLogFile";

		long cmillis = System.currentTimeMillis();

		if (null == SS_LOG_HOME)
		{

			SS_LOG_HOME = CUtil.get("LOG_PATH");
			SI_LOG_SAVE_DAY = Integer.parseInt(CUtil.get("LOG_SAVE_DAYS"));


			if (null == SS_LOG_HOME)
			{
				LUtil.println(fnm + " ERROR : LOG_DIR_PATH!!");
				return;
			}
		}

		File dir = new File(SS_LOG_HOME);
		if (dir == null || !dir.isDirectory())
		{
			LUtil.println(fnm + " ERROR : LOG_DIR_PATH!!");
			return;
		}

		deleteOldFiles(dir, (cmillis-(86400000L*SI_LOG_SAVE_DAY)), true, false);
	}

	private void deleteOldFiles(File dir, long lastModified, boolean deleteSub, boolean deleteSubDir)
	{
		File[] fs = dir.listFiles();
		for(int i=0; i<fs.length; i++)
		{
			if (deleteSub && fs[i].isDirectory()) deleteOldFiles(fs[i], lastModified, true, deleteSubDir);
			if (fs[i].lastModified() < lastModified)
			{
				if (!fs[i].isDirectory() || deleteSubDir)
				{
					boolean rtn = fs[i].delete();
					//LUtil.println("====deleteOldFiles==== [" + rtn + ":"+fs[i].getName()+"]!!");;
				}
			}
		}
	}
}