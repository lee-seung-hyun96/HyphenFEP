package Firm_Bypass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;

import Crypto.KSBankEncSocket;
import Util.*;

public class FirmBypass{

	//input TestData
	final static int firmEGate = 1; 
	final static int firmDebitGate = 2;
	final static int firmVirtureGate = 3;
	final static int nomal = 4;
	
	public static void main(String[] args) throws Exception {
		
		//		CUtil.setConfig(args[0]);
		String path = System.getProperty("user.dir") + "\\conf\\config.ini";
		CUtil.setConfig(path);
		
		long SLEEP_TIME = Long.parseLong(CUtil.get("SLEEP_TIME")); /* request sleep time */
		int THREAD_DELAY_TIME = 1000 / Integer.parseInt(CUtil.get("SEND_CNT_PER_SEC")); /* db select sleep time */

		LUtil.println("START[" + CUtil.get("SEND_CNT_PER_SEC") + "]");

		new LogDeleteTimer().start();
			while (true) {
				String send_info[] = DUtil.Select_SendData();

				if (send_info[0].equals("0000")) {
					//testcall
					int testcnt = 1;
					
					switch(testcnt) {
					case firmEGate:
						//egate
						new FirmGateHandler(send_info).start();;
						
						break;
					case firmDebitGate:
						//debit_gate
						new DebitBinHandler(send_info).start();
						
						break;
					case firmVirtureGate:
					
						break;
					case nomal:
						
						break;
					
					default:
						break;
					}
					Thread.sleep(THREAD_DELAY_TIME);
				} else /* db select not found */
				{
					Thread.sleep(SLEEP_TIME);
				}
			}
		
		//connect socket

	}
}
