package im.hyphen.msgVO;

public class M3000700  extends SDSHeader{
    private String sendRequestDate;               /* ��ȭ�۱��Ƿ�����*/
    private String sendRequestSeqNo;              /* ��ȭ�۱��Ƿ�������ȣ*/
    private String customerNo;                    /* ����ȣ*/
    private String mAccount;                      /* ��ݰ��¹�ȣ(�۱ݾ�)*/
    private String amt;                           /* �۱ݱݾ�(��ȭ�ݾ�) �Ҽ������� 3�ڸ� ���� 12�ڸ� ����, �Ҽ��� ���� 3�ڸ�*/
    private String currency;                      /* �۱���ȭ*/
    private String sendKubun;                     /* �۱ݱ���*/
    private String senderName;                    /* �۱��θ�*/
    private String receiverName;                  /* �����θ�*/
    private String receiverAccount;               /* �����ΰ��¹�ȣ*/
    private String receiverAddress;               /* �������ּ�*/
    private String receiverMomo;                  /* �����ξ� ���û���*/
    private String countryCode;                   /* ��뱹 �ڵ�*/
    private String hostileCountry;                /* ���������� �۱� 'Y'*/
    private String receiverBicCode;               /* �������� �ڵ�, ������ �� �۱ݾ��� ������ ����*/
    private String receiverBankName;              /* �������� ����� �� �ּ�*/
    private String msgFormat;                     /* �۱� �������� 1 mt100 2 mt100 & mt202*/
    private String settlementBankCode;            /* �������� bic�ڵ� */
    private String settlementBanName;             /* ��������*/
    private String routeBankCode1;                /* �۱ݰ�������1 �ڵ�*/
    private String routeBankName1;                /* �۱ݰ�������1*/
    private String routeBankCode2;                /* �۱ݰ�������2 �ڵ�*/
    private String routeBankName2;                /* �۱ݰ�������2*/
    private String routeBankCode3;                /* �۱ݰ�������3 �ڵ�*/
    private String routeBankName3;                /* �۱ݰ�������3*/
    private String feeChargeAccountNo;            /* ������ ������¹�ȣ*/
    private String feePayer;                      /* �ؿ����� ������ �δ��� 1 : �����κδ�, 2�۱��κδ�*/
    private String whichFeeAccount;               /* �ؿ����� ������ ������� ���� 1 : ��ݰ���, 2 : ������ �������*/
    private String amtForTheirCurrency;           /* �۱ݾ�(��ݰ��� ��ȭ����)*/
    private String feeCurRate;                    /* �۱ݾ� ����� ���� ȯ��*/
    private String feeAmtForLocal;                /* �۱ݼ�����(����)*/
    private String feeCurRateForLocal;            /* �۱ݼ����� ���� ����ȯ��(����)*/
    private String feeToForeignBank;               /* �ؿ������ ���޼�����*/
    private String currencyRateToForeignBank;      /* �ؿ����� ������ ���� ���� ȯ��*/
    private String payCauseCode;                  /* ���޻����ڵ�*/
    private String payCause;                      /* ���޻���*/
    private String transactionNo;                 /* �ŷ���ȣ*/
    private String valueDate;                     /* */
    private String reserved;                      /* ����*/

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
