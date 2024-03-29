package im.hyphen.util;

import im.hyphen.msgVO.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class DUtil
{

	static String DRIVER_NAME   = null;
	static String DB_URL        = null;
	static String USER_NAME     = "";
	static String PASSWORD      = "";
	public static String VrUpdateType = null;
	final static int MaxLen = 10000;
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
			LUtil.println("SND", e);
		}
		return null;
	}

	public static boolean Update_RecvData(HyphenTradeData htd)
	{
		Connection          con = null;
		PreparedStatement   pstmt = null;
		String recv_flag =  " ";
		String RecvDate     = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

		if (htd.getErrCode().equals("0000")) recv_flag = "Y";
		else if (htd.getErrCode().equals("CONE")) recv_flag = "C";  // connect error 
		else if (htd.getErrCode().equals("TIME")) recv_flag = "T";  // response timeout
		else recv_flag = "F"; 								

		String QRY = "UPDATE HYPHEN_TRADE_REQUEST SET RECV_FLAG = ?, RECV_DATE = ?, RECV_TIME = ?, RECV_MSG = ? WHERE REQ_DATE = ? AND SVC_TYPE = ? AND BANK_CODE = ? AND COMP_CODE = ? AND SEQ_NO = ? AND MSG_CODE = ? AND SEND_FLAG = 'Y' AND RECV_FLAG = 'N' ";

		int cnt = 0;
		try {
			con = getConnection();
			con.setAutoCommit(false);

			pstmt = con.prepareStatement(QRY);

			pstmt.setString (1, recv_flag       );
			pstmt.setString (2, RecvDate.substring(0,8) );
			pstmt.setString (3, RecvDate.substring(8,14) );
			pstmt.setString (4, htd.getRecvMsg()     );
			pstmt.setString (5, htd.getReqDate()     );
			pstmt.setString (6, htd.getSvcType()     );
			pstmt.setString (7, htd.getBankCode()     );
			pstmt.setString (8, htd.getCompCode()     );
			pstmt.setString (9, htd.getSeqNo()     );
			pstmt.setString (10, htd.getMsgCode()    );

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

	public static HyphenTradeData[] Select_SendData ()
	{
		Connection          con     = null;
		PreparedStatement   pstmt   = null;
		ResultSet           rs      = null;

		int                 cnt     = 0;
		HyphenTradeData[] 	htd = new HyphenTradeData[MaxLen];
		HyphenTradeData[] 	sendHtd = null;

		String RequestDate  = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
		/* db select */
		String QRY = "SELECT REQ_DATE, SVC_TYPE, BANK_CODE, COMP_CODE, SEQ_NO, MSG_CODE, SEND_MSG, BIN_DATA  FROM HYPHEN_TRADE_REQUEST WHERE REQ_DATE = ? AND SEND_FLAG = 'N' ORDER BY REQ_DATE, REQ_TIME, SEQ_NO";

		try {
			con = getConnection();
			con.setAutoCommit(false);

			pstmt = con.prepareStatement(QRY);

			pstmt.setString (1, RequestDate.substring(0,8) );

			rs = pstmt.executeQuery();

			while (rs.next()) {

				htd[cnt] = new HyphenTradeData();
				htd[cnt].setReqDate(rs.getString(1));	/* request date */
				htd[cnt].setSvcType(rs.getString(2));	/* svc_type */
				htd[cnt].setBankCode(rs.getString(3));	/* bank_code */
				htd[cnt].setCompCode(rs.getString(4));	/* comp_code */
				htd[cnt].setSeqNo(rs.getString(5));		/* seq_no */
				htd[cnt].setMsgCode(rs.getString(6));	/* msg_code */
				htd[cnt].setSendMsg(rs.getString(7));	/* send_msg */
				/* binary include msg_len */
				if ((htd[cnt].getSvcType().equals("PRW") || htd[cnt].getSvcType().equals("PRD")) && htd[cnt].getMsgCode().equals("0600601")) {
					InputStream rs_in = rs.getBinaryStream(8); /* binary data */
					CopyInputStream cis = new CopyInputStream(rs_in);
					htd[cnt].setBin_in(cis.getCopy());
					rs_in.close();
				}
				cnt++;
				if (cnt >= (MaxLen)) break;  
			}
			if (cnt < 1)
			{
				htd = null;  /* not found */

				return htd;
			}else {
				sendHtd = getSendData(htd, cnt);
				pstmt.close(); pstmt= null;
				for (int i = 0; i < cnt; i++) {

					QRY = "UPDATE HYPHEN_TRADE_REQUEST SET SEND_FLAG = 'Y', SEND_DATE = ?, SEND_TIME = ?  WHERE COMP_CODE = ? AND SEQ_NO = ?";

					pstmt = con.prepareStatement(QRY);
					pstmt.setString (1, RequestDate.substring(0,8) );
					pstmt.setString (2, RequestDate.substring(8,14) );
					pstmt.setString (3, htd[i].getCompCode() );
					pstmt.setString (4, htd[i].getSeqNo() );

					pstmt.executeUpdate();
				}
				con.commit();
			}
		}catch(Throwable e) {
			LUtil.println("SND", e);
		} finally {
			try {if(pstmt!=null){pstmt.close();pstmt= null;}}catch(Exception e){}
			try {if(con !=null) {con.close( );}}catch(Exception e){}
			try {if(rs !=null) {rs.close( ); rs = null;}}catch(Exception e){}
		}
		return sendHtd;
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
		SEL_QRY = "SELECT CORP_NAME, AMT, FINAL_DATE FROM HYPHEN_VR_ACCOUNT WHERE BANK_CODE = ? AND VR_ACCT_NO = ? AND USE_FLAG = 'Y' ";
		try {
			con = getConnection();
			con.setAutoCommit(false);

			pstmt = con.prepareStatement(SEL_QRY);

			LUtil.println("SND", "DEBUG 0900/100 search BANKCODE[" + bank_code + "], VR_ACC[" + vr_acct_no + "], AMT[" + amt + "], SEND_DATE[" + send_date + "]");

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
				LUtil.println( "SND", "ERROR 0900/100 Not found information  bank_code("+bank_code+") account_no("+vr_acct_no+")");
				rHash.put("error_code", "L008");   /* not found account */
			}
			else if (d_db_amt > 0 && d_amt != d_db_amt)
			{
				LUtil.println( "SND", "ERROR 0900/100 mismatch amt!  bank_code("+bank_code+") account_no("+vr_acct_no+") amount("+amt+") db_amount("+db_amt+")");
				rHash.put("error_code", "L002");

			}
			else if (Integer.parseInt(final_date) < Integer.parseInt(send_date))
			{
				LUtil.println( "SND", "ERROR 0900/100 final date!  bank_code("+bank_code+") account_no("+vr_acct_no+") final_date("+final_date+")");
				rHash.put("error_code", "L004");  
			}
			else 
			{
				rHash.put("error_code", "0000");  
				rHash.put("corp_name", corp_name);  
			}

		}catch(Throwable e) {
			LUtil.println("SND", e);
		} finally {
			try {if(pstmt!=null){pstmt.close();pstmt= null;}}catch(Exception e){}
			try {if(con !=null) {con.close( );/*con = null;*/}}catch(Exception e){}
			try {if(rs !=null) {rs.close( ); rs = null;}}catch(Exception e){}
		}

		return rHash;
	}


	public static boolean insert0200_300(M0200300 m0200300){
		return insert0200_300(m0200300.getTranCode(),
				m0200300.getCompCode(),
				m0200300.getBankCode2(),
				m0200300.getMsgCode(),
				m0200300.getMsgDiff(),
				m0200300.getTransCnt(),
				m0200300.getSeqNo(),
				m0200300.getSendDate(),
				m0200300.getSendTime(),
				m0200300.getRespCode(),
				m0200300.getBankRespCode(),
				m0200300.getInqDate(),
				m0200300.getInqNo(),
				m0200300.getBankSeqNo(),
				m0200300.getBankCode(),
				m0200300.getReserved(),
				m0200300.getCorp_acct_no(),
				m0200300.getComp_cnt(),
				m0200300.getDeal_sele(),
				m0200300.getIn_bank_code(),
				m0200300.getTotal_amt(),
				m0200300.getBalance(),
				m0200300.getBran_code(),
				m0200300.getCust_name(),
				m0200300.getCheck_no(),
				m0200300.getCash(),
				m0200300.getOut_bank_check(),
				m0200300.getEtc_check(),
				m0200300.getVr_acct_no(),
				m0200300.getDeal_date(),
				m0200300.getDeal_time(),
				m0200300.getSerial_no(),
				m0200300.getIn_bank_code_3(),
				m0200300.getBran_code_3(),
				m0200300.getFiller_2());

	}
	public static boolean insert0200_300(String tran_code,String comp_code,String bank_code,String mess_code,String mess_diff,String tran_cnt,String seq_no,String tran_date,String tran_time,String stan_resp_code,String bank_resp_code,String inqu_date,String inqu_no,String bank_seq_no,String bank_code_3,String filler_1,String corp_acct_no,String comp_cnt,String deal_sele,String in_bank_code,String total_amt,String balance,String bran_code,String cust_name,String check_no,String cash,String out_bank_check,String etc_check,String vr_acct_no,String deal_date,String deal_time,String serial_no,String in_bank_code_3,String bran_code_3,String filler_2)
	{
		Connection        con   = null;
		PreparedStatement pstmt = null;

		String  INS2_QRY = "INSERT INTO HYPHEN_TRADE_DATA (tran_code,comp_code,bank_code,mess_code,mess_diff, tran_cnt,seq_no,tran_date,tran_time,stan_resp_code,bank_resp_code,inqu_date,inqu_no,bank_seq_no,filler_1,corp_acct_no,comp_cnt,deal_sele,in_bank_code,total_amt,balance,bran_code,cust_name,check_no,cash,out_bank_check, etc_check,vr_acct_no,deal_date,deal_time,serial_no,filler_2) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String  INS3_QRY = "UPDATE HYPHEN_VR_ACCOUNT SET USE_FLAG = 'N' WHERE BANK_CODE = ? AND VR_ACCT_NO = ? AND AMT = ? AND USE_FLAG = 'Y'";
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
			e.printStackTrace();
			int sql_code = 0;
			if (e instanceof SQLException)  /* case 1: ORA-00001 */
			{
				sql_code = ((SQLException)e).getErrorCode();
			}
			try{con.rollback();}catch(SQLException se){}

			if(sql_code == 1){
				LUtil.println("SND", "Oracle duplicate keys");
				return true;
			}
			else if (sql_code == -239){
				LUtil.println("SND", "Informix duplicate keys");
				return true;
			}
			else if (sql_code == 2627 || sql_code == 2601){
				LUtil.println("SND", "MSSQL duplicate keys");
				return true;
			}
			else if (sql_code == 1062){
				LUtil.println("SND", "MYSQL duplicate keys");
				return true;
			}
			else if (sql_code == -803){
				LUtil.println("SND", "DB2 duplicate keys");
				return true;
			}

			LUtil.println("SND", e);

		} finally {
			try {if(pstmt != null){pstmt.close();pstmt = null;}}catch(Exception e){}
			try {if(con != null) {con.close( );/*con = null;*/}}catch(Exception e){}
		}
		return false;
	}

	public static boolean insert0400_100(M0400100 m0400100){
		return insert0400_100(m0400100.getTranCode(),
				m0400100.getCompCode(),
				m0400100.getBankCode2(),
				m0400100.getMsgCode(),
				m0400100.getMsgDiff(),
				m0400100.getTransCnt(),
				m0400100.getSeqNo(),
				m0400100.getSendDate(),
				m0400100.getSendTime(),
				m0400100.getRespCode(),
				m0400100.getBankRespCode(),
				m0400100.getInqDate(),
				m0400100.getInqNo(),
				m0400100.getBankSeqNo(),
				m0400100.getBankCode(),
				m0400100.getReserved(),
				m0400100.getOrg_seq_no(),
				m0400100.getOut_account_no(),
				m0400100.getIn_account_no(),
				m0400100.getIn_money(),
				m0400100.getIn_bank_code(),
				m0400100.getNor_money(),
				m0400100.getAbnor_money(),
				m0400100.getDiv_proc_cnt(),
				m0400100.getDiv_proc_no(),
				m0400100.getTa_no(),
				m0400100.getNot_in_amt(),
				m0400100.getErr_code(),
				m0400100.getIn_bank_code_3(),
				m0400100.getFiller_2());

	}



	public static boolean insert0400_100(String tran_code, String comp_code, String bank_code, String mess_code, String mess_diff, String tran_cnt, String seq_no, String tran_date, String tran_time, String stan_resp_code, String bank_resp_code, String inqu_date, String inqu_no, String bank_seq_no, String bank_code_3, String filler_1, String org_seq_no, String out_account_no, String in_account_no, String in_money, String in_bank_code, String nor_money, String abnor_money, String div_proc_cnt, String div_proc_no, String ta_no, String not_in_amt, String err_code, String in_bank_code_3, String filler_2)
	{
		Connection          con     = null;
		PreparedStatement   pstmt   = null;

		String  INS4_QRY = "INSERT INTO HYPHEN_TRADE_ERR (tran_code, comp_code, bank_code, mess_code, mess_diff, tran_cnt, seq_no, tran_date, tran_time, stan_resp_code, bank_resp_code, inqu_date, inqu_no, bank_seq_no, filler_1, org_seq_no, out_account_no, in_account_no, in_money, in_bank_code, nor_money, abnor_money, div_proc_cnt, div_proc_no, ta_no, not_in_amt, err_code, filler_2) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

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
				LUtil.println("SND", "Oracle duplicate keys");
				return true;
			}
			else if (sql_code == -239){
				LUtil.println("SND", "Informix duplicate keys");
				return true;
			}
			else if (sql_code == 2627 || sql_code == 2601){
				LUtil.println("SND", "MSSQL duplicate keys");
				return true;
			}
			else if (sql_code == 1062){
				LUtil.println("SND", "MYSQL duplicate keys");
				return true;
			}
			else if (sql_code == -803){
				LUtil.println("SND", "DB2 duplicate keys");
				return true;
			}

			LUtil.println("SND", e);

		} finally {
			try {if(pstmt!=null){pstmt.close();pstmt= null;}}catch(Exception e){}
			try {if(con !=null) {con.close( );/*con = null;*/}}catch(Exception e){}
		}
		return false;
	}



	public static boolean insert8000601(M8000601 m8000601){
		return insert8000601(
				m8000601.getTrCode(), /* Transaction Code [9] */
				m8000601.getCompCode(), /* 은행부여 업체코드 [12] */
				m8000601.getBankCode(), /* 은행 코드 [2] */
				m8000601.getMsgCode(), /* 전문 코드 [4] */
				m8000601.getKubun(), /* 업무 구분 [3] */
				m8000601.getSendCnt(), /* 송신 횟수 [1] */
				m8000601.getMsgNo(), /* 전문 번호 [6] */
				m8000601.getSendDate(), /* 송신 일자 [8] */
				m8000601.getSendTime(), /* 송신 시간 [6] */
				m8000601.getRetCode(), /* 응답 코드 [4] */
				m8000601.getSikByulCode(), /* 식별 코드 [9] */
				m8000601.getSdsArea(), /* SDS 영역 [15] */
				m8000601.getCompArea(), /* 업체 영역 [11] */
				m8000601.getBankArea(), /* 은행 영역 [10] */
				m8000601.getAccount(),
				m8000601.getInOutKubun(),
				m8000601.getTranDate(),
				m8000601.getTranTime(),
				m8000601.getTranSeqNo(),
				m8000601.getCurrency(),
				m8000601.getAmt(),
				m8000601.getBalance(),
				m8000601.getAccountMemo(),
				m8000601.getCancelTranDate(),
				m8000601.getCancelOriSeqNo(),
				m8000601.getBankCode3(),
				m8000601.getBranchCode(),
				m8000601.getBranchName(),
				m8000601.getAccountMemo2(),
				m8000601.getBranchGiroCodel(),
				m8000601.getTranKubun(),
				m8000601.getSignAfterTran(),
				m8000601.getCustomerName(),
				m8000601.getReserved()
		);
	}

	private static boolean  insert8000601(String trCode,String compCode,String bankCode,String msgCode,String kubun,String sendCnt,String msgNo,String sendDate,String sendTime,String retCode,String sikByulCode,String sdsArea,String compArea,String bankArea,String account,String inOutKubun,String tranDate,String tranTime,String tranSeqNo,String currency,String amt,String balance,String accountMemo,String cancelTranDate,String cancelOriSeqNo,String bankCode3,String branchCode,String branchName,String accountMemo2,String branchGiroCodel,String tranKubun,String signAfterTran,String customerName,String reserved)
	{
		Connection          con     = null;
		PreparedStatement   pstmt   = null;

		String  INS4_QRY = "INSERT INTO MSG8000601 (trCode,compCode,bankCode,msgCode,kubun,sendCnt,msgNo,sendDate,sendTime,retCode,sikByulCode,sdsArea,compArea,bankArea,account,inOutKubun,tranDate,tranTime,tranSeqNo,currency,amt,balance,accountMemo,cancelTranDate,cancelOriSeqNo,bankCode3,branchCode,branchName,accountMemo2,branchGiroCodel,tranKubun,signAfterTran,customerName,reserved) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			con = getConnection();
			con.setAutoCommit(false);

			pstmt   = con.prepareStatement(INS4_QRY);

			pstmt.setString (1,  trCode);
			pstmt.setString (2,  compCode);
			pstmt.setString (3,  bankCode);
			pstmt.setString (4,  msgCode);
			pstmt.setString (5,  kubun);
			pstmt.setString (6,  sendCnt);
			pstmt.setString (7,  msgNo);
			pstmt.setString (8,  sendDate);
			pstmt.setString (9,  sendTime);
			pstmt.setString (10, retCode);
			pstmt.setString (11, sikByulCode);
			pstmt.setString (12, sdsArea);
			pstmt.setString (13, compArea);
			pstmt.setString (14, bankArea);
			pstmt.setString (15, account);
			pstmt.setString (16, inOutKubun);
			pstmt.setString (17, tranDate);
			pstmt.setString (18, tranTime);
			pstmt.setString (19, tranSeqNo);
			pstmt.setString (20, currency);
			pstmt.setString (21, amt);
			pstmt.setString (22, balance);
			pstmt.setString (23, accountMemo);
			pstmt.setString (24, cancelTranDate);
			pstmt.setString (25, cancelOriSeqNo);
			pstmt.setString (26, bankCode3);
			pstmt.setString (27, branchCode);
			pstmt.setString (28, branchName);
			pstmt.setString (29, accountMemo2);
			pstmt.setString (30, branchGiroCodel);
			pstmt.setString (31, tranKubun);
			pstmt.setString (32, signAfterTran);
			pstmt.setString (33, customerName);
			pstmt.setString (34, reserved);

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
				LUtil.println("SND","Oracle duplicate keys");
				return true;
			}
			else if (sql_code == -239){
				LUtil.println("SND","Informix duplicate keys");
				return true;
			}
			else if (sql_code == 2627 || sql_code == 2601){
				LUtil.println("SND","MSSQL duplicate keys");
				return true;
			}
			else if (sql_code == 1062){
				LUtil.println("SND","MYSQL duplicate keys");
				return true;
			}
			else if (sql_code == -803){
				LUtil.println("SND","DB2 duplicate keys");
				return true;
			}

			LUtil.println("SND",e);

		} finally {
			try {if(pstmt!=null){pstmt.close();pstmt= null;}}catch(Exception e){}
			try {if(con !=null) {con.close( );/*con = null;*/}}catch(Exception e){}
		}
		return false;
	}

	public static boolean insert3000700(M3000700 m3000700) {
		return insert3000700(
				m3000700.getTrCode(), /* Transaction Code [9] */
				m3000700.getCompCode(), /* 은행부여 업체코드 [12] */
				m3000700.getBankCode(), /* 은행 코드 [2] */
				m3000700.getMsgCode(), /* 전문 코드 [4] */
				m3000700.getKubun(), /* 업무 구분 [3] */
				m3000700.getSendCnt(), /* 송신 횟수 [1] */
				m3000700.getMsgNo(), /* 전문 번호 [6] */
				m3000700.getSendDate(), /* 송신 일자 [8] */
				m3000700.getSendTime(), /* 송신 시간 [6] */
				m3000700.getRetCode(), /* 응답 코드 [4] */
				m3000700.getSikByulCode(), /* 식별 코드 [9] */
				m3000700.getSdsArea(), /* SDS 영역 [15] */
				m3000700.getCompArea(), /* 업체 영역 [11] */
				m3000700.getBankArea(), /* 은행 영역 [10] */
				m3000700.getSendRequestDate(),               /* 외화송금의뢰일자*/
				m3000700.getSendRequestSeqNo(),              /* 외화송금의뢰전문번호*/
				m3000700.getCustomerNo(),                    /* 고객번호*/
				m3000700.getmAccount(),                      /* 출금계좌번호(송금액)*/
				m3000700.getAmt(),                           /* 송금금액(외화금액) 소수점이하 3자리 포함 12자리 정수, 소수점 이하 3자리*/
				m3000700.getCurrency(),                      /* 송금통화*/
				m3000700.getSendKubun(),                     /* 송금구분*/
				m3000700.getSenderName(),                    /* 송금인명*/
				m3000700.getReceiverName(),                  /* 수취인명*/
				m3000700.getReceiverAccount(),               /* 수취인계좌번호*/
				m3000700.getReceiverAddress(),               /* 수취인주소*/
				m3000700.getReceiverMomo(),                  /* 수취인앞 지시사항*/
				m3000700.getCountryCode(),                   /* 상대국 코드*/
				m3000700.getHostileCountry(),                /* 적성국가앞 송금 'Y'*/
				m3000700.getReceiverBicCode(),               /* 수취은행 코드, 수취인 앞 송금액을 지급한 은행*/
				m3000700.getReceiverBankName(),              /* 수취은행 은행명 및 주소*/
				m3000700.getMsgFormat(),                     /* 송금 전문형태 1 mt100 2 mt100 & mt202*/
				m3000700.getSettlementBankCode(),            /* 결제은행 bic코드 */
				m3000700.getSettlementBanName(),             /* 결제은행*/
				m3000700.getRouteBankCode1(),                /* 송금경유은행1 코드*/
				m3000700.getRouteBankName1(),                /* 송금경유은행1*/
				m3000700.getRouteBankCode2(),                /* 송금경유은행2 코드*/
				m3000700.getRouteBankName2(),                /* 송금경유은행2*/
				m3000700.getRouteBankCode3(),                /* 송금경유은행3 코드*/
				m3000700.getRouteBankName3(),                /* 송금경유은행3*/
				m3000700.getFeeChargeAccountNo(),            /* 수수료 인출계좌번호*/
				m3000700.getFeePayer(),                      /* 해외은행 수수료 부담자 1 : 수취인부담, 2송금인부담*/
				m3000700.getWhichFeeAccount(),               /* 해외은행 수수료 인출계좌 지정 1 : 출금계좌, 2 : 수수료 인출계좌*/
				m3000700.getAmtForTheirCurrency(),           /* 송금액(출금계좌 통화기준)*/
				m3000700.getFeeCurRate(),                    /* 송금액 인출시 적용 환율*/
				m3000700.getFeeAmtForLocal(),                /* 송금수수료(국내)*/
				m3000700.getFeeCurRateForLocal(),            /* 송금수수료 인출 적용환율(국내)*/
				m3000700.getFeeToForeignBank(),               /* 해외은행앞 지급수수료*/
				m3000700.getCurrencyRateToForeignBank(),      /* 해외은행 수수료 인출 적용 환율*/
				m3000700.getPayCauseCode(),                  /* 지급사유코드*/
				m3000700.getPayCause(),                      /* 지급사유*/
				m3000700.getTransactionNo(),                 /* 거래번호*/
				m3000700.getValueDate(),                     /* */
				m3000700.getReserved()                      /* 예비*/
			);
	}

	private static boolean  insert3000700(String trCode, String compCode, String bankCode, String msgCode, String kubun, String sendCnt, String msgNo , String sendDate, String sendTime, String retCode, String sikByulCode, String sdsArea, String compArea, String bankArea,String sendRequestDate,String sendRequestSeqNo,String customerNo, String mAccount, String amt,String currency, String sendKubun,String senderName, String receiverName, String receiverAccount,String receiverAddress,String receiverMomo, String countryCode,String hostileCountry, String receiverBicCode,String receiverBankName,String msgFormat,String settlementBankCode,String settlementBanName, String routeBankCode1, String routeBankName1, String routeBankCode2, String routeBankName2, String routeBankCode3, String routeBankName3, String feeChargeAccountNo,String feePayer, String whichFeeAccount,String amtForTheirCurrency, String feeCurRate, String feeAmtForLocal, String feeCurRateForLocal,String feeToForeignBank,String currencyRateToForeignBank, String payCauseCode, String payCause, String transactionNo,String valueDate,String reserved)
	{
		Connection          con     = null;
		PreparedStatement   pstmt   = null;

		String  INS4_QRY = "INSERT INTO MSG3000700 (trCode, compCode, bankCode, msgCode, kubun, sendCnt, msgNo , sendDate, sendTime, retCode, sikByulCode, sdsArea, compArea, bankArea,sendRequestDate,sendRequestSeqNo,customerNo, mAccount, amt,currency, sendKubun,senderName, receiverName, receiverAccount,receiverAddress,receiverMomo, countryCode,hostileCountry, receiverBicCode,receiverBankName,msgFormat,settlementBankCode,settlementBanName, routeBankCode1, routeBankName1, routeBankCode2, routeBankName2, routeBankCode3, routeBankName3, feeChargeAccountNo,feePayer, whichFeeAccount,amtForTheirCurrency, feeCurRate, feeAmtForLocal, feeCurRateForLocal,feeToForeignBank,currencyRateToForeignBank, payCauseCode, payCause, transactionNo,valueDate,reserved) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			con = getConnection();
			con.setAutoCommit(false);

			pstmt   = con.prepareStatement(INS4_QRY);
			pstmt.setString(1,trCode);
			pstmt.setString(2,compCode);
			pstmt.setString(3,bankCode);
			pstmt.setString(4,msgCode);
			pstmt.setString(5,kubun);
			pstmt.setString(6,sendCnt);
			pstmt.setString(7,msgNo);
			pstmt.setString(8,sendDate);
			pstmt.setString(9,sendTime);
			pstmt.setString(10,retCode);
			pstmt.setString(11,sikByulCode);
			pstmt.setString(12,sdsArea);
			pstmt.setString(13,compArea);
			pstmt.setString(14,bankArea);
			pstmt.setString(15,sendRequestDate);
			pstmt.setString(16,sendRequestSeqNo);
			pstmt.setString(17,customerNo);
			pstmt.setString(18,mAccount);
			pstmt.setString(19,amt);
			pstmt.setString(20,currency);
			pstmt.setString(21,sendKubun);
			pstmt.setString(22,senderName);
			pstmt.setString(23,receiverName);
			pstmt.setString(24,receiverAccount);
			pstmt.setString(25,receiverAddress);
			pstmt.setString(26,receiverMomo);
			pstmt.setString(27,countryCode);
			pstmt.setString(28,hostileCountry);
			pstmt.setString(29,receiverBicCode);
			pstmt.setString(30,receiverBankName);
			pstmt.setString(31,msgFormat);
			pstmt.setString(32,settlementBankCode);
			pstmt.setString(33,settlementBanName);
			pstmt.setString(34,routeBankCode1);
			pstmt.setString(35,routeBankName1);
			pstmt.setString(36,routeBankCode2);
			pstmt.setString(37,routeBankName2);
			pstmt.setString(38,routeBankCode3);
			pstmt.setString(39,routeBankName3);
			pstmt.setString(40,feeChargeAccountNo);
			pstmt.setString(41,feePayer);
			pstmt.setString(42,whichFeeAccount);
			pstmt.setString(43,amtForTheirCurrency);
			pstmt.setString(44,feeCurRate);
			pstmt.setString(45,feeAmtForLocal);
			pstmt.setString(46,feeCurRateForLocal);
			pstmt.setString(47,feeToForeignBank);
			pstmt.setString(48,currencyRateToForeignBank);
			pstmt.setString(49,payCauseCode);
			pstmt.setString(50,payCause);
			pstmt.setString(51,transactionNo);
			pstmt.setString(52,valueDate);
			pstmt.setString(53,reserved);



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
				LUtil.println("SND","Oracle duplicate keys");
				return true;
			}
			else if (sql_code == -239){
				LUtil.println("SND","Informix duplicate keys");
				return true;
			}
			else if (sql_code == 2627 || sql_code == 2601){
				LUtil.println("SND","MSSQL duplicate keys");
				return true;
			}
			else if (sql_code == 1062){
				LUtil.println("SND","MYSQL duplicate keys");
				return true;
			}
			else if (sql_code == -803){
				LUtil.println("SND","DB2 duplicate keys");
				return true;
			}

			LUtil.println("SND",e);

		} finally {
			try {if(pstmt!=null){pstmt.close();pstmt= null;}}catch(Exception e){}
			try {if(con !=null) {con.close( );/*con = null;*/}}catch(Exception e){}
		}
		return false;
	}


	public static boolean insert4000501(M4000501 m4000501) {
		return insert4000501(
				m4000501.getTrCode(), /* Transaction Code [9] */
				m4000501.getCompCode(), /* 은행부여 업체코드 [12] */
				m4000501.getBankCode(), /* 은행 코드 [2] */
				m4000501.getMsgCode(), /* 전문 코드 [4] */
				m4000501.getKubun(), /* 업무 구분 [3] */
				m4000501.getSendCnt(), /* 송신 횟수 [1] */
				m4000501.getMsgNo(), /* 전문 번호 [6] */
				m4000501.getSendDate(), /* 송신 일자 [8] */
				m4000501.getSendTime(), /* 송신 시간 [6] */
				m4000501.getRetCode(), /* 응답 코드 [4] */
				m4000501.getSikByulCode(), /* 식별 코드 [9] */
				m4000501.getSdsArea(), /* SDS 영역 [15] */
				m4000501.getCompArea(), /* 업체 영역 [11] */
				m4000501.getBankArea(), /* 은행 영역 [10] */
				m4000501.getTrKubun(),
				m4000501.getAccountNo(),
				m4000501.getTrDate(),
				m4000501.getTrTime(),
				m4000501.getTrSeqno(),
				m4000501.getEbondNo(),
				m4000501.getEbondKind(),
				m4000501.getEbondAmount(),
				m4000501.getEbondMaker(),
				m4000501.getEbondDeadline(),
				m4000501.getEbondDepoBranch(),
				m4000501.getEbondPayBankCode(),
				m4000501.getEbondPayBankName(),
				m4000501.getEbondPayBranchName(),
				m4000501.getEbondCollectionKubun(), /*추심구분 1:교환, 2:추심, 3:당점*/
				m4000501.getEbondPayerCode(),      /*사업자번호, 주민등록번호*/
				m4000501.getAfterPayAmount(),
				m4000501.getMakerKubun(),         /*자수 타수 구분, 1:자수, 9:타수*/
				m4000501.getCancelDate(),
				m4000501.getCancelOriSeqNo(),
				m4000501.getReserved()

				);
	}



	private static boolean  insert4000501(String trCode, String compCode,String bankCode,String msgCode,String kubun,String sendCnt,String msgNo ,String sendDate,String sendTime,String retCode,String sikByulCode,String sdsArea,String compArea,String bankArea,String trKubun,String accountNo,String trDate,String trTime,String trSeqno,String ebondNo,String ebondKind,String ebondAmount,String ebondMaker,String ebondDeadline,String ebondDepoBranch,String ebondPayBankCode,String ebondPayBankName,String ebondPayBranchName,String ebondCollectionKubun ,String ebondPayerCode       ,String afterPayAmount,String makerKubun           ,String cancelDate,String cancelOriSeqNo,String reserved )
	{
		Connection          con     = null;
		PreparedStatement   pstmt   = null;

		String  INS4_QRY = "INSERT INTO MSG4000501 (trCode, compCode,bankCode,msgCode,kubun,sendCnt,msgNo ,sendDate,sendTime,retCode,sikByulCode,sdsArea,compArea,bankArea,trKubun,accountNo,trDate,trTime,trSeqno,ebondNo,ebondKind,ebondAmount,ebondMaker,ebondDeadline,ebondDepoBranch,ebondPayBankCode,ebondPayBankName,ebondPayBranchName,ebondCollectionKubun ,ebondPayerCode       ,afterPayAmount,makerKubun, cancelDate, cancelOriSeqNo, reserved) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			con = getConnection();
			con.setAutoCommit(false);

			pstmt   = con.prepareStatement(INS4_QRY);

			pstmt.setString(1,trCode);
			pstmt.setString(2,compCode);
			pstmt.setString(3,bankCode);
			pstmt.setString(4,msgCode);
			pstmt.setString(5,kubun);
			pstmt.setString(6,sendCnt);
			pstmt.setString(7,msgNo );
			pstmt.setString(8,sendDate);
			pstmt.setString(9,sendTime);
			pstmt.setString(10,retCode);
			pstmt.setString(11,sikByulCode);
			pstmt.setString(12,sdsArea);
			pstmt.setString(13,compArea);
			pstmt.setString(14,bankArea);
			pstmt.setString(15,trKubun);
			pstmt.setString(16,accountNo);
			pstmt.setString(17,trDate);
			pstmt.setString(18,trTime);
			pstmt.setString(19,trSeqno);
			pstmt.setString(20,ebondNo);
			pstmt.setString(21,ebondKind);
			pstmt.setString(22,ebondAmount);
			pstmt.setString(23,ebondMaker);
			pstmt.setString(24,ebondDeadline);
			pstmt.setString(25,ebondDepoBranch);
			pstmt.setString(26,ebondPayBankCode);
			pstmt.setString(27,ebondPayBankName);
			pstmt.setString(28,ebondPayBranchName);
			pstmt.setString(29,ebondCollectionKubun );
			pstmt.setString(30,ebondPayerCode       );
			pstmt.setString(31,afterPayAmount);
			pstmt.setString(32,makerKubun);
			pstmt.setString(33,cancelDate);
			pstmt.setString(34,cancelOriSeqNo);
			pstmt.setString(35,reserved);



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
				LUtil.println("SND","Oracle duplicate keys");
				return true;
			}
			else if (sql_code == -239){
				LUtil.println("SND","Informix duplicate keys");
				return true;
			}
			else if (sql_code == 2627 || sql_code == 2601){
				LUtil.println("SND","MSSQL duplicate keys");
				return true;
			}
			else if (sql_code == 1062){
				LUtil.println("SND","MYSQL duplicate keys");
				return true;
			}
			else if (sql_code == -803){
				LUtil.println("SND","DB2 duplicate keys");
				return true;
			}

			LUtil.println("SND",e);

		} finally {
			try {if(pstmt!=null){pstmt.close();pstmt= null;}}catch(Exception e){}
			try {if(con !=null) {con.close( );/*con = null;*/}}catch(Exception e){}
		}
		return false;
	}


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

	public static HyphenTradeData[] getSendData(HyphenTradeData[] htd, int cnt){
		HyphenTradeData[] sendHtd = new HyphenTradeData[cnt];
		System.arraycopy(htd, 0, sendHtd, 0, cnt);

		return sendHtd;
	}
}