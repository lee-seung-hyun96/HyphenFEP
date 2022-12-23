package im.hyphen.util;

import java.io.File;

public class TimerDeleteLog extends Thread
{
	private static int days = 0; 
	private String SS_LOG_HOME  = null;

	public void run()
	{
		try {
			while(true)
			{
				deleteLogFile(days);
				Thread.sleep(1000*3600);
			}
																																	   			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TimerDeleteLog (int del_days, String log_home) 
	{
		this.days = del_days;				
		this.SS_LOG_HOME = log_home;				
	}

	private void deleteLogFile(int days)
	{
		String fnm = "deleteLogFile";

		try {

			long cmillis = System.currentTimeMillis();


			if (null == SS_LOG_HOME)
			{
				if (null == SS_LOG_HOME)
				{
					LUtil.println(fnm + " ERROR : "+SS_LOG_HOME);
					return;
				}
			}
	
			File dir = new File(SS_LOG_HOME);
			if (dir == null || !dir.isDirectory())
			{
				LUtil.println(fnm + " ERROR : "+SS_LOG_HOME);
				return;
			}
			/* Log 7day*/
			//deleteOldFiles(dir, (cmillis-604800000L), true, false);
			deleteOldFiles(dir, (cmillis-(86400000*days)), true, false);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
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
					//LUtil.println("====deleteOldFiles==== [" + rtn + ":"+fs[i].getName()+"]!!");
				}
			}
		}
	}
}