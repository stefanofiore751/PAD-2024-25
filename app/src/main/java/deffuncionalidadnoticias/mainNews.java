package deffuncionalidadnoticias;

import java.util.ArrayList;

public class mainNews {

    private String status;
    private String totalResults;
    private ArrayList<ModelClass> articles;

    public mainNews(String status, String totalResults, ArrayList<ModelClass> articles) {
        this.status = status;
        this.totalResults = totalResults;
        this.articles = articles;
    }

    public ArrayList<ModelClass> getArticles() {
        return articles;
    }
}
