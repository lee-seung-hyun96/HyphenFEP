package im.hyphen.msgVO;

public class SDSHeader {

    private String trCode; /* Transaction Code [9] */
    private String compCode; /* 은행부여 업체코드 [12] */
    private String bankCode; /* 은행 코드 [2] */
    private String msgCode; /* 전문 코드 [4] */
    private String kubun; /* 업무 구분 [3] */
    private String sendCnt; /* 송신 횟수 [1] */
    private String msgNo; /* 전문 번호 [6] */
    private String sendDate; /* 송신 일자 [8] */
    private String sendTime; /* 송신 시간 [6] */
    private String retCode; /* 응답 코드 [4] */
    private String sikByulCode; /* 식별 코드 [9] */
    private String sdsArea; /* SDS 영역 [15] */
    private String compArea; /* 업체 영역 [11] */
    private String bankArea; /* 은행 영역 [10] */

    public String getTrCode() {
        return trCode;
    }

    public void setTrCode(String trCode) {
        this.trCode = trCode;
    }

    public String getCompCode() {
        return compCode;
    }

    public void setCompCode(String compCode) {
        this.compCode = compCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getKubun() {
        return kubun;
    }

    public void setKubun(String kubun) {
        this.kubun = kubun;
    }

    public String getSendCnt() {
        return sendCnt;
    }

    public void setSendCnt(String sendCnt) {
        this.sendCnt = sendCnt;
    }

    public String getMsgNo() {
        return msgNo;
    }

    public void setMsgNo(String msgNo) {
        this.msgNo = msgNo;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getSikByulCode() {
        return sikByulCode;
    }

    public void setSikByulCode(String sikByulCode) {
        this.sikByulCode = sikByulCode;
    }

    public String getSdsArea() {
        return sdsArea;
    }

    public void setSdsArea(String sdsArea) {
        this.sdsArea = sdsArea;
    }

    public String getCompArea() {
        return compArea;
    }

    public void setCompArea(String compArea) {
        this.compArea = compArea;
    }

    public String getBankArea() {
        return bankArea;
    }

    public void setBankArea(String bankArea) {
        this.bankArea = bankArea;
    }

    @Override
    public String toString() {
        return
                trCode +
                compCode +
                bankCode +
                msgCode +
                kubun +
                sendCnt +
                msgNo +
                sendDate +
                sendTime +
                retCode +
                sikByulCode +
                sdsArea +
                compArea +
                bankArea ;
    }
}
