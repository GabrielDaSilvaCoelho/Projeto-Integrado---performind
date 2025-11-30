package com.example.minhaparte;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class QuestionModel implements Parcelable {

    private long id;
    private String questionText;
    private ArrayList<String> alternatives;
    private int correctIndex;

    // Construtor usado para enviar ao Supabase
    public QuestionModel(long id, String questionText, ArrayList<String> alternatives, int correctIndex) {
        this.id = id;
        this.questionText = questionText;
        this.alternatives = alternatives;
        this.correctIndex = correctIndex;
    }

    // Construtor usado ao criar questão NOVA (id indefinido)
    public QuestionModel(String questionText, ArrayList<String> alternatives, int correctIndex) {
        this.id = -1;
        this.questionText = questionText;
        this.alternatives = alternatives;
        this.correctIndex = correctIndex;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public ArrayList<String> getAlternatives() { return alternatives; }
    public int getCorrectIndex() { return correctIndex; }

    protected QuestionModel(Parcel in) {
        id = in.readLong();
        questionText = in.readString();
        alternatives = in.createStringArrayList();
        correctIndex = in.readInt();
    }

    public static final Creator<QuestionModel> CREATOR = new Creator<QuestionModel>() {
        @Override
        public QuestionModel createFromParcel(Parcel in) {
            return new QuestionModel(in);
        }

        @Override
        public QuestionModel[] newArray(int size) {
            return new QuestionModel[size];
        }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(questionText);
        parcel.writeStringList(alternatives);
        parcel.writeInt(correctIndex);
    }
}
