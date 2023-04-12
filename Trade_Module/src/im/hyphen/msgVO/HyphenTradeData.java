package im.hyphen.msgVO;

import java.io.InputStream;

public class HyphenTradeData {
	
	private String errCode;
    private String reqDate;
    private String reqTime;
    private String svcType;
    private String bankCode;
    private String compCode;
    private String seqNo;
    private String msgCode;
    private String sendFlag;
    private String recvFlag;
    private String sendData;
    private String sendTime;
    private String recvDate;
    private String recvTime;
    private String sendMsg;
    private String recvMsg;
    
    private InputStream bin_in = null;
	
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
