package im.hyphen.msgVO;

import im.hyphen.util.SUtil;

public class M4000501 extends SDSHeader{
    private String trKubun;
    private String accountNo;
    private String trDate;
    private String trTime;
    private String trSeqno;
    private String ebondNo;
    private String ebondKind;
    private String ebondAmount;
    private String ebondMaker;
    private String ebondDeadline;
    private String ebondDepoBranch;
    private String ebondPayBankCode;
    private String ebondPayBankName;
    private String ebondPayBranchName;
    private String ebondCollectionKubun; /*추심구분 1:교환, 2:추심, 3:당점*/
    private String ebondPayerCode;      /*사업자번호, 주민등록번호*/
    private String afterPayAmount;
    private String makerKubun;         /*자수 타수 구분, 1:자수, 9:타수*/
    private String cancelDate;
    private String cancelOriSeqNo;
    private String reserved;

    public String getTrKubun() {
        return trKubun;
    }

    public void setTrKubun(String trKubun) {
        this.trKubun = trKubun;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getTrDate() {
        return trDate;
    }

    public void setTrDate(String trDate) {
        this.trDate = trDate;
    }

    public String getTrTime() {
        return trTime;
    }

    public void setTrTime(String trTime) {
        this.trTime = trTime;
    }

    public String getTrSeqno() {
        return trSeqno;
    }

    public void setTrSeqno(String trSeqno) {
        this.trSeqno = trSeqno;
    }

    public String getEbondNo() {
        return ebondNo;
    }

    public void setEbondNo(String ebondNo) {
        this.ebondNo = ebondNo;
    }

    public String getEbondKind() {
        return ebondKind;
    }

    public void setEbondKind(String ebondKind) {
        this.ebondKind = ebondKind;
    }

    public String getEbondAmount() {
        return ebondAmount;
    }

    public void setEbondAmount(String ebondAmount) {
        this.ebondAmount = ebondAmount;
    }

    public String getEbondMaker() {
        return ebondMaker;
    }

    public void setEbondMaker(String ebondMaker) {
        this.ebondMaker = ebondMaker;
    }

    public String getEbondDeadline() {
        return ebondDeadline;
    }

    public void setEbondDeadline(String ebondDeadline) {
        this.ebondDeadline = ebondDeadline;
    }

    public String getEbondDepoBranch() {
        return ebondDepoBranch;
    }

    public void setEbondDepoBranch(String ebondDepoBranch) {
        this.ebondDepoBranch = ebondDepoBranch;
    }

    public String getEbondPayBankCode() {
        return ebondPayBankCode;
    }

    public void setEbondPayBankCode(String ebondPayBankCode) {
        this.ebondPayBankCode = ebondPayBankCode;
    }

    public String getEbondPayBankName() {
        return ebondPayBankName;
    }

    public void setEbondPayBankName(String ebondPayBankName) {
        this.ebondPayBankName = ebondPayBankName;
    }

    public String getEbondPayBranchName() {
        return ebondPayBranchName;
    }

    public void setEbondPayBranchName(String ebondPayBranchName) {
        this.ebondPayBranchName = ebondPayBranchName;
    }

    public String getEbondCollectionKubun() {
        return ebondCollectionKubun;
    }

    public void setEbondCollectionKubun(String ebondCollectionKubun) {
        this.ebondCollectionKubun = ebondCollectionKubun;
    }

    public String getEbondPayerCode() {
        return ebondPayerCode;
    }

    public void setEbondPayerCode(String ebondPayerCode) {
        this.ebondPayerCode = ebondPayerCode;
    }

    public String getAfterPayAmount() {
        return afterPayAmount;
    }

    public void setAfterPayAmount(String afterPayAmount) {
        this.afterPayAmount = afterPayAmount;
    }

    public String getMakerKubun() {
        return makerKubun;
    }

    public void setMakerKubun(String makerKubun) {
        this.makerKubun = makerKubun;
    }

    public String getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(String cancelDate) {
        this.cancelDate = cancelDate;
    }

    public String getCancelOriSeqNo() {
        return cancelOriSeqNo;
    }

    public void setCancelOriSeqNo(String cancelOriSeqNo) {
        this.cancelOriSeqNo = cancelOriSeqNo;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public void parseMsg(byte[] msg){
        int ipos = 100;
        super.parseMsg(msg);
        trKubun             = SUtil.toHanX(msg,ipos, 2); ipos += 2;
        accountNo           = SUtil.toHanX(msg,ipos, 15); ipos += 15;
        trDate              = SUtil.toHanX(msg,ipos, 8); ipos += 8;
        trTime              = SUtil.toHanX(msg,ipos, 6); ipos += 6;
        trSeqno             = SUtil.toHanX(msg,ipos, 5); ipos += 5;
        ebondNo             = SUtil.toHanX(msg,ipos, 10); ipos += 10;
        ebondKind           = SUtil.toHanX(msg,ipos, 2); ipos += 2;
        ebondAmount         = SUtil.toHanX(msg,ipos, 13 ); ipos += 13;
        ebondMaker          = SUtil.toHanX(msg,ipos, 12 ); ipos += 12;
        ebondDeadline       = SUtil.toHanX(msg,ipos, 8 ); ipos += 8;
        ebondDepoBranch     = SUtil.toHanX(msg,ipos, 6 ); ipos += 6;
        ebondPayBankCode    = SUtil.toHanX(msg,ipos, 3); ipos += 3;
        ebondPayBankName    = SUtil.toHanX(msg,ipos, 4); ipos +=4 ;
        ebondPayBranchName  = SUtil.toHanX(msg,ipos, 10); ipos += 10;
        ebondCollectionKubun = SUtil.toHanX(msg,ipos, 1); ipos += 1;               
        ebondPayerCode      = SUtil.toHanX(msg,ipos, 16); ipos += 16;
        afterPayAmount      = SUtil.toHanX(msg,ipos, 14); ipos += 14;
        makerKubun          = SUtil.toHanX(msg,ipos, 1); ipos += 1;
        cancelDate          = SUtil.toHanX(msg,ipos, 8); ipos +=8 ;
        cancelOriSeqNo      = SUtil.toHanX(msg,ipos, 6); ipos += 6;
        reserved            = SUtil.toHanX(msg,ipos, 50); ipos += 50;
    }
}
