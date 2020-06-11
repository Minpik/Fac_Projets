package com.example.rhodier.mplrss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class MyHandler extends DefaultHandler {
    private String node;
    private String rssTitle;
    private Item item;
    private List<Item> itemList;

    public MyHandler() {
        itemList = new ArrayList<>();
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        node = qName;
        if (qName.equals("item"))
            item = new Item();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("item"))
            itemList.add(item);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);

        String str = new String(ch, start, length);
        if (item != null) {
            if (node.equals("title")) {
                item.setTitle(str);
            } else if (node.equals("description")) {
                item.setDescription(str);
            } else if (node.equals("link")) {
                item.setLink(str);
            } else if (node.equals("pubDate")) {
                try {
                    SimpleDateFormat formatter =
                            new SimpleDateFormat("EEEE dd MMMM yyyy à HH:mm",
                                    new Locale("fr", "fr_FR"));
                    Date date = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss").parse(str);
                    item.setPubDate(formatter.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else if (node.equals("title"))
            rssTitle = str;
    }

    String getRssTitle() {
        return rssTitle;
    }

    List<Item> getItemList() {
        return itemList;
    }
}
