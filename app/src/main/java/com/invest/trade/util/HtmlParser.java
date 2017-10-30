package com.invest.trade.util;

import com.invest.trade.data.model.Active;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TechnoA on 26.10.2017.
 */

public class HtmlParser {

    private Document document;

    public HtmlParser(String url) {
        try {
            this.document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Active> getListActives() {

        ArrayList<Active> listActives = new ArrayList<>();

        if(document==null){
            return listActives;
        }

        Elements elements = document.getElementsByClass("o-tr-line active-pr-alert");
        for(Element element: elements) {

            Element elementTable;
            Element elementMainLineRow;

            if (element.getElementsByClass("table-l").size() != 0) {
                elementTable = element.getElementsByClass("table-l").get(0);
            }else continue;

            if (elementTable != null && elementTable.getElementsByClass("main-line row").size() != 0) {
                elementMainLineRow = elementTable.getElementsByClass("main-line row").get(0);
            }else continue;

            Active active = new Active();

            //ASSETS
            String assets = element.attr("asset_name");

            //TIMESTAMP
            String timestamp = element.attr("microtime");

            //CURRENT RATE
            String currentRate = parseCurrentRate(elementMainLineRow, active);

            //CHANGE
            String change = parseChange(elementMainLineRow);

            active.setAssets(assets);
            active.setCurrentRate(currentRate);
            active.setChange(change);
            active.setTimestamp(timestamp);

            listActives.add(active);
        }

        return listActives;
    }

    private String parseChange(Element elementMainLineRow) {

        String change = null;

        Element elementCellChange = null;
        Element elementItemChange = null;

        if (elementMainLineRow != null && elementMainLineRow.getElementsByClass("cell change-td ltr").size() != 0) {
            elementCellChange = elementMainLineRow.getElementsByClass("cell change-td ltr").get(0);
        }else return null;

        if (elementCellChange!= null && elementCellChange.getElementsByClass("item").size() != 0) {
            elementItemChange = elementCellChange.getElementsByClass("item").get(0);
        }else return null;

        List<Node> listItemChangeNodes = elementItemChange.childNodes();
        if(listItemChangeNodes.size() > 0) {
            for(Node node: listItemChangeNodes){
                //CHANGE
                change = node.toString().trim();
                return change;
            }

        }
        return change;
    }

    private String parseCurrentRate(Element elementMainLineRow, Active active) {

        String currentRate = null;

        Element elementCellCurRate;
        Element elementItem;
        Element elementRateArrow;

        if (elementMainLineRow != null && elementMainLineRow.getElementsByClass("cell cur-rate up").size() != 0) {
            elementCellCurRate = elementMainLineRow.getElementsByClass("cell cur-rate up").get(0);
            active.setColor("#49aa34");
        }else
        if (elementMainLineRow != null && elementMainLineRow.getElementsByClass("cell cur-rate down").size() != 0) {
            elementCellCurRate = elementMainLineRow.getElementsByClass("cell cur-rate down").get(0);
            active.setColor("#d9050a");
        }else return null;

        if (elementCellCurRate!= null && elementCellCurRate.getElementsByClass("item").size() != 0) {
            elementItem = elementCellCurRate.getElementsByClass("item").get(0);
        }else return null;

        if (elementItem!= null && elementItem.getElementsByClass("rate-arrow").size() != 0) {
            elementRateArrow = elementItem.getElementsByClass("rate-arrow").get(0);
        }else return null;

        List<Node> listNodes = elementRateArrow.childNodes();
        if(listNodes.size() > 0){
            StringBuilder rateBuilder = new StringBuilder();
            boolean bigSymbols = false;
            for(Node node: listNodes) {

                if(node.childNodes().size()==1){
                    if (((Element) node).hasClass("big")) {
                        bigSymbols = true;
                    }
                    rateBuilder.append(node.childNodes().get(0).toString());
                }
            }

            //CURRENT RATE
            currentRate = rateBuilder.toString();
            active.setBigSymbols(bigSymbols);
            return currentRate;
        }

        return currentRate;
    }

}
