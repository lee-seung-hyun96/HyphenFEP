package im.hyphen.sender;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import im.hyphen.firm_bypass.DebitBinHandler;
import im.hyphen.firm_bypass.FirmGateHandler;
import im.hyphen.msgVO.HyphenTradeData;
import im.hyphen.util.CUtil;
import im.hyphen.util.DUtil;
import im.hyphen.util.LUtil;

public class MsgSender extends Thread {

	final static String firmEGate = "firmEGate"; 
	final static String firmDebitGate = "firmDebitGate";
	final static String firmVirtureGate = "firmVirtureGate";
	final static String normal = "";
	static int htdCnt = 0;

	final static int MaxLen = 100; /* db select maxlen*/
	HyphenTradeData htd_tmp = new HyphenTradeData();
	public void run() {
		long SLEEP_TIME = Long.parseLong(CUtil.get("SLEEP_TIME")); /* request sleep time */
		int THREAD_DELAY_TIME = 1000 / Integer.parseInt(CUtil.get("SEND_CNT_PER_SEC")); /* db select sleep time */
		
		//ThreadPool max = 3
		ExecutorService executor = Executors.newFixedThreadPool(3);

		Runnable egateRunnable = new Runnable() {
			@Override
			public void run() {
				new FirmGateHandler(htd_tmp).run();
			}};
			Runnable debitRunnable = new Runnable() {
				@Override
				public void run() {
					try {
						new DebitBinHandler(htd_tmp).start();
					} catch (Exception e) {e.printStackTrace();}
				}};

				while (true) {
					HyphenTradeData[] htd = DUtil.Select_SendData();
					try {
						if (htd != null) {
							for (int i = 0; i < htd.length; i++) {
								String svcType = getSvcCode(htd[i].getSvcType());

								switch(svcType) {
								case firmEGate:
									htd_tmp = htd[i];
									executor.execute(egateRunnable);
									break;
								case firmDebitGate:
									htd_tmp = htd[i];
									executor.execute(debitRunnable);
									break;
								case normal:

									break;
								default:

									break;
								}
								Thread.sleep(THREAD_DELAY_TIME);
							}
							
							/*DB Select Data initialize*/
							htd = null;

						} else /* db select not found */
						{
							System.out.println("htd is null"); 
							Thread.sleep(SLEEP_TIME);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	}

	public String getSvcCode(String svc) {
		if(svc.equals("PRW")||svc.equals("PRD")) {
			return "firmDebitGate";
		}else {
			return "firmEGate";
		}
	}

}
