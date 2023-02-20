package im.hyphen.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.Calendar;

public class LUtil
{
	private static final Object lock = new Object();
	static String LOG_DIR = null;
	private static synchronized boolean init()
	{
		if (null == LOG_FILE || CUtil.isNew())
		{
			LOG_DIR = CUtil.get("LOG_PATH");
			if (null == LOG_DIR) return false;

			File log_dir = new File(LOG_DIR);
			if (!log_dir.exists()) log_dir.mkdirs();
		}
		return true;
	}

	static Calendar LOG_CAL     = Calendar.getInstance();
	static String LOG_DT        = null;
	static String LOG_FILE      = null;
	static String  LOG_PREFIX   = null;
	
	private static synchronized void day_check(String prefix, String curr_dt8)
	{
		if (LOG_DT == null || LOG_PREFIX == null || !LOG_DT.equals(curr_dt8) || !LOG_PREFIX.equals(prefix))
		{
			File log_dir_file = new File(LOG_DIR);
			if (!log_dir_file.exists())log_dir_file.mkdirs();

			StringBuffer sb = new StringBuffer();

			sb.append(LOG_DIR);
			if (!LOG_DIR.endsWith("/") && !LOG_DIR.endsWith("\\")) sb.append("/");
			sb.append(curr_dt8);
			sb.append("_");
			sb.append(prefix);
			sb.append(".log");

			LOG_FILE = sb.toString();

			LOG_DT = curr_dt8;
			LOG_PREFIX = prefix;
		}
	}

	
	static PrintStream out = null;
	public static void println(String prefix, Object pstr)
	{
		if (!init())
		{
			System.out.println("LOG_DIR_PATH Setting Error");
			return;
		}


		String curr_date = SUtil.getCurrDate();

		day_check(prefix, curr_date.substring(0,8));
		RandomAccessFile file = null;

		synchronized (lock) {
			try {
				file = new RandomAccessFile(LOG_FILE, "rw");
				File openFile = new File(LOG_FILE);
				if (openFile.exists()) {
					file.writeInt(10);
					out = new PrintStream(new FileOutputStream(LOG_FILE, true), true);
				} else {
					file.writeInt(20);
					out = new PrintStream(new FileOutputStream(LOG_FILE), true);
				}


				if (pstr instanceof Throwable) {
					Throwable tw = (Throwable) pstr;
					tw.printStackTrace(out);
					out.println();
				} else {
					StringBuffer sb = new StringBuffer();

					sb.append("[");
					sb.append(curr_date.substring(8, 10)).append(":").append(curr_date.substring(10, 12)).append(":").append(curr_date.substring(12, 14));
					sb.append("]");
					sb.append(pstr);
					out.println(sb.toString());

				}
			} catch (Exception e) {
				e.printStackTrace();

			} finally {

				try {if (out != null) out.close();} catch (Exception e) {}
				;

			}
		}
	}
	
	
}
