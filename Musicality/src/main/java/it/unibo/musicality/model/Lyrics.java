package it.unibo.musicality.model;

public class Lyrics {
    private int idLyrics;
    private String text;
    private int idContent;

    public Lyrics(String text, int idContenuto) {
        this.text = text;
        this.idContent = idContenuto;
    }

    public Lyrics(int idLyrics, String text, int idContenuto) {
        this.idLyrics = idLyrics;
        this.text = text;
        this.idContent = idContenuto;
    }

    public int getIdLyrics() {return idLyrics;}
    public void setIdLyrics(int idLyrics) {this.idLyrics = idLyrics;}
    public String getText() {return text;}
    public void setText(String text) {this.text = text;}
    public int getIdContent() {return idContent;}
    public void setIdContent(int idContent) {this.idContent = idContent;}
}