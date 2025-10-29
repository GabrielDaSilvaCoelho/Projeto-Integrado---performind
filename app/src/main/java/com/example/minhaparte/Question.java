package com.example.minhaparte;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class Question implements Parcelable {
    private String questionText;
    private ArrayList<String> alternatives;
    private int correctIndex;

    public Question(String questionText, ArrayList<String> alternatives, int correctIndex) {
        this.questionText = questionText;
        this.alternatives = alternatives;
        this.correctIndex = correctIndex;
    }

    public String getQuestionText() { return questionText; }
    public ArrayList<String> getAlternatives() { return alternatives; }
    public int getCorrectIndex() { return correctIndex; }

    protected Question(Parcel in) {
        questionText = in.readString();
        alternatives = in.createStringArrayList();
        correctIndex = in.readInt();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override public Question createFromParcel(Parcel in) { return new Question(in); }
        @Override public Question[] newArray(int size) { return new Question[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(questionText);
        parcel.writeStringList(alternatives);
        parcel.writeInt(correctIndex);
    }
}
