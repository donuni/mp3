package com.example.user.mymp3_2;

public class MainData {
    String fileName;
    String singer;
    String genre;
    String star;

    public MainData(String fileName, String singer, String genre, String star) {
        this.fileName = fileName;
        this.singer = singer;
        this.genre = genre;
        this.star = star;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }
}
