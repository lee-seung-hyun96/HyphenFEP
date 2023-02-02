package im.hyphen.sender;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import im.hyphen.msgVO.HyphenTask;
import im.hyphen.msgVO.HyphenTradeData;
import im.hyphen.util.CUtil;
import im.hyphen.util.DUtil;

public class MsgSender extends Thread {

	//input TestData

	final static String normal = "";
	final int MAXLEN = 1000;
	//요청 객체
//	HyphenTradeData htd_tmp = new HyphenTradeData();

	HyphenTradeData[] ehtd = new HyphenTradeData[MAXLEN];
	HyphenTradeData[] debithtd = new HyphenTradeData[MAXLEN];


	Future future;
	public void run() {
		//		long SLEEP_TIME = Long.parseLong(CUtil.get("SLEEP_TIME")); /* request sleep time */
		int THREAD_CNT = Integer.parseInt(CUtil.get("THREAD_CNT"));

		//쓰레드풀 자원 관리 MAX = 5
		if(THREAD_CNT > 5) {
			THREAD_CNT = 5;
		}

		ExecutorService executor = Executors.newFixedThreadPool(THREAD_CNT);


				while (true) {
					HyphenTradeData[] htd = DUtil.Select_SendData();
					List<HyphenTask> list = new ArrayList<HyphenTask>();
					try {
						if (htd != null) {
							System.out.println("length = "+ htd.length);
							for (int i = 0; i < htd.length; i++) {
								list.add(new HyphenTask(htd[i]));
								htd[i].settCnt(i+1);
							}
							   List<Future<Integer>> futureList = executor.invokeAll(list, Integer.parseInt(CUtil.get("SND_TIMEOUT")), TimeUnit.SECONDS);
							   
							/*DB Select Data initialize*/
							htd = null;

						} else /* db select not found */
						{
							System.out.println("DB Select is null"); 
							//    					Thread.sleep(5000);
							Thread.sleep(1000);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	}


}
//	public void ParseHTD(HyphenTradeData[] htd) {
//
//		System.arraycopy(ehtd, NORM_PRIORITY, debithtd, MIN_PRIORITY, MAX_PRIORITY);
//		int eCnt = 0;
//		int debitCnt = 0;
//
//		for (int i = 0; i < htd.length; i++) {
//			String svcType = SvcCode(htd[i].getSvcType());
//			if(svcType.equals(firmEGate)) {
//				ehtd[eCnt] = htd[i];
//				eCnt++;
//			}else {
//				debithtd[debitCnt] = htd[i];
//				debitCnt++;
//			}
//		}
//		ehtd = Arrays.copyOfRange(ehtd, 0, eCnt);
//		debithtd = Arrays.copyOfRange(debithtd, 0, debitCnt);
//	}
