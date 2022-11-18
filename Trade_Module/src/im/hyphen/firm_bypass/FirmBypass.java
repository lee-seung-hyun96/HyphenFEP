package im.hyphen.firm_bypass;

import im.hyphen.util.*;

import java.io.InputStream;

import im.hyphen.receiver.MsgReceiver;

public class FirmBypass{

	//input TestData
	final static int firmEGate = 1; 
	final static int firmDebitGate = 2;
	final static int firmVirtureGate = 3;
	final static int normal = 4;


	public static void main(String[] args) throws Exception {

		//		CUtil.setConfig(args[0]);
		String path = System.getProperty("user.dir") + "\\Trade_Module\\conf\\config.ini";
		System.out.println(path);
		CUtil.setConfig(path);
		
		long SLEEP_TIME = Long.parseLong(CUtil.get("SLEEP_TIME")); /* request sleep time */
		int THREAD_DELAY_TIME = 1000 / Integer.parseInt(CUtil.get("SEND_CNT_PER_SEC")); /* db select sleep time */

		LUtil.println("START[" + CUtil.get("SEND_CNT_PER_SEC") + "]");

		TimerDeleteLog DeleteLog = new TimerDeleteLog(Integer.parseInt(CUtil.get("LOG_SAVE_DAYS")), CUtil.get("LOG_PATH"));
		DeleteLog.start();
		new MsgReceiver().start();


			while (true) {
				String send_info[] = DUtil.Select_SendData();

				if (send_info[0].equals("0000")) {
					
					
					//testcall
					int testcnt = 2;
					
					switch(testcnt) {
					case firmEGate:
						//egate
						new FirmGateHandler(send_info).start();
						
						break;
					case firmDebitGate:
						//debit_gate
						InputStream rs_in = null;
						if ((send_info[2].equals("PRW") || send_info[2].equals("PRD")) && send_info[7].equals("0600601")) {
							CopyInputStream cis = new CopyInputStream(DUtil.bin_in);
							rs_in = cis.getCopy();
						}
						new DebitBinHandler(send_info, rs_in).start();
						
						break;
					case firmVirtureGate:
					
						break;
					case normal:
						
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
