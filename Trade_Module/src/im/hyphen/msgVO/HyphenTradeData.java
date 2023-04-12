package im.hyphen.msgVO;

import java.io.InputStream;

public class HyphenTradeData {

	private String errCode;		 /*에러코드*/
	private String reqDate;		 /*요청일자*/
	private String reqTime;		 /*요청일자*/
	private String svcType; 	 /*서비스타입*/
	private String bankCode; 	 /*은행코드*/
	private String compCode;	 /*업체코드*/
	private String seqNo; 	 	 /*전문번호*/
	private String msgCode; 	 /*전문구분*/
	private String sendFlag;	 /*전송여부*/
	private String recvFlag;	 /*응답수신여부*/
	private String sendData;	 /*송신일자*/
	private String sendTime;	 /*송신시간*/
	private String recvDate;	 /*수신일자*/
	private String recvTime;	 /*수신시간*/
	private String sendMsg;		 /*요청전문*/
	private String recvMsg;		 /*응답전문*/

	private InputStream bin_in = null;	/*증빙데이터*/
	
    private int tCnt;
	public int gettCnt() {
		return tCnt;
	}
	public void settCnt(int tCnt) {
		this.tCnt = tCnt;
	}
	public String getErrCode() {
		return errCode;
	}
	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}
	public String getReqDate() {
		return reqDate;
	}
	public void setReqDate(String reqDate) {
		this.reqDate = reqDate;
	}
	public String getReqTime() {
		return reqTime;
	}
	public void setReqTime(String reqTime) {
		this.reqTime = reqTime;
	}
	public String getSvcType() {
		return svcType;
	}
	public void setSvcType(String svcType) {
		this.svcType = svcType;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getCompCode() {
		return compCode;
	}
	public void setCompCode(String compCode) {
		this.compCode = compCode;
	}
	public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	public String getMsgCode() {
		return msgCode;
	}
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}
	public String getSendFlag() {
		return sendFlag;
	}
	public void setSendFlag(String sendFlag) {
		this.sendFlag = sendFlag;
	}
	public String getRecvFlag() {
		return recvFlag;
	}
	public void setRecvFlag(String recvFlag) {
		this.recvFlag = recvFlag;
	}
	public String getSendData() {
		return sendData;
	}
	public void setSendData(String sendData) {
		this.sendData = sendData;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getRecvDate() {
		return recvDate;
	}
	public void setRecvDate(String recvDate) {
		this.recvDate = recvDate;
	}
	public String getRecvTime() {
		return recvTime;
	}
	public void setRecvTime(String recvTime) {
		this.recvTime = recvTime;
	}
	public String getSendMsg() {
		return sendMsg;
	}
	public void setSendMsg(String sendMsg) {
		this.sendMsg = sendMsg;
	}
	public String getRecvMsg() {
		return recvMsg;
	}
	public void setRecvMsg(String recvMsg) {
		this.recvMsg = recvMsg;
	}
	public InputStream getBin_in() {
		return bin_in;
	}
	public void setBin_in(InputStream bin_in) {
		this.bin_in = bin_in;
	}
    
    
}
