package im.hyphen.msgVO;

public class M3000700  extends SDSHeader{
    private String sendRequestDate;               /* 외화송금의뢰일자*/
    private String sendRequestSeqNo;              /* 외화송금의뢰전문번호*/
    private String customerNo;                    /* 고객번호*/
    private String mAccount;                      /* 출금계좌번호(송금액)*/
    private String amt;                           /* 송금금액(외화금액) 소수점이하 3자리 포함 12자리 정수, 소수점 이하 3자리*/
    private String currency;                      /* 송금통화*/
    private String sendKubun;                     /* 송금구분*/
    private String senderName;                    /* 송금인명*/
    private String receiverName;                  /* 수취인명*/
    private String receiverAccount;               /* 수취인계좌번호*/
    private String receiverAddress;               /* 수취인주소*/
    private String receiverMomo;                  /* 수취인앞 지시사항*/
    private String countryCode;                   /* 상대국 코드*/
    private String hostileCountry;                /* 적성국가앞 송금 'Y'*/
    private String receiverBicCode;               /* 수취은행 코드, 수취인 앞 송금액을 지급한 은행*/
    private String receiverBankName;              /* 수취은행 은행명 및 주소*/
    private String msgFormat;                     /* 송금 전문형태 1 mt100 2 mt100 & mt202*/
    private String settlementBankCode;            /* 결제은행 bic코드 */
    private String settlementBanName;             /* 결제은행*/
    private String routeBankCode1;                /* 송금경유은행1 코드*/
    private String routeBankName1;                /* 송금경유은행1*/
    private String routeBankCode2;                /* 송금경유은행2 코드*/
    private String routeBankName2;                /* 송금경유은행2*/
    private String routeBankCode3;                /* 송금경유은행3 코드*/
    private String routeBankName3;                /* 송금경유은행3*/
    private String feeChargeAccountNo;            /* 수수료 인출계좌번호*/
    private String feePayer;                      /* 해외은행 수수료 부담자 1 : 수취인부담, 2송금인부담*/
    private String whichFeeAccount;               /* 해외은행 수수료 인출계좌 지정 1 : 출금계좌, 2 : 수수료 인출계좌*/
    private String amtForTheirCurrency;           /* 송금액(출금계좌 통화기준)*/
    private String feeCurRate;                    /* 송금액 인출시 적용 환율*/
    private String feeAmtForLocal;                /* 송금수수료(국내)*/
    private String feeCurRateForLocal;            /* 송금수수료 인출 적용환율(국내)*/
    private String feeToForeignBank;               /* 해외은행앞 지급수수료*/
    private String currencyRateToForeignBank;      /* 해외은행 수수료 인출 적용 환율*/
    private String payCauseCode;                  /* 지급사유코드*/
    private String payCause;                      /* 지급사유*/
    private String transactionNo;                 /* 거래번호*/
    private String valueDate;                     /* */
    private String reserved;                      /* 예비*/

    public String getSendRequestDate() {
        return sendRequestDate;
    }

    public void setSendRequestDate(String sendRequestDate) {
        this.sendRequestDate = sendRequestDate;
    }

    public String getSendRequestSeqNo() {
        return sendRequestSeqNo;
    }

    public void setSendRequestSeqNo(String sendRequestSeqNo) {
        this.sendRequestSeqNo = sendRequestSeqNo;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getmAccount() {
        return mAccount;
    }

    public void setMAccount(String mAccount) {
        this.mAccount = mAccount;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSendKubun() {
        return sendKubun;
    }

    public void setSendKubun(String sendKubun) {
        this.sendKubun = sendKubun;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(String receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverMomo() {
        return receiverMomo;
    }

    public void setReceiverMomo(String receiverMomo) {
        this.receiverMomo = receiverMomo;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getHostileCountry() {
        return hostileCountry;
    }

    public void setHostileCountry(String hostileCountry) {
        this.hostileCountry = hostileCountry;
    }

    public String getReceiverBicCode() {
        return receiverBicCode;
    }

    public void setReceiverBicCode(String receiverBicCode) {
        this.receiverBicCode = receiverBicCode;
    }

    public String getReceiverBankName() {
        return receiverBankName;
    }

    public void setReceiverBankName(String receiverBankName) {
        this.receiverBankName = receiverBankName;
    }

    public String getMsgFormat() {
        return msgFormat;
    }

    public void setMsgFormat(String msgFormat) {
        this.msgFormat = msgFormat;
    }

    public String getSettlementBankCode() {
        return settlementBankCode;
    }

    public void setSettlementBankCode(String settlementBankCode) {
        this.settlementBankCode = settlementBankCode;
    }

    public String getSettlementBanName() {
        return settlementBanName;
    }

    public void setSettlementBanName(String settlementBanName) {
        this.settlementBanName = settlementBanName;
    }

    public String getRouteBankCode1() {
        return routeBankCode1;
    }

    public void setRouteBankCode1(String routeBankCode1) {
        this.routeBankCode1 = routeBankCode1;
    }

    public String getRouteBankName1() {
        return routeBankName1;
    }

    public void setRouteBankName1(String routeBankName1) {
        this.routeBankName1 = routeBankName1;
    }

    public String getRouteBankCode2() {
        return routeBankCode2;
    }

    public void setRouteBankCode2(String routeBankCode2) {
        this.routeBankCode2 = routeBankCode2;
    }

    public String getRouteBankName2() {
        return routeBankName2;
    }

    public void setRouteBankName2(String routeBankName2) {
        this.routeBankName2 = routeBankName2;
    }

    public String getRouteBankCode3() {
        return routeBankCode3;
    }

    public void setRouteBankCode3(String routeBankCode3) {
        this.routeBankCode3 = routeBankCode3;
    }

    public String getRouteBankName3() {
        return routeBankName3;
    }

    public void setRouteBankName3(String routeBankName3) {
        this.routeBankName3 = routeBankName3;
    }

    public String getFeeChargeAccountNo() {
        return feeChargeAccountNo;
    }

    public void setFeeChargeAccountNo(String feeChargeAccountNo) {
        this.feeChargeAccountNo = feeChargeAccountNo;
    }

    public String getFeePayer() {
        return feePayer;
    }

    public void setFeePayer(String feePayer) {
        this.feePayer = feePayer;
    }

    public String getWhichFeeAccount() {
        return whichFeeAccount;
    }

    public void setWhichFeeAccount(String whichFeeAccount) {
        this.whichFeeAccount = whichFeeAccount;
    }

    public String getAmtForTheirCurrency() {
        return amtForTheirCurrency;
    }

    public void setAmtForTheirCurrency(String amtForTheirCurrency) {
        this.amtForTheirCurrency = amtForTheirCurrency;
    }

    public String getFeeCurRate() {
        return feeCurRate;
    }

    public void setFeeCurRate(String feeCurRate) {
        this.feeCurRate = feeCurRate;
    }

    public String getFeeAmtForLocal() {
        return feeAmtForLocal;
    }

    public void setFeeAmtForLocal(String feeAmtForLocal) {
        this.feeAmtForLocal = feeAmtForLocal;
    }

    public String getFeeCurRateForLocal() {
        return feeCurRateForLocal;
    }

    public void setFeeCurRateForLocal(String feeCurRateForLocal) {
        this.feeCurRateForLocal = feeCurRateForLocal;
    }

    public String getFeeToForeignBank() {
        return feeToForeignBank;
    }

    public void setFeeToForeignBank(String feeToForeignBank) {
        this.feeToForeignBank = feeToForeignBank;
    }

    public String getCurrencyRateToForeignBank() {
        return currencyRateToForeignBank;
    }

    public void setCurrencyRateToForeignBank(String currencyRateToForeignBank) {
        this.currencyRateToForeignBank = currencyRateToForeignBank;
    }

    public String getPayCauseCode() {
        return payCauseCode;
    }

    public void setPayCauseCode(String payCauseCode) {
        this.payCauseCode = payCauseCode;
    }

    public String getPayCause() {
        return payCause;
    }

    public void setPayCause(String payCause) {
        this.payCause = payCause;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    @Override
    public String toString() {
        return  super.toString()
                + sendRequestDate
                + sendRequestSeqNo
                + customerNo
                + mAccount
                + amt
                + currency
                + sendKubun
                + senderName
                + receiverName
                + receiverAccount
                + receiverAddress
                + receiverMomo
                + countryCode
                + hostileCountry
                + receiverBicCode
                + receiverBankName
                + msgFormat
                + settlementBankCode
                + settlementBanName
                + routeBankCode1
                + routeBankName1
                + routeBankCode2
                + routeBankName2
                + routeBankCode3
                + routeBankName3
                + feeChargeAccountNo
                + feePayer
                + whichFeeAccount
                + amtForTheirCurrency
                + feeCurRate
                + feeAmtForLocal
                + feeCurRateForLocal
                + feeToForeignBank
                + currencyRateToForeignBank
                + payCauseCode
                + payCause
                + transactionNo
                + valueDate
                + reserved;
    }
}
