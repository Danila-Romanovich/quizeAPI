package ru.quize.api.params;

public class GameParam {
    private String url = "https://opentdb.com/api.php?amount=10";
    private int numCategory;
    private String difficulty;
    private String answerType;

    // Конструкторы

    public GameParam(){
        this.numCategory = 0;
        this.difficulty = "all";
        this.answerType = "all";
    }

    public GameParam(int numCategory){
        this.numCategory = numCategory;
        this.difficulty = "all";
        this.answerType = "all";
    }

    public GameParam(int numCategory, String difficulty){
        this.numCategory = numCategory;
        this.difficulty = difficulty;
        this.answerType = "all";
    }

    public GameParam(int numCategory, String difficulty, String answerType){
        this.numCategory = numCategory;
        this.difficulty = difficulty;
        this.answerType = answerType;
    }

    // Геттеры и Сеттеры

    public int getNumCategory() {
        return numCategory;
    }

    public void setNumCategory(int numCategory) {
        this.numCategory = numCategory;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    // Методы

    public String getUrl() {
        return (this.url + checkNumCategory() + checkDifficulty() + checkType());
    }

    private String checkNumCategory() {
        if (this.numCategory > 8 && this.numCategory < 33) {
            return ("&category=" + this.numCategory);
        } else return "";
    }

    private String checkDifficulty() {
        if (this.difficulty == "all") {
            return "";
        } else return ("&difficulty=" + this.difficulty);
    }

    private String checkType() {
        if (this.answerType == "all") {
            return "";
        } else return ("&type=" + this.answerType);
    }
}
