package com.zkteco.android.IDReader;

import com.zkteco.android.biometric.module.idcard.meta.IDCardInfo;
import com.zkteco.android.biometric.module.idcard.meta.IDPRPCardInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardInfo {
    private String cnName = "";             // 中文姓名
    private String enName = "";             // 英文姓名
    private String countryCode = "";        // 国籍代码
    private String countryName = "";        // 国籍名称
    private String sex = "";                // 性别
    private String nation = "";             // 民族
    private String birth = "";              // 出生日期
    private int idType = 0;                 // 证件类型
    private String idNum = "";              // 证件号
    private String traNum = "";             // 通行证号码
    private String idAddress = "";          // 证件住址
    private String validityTime = "";       // 证件有效日期
    private String validityStart ="";       // 证件有效开始日期
    private String validityEnd ="";         // 证件有效结束日期
    private String signNum = "";            // 签发次数
    private String signOffice = "";         // 签发机关
    private int photolength;                // 证件照片长度
    private static byte[] idImg = null;     // 证件照片

    /**
     * 传入外国人长期居住证
     *
     * @param cardType
     * @param card
     */
    public CardInfo(int cardType, IDPRPCardInfo card) {
        this.cnName = card.getCnName();
        this.enName = card.getEnName();
        this.countryCode = card.getCountryCode();
        this.countryName = card.getCountry();
        this.sex = card.getSex();
        this.nation ="";
        this.birth = card.getBirth();
        this.idType = 6;
        this.idNum = card.getId();
        this.traNum = "";
        this.idAddress = "";
        this.validityTime = card.getValidityTime();
        this.validityStart ="" ;
        this.validityEnd = "";
        this.signNum = "";
        this.signOffice = "公安部";
        this.photolength = card.getPhotolength();
        this.idImg = card.getPhoto();
    }

    /**
     * 传入居民身份证或者港澳台胞居住证
     *
     * @param cardType
     * @param idcard
     */
    public CardInfo(int cardType, IDCardInfo idcard) {
        this.cnName = idcard.getName();
        this.sex = idcard.getSex();
        if (cardType == 1) {
            this.nation = idcard.getNation();
        } else {
            this.nation = "";
        }
        this.birth = idcard.getBirth();
        this.idType = cardType;
        this.idNum = idcard.getId();
        if (cardType == 1) {
            this.traNum = "";
        } else {
            this.traNum = idcard.getPassNum();
        }
        this.idAddress = idcard.getAddress();
        this.validityTime = idcard.getValidityTime();
        if (cardType == 1) {
            this.signNum = "";
        } else {
            this.signNum = idcard.getVisaTimes() + "";
        }
        this.signOffice = idcard.getDepart();
        this.photolength = idcard.getPhotolength();
        this.idImg = idcard.getPhoto();
    }

    public String getName() {
        return this.cnName;
    }

    public String getEnName() {
        return this.enName;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public String getCountryName() {
        return this.countryName;
    }

    public String getSex() {
        return this.sex;
    }

    public String getNation() {
        return this.nation;
    }

    public String getBirth() {
        String regEX = "[^0-9]";
        Pattern pattern = Pattern.compile(regEX);
        Matcher matcher = pattern.matcher(this.birth);
        return matcher.replaceAll("").trim();
    }

    public int getIdType() {
        return this.idType;
    }

    public String getId() {
        return this.idNum;
    }

    public String getPassNum() {
        return this.traNum;
    }

    public String getAddress() {
        return this.idAddress;
    }

    public String getValidityTime() {
        return this.validityTime;
    }

    public String getSignNum() {
        return this.signNum;
    }

    public String getDepart() {
        return this.signOffice;
    }

    public int getPhotoLength() {
        return this.photolength;
    }

    public byte[] getPhoto() {
        return this.idImg;
    }
}
