package im.hyphen.msgVO;

import im.hyphen.util.SUtil;

public class HYPHENHeader {

    private String tranCode; /*9자리 식별코드*/
    private String compCode; /*8자리 업체코드*/
    private String bankCode2; /*2자리 은행코드*/
    private String msgCode; /*4자리 전문코드*/

    private String msgDiff; /*3자리 업무코드*/
    private String transCnt; /*1자리 송신횟수*/
    private String seqNo; /*6자리 전문번호*/
    private String sendDate; /*8자리 전송일자*/
    private String sendTime; /*6자리 전송시간*/
    private String respCode; /*4자리 응답코드*/
    private String bankRespCode; /*4자리 은행응답코드*/
    private String inqDate; /*8자리 조회일자*/
    private String inqNo; /*6자리 조회번호*/
    private String bankSeqNo; /*15자리 은행전문번호*/
    private String bankCode; /*3자리 은행코드*/
    private String reserved; /*13자리 예비*/

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
        this.inqDate = SUtil.createBlankPadding(8);
        this.inqNo = SUtil.createBlankPadding(6);
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

    public String getInqDate() {
        return inqDate;
    }

    public String getInqNo() {
        return inqNo;
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

    public String getMsgDiff() {
        return msgDiff;
    }

    public void setMsgDiff(String msgDiff) {
        this.msgDiff = msgDiff;
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

    public void setInqDate(String inqDate) {
        this.inqDate = inqDate;
    }

    public void setInqNo(String inqNo) {
        this.inqNo = inqNo;
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
                + inqDate
                + inqNo
                + bankSeqNo
                + bankCode
                + reserved;
    }

}