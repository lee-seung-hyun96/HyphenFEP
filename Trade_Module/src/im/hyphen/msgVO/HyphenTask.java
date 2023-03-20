package im.hyphen.msgVO;

import java.util.concurrent.Callable;

import im.hyphen.firm_bypass.DebitBinHandler;
import im.hyphen.firm_bypass.FirmGateHandler;
import im.hyphen.util.CUtil;
import im.hyphen.util.LUtil;

public class HyphenTask implements Callable <Integer> {
	
	int cnt;
	int totalCnt;
	HyphenTradeData[] htd;
	HyphenTradeData htd_tmp;
	
	final static String firmEGate = "firmEGate"; 
	final static String firmDebitGate = "firmDebitGate";
	final static String normal = "";
	int THREAD_DELAY_TIME = 1000 / Integer.parseInt(CUtil.get("SEND_CNT_PER_SEC")); /* db select sleep time */

	public HyphenTask(HyphenTradeData htd_tmp) {
		this.htd_tmp = htd_tmp;
	}
	
	public Integer call() throws Exception{
		
		String svcType = SvcCode(htd_tmp.getSvcType(), htd_tmp.getMsgCode());
		switch(svcType) {
		case firmEGate:
			String threadName = Thread.currentThread().getName();
			new FirmGateHandler(htd_tmp, threadName).run();
			break;
		case firmDebitGate:
			new DebitBinHandler(htd_tmp).start();
			break;
		default:
			break;
		}
		
		Thread.sleep(THREAD_DELAY_TIME);

		return cnt;
		
	}
	
	public String SvcCode(String svc, String msgcode) {
		String lineType = CUtil.get("COMM_LINE_TYPE");
		if(svc.equals("PRW")||svc.equals("PRD")|| msgcode.equals("0600601")||(svc.equals("DBT") && (lineType.equals("I")||lineType.equals("i")))) {
			return "firmDebitGate";
		}else {
			return "firmEGate";
		}
	}

}
