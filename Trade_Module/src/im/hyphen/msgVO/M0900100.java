package im.hyphen.msgVO;

public class M0900100 extends HYPHENHeader {
    private String  send_date  ;
    private String  bank_code  ;
    private String  vr_acct_no ;
    private String  amt        ;


    public String getSend_date() {
        return send_date;
    }

    public void setSend_date(String send_date) {
        this.send_date = send_date;
    }

    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public String getVr_acct_no() {
        return vr_acct_no;
    }

    public void setVr_acct_no(String vr_acct_no) {
        this.vr_acct_no = vr_acct_no;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }
}
