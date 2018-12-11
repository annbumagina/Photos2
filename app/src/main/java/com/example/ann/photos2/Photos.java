package com.example.ann.photos2;

public class Photos {
    String title;
    String link;
    String description;
    String modified;
    String generator;
    Items[] items;

    static public class Items {
        String title;
        String link;
        Media media;
        String date_taken;
        String description;
        String published;
        String author;
        String author_id;
        String tags;

        static public class Media { String m; };
    }
}
