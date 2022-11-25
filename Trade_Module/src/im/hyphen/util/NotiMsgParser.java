package im.hyphen.util;

import im.hyphen.msgVO.*;

public class NotiMsgParser {
    public M0200300 m0200300(byte[] byteMsg){
        int position = 0;
        
        M0200300 m0200300 = new M0200300();
        m0200300.setTranCode           (SUtil.toHanX(byteMsg, position, 9   )); position +=  9 ; /*9자리 식별코드*/
        m0200300.setCompCode           (SUtil.toHanX(byteMsg, position, 8   )); position +=  8 ; /*8자리 업체코드*/
        m0200300.setBankCode2          (SUtil.toHanX(byteMsg, position, 2   )); position +=  2 ; /*2자리 은행코드*/
        m0200300.setMsgCode            (SUtil.toHanX(byteMsg, position, 4   )); position +=  4 ; /*7자리 전문코드*/
        m0200300.setMsgDiff             (SUtil.toHanX(byteMsg, position, 3   )); position +=  3 ; /*7자리 전문코드*/
        m0200300.setTransCnt           (SUtil.toHanX(byteMsg, position, 1   )); position +=  1 ; /*1자리 송신횟수*/
        m0200300.setSeqNo              (SUtil.toHanX(byteMsg, position, 6   )); position +=  6 ; /*6자리 전문번호*/
        m0200300.setSendDate           (SUtil.toHanX(byteMsg, position, 8   )); position +=  8 ; /*8자리 전송일자*/
        m0200300.setSendTime           (SUtil.toHanX(byteMsg, position, 6   )); position +=  6 ; /*6자리 전송시간*/
        m0200300.setRespCode           (SUtil.toHanX(byteMsg, position, 4   )); position +=  4 ; /*4자리 응답코드*/
        m0200300.setBankRespCode       (SUtil.toHanX(byteMsg, position, 4   )); position +=  4 ; /*4자리 은행응답코드*/
        m0200300.setInqDate            (SUtil.toHanX(byteMsg, position, 8   )); position +=  8 ; /*8자리 조회일자*/
        m0200300.setInqNo              (SUtil.toHanX(byteMsg, position, 6   )); position +=  6 ; /*6자리 조회번호*/
        m0200300.setBankSeqNo          (SUtil.toHanX(byteMsg, position, 15   )); position += 15 ; /*15자리 은행전문번호*/
        m0200300.setBankCode           (SUtil.toHanX(byteMsg, position, 3   )); position +=   3; /*3자리 은행코드*/
        m0200300.setReserved           (SUtil.toHanX(byteMsg, position, 13  )); position +=  13 ; /*13자리 예비*/


        m0200300.setCorp_acct_no    ( SUtil.toHanX(byteMsg, position, 15 )); position +=  15;
        m0200300.setComp_cnt        (SUtil.toHanX(byteMsg, position, 2   )); position +=  2 ;
        m0200300.setDeal_sele       (SUtil.toHanX(byteMsg, position, 2   )); position +=  2 ;
        m0200300.setIn_bank_code    (SUtil.toHanX(byteMsg, position, 2   )); position +=  2 ;
        m0200300.setTotal_amt       (SUtil.toHanX(byteMsg, position, 13  )); position +=  13;
        m0200300.setBalance         (SUtil.toHanX(byteMsg, position, 13  )); position +=  13;
        m0200300.setBran_code       (SUtil.toHanX(byteMsg, position, 6   )); position +=  6 ;
        m0200300.setCust_name       (SUtil.toHanX(byteMsg, position, 14  )); position +=  14;
        m0200300.setCheck_no        (SUtil.toHanX(byteMsg, position, 10  )); position +=  10;
        m0200300.setCash            (SUtil.toHanX(byteMsg, position, 13  )); position +=  13;
        m0200300.setOut_bank_check  (SUtil.toHanX(byteMsg, position, 13  )); position +=  13;
        m0200300.setEtc_check       (SUtil.toHanX(byteMsg, position, 13  )); position +=  13;
        m0200300.setVr_acct_no      ( SUtil.toHanX(byteMsg, position, 16 )); position +=  16;
        m0200300.setDeal_date       (SUtil.toHanX(byteMsg, position, 8   )); position +=  8 ;
        m0200300.setDeal_time       (SUtil.toHanX(byteMsg, position, 6   )); position +=  6 ;
        m0200300.setSerial_no       (SUtil.toHanX(byteMsg, position, 6   )); position +=  6 ;
        m0200300.setIn_bank_code_3  (SUtil.toHanX(byteMsg, position, 3   )); position +=  3 ;
        m0200300.setBran_code_3     (SUtil.toHanX(byteMsg, position, 7   )); position +=  7 ;
        m0200300.setFiller_2        (SUtil.toHanX(byteMsg, position, 38  )); position +=  38;
        return m0200300;
    }

    public M0900100 m0900100(byte[] byteMsg){
        M0900100 m0900100 = new M0900100();

        m0900100.setSend_date  (SUtil.toHanX(byteMsg, 33, 8     ));
        m0900100.setBank_code  (SUtil.toHanX(byteMsg, 84, 3     ));
        m0900100.setVr_acct_no (SUtil.toHanX(byteMsg, 100, 16   ));
        m0900100.setAmt        (SUtil.toHanX(byteMsg, 170, 13   ));
        return m0900100;
    }

    public M3000700 m3000700(byte[] byteMsg){
        M3000700 m3000700 = new M3000700();
        System.out.println("m0300700");
        int position = 0;
        m3000700.setTrCode(SUtil.toHanX(byteMsg, position, 9   )); position +=  9; /* Transaction Code [9] */
        m3000700.setCompCode(SUtil.toHanX(byteMsg, position, 12   )); position +=  12; /* 은행부여 업체코드 [12] */
        m3000700.setBankCode(SUtil.toHanX(byteMsg, position, 2   )); position +=  2; /* 은행 코드 [2] */
        m3000700.setMsgCode(SUtil.toHanX(byteMsg, position, 4   )); position +=  4; /* 전문 코드 [4] */
        m3000700.setKubun(SUtil.toHanX(byteMsg, position, 3   )); position +=  3; /* 업무 구분 [3] */
        m3000700.setSendCnt(SUtil.toHanX(byteMsg, position, 1   )); position +=  1; /* 송신 횟수 [1] */
        m3000700.setMsgNo(SUtil.toHanX(byteMsg, position, 6   )); position +=  6; /* 전문 번호 [6] */
        m3000700.setSendDate(SUtil.toHanX(byteMsg, position, 8   )); position +=  8; /* 송신 일자 [8] */
        m3000700.setSendTime(SUtil.toHanX(byteMsg, position, 6   )); position +=  6; /* 송신 시간 [6] */
        m3000700.setRetCode(SUtil.toHanX(byteMsg, position, 4   )); position +=  4; /* 응답 코드 [4] */
        m3000700.setSikByulCode(SUtil.toHanX(byteMsg, position, 9   )); position +=  9; /* 식별 코드 [9] */
        m3000700.setSdsArea(SUtil.toHanX(byteMsg, position, 15   )); position +=  15; /* SDS 영역 [15] */
        m3000700.setCompArea(SUtil.toHanX(byteMsg, position, 11   )); position +=  11; /* 업체 영역 [11] */
        m3000700.setBankArea(SUtil.toHanX(byteMsg, position, 10   )); position +=  10; /* 은행 영역 [10] */


        m3000700.setSendRequestDate(SUtil.toHanX(byteMsg, position, 8   )); position +=  8;                 /* 외화송금의뢰일자*/
        m3000700.setSendRequestSeqNo(SUtil.toHanX(byteMsg, position, 6   )); position +=  6;               /* 외화송금의뢰전문번호*/
        m3000700.setCustomerNo(SUtil.toHanX(byteMsg, position, 10   )); position +=  10;                    /* 고객번호*/
        m3000700.setMAccount(SUtil.toHanX(byteMsg, position, 16   )); position +=  16;                      /* 출금계좌번호(송금액)*/
        m3000700.setAmt(SUtil.toHanX(byteMsg, position, 15   )); position +=  15;                           /* 송금금액(외화금액) 소수점이하 3자리 포함 12자리 정수, 소수점 이하 3자리*/
        m3000700.setCurrency(SUtil.toHanX(byteMsg, position, 3   )); position +=  3;                      /* 송금통화*/
        m3000700.setSendKubun(SUtil.toHanX(byteMsg, position, 1   )); position +=  1;                     /* 송금구분*/
        m3000700.setSenderName(SUtil.toHanX(byteMsg, position, 35   )); position +=  35;                    /* 송금인명*/
        m3000700.setReceiverName(SUtil.toHanX(byteMsg, position, 35   )); position +=  35;                  /* 수취인명*/
        m3000700.setReceiverAccount(SUtil.toHanX(byteMsg, position, 35   )); position +=  35;               /* 수취인계좌번호*/
        m3000700.setReceiverAddress(SUtil.toHanX(byteMsg, position, 35*3   )); position +=  35*3;               /* 수취인주소*/
        m3000700.setReceiverMomo(SUtil.toHanX(byteMsg, position, 4*35   )); position +=  35*4;                  /* 수취인앞 지시사항*/
        m3000700.setCountryCode(SUtil.toHanX(byteMsg, position, 2   )); position +=  2;                 /* 상대국 코드*/
        m3000700.setHostileCountry(SUtil.toHanX(byteMsg, position, 1   )); position +=  1;                /* 적성국가앞 송금 'Y'*/
        m3000700.setReceiverBicCode(SUtil.toHanX(byteMsg, position, 11   )); position +=  11;               /* 수취은행 코드, 수취인 앞 송금액을 지급한 은행*/
        m3000700.setReceiverBankName(SUtil.toHanX(byteMsg, position, 4*35   )); position +=  35*4;              /* 수취은행 은행명 및 주소*/
        m3000700.setMsgFormat(SUtil.toHanX(byteMsg, position, 1   )); position +=  1;                     /* 송금 전문형태 1 mt100 2 mt100 & mt202*/
        m3000700.setSettlementBankCode(SUtil.toHanX(byteMsg, position, 11   )); position +=  11;            /* 결제은행 bic코드 */
        m3000700.setSettlementBanName(SUtil.toHanX(byteMsg, position, 35   )); position +=  35;             /* 결제은행*/
        m3000700.setRouteBankCode1(SUtil.toHanX(byteMsg, position, 11   )); position +=  11;               /* 송금경유은행1 코드*/
        m3000700.setRouteBankName1(SUtil.toHanX(byteMsg, position, 35   )); position +=  35;                /* 송금경유은행1*/
        m3000700.setRouteBankCode2(SUtil.toHanX(byteMsg, position, 11   )); position +=  11;                /* 송금경유은행2 코드*/
        m3000700.setRouteBankName2(SUtil.toHanX(byteMsg, position, 35   )); position +=  35;                /* 송금경유은행2*/
        m3000700.setRouteBankCode3(SUtil.toHanX(byteMsg, position, 11   )); position +=  11;                /* 송금경유은행3 코드*/
        m3000700.setRouteBankName3(SUtil.toHanX(byteMsg, position, 35   )); position +=  35;                /* 송금경유은행3*/
        m3000700.setFeeChargeAccountNo(SUtil.toHanX(byteMsg, position, 16   )); position +=  16;            /* 수수료 인출계좌번호*/
        m3000700.setFeePayer(SUtil.toHanX(byteMsg, position, 1   )); position +=  1;                      /* 해외은행 수수료 부담자 1 : 수취인부담, 2송금인부담*/
        m3000700.setWhichFeeAccount(SUtil.toHanX(byteMsg, position, 1   )); position +=  1;               /* 해외은행 수수료 인출계좌 지정 1 : 출금계좌, 2 : 수수료 인출계좌*/
        m3000700.setAmtForTheirCurrency(SUtil.toHanX(byteMsg, position, 15   )); position +=  15;           /* 송금액(출금계좌 통화기준)*/
        m3000700.setFeeCurRate(SUtil.toHanX(byteMsg, position, 7   )); position +=  7;                    /* 송금액 인출시 적용 환율*/
        m3000700.setFeeAmtForLocal(SUtil.toHanX(byteMsg, position, 15   )); position +=  15;                /* 송금수수료(국내)*/
        m3000700.setFeeCurRateForLocal(SUtil.toHanX(byteMsg, position, 7   )); position +=  7;            /* 송금수수료 인출 적용환율(국내)*/
        m3000700.setFeeToForeignBank(SUtil.toHanX(byteMsg, position, 15   )); position +=  15;               /* 해외은행앞 지급수수료*/
        m3000700.setCurrencyRateToForeignBank(SUtil.toHanX(byteMsg, position, 7   )); position +=  7;      /* 해외은행 수수료 인출 적용 환율*/
        m3000700.setPayCauseCode(SUtil.toHanX(byteMsg, position, 3   )); position +=  3;                  /* 지급사유코드*/
        m3000700.setPayCause(SUtil.toHanX(byteMsg, position, 35   )); position +=  35;                      /* 지급사유*/
        m3000700.setTransactionNo(SUtil.toHanX(byteMsg, position, 20   )); position +=  20;                 /* 거래번호*/
        m3000700.setValueDate(SUtil.toHanX(byteMsg, position, 8   )); position +=  8;                     /* */
        m3000700.setReserved(SUtil.toHanX(byteMsg, position, 1002   )); position +=  1002;                      /* 예비*/
     /*   System.out.println(m3000700.getSendRequestDate());               *//* 외화송금의뢰일자*//*
        System.out.println(m3000700.getSendRequestSeqNo());              *//* 외화송금의뢰전문번호*//*
        System.out.println(m3000700.getCustomerNo());                    *//* 고객번호*//*
        System.out.println(m3000700.getmAccount());                      *//* 출금계좌번호(송금액)*//*
        System.out.println(m3000700.getAmt());                           *//* 송금금액(외화금액) 소수점이하 3자리 포함 12자리 정수, 소수점 이하 3자리*//*
        System.out.println(m3000700.getCurrency());                      *//* 송금통화*//*
        System.out.println(m3000700.getSendKubun());                     *//* 송금구분*//*
        System.out.println(m3000700.getSenderName());                    *//* 송금인명*//*
        System.out.println(m3000700.getReceiverName());                  *//* 수취인명*//*
        System.out.println(m3000700.getReceiverAccount());               *//* 수취인계좌번호*//*
        System.out.println(m3000700.getReceiverAddress());               *//* 수취인주소*//*
        System.out.println(m3000700.getReceiverMomo());                  *//* 수취인앞 지시사항*//*
        System.out.println(m3000700.getCountryCode());                   *//* 상대국 코드*//*
        System.out.println(m3000700.getHostileCountry());                *//* 적성국가앞 송금 'Y'*//*
        System.out.println(m3000700.getReceiverBicCode());               *//* 수취은행 코드, 수취인 앞 송금액을 지급한 은행*//*
        System.out.println(m3000700.getReceiverBankName());              *//* 수취은행 은행명 및 주소*//*
        System.out.println(m3000700.getMsgFormat());                     *//* 송금 전문형태 1 mt100 2 mt100 & mt202*//*
        System.out.println(m3000700.getSettlementBankCode());            *//* 결제은행 bic코드 *//*
        System.out.println(m3000700.getSettlementBanName());             *//* 결제은행*//*
        System.out.println(m3000700.getRouteBankCode1());                *//* 송금경유은행1 코드*//*
        System.out.println(m3000700.getRouteBankName1());                *//* 송금경유은행1*//*
        System.out.println(m3000700.getRouteBankCode2());                *//* 송금경유은행2 코드*//*
        System.out.println(m3000700.getRouteBankName2());                *//* 송금경유은행2*//*
        System.out.println(m3000700.getRouteBankCode3());                *//* 송금경유은행3 코드*//*
        System.out.println(m3000700.getRouteBankName3());                *//* 송금경유은행3*//*
        System.out.println(m3000700.getFeeChargeAccountNo());            *//* 수수료 인출계좌번호*//*
        System.out.println(m3000700.getFeePayer());                      *//* 해외은행 수수료 부담자 1 : 수취인부담, 2송금인부담*//*
        System.out.println(m3000700.getWhichFeeAccount());               *//* 해외은행 수수료 인출계좌 지정 1 : 출금계좌, 2 : 수수료 인출계좌*//*
        System.out.println(m3000700.getAmtForTheirCurrency());           *//* 송금액(출금계좌 통화기준)*//*
        System.out.println(m3000700.getFeeCurRate());                    *//* 송금액 인출시 적용 환율*//*
        System.out.println(m3000700.getFeeAmtForLocal());                *//* 송금수수료(국내)*//*
        System.out.println(m3000700.getFeeCurRateForLocal());            *//* 송금수수료 인출 적용환율(국내)*//*
        System.out.println(m3000700.getFeeToForeignBank());               *//* 해외은행앞 지급수수료*//*
        System.out.println(m3000700.getCurrencyRateToForeignBank());      *//* 해외은행 수수료 인출 적용 환율*//*
        System.out.println(m3000700.getPayCauseCode());                  *//* 지급사유코드*//*
        System.out.println(m3000700.getPayCause());                      *//* 지급사유*//*
        System.out.println(m3000700.getTransactionNo());                 *//* 거래번호*//*
        System.out.println(m3000700.getValueDate());                     *//* *//*
        System.out.println(m3000700.getReserved());                      *//* 예비*/
        return m3000700;
    }

    public M8000601 m8000601(byte[] byteMsg){
        M8000601 m8000601 = new M8000601();
        int position = 0;
        m8000601.setTrCode(SUtil.toHanX(byteMsg, position, 9   )); position +=  9; /* Transaction Code [9] */
        m8000601.setCompCode(SUtil.toHanX(byteMsg, position, 12   )); position +=  12; /* 은행부여 업체코드 [12] */
        m8000601.setBankCode(SUtil.toHanX(byteMsg, position, 2   )); position +=  2; /* 은행 코드 [2] */
        m8000601.setMsgCode(SUtil.toHanX(byteMsg, position, 4   )); position +=  4; /* 전문 코드 [4] */
        m8000601.setKubun(SUtil.toHanX(byteMsg, position, 3   )); position +=  3; /* 업무 구분 [3] */
        m8000601.setSendCnt(SUtil.toHanX(byteMsg, position, 1   )); position +=  1; /* 송신 횟수 [1] */
        m8000601.setMsgNo(SUtil.toHanX(byteMsg, position, 6   )); position +=  6; /* 전문 번호 [6] */
        m8000601.setSendDate(SUtil.toHanX(byteMsg, position, 8   )); position +=  8; /* 송신 일자 [8] */
        m8000601.setSendTime(SUtil.toHanX(byteMsg, position, 6   )); position +=  6; /* 송신 시간 [6] */
        m8000601.setRetCode(SUtil.toHanX(byteMsg, position, 4   )); position +=  4; /* 응답 코드 [4] */
        m8000601.setSikByulCode(SUtil.toHanX(byteMsg, position, 9   )); position +=  9; /* 식별 코드 [9] */
        m8000601.setSdsArea(SUtil.toHanX(byteMsg, position, 15   )); position +=  15; /* SDS 영역 [15] */
        m8000601.setCompArea(SUtil.toHanX(byteMsg, position, 11   )); position +=  11; /* 업체 영역 [11] */
        m8000601.setBankArea(SUtil.toHanX(byteMsg, position, 10   )); position +=  10; /* 은행 영역 [10] */


        m8000601.setAccount(SUtil.toHanX(byteMsg, position, 16   )); position +=  16;
        m8000601.setInOutKubun(SUtil.toHanX(byteMsg, position, 2   )); position +=  2;
        m8000601.setTranDate(SUtil.toHanX(byteMsg, position, 8   )); position +=  8;
        m8000601.setTranTime(SUtil.toHanX(byteMsg, position, 6   )); position +=  6;
        m8000601.setTranSeqNo(SUtil.toHanX(byteMsg, position, 5   )); position +=  5;
        m8000601.setCurrency(SUtil.toHanX(byteMsg, position, 3   )); position +=  3;
        m8000601.setAmt(SUtil.toHanX(byteMsg, position, 15   )); position +=  15;
        m8000601.setBalance(SUtil.toHanX(byteMsg, position, 15   )); position +=  15;
        m8000601.setAccountMemo(SUtil.toHanX(byteMsg, position, 14   )); position +=  14;
        m8000601.setCancelTranDate(SUtil.toHanX(byteMsg, position, 8   )); position +=  8;
        m8000601.setCancelOriSeqNo(SUtil.toHanX(byteMsg, position, 6   )); position +=  6;
        m8000601.setBankCode3(SUtil.toHanX(byteMsg, position, 3   )); position +=  3;
        m8000601.setBranchCode(SUtil.toHanX(byteMsg, position, 3   )); position +=  3;
        m8000601.setBranchName(SUtil.toHanX(byteMsg, position, 10   )); position +=  10;
        m8000601.setAccountMemo2(SUtil.toHanX(byteMsg, position, 24   )); position +=  24;
        m8000601.setBranchGiroCodel(SUtil.toHanX(byteMsg, position, 7   )); position +=  7;
        m8000601.setTranKubun(SUtil.toHanX(byteMsg, position, 2   )); position +=  2;
        m8000601.setSignAfterTran(SUtil.toHanX(byteMsg, position, 1   )); position +=  1;
        m8000601.setCustomerName(SUtil.toHanX(byteMsg, position, 12   )); position +=  12;
        m8000601.setReserved(SUtil.toHanX(byteMsg, position, 1740   )); position +=  1740;



        return m8000601;
    }

    public M0400100 m0400100(byte[] byteMsg ){
        M0400100 m0400100 = new M0400100();
        int position = 0;

        m0400100.setTranCode           (SUtil.toHanX(byteMsg, position, 9   )); position +=  9 ; /*9자리 식별코드*/
        m0400100.setCompCode           (SUtil.toHanX(byteMsg, position, 8   )); position +=  8 ; /*8자리 업체코드*/
        m0400100.setBankCode2          (SUtil.toHanX(byteMsg, position, 2   )); position +=  2 ; /*2자리 은행코드*/
        m0400100.setMsgCode            (SUtil.toHanX(byteMsg, position, 4   )); position +=  4 ; /*7자리 전문코드*/
        m0400100.setMsgDiff             (SUtil.toHanX(byteMsg, position, 3   )); position +=  3 ; /*7자리 전문코드*/
        m0400100.setTransCnt           (SUtil.toHanX(byteMsg, position, 1   )); position +=  1 ; /*1자리 송신횟수*/
        m0400100.setSeqNo              (SUtil.toHanX(byteMsg, position, 6   )); position +=  6 ; /*6자리 전문번호*/
        m0400100.setSendDate           (SUtil.toHanX(byteMsg, position, 8   )); position +=  8 ; /*8자리 전송일자*/
        m0400100.setSendTime           (SUtil.toHanX(byteMsg, position, 6   )); position +=  6 ; /*6자리 전송시간*/
        m0400100.setRespCode           (SUtil.toHanX(byteMsg, position, 4   )); position +=  4 ; /*4자리 응답코드*/
        m0400100.setBankRespCode       (SUtil.toHanX(byteMsg, position, 4   )); position +=  4 ; /*4자리 은행응답코드*/
        m0400100.setInqDate            (SUtil.toHanX(byteMsg, position, 8   )); position +=  8 ; /*8자리 조회일자*/
        m0400100.setInqNo              (SUtil.toHanX(byteMsg, position, 6   )); position +=  6 ; /*6자리 조회번호*/
        m0400100.setBankSeqNo          (SUtil.toHanX(byteMsg, position, 15   )); position += 15 ; /*15자리 은행전문번호*/
        m0400100.setBankCode           (SUtil.toHanX(byteMsg, position, 3   )); position +=   3; /*3자리 은행코드*/
        m0400100.setReserved           (SUtil.toHanX(byteMsg, position, 13  )); position +=  13 ; /*13자리 예비*/


        m0400100.setOrg_seq_no      (SUtil.toHanX(byteMsg, position, 6   )); position +=  6 ;
        m0400100.setOut_account_no  (SUtil.toHanX(byteMsg, position, 15  )); position +=  15;
        m0400100.setIn_account_no   (SUtil.toHanX(byteMsg, position, 15  )); position +=  15;
        m0400100.setIn_money        (SUtil.toHanX(byteMsg, position, 13  )); position +=  13;
        m0400100.setIn_bank_code    (SUtil.toHanX(byteMsg, position, 2   )); position +=  2 ;
        m0400100.setNor_money       (SUtil.toHanX(byteMsg, position, 13  )); position +=  13;
        m0400100.setAbnor_money     (SUtil.toHanX(byteMsg, position, 13  )); position +=  13;
        m0400100.setDiv_proc_cnt    (SUtil.toHanX(byteMsg, position, 2   )); position +=  2 ;
        m0400100.setDiv_proc_no     (SUtil.toHanX(byteMsg, position, 2   )); position +=  2 ;
        m0400100.setTa_no           (SUtil.toHanX(byteMsg, position, 6   )); position +=  6 ;
        m0400100.setNot_in_amt      (SUtil.toHanX(byteMsg, position, 9   )); position +=  9 ;
        m0400100.setErr_code        (SUtil.toHanX(byteMsg, position, 3   )); position +=  3 ;
        m0400100.setIn_bank_code_3  (SUtil.toHanX(byteMsg, position, 3   )); position +=  3 ;
        m0400100.setFiller_2        (SUtil.toHanX(byteMsg, position, 98  )); position +=  98;

        return m0400100;

    }
}
