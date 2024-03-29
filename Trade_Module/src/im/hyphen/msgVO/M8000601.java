package im.hyphen.msgVO;

import im.hyphen.util.SUtil;

public class M8000601 extends SDSHeader{
    private String account;
    private String inOutKubun;
    private String tranDate;
    private String tranTime;
    private String tranSeqNo;
    private String currency;
    private String amt;
    private String balance;
    private String accountMemo;
    private String cancelTranDate;
    private String cancelOriSeqNo;
    private String bankCode3;
    private String branchCode;
    private String branchName;
    private String accountMemo2;
    private String branchGiroCodel;
    private String tranKubun;
    private String signAfterTran;
    private String customerName;
    private String reserved;


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getInOutKubun() {
        return inOutKubun;
    }

    public void setInOutKubun(String inOutKubun) {
        this.inOutKubun = inOutKubun;
    }

    public String getTranDate() {
        return tranDate;
    }

    public void setTranDate(String tranDate) {
        this.tranDate = tranDate;
    }

    public String getTranTime() {
        return tranTime;
    }

    public void setTranTime(String tranTime) {
        this.tranTime = tranTime;
    }

    public String getTranSeqNo() {
        return tranSeqNo;
    }

    public void setTranSeqNo(String tranSeqNo) {
        this.tranSeqNo = tranSeqNo;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAccountMemo() {
        return accountMemo;
    }

    public void setAccountMemo(String accountMemo) {
        this.accountMemo = accountMemo;
    }

    public String getCancelTranDate() {
        return cancelTranDate;
    }

    public void setCancelTranDate(String cancelTranDate) {
        this.cancelTranDate = cancelTranDate;
    }

    public String getCancelOriSeqNo() {
        return cancelOriSeqNo;
    }

    public void setCancelOriSeqNo(String cancelOriSeqNo) {
        this.cancelOriSeqNo = cancelOriSeqNo;
    }

    public String getBankCode3() {
        return bankCode3;
    }

    public void setBankCode3(String bankCode3) {
        this.bankCode3 = bankCode3;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAccountMemo2() {
        return accountMemo2;
    }

    public void setAccountMemo2(String accountMemo2) {
        this.accountMemo2 = accountMemo2;
    }

    public String getBranchGiroCodel() {
        return branchGiroCodel;
    }

    public void setBranchGiroCodel(String branchGiroCodel) {
        this.branchGiroCodel = branchGiroCodel;
    }

    public String getTranKubun() {
        return tranKubun;
    }

    public void setTranKubun(String tranKubun) {
        this.tranKubun = tranKubun;
    }

    public String getSignAfterTran() {
        return signAfterTran;
    }

    public void setSignAfterTran(String signAfterTran) {
        this.signAfterTran = signAfterTran;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public void parseMsg(byte[] msg){
        int ipos = 0;
        super.parseMsg(msg);
        account         = SUtil.toHanX(msg, ipos, 16); ipos+=16;
        inOutKubun      = SUtil.toHanX(msg, ipos, 2); ipos+=2;
        tranDate        = SUtil.toHanX(msg, ipos, 8); ipos+=8;
        tranTime        = SUtil.toHanX(msg, ipos, 6); ipos+=6;
        tranSeqNo       = SUtil.toHanX(msg, ipos, 5); ipos+=5;
        currency        = SUtil.toHanX(msg, ipos, 3); ipos+=3;
        amt             = SUtil.toHanX(msg, ipos, 15); ipos+=15;
        balance         = SUtil.toHanX(msg, ipos, 15); ipos+=15;
        accountMemo     = SUtil.toHanX(msg, ipos, 14); ipos+=14;
        cancelTranDate  = SUtil.toHanX(msg, ipos, 8); ipos+=8;
        cancelOriSeqNo  = SUtil.toHanX(msg, ipos, 6); ipos+=6;
        bankCode3       = SUtil.toHanX(msg, ipos, 3); ipos+=3;
        branchCode      = SUtil.toHanX(msg, ipos, 3); ipos+=3;
        branchName      = SUtil.toHanX(msg, ipos, 10); ipos+=10;
        accountMemo2    = SUtil.toHanX(msg, ipos, 24); ipos+=24;
        branchGiroCodel = SUtil.toHanX(msg, ipos, 7); ipos+=7;
        tranKubun       = SUtil.toHanX(msg, ipos, 2); ipos+=2;
        signAfterTran   = SUtil.toHanX(msg, ipos, 1); ipos+=1;
        customerName    = SUtil.toHanX(msg, ipos, 12); ipos+=12;
        reserved        = SUtil.toHanX(msg, ipos, 1740); ipos+=1740;
    }
}
