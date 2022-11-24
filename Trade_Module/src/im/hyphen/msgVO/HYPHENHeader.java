package im.hyphen.msgVO;

import im.hyphen.util.SUtil;

public class HYPHENHeader {

    private String tranCode; /*9�ڸ� �ĺ��ڵ�*/
    private String compCode; /*8�ڸ� ��ü�ڵ�*/
    private String bankCode2; /*2�ڸ� �����ڵ�*/
    private String msgCode; /*7�ڸ� �����ڵ�*/
    private String transCnt; /*1�ڸ� �۽�Ƚ��*/
    private String seqNo; /*6�ڸ� ������ȣ*/
    private String sendDate; /*8�ڸ� ��������*/
    private String sendTime; /*6�ڸ� ���۽ð�*/
    private String respCode; /*4�ڸ� �����ڵ�*/
    private String bankRespCode; /*4�ڸ� ���������ڵ�*/
    private String reqDate; /*8�ڸ� ��ȸ����*/
    private String reqNo; /*6�ڸ� ��ȸ��ȣ*/
    private String bankSeqNo; /*15�ڸ� ����������ȣ*/
    private String bankCode; /*3�ڸ� �����ڵ�*/
    private String reserved; /*13�ڸ� ����*/

    public HYPHENHeader () {}

    public HYPHENHeader (String compCode, String bankCode, String msgCode) {
        super();
        this.tranCode = SUtil.createBlankPadding(9);
        this.compCode = compCode;
        this.bankCode2 = bankCode.substring(1);
        this.msgCode = msgCode;
        this.transCnt = "1";
        this.sendDate = SUtil.getDate();
        this.sendTime = SUtil.getTime();
        this.respCode = SUtil.createBlankPadding(4);
        this.bankRespCode = SUtil.createBlankPadding(4);
        this.reqDate = SUtil.createBlankPadding(8);
        this.reqNo = SUtil.createBlankPadding(6);
        this.bankSeqNo = SUtil.createBlankPadding(15);
        this.bankCode =  bankCode;
        this.reserved = SUtil.createBlankPadding(13);

    }

    public String getTranCode() {
        return tranCode;
    }

    public String getCompCode() {
        return compCode;
    }

    public String getBankCode2() {
        return bankCode2;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public String getTransCnt() {
        return transCnt;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public String getSendDate() {
        return sendDate;
    }

    public String getSendTime() {
        return sendTime;
    }

    public String getRespCode() {
        return respCode;
    }

    public String getBankRespCode() {
        return bankRespCode;
    }

    public String getReqDate() {
        return reqDate;
    }

    public String getReqNo() {
        return reqNo;
    }

    public String getBankSeqNo() {
        return bankSeqNo;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getReserved() {
        return reserved;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public void setCompCode(String compCode) {
        this.compCode = compCode;
    }

    public void setBankCode2(String bankCode2) {
        this.bankCode2 = bankCode2;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public void setTransCnt(String transCnt) {
        this.transCnt = transCnt;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public void setBankRespCode(String bankRespCode) {
        this.bankRespCode = bankRespCode;
    }

    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    public void setReqNo(String reqNo) {
        this.reqNo = reqNo;
    }

    public void setBankSeqNo(String bankSeqNo) {
        this.bankSeqNo = bankSeqNo;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    @Override
    public String toString() {
        return 	  tranCode
                + compCode
                + bankCode2
                + msgCode
                + transCnt
                + seqNo
                + sendDate
                + sendTime
                + respCode
                + bankRespCode
                + reqDate
                + reqNo
                + bankSeqNo
                + bankCode
                + reserved;
    }

}