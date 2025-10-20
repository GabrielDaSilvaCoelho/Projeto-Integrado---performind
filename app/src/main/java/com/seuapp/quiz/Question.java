package com.seuapp.quiz;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Question implements Parcelable {
    public String enunciado;
    public List<String> alternativas; // 5 itens (1 correta + 4 erradas)
    public int idxCorreta;            // 0..4

    public Question(String enunciado, List<String> alternativas, int idxCorreta) {
        this.enunciado = enunciado;
        this.alternativas = alternativas;
        this.idxCorreta = idxCorreta;
    }

    protected Question(Parcel in) {
        enunciado = in.readString();
        alternativas = new ArrayList<>();
        in.readStringList(alternativas);
        idxCorreta = in.readInt();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override public Question createFromParcel(Parcel in) { return new Question(in); }
        @Override public Question[] newArray(int size) { return new Question[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(enunciado);
        dest.writeStringList(alternativas);
        dest.writeInt(idxCorreta);
    }
}
