package im.hyphen.firm_bypass;

import im.hyphen.util.*;
import im.hyphen.receiver.MsgReceiver;
import im.hyphen.sender.MsgSender;

public class FirmBypass{
//...........................

	public static void main(String[] args) throws Exception {
		//		CUtil.setConfig(args[0]); 
		String path = System.getProperty("user.dir") + "\\conf\\config.ini";
//		String path = System.getProperty("user.dir") + "\\Trade_Module\\conf\\config.ini";
		System.out.println(path);
		CUtil.setConfig(path);

		LUtil.println("START[" + CUtil.get("SEND_CNT_PER_SEC") + "]");

		TimerDeleteLog DeleteLog = new TimerDeleteLog(Integer.parseInt(CUtil.get("LOG_SAVE_DAYS")), CUtil.get("LOG_PATH"));
		DeleteLog.start();

		new MsgSender().start();
		new MsgReceiver().start();
		
		
	}
}
