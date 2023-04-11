package im.hyphen.firm_bypass;

import im.hyphen.util.*;
import im.hyphen.receiver.MsgReceiver;
import im.hyphen.sender.MsgSender;

public class FirmBypass{


	public static void main(String[] args) throws Exception {
		//		CUtil.setConfig(args[0]);
		String config_path = System.getProperty("user.dir") + "\\conf\\config.ini";
		System.out.println( System.getProperty("user.dir"));
		String system_path = System.getProperty("user.dir") + "\\conf\\system.ini";
//		String path = "/home/firmbk/kst/Trade_Module/Trade_Module/conf/config.ini";
//		System.out.println(path);
		CUtil.setConfig(config_path);
		IUtil.setConfig(system_path);

//		LUtil.println("START[" + CUtil.get("SEND_CNT_PER_SEC") + "]");
		LUtil.println("SND", "START[" + CUtil.get("SEND_CNT_PER_SEC") + "]");

		TimerDeleteLog DeleteLog = new TimerDeleteLog(Integer.parseInt(CUtil.get("LOG_SAVE_DAYS")), CUtil.get("LOG_PATH"));
		DeleteLog.start();
		
		new MsgSender().start();
		MsgReceiver msgReceiver = new MsgReceiver();
		msgReceiver.start();
		
		
	}
}
