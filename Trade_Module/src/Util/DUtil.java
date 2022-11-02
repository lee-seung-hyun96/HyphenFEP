package Util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;


public class DUtil
{
	static String DRIVER_NAME   = null;
	static String DB_URL        = null;
	static String USER_NAME     = "";
	static String PASSWORD      = "";
	static String TABLE_NAME    = "";
	public static String VrUpdateType = null;
	public static Connection getConnection()
	{
		if (null == DRIVER_NAME || CUtil.isNew())
		{
			DRIVER_NAME = CUtil.get("JDBC_DRIVER"   );
			DB_URL      = CUtil.get("JDBC_URL"      );
			USER_NAME   = CUtil.get("JDBC_USER"     );
			PASSWORD    = CUtil.get("JDBC_PASSWORD" );

			if (USER_NAME   == null) USER_NAME  = "";
			if (PASSWORD    == null) PASSWORD   = "";
		}

		try{
			Class.forName (DRIVER_NAME);
			if (USER_NAME == null || USER_NAME.length() == 0)
				return DriverManager.getConnection(DB_URL);
			else
				return DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
		}catch(Exception e)
		{
			LUtil.println(e);
		}
		return null;
	}

	public static boolean Update_RecvData(String[] msg_info)
	{
		Connection          con = null;
		PreparedStatement   pstmt = null;
		String recv_flag =  " ";
		String RecvDate     = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
		TABLE_NAME          = CUtil.get("JDBC_TABLENAME");

		if (msg_info[0].equals("0000")) recv_flag = "Y";
		else if (msg_info[0].equals("CONE")) recv_flag = "C";  // connect error 
		else if (msg_info[0].equals("TIME")) recv_flag = "T";  // response timeout
		else recv_flag = "F"; 								

		String QRY = "UPDATE "+TABLE_NAME+" SET RECV_FLAG = ?, RECV_DATE = ?, RECV_TIME = ?, RECV_MSG = ? WHERE REQ_DATE = ? AND SVC_TYPE = ? AND BANK_CODE = ? AND COMP_CODE = ? AND SEQ_NO = ? AND MSG_CODE = ? AND SEND_FLAG = 'Y' AND RECV_FLAG = 'N' ";

		int cnt = 0;
		try {
			con = getConnection();
			con.setAutoCommit(false);

			pstmt = con.prepareStatement(QRY);

			pstmt.setString (1, recv_flag       );
			pstmt.setString (2, RecvDate.substring(0,8) );
			pstmt.setString (3, RecvDate.substring(8,14) );
			pstmt.setString (4, msg_info[9]     );
			pstmt.setString (5, msg_info[1]     );
			pstmt.setString (6, msg_info[2]     );
			pstmt.setString (7, msg_info[4]     );
			pstmt.setString (8, msg_info[5]     );
			pstmt.setString (9, msg_info[6]     );
			pstmt.setString (10, msg_info[7]    );

			cnt = pstmt.executeUpdate();

			con.commit();
			return true;
		} catch(Throwable t) {
			t.printStackTrace();
		} finally {
			try {if(pstmt!=null){pstmt.close();pstmt= null;}}catch(Exception e){}
			try {if(con !=null) {con.close( );}}catch(Exception e){}
		}
		return false;
	}

	public static String[] Select_SendData ()
	{
		Connection          con     = null;
		PreparedStatement   pstmt   = null;
		ResultSet           rs      = null;
		int                 cnt     = 0;
		String send_info[]  = {"9999", " ", " ", " ", " ", " ", " ", " ", " "} ;
		String RequestDate  = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
		TABLE_NAME          = CUtil.get("JDBC_TABLENAME");

		/* db select */
		String QRY = "SELECT REQ_DATE, SVC_TYPE, BANK_CODE, COMP_CODE, SEQ_NO, MSG_CODE, SEND_MSG FROM " + TABLE_NAME + " WHERE REQ_DATE = ? AND SEND_FLAG = 'N' ORDER BY REQ_DATE, REQ_TIME";

		try {
			con = getConnection();
			con.setAutoCommit(false);

			pstmt = con.prepareStatement(QRY);

			pstmt.setString (1, RequestDate.substring(0,8) );

			rs = pstmt.executeQuery();

			while (rs.next()) {
				cnt++;
				send_info[0] = "0000";              
				send_info[1] = rs.getString(1);     /* request date */
				send_info[2] = rs.getString(2);     /* svc_type */
				send_info[3] = "0300";     			/* msg_len */
				send_info[4] = rs.getString(3);     /* bank_code */
				send_info[5] = rs.getString(4);     /* comp_code */
				send_info[6] = rs.getString(5);     /* seq_numb */
				send_info[7] = rs.getString(6);     /* msg_code */
				send_info[8] = rs.getString(7);     /* send_msg */

				if (cnt >= 1) break;  
			}

			if (cnt < 1)
			{
				send_info[0] = "1404";  /* not found */
			}
			else
			{
				pstmt.close(); pstmt= null;

				QRY = "UPDATE " + TABLE_NAME + " SET SEND_FLAG = 'Y', SEND_DATE = ?, SEND_TIME = ? WHERE REQ_DATE = ? AND SVC_TYPE = ? AND BANK_CODE = ? AND COMP_CODE = ? AND SEQ_NO= ? AND MSG_CODE = ? ";

				pstmt = con.prepareStatement(QRY);

				pstmt.setString (1, RequestDate.substring(0,8) );
				pstmt.setString (2, RequestDate.substring(8,14) );
				pstmt.setString (3, send_info[1] );
				pstmt.setString (4, send_info[2] );
				pstmt.setString (5, send_info[4] );
				pstmt.setString (6, send_info[5] );
				pstmt.setString (7, send_info[6] );
				pstmt.setString (8, send_info[7] );

				cnt  = pstmt.executeUpdate();
				con.commit();
			}
		}catch(Throwable e) {
			send_info[0] = "9999";
			LUtil.println(e);
		} finally {
			try {if(pstmt!=null){pstmt.close();pstmt= null;}}catch(Exception e){}
			try {if(con !=null) {con.close( );}}catch(Exception e){}
			try {if(rs !=null) {rs.close( ); rs = null;}}catch(Exception e){}
		}
		return send_info;
	}
	
	public static HashMap<String,String> select0900_100 (HashMap<String,String> dHash)
	{
		Connection          con     = null;
		PreparedStatement   pstmt   = null;
		ResultSet rs = null;

		int cnt = 0;
		String SEL_QRY = "";

		String 	corp_name = "";
		String 	final_date = "";
		String 	chkFlag = "";
		String 	db_amt = "";
		double 	d_db_amt = 0;
		double 	d_amt = 0;
		
		String  send_date  	= dHash.get("send_date");
		String  bank_code		= dHash.get("bank_code");
		String  vr_acct_no 	= dHash.get("vr_acct_no");
		String  amt         = dHash.get("amt");
		
		HashMap<String,String> rHash = new HashMap<String,String>();
		
		rHash.put("error_code", "L099");

		/* check account */
		SEL_QRY = "SELECT CORP_NAME, AMT, FINAL_DATE FROM KSNET_VR_ACCOUNT WHERE BANK_CODE = ? AND VR_ACCT_NO = ? AND USE_FLAG = 'Y' ";
		try {
			con = getConnection();
			con.setAutoCommit(false);

			pstmt = con.prepareStatement(SEL_QRY);

			LUtil.println("DEBUG 0900/100 search BANKCODE[" + bank_code + "], VR_ACC[" + vr_acct_no + "], AMT[" + amt + "], SEND_DATE[" + send_date + "]");

			d_amt = Long.parseLong(amt);
			
			pstmt.setString (1, bank_code     );
			pstmt.setString (2, vr_acct_no     );

			rs = pstmt.executeQuery();

			while (rs.next()) {
				cnt++;
				corp_name = rs.getString(1);
				db_amt = rs.getString(2);
				d_db_amt = Long.parseLong(db_amt);
				final_date = rs.getString(3);

				break;
			}
			
			if(final_date.equals(""))
			{
				final_date = "99991231";
			}
			
			if (cnt < 1)
			{
				LUtil.println( "ERROR 0900/100 Not found information  bank_code("+bank_code+") account_no("+vr_acct_no+")");
				rHash.put("error_code", "L008");   /* not found account */
			}
			else if (d_db_amt > 0 && d_amt != d_db_amt)
			{
				LUtil.println( "ERROR 0900/100 mismatch amt!  bank_code("+bank_code+") account_no("+vr_acct_no+") amount("+amt+") db_amount("+db_amt+")");
				rHash.put("error_code", "L002");

			}
			else if (Integer.parseInt(final_date) < Integer.parseInt(send_date))
			{
				LUtil.println( "ERROR 0900/100 final date!  bank_code("+bank_code+") account_no("+vr_acct_no+") final_date("+final_date+")");
				rHash.put("error_code", "L004");  
			}
			else 
			{
				rHash.put("error_code", "0000");  
				rHash.put("corp_name", corp_name);  
			}
	
		}catch(Throwable e) {
			LUtil.println(e);
		} finally {
			try {if(pstmt!=null){pstmt.close();pstmt= null;}}catch(Exception e){}
			try {if(con !=null) {con.close( );/*con = null;*/}}catch(Exception e){}
			try {if(rs !=null) {rs.close( ); rs = null;}}catch(Exception e){}
		}
		
		return rHash;
	}

	
	public static boolean insert0200_300(String tran_code,String comp_code,String bank_code,String mess_code,String mess_diff,String tran_cnt,String seq_no,String tran_date,String tran_time,String stan_resp_code,String bank_resp_code,String inqu_date,String inqu_no,String bank_seq_no,String bank_code_3,String filler_1,String corp_acct_no,String comp_cnt,String deal_sele,String in_bank_code,String total_amt,String balance,String bran_code,String cust_name,String check_no,String cash,String out_bank_check,String etc_check,String vr_acct_no,String deal_date,String deal_time,String serial_no,String in_bank_code_3,String bran_code_3,String filler_2)
	{
		Connection        con   = null;
		PreparedStatement pstmt = null;

		String  INS2_QRY = "INSERT INTO KSNET_TRADE_DATA (tran_code,comp_code,bank_code,mess_code,mess_diff, tran_cnt,seq_no,tran_date,tran_time,stan_resp_code,bank_resp_code,inqu_date,inqu_no,bank_seq_no,filler_1,corp_acct_no,comp_cnt,deal_sele,in_bank_code,total_amt,balance,bran_code,cust_name,check_no,cash,out_bank_check, etc_check,vr_acct_no,deal_date,deal_time,serial_no,filler_2) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String  INS3_QRY = "UPDATE KSNET_VR_ACCOUNT SET USE_FLAG = 'N' WHERE BANK_CODE = ? AND VR_ACCT_NO = ? AND AMT = ? AND USE_FLAG = 'Y'";
		VrUpdateType = CUtil.get("AUTO_VR_UPDATE");
		try {
			con = getConnection();
			con.setAutoCommit(false);

			pstmt = con.prepareStatement(INS2_QRY);

			pstmt.setString (1,  tran_code     );
			pstmt.setString (2,  comp_code     );
			pstmt.setString (3,  bank_code_3   );
			pstmt.setString (4,  mess_code     );
			pstmt.setString (5,  mess_diff     );
			pstmt.setString (6,  tran_cnt      );
			pstmt.setString (7,  seq_no        );
			pstmt.setString (8,  tran_date     );
			pstmt.setString (9,  tran_time     );
			pstmt.setString (10, stan_resp_code);
			pstmt.setString (11, bank_resp_code);
			pstmt.setString (12, inqu_date     );
			pstmt.setString (13, inqu_no       );
			pstmt.setString (14, bank_seq_no   );
			pstmt.setString (15, filler_1      );
			pstmt.setString (16, corp_acct_no   );
			pstmt.setString (17, comp_cnt      );
			pstmt.setString (18, deal_sele     );
			pstmt.setString (19, in_bank_code_3);
			pstmt.setString (20, total_amt     );
			pstmt.setString (21, balance       );
			pstmt.setString (22, bran_code_3   );
			pstmt.setString (23, cust_name     );
			pstmt.setString (24, check_no      );
			pstmt.setString (25, cash          );
			pstmt.setString (26, out_bank_check);
			pstmt.setString (27, etc_check     );
			pstmt.setString (28, vr_acct_no     );
			pstmt.setString (29, deal_date     );
			pstmt.setString (30, deal_time     );
			pstmt.setString (31, serial_no     );
			pstmt.setString (32, filler_2      );
			
			pstmt.executeUpdate();
			
			if (VrUpdateType.equals("Y") && Long.parseLong(total_amt) > 0)
			{
				pstmt = con.prepareStatement(INS3_QRY);	
				pstmt.setString (1, bank_code_3   );
				pstmt.setString (2, vr_acct_no     );
				pstmt.setString (3, total_amt     );
				pstmt.executeUpdate();
			}
			

			con.commit();
			return true;

		}catch(Throwable e) {
			int sql_code = 0;
			if (e instanceof SQLException)  /* case 1: ORA-00001 */
			{
				sql_code = ((SQLException)e).getErrorCode();
			}
			try{con.rollback();}catch(SQLException se){}

			if(sql_code == 1){
				LUtil.println("Oracle duplicate keys");
				return true;
			}
			else if (sql_code == -239){
				LUtil.println("Informix duplicate keys");
				return true;
			}
			else if (sql_code == 2627 || sql_code == 2601){
				LUtil.println("MSSQL duplicate keys");
				return true;
			}
			else if (sql_code == 1062){
				LUtil.println("MYSQL duplicate keys");
				return true;
			}
			else if (sql_code == -803){
				LUtil.println("DB2 duplicate keys");
				return true;
			}

			LUtil.println(e);

		} finally {
			try {if(pstmt != null){pstmt.close();pstmt = null;}}catch(Exception e){}
			try {if(con != null) {con.close( );/*con = null;*/}}catch(Exception e){}
		}
		return false;
	}
	public static boolean insert0400_100(String tran_code, String comp_code, String bank_code, String mess_code, String mess_diff, String tran_cnt, String seq_no, String tran_date, String tran_time, String stan_resp_code, String bank_resp_code, String inqu_date, String inqu_no, String bank_seq_no, String bank_code_3, String filler_1, String org_seq_no, String out_account_no, String in_account_no, String in_money, String in_bank_code, String nor_money, String abnor_money, String div_proc_cnt, String div_proc_no, String ta_no, String not_in_amt, String err_code, String in_bank_code_3, String filler_2)
	{
		Connection          con     = null;
		PreparedStatement   pstmt   = null;

		String  INS4_QRY = "INSERT INTO KSNET_TRADE_ERR (tran_code, comp_code, bank_code, mess_code, mess_diff, tran_cnt, seq_no, tran_date, tran_time, stan_resp_code, bank_resp_code, inqu_date, inqu_no, bank_seq_no, filler_1, org_seq_no, out_account_no, in_account_no, in_money, in_bank_code, nor_money, abnor_money, div_proc_cnt, div_proc_no, ta_no, not_in_amt, err_code, filler_2) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			con = getConnection();
			con.setAutoCommit(false);

			pstmt   = con.prepareStatement(INS4_QRY);

			pstmt.setString (1,  tran_code     );
			pstmt.setString (2,  comp_code     );
			pstmt.setString (3,  bank_code_3     );
			pstmt.setString (4,  mess_code     );
			pstmt.setString (5,  mess_diff     );
			pstmt.setString (6,  tran_cnt      );
			pstmt.setString (7,  seq_no        );
			pstmt.setString (8,  tran_date     );
			pstmt.setString (9,  tran_time     );
			pstmt.setString (10, stan_resp_code);
			pstmt.setString (11, bank_resp_code);
			pstmt.setString (12, inqu_date     );
			pstmt.setString (13, inqu_no       );
			pstmt.setString (14, bank_seq_no   );
			pstmt.setString (15, filler_1      );
			pstmt.setString (16, org_seq_no    );
			pstmt.setString (17, out_account_no);
			pstmt.setString (18, in_account_no );
			pstmt.setString (19, in_money      );
			pstmt.setString (20, in_bank_code_3  );
			pstmt.setString (21, nor_money     );
			pstmt.setString (22, abnor_money   );
			pstmt.setString (23, div_proc_cnt  );
			pstmt.setString (24, div_proc_no   );
			pstmt.setString (25, ta_no         );
			pstmt.setString (26, not_in_amt    );
			pstmt.setString (27, err_code      );
			pstmt.setString (28, filler_2      );

			pstmt.executeUpdate();

			con.commit();

			return true;

		}catch(Throwable e) {
			int sql_code = 0;
			if (e instanceof SQLException)  /* case 1: ORA-00001 (duplicate keys) */
			{
				sql_code = ((SQLException)e).getErrorCode();
			}
			try{con.rollback();}catch(SQLException se){}

			if(sql_code == 1){
				LUtil.println("Oracle duplicate keys");
				return true;
			}
			else if (sql_code == -239){
				LUtil.println("Informix duplicate keys");
				return true;
			}
			else if (sql_code == 2627 || sql_code == 2601){
				LUtil.println("MSSQL duplicate keys");
				return true;
			}
			else if (sql_code == 1062){
				LUtil.println("MYSQL duplicate keys");
				return true;
			}
			else if (sql_code == -803){
				LUtil.println("DB2 duplicate keys");
				return true;
			}

			LUtil.println(e);

		} finally {
			try {if(pstmt!=null){pstmt.close();pstmt= null;}}catch(Exception e){}
			try {if(con !=null) {con.close( );/*con = null;*/}}catch(Exception e){}
		}
		return false;
	}
	
	static final String HOLIDAY_FILE_CONFIG = "fpeg.holiday.file";

	public static String getCurrDate()
	{
		return getDate(0);
	}

	public static String getDate(int cday)
	{
		Calendar cal = Calendar.getInstance();//Calendar.getInstance(TimeZone.getTimeZone("ZMT+9"),Locale.KOREAN);//Calendar.getInstance();


		if (cday != 0) cal.add(Calendar.DATE, cday);

		return getDate(cal);
	}

	public static String getDate(Calendar cal)
	{
		int li_yyyy,li_MM,li_dd,li_hour,li_min,li_sec;

		li_yyyy = cal.get(Calendar.YEAR); li_MM = cal.get(Calendar.MONTH); li_dd = cal.get(Calendar.DATE);
		li_hour = cal.get(Calendar.HOUR_OF_DAY); li_min = cal.get(Calendar.MINUTE); li_sec = cal.get(Calendar.SECOND);

		StringBuffer sb = new StringBuffer();

		sb.append(li_yyyy).append(li_MM<9 ? "0" : "").append(li_MM + 1).append(li_dd<10 ? "0" : "").append(li_dd);
		sb.append(li_hour<10 ? "0" : "").append(li_hour).append(li_min<10 ? "0" : "").append(li_min).append(li_sec<10 ? "0" : "").append(li_sec);

		return sb.toString();
	}

	public static String getDate(String base_date, int cday)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(base_date).append("00000000000000000000000");

		Calendar cal = Calendar.getInstance();//Calendar.getInstance(TimeZone.getTimeZone("ZMT+9"),Locale.KOREAN);//Calendar.getInstance();

		int[] cis = new int[6];
		cis[0] = Integer.parseInt(sb.substring( 0, 4));
		cis[1] = Integer.parseInt(sb.substring( 4, 6))-1;
		cis[2] = Integer.parseInt(sb.substring( 6, 8));
		cis[3] = Integer.parseInt(sb.substring( 8,10));
		cis[4] = Integer.parseInt(sb.substring(10,12));
		cis[5] = Integer.parseInt(sb.substring(12,14));

		cal.set(cis[0],cis[1],cis[2],cis[3],cis[4],cis[5]);

		cal.add(Calendar.DATE,cday);

		return getDate(cal);
	}

	static Hashtable hht = new Hashtable();
	public static boolean isHoliday(Calendar cal)
	{
		int dtype = cal.get(Calendar.DAY_OF_WEEK);
		if (dtype == Calendar.SUNDAY || dtype == Calendar.SATURDAY) return true;

		String check_date	= new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		String year			= check_date.substring(0,4);
		if (null == hht.get(HOLIDAY_FILE_CONFIG))
		{
			String	fname = CUtil.get(HOLIDAY_FILE_CONFIG);
			if (fname == null) throw new IllegalArgumentException("DUtil.isHoliday(휴일정보파일:"+HOLIDAY_FILE_CONFIG+") 오류!!");
			BufferedReader buf = null;
			try{
				buf = new BufferedReader(new InputStreamReader(new FileInputStream(fname)));

				String tmpStr = null;
				while((tmpStr = buf.readLine()) != null)
				{
					if (!tmpStr.startsWith(year)) continue;
					hht.put(tmpStr.substring(0,8),tmpStr.substring(9,10));
				}

				hht.put(HOLIDAY_FILE_CONFIG,"O");
			}catch(Exception e)
			{
				LUtil.println(e);
			}finally
			{
				try{if (buf != null) buf.close();buf = null;}catch(Exception e){}
			}
		}

		String hstate = (String)hht.get(check_date);

		return (null != hstate);
	}

	public static String[] getFWokDays(Calendar cal)
	{
		LinkedList aList = new LinkedList();

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		while(true)
		{
			aList.add(df.format(cal.getTime()));

			if (!isHoliday(cal)) break; /* 휴일이 아닌 날 하루가 있으면 그날까지 집계 점검 */;
			cal.add(Calendar.DATE,-1);
		}

		int size = aList.size();
		String[] wdays = new String[size];
		for(int i=0; i<size; i++)
		{
			wdays[size-i-1] = (String)aList.get(i);
		}
		return wdays;
	}

	public static String[] getFWokDays()
	{
		Calendar cal = Calendar.getInstance();//Calendar.getInstance(TimeZone.getTimeZone("ZMT+9"),Locale.KOREAN);//Calendar.getInstance();

		int dtype = cal.get(Calendar.DAY_OF_WEEK);

		if (isHoliday(cal)) return new String[0];//휴일에는 작업하지 않는다.

		if (dtype == Calendar.SUNDAY || dtype == Calendar.SATURDAY)
		{
			LUtil.println("CHECK : OKSummaryHandler.getFWokDays() : 공휴일에는 수납파일을 생성하지 않습니다.!!");

			return new String[0];
		}
		cal.add(Calendar.DATE,-1);

		return getFWokDays(cal) ;
	}

	public static String[] getFWokDays(String lastDay)
	{
		Calendar cal = Calendar.getInstance();//Calendar.getInstance(TimeZone.getTimeZone("ZMT+9"),Locale.KOREAN);//Calendar.getInstance();
		cal.set(Integer.parseInt(lastDay.substring(0,4)), Integer.parseInt(lastDay.substring(4,6))-1, Integer.parseInt(lastDay.substring(6,8)));
		return getFWokDays(cal) ;
	}

	public static String[] getBetweenDate(String p_sdate, String p_edate)
	{
		ArrayList aList = new ArrayList();
		try
		{
			String sdate = ((p_sdate == null) ? getCurrDate() : p_sdate);
			String edate = ((p_edate == null) ? getCurrDate() : p_edate);

			int s_yyyy = Integer.parseInt(sdate.substring(0,4));
			int s_mm   = Integer.parseInt(sdate.substring(4,6))-1;
			int s_dd   = Integer.parseInt(sdate.substring(6,8));

			int e_yyyy = Integer.parseInt(edate.substring(0,4));
			int e_mm   = Integer.parseInt(edate.substring(4,6))-1;
			int e_dd   = Integer.parseInt(edate.substring(6,8));

			StringBuffer sb = new StringBuffer();
			for(int i=s_yyyy; i<=e_yyyy; i++)
			{
				sb.setLength(0);
				sb.append(i);

				int t_s_mm = (i==s_yyyy && s_mm>0) ? s_mm : 0;
				int t_e_mm = (i==e_yyyy && e_mm<11) ? e_mm : 11;
				for(int j=t_s_mm; j<=t_e_mm; j++)
				{
					sb.setLength(4);
					if (j<9) sb.append('0');
					sb.append(j+1);

					int t_max_dd = getMaxMday(i, j);

					int t_s_dd = (i==s_yyyy && j==s_mm && s_dd>1       ) ? s_dd : 1;
					int t_e_dd = (i==e_yyyy && j==e_mm && e_dd<t_max_dd) ? e_dd : t_max_dd;
					for(int k=t_s_dd; k<=t_e_dd; k++)
					{
						sb.setLength(6);
						if (k<10) sb.append('0');
						sb.append(k);

						aList.add(sb.toString());
					}
				}
			}
		}catch(Exception e)
		{
			LUtil.println(e);
		}
		return (String[])aList.toArray(new String[0]);
	}

	public static String[] getBetweenMonth(String p_sdate, String p_edate)
	{
		ArrayList aList = new ArrayList();
		try
		{
			String sdate = ((p_sdate == null) ? getCurrDate() : p_sdate);
			String edate = ((p_edate == null) ? getCurrDate() : p_edate);

			int s_yyyy = Integer.parseInt(sdate.substring(0,4));
			int s_mm   = Integer.parseInt(sdate.substring(4,6))-1;

			int e_yyyy = Integer.parseInt(edate.substring(0,4));
			int e_mm   = Integer.parseInt(edate.substring(4,6))-1;

			StringBuffer sb = new StringBuffer();
			for(int i=s_yyyy; i<=e_yyyy; i++)
			{
				sb.setLength(0);
				sb.append(i);

				int t_s_mm = (i==s_yyyy && s_mm>0) ? s_mm : 0;
				int t_e_mm = (i==e_yyyy && e_mm<11) ? e_mm : 11;
				for(int j=t_s_mm; j<=t_e_mm; j++)
				{
					sb.setLength(4);
					if (j<9) sb.append('0');
					sb.append(j+1);

					aList.add(sb.toString());
				}
			}
		}catch(Exception e)
		{
			LUtil.println(e);
		}
		return (String[])aList.toArray(new String[0]);
	}

	public static String[] getBetweenYear(String p_sdate, String p_edate)
	{
		ArrayList aList = new ArrayList();
		try
		{
			String sdate = ((p_sdate == null) ? getCurrDate() : p_sdate);
			String edate = ((p_edate == null) ? getCurrDate() : p_edate);

			int s_yyyy = Integer.parseInt(sdate.substring(0,4));
			int e_yyyy = Integer.parseInt(edate.substring(0,4));

			StringBuffer sb = new StringBuffer();
			for(int i=s_yyyy; i<=e_yyyy; i++)
			{
				sb.setLength(0);
				sb.append(i);

				aList.add(sb.toString());
			}
		}catch(Exception e)
		{
			LUtil.println(e);
		}
		return (String[])aList.toArray(new String[0]);
	}

	private static int getMaxMday(int year, int midx)
	{
		int[] m_dates={31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

		if (midx < 0 || midx > 11) return 0;
		if (midx != 1) return m_dates[midx];

		if  (year % 400 == 0 || (year % 4 ==0 && year % 100 !=0)) return 29;
		return 28;
	}
	
	
	
}
