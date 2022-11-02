package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

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
}