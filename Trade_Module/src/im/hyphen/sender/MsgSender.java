package im.hyphen.sender;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import im.hyphen.msgVO.HyphenTask;
import im.hyphen.msgVO.HyphenTradeData;
import im.hyphen.util.*;

public class MsgSender extends Thread {

	public void run() {
		/*Thread sleep count*/
		int THREAD_CNT = Integer.parseInt(CUtil.get("THREAD_CNT"));

		//쓰레드풀 자원 관리 MAX = 5
		if (THREAD_CNT >= 5) {
			THREAD_CNT = 5;
		}

		ExecutorService executor = Executors.newFixedThreadPool(THREAD_CNT);;


		while (true) {
			HyphenTradeData[] htd = DUtil.Select_SendData();
			List<HyphenTask> list = new ArrayList<HyphenTask>();
			try {
				if (htd != null) {
					for (int i = 0; i < htd.length; i++) {
						list.add(new HyphenTask(htd[i]));
						htd[i].settCnt(i + 1);
					}
					executor.invokeAll(list, Integer.parseInt(CUtil.get("SND_TIMEOUT")), TimeUnit.SECONDS);

					/*DB Select Data initialize*/
					htd = null;

				} else /* db select not found */ {
					Thread.sleep(1000);
				}
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				LUtil.println("SND", e);
			}
		}
	}
}