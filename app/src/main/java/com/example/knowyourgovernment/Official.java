package com.example.knowyourgovernment;

import java.io.Serializable;

public class Official implements Serializable {

    private String officeName;
    private String officialName;
    private String partyName;

    private String address;
    private String phone;
    private String url;
    private String email;
    private String photoUrl;
    private String googlePlus;
    private String facebook;
    private String twitter;
    private String youTube;

    public static final String OMITTED = "Section Omitted";
    public static final String UNKNOWN = "Nonpartisan";


    Official(String officeName, String officialName, String partyName,
             String address, String phone, String url,
             String email, String photoUrl, String googlePlus,
             String facebook, String twitter, String youTube) {

        this.officeName = officeName;
        this.officialName = officialName;
        this.partyName = partyName;
        this.address = address;
        this.phone = phone;
        this.url = url;
        this.email = email;
        this.photoUrl = photoUrl;
        this.googlePlus = googlePlus;
        this.facebook = facebook;
        this.twitter = twitter;
        this.youTube = youTube;
    }

    Official() {

        this.officeName = OMITTED;
        this.officialName = OMITTED;
        this.partyName = UNKNOWN;
        this.address = OMITTED;
        this.phone = OMITTED;
        this.url = OMITTED;
        this.email = OMITTED;
        this.photoUrl = OMITTED;
        this.googlePlus = OMITTED;
        this.facebook = OMITTED;
        this.twitter = OMITTED;
        this.youTube = OMITTED;
    }


    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getGooglePlus() {
        return googlePlus;
    }

    public void setGooglePlus(String googlePlus) {
        this.googlePlus = googlePlus;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getYouTube() {
        return youTube;
    }

    public void setYouTube(String youTube) {
        this.youTube = youTube;
    }

    @Override
    public String toString() {
        return "Official{" +
                "officeName='" + officeName + '\'' +
                ", officialName='" + officialName + '\'' +
                ", partyName='" + partyName + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", url='" + url + '\'' +
                ", email='" + email + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", googlePlus='" + googlePlus + '\'' +
                ", facebook='" + facebook + '\'' +
                ", twitter='" + twitter + '\'' +
                ", youTube='" + youTube + '\'' +
                '}';
    }
}

