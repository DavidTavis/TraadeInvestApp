package com.invest.trade.view;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.invest.trade.R;
import com.invest.trade.adapter.ActiveAdapter;
import com.invest.trade.model.Active;
import com.invest.trade.util.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TechnoA on 24.10.2017.
 */

public class ActiveListFragment extends Fragment {

    public static final String ARG_ITEM_ID = "active_list";
    public static final String PAGE_URL = "https://trade.tradeinvest90.com/trade/iframe?view=table";

    private Activity activity;
    private ListView activeListView;
    private ArrayList<Active> actives;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_list, container, false);

        activeListView = (ListView) view.findViewById(R.id.list_actives);

        ParsingTask task = new ParsingTask();
        task.execute();

        return view;

    }

    private class ParsingTask extends AsyncTask<Void,Void,ArrayList<Active>>{

        @Override
        protected ArrayList<Active> doInBackground(Void... params) {

            ArrayList<Active> listActives = new ArrayList<>();

            listActives = parseDocument(listActives);

            return listActives;

        }

        private ArrayList<Active> parseDocument(ArrayList<Active> listActives) {

            Document document;

            try {
                document = Jsoup.connect(PAGE_URL).get();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
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

                //CURRENT RATE
                String currentRate = parseCurrentRate(elementMainLineRow, active);

                //CHANGE
                String change = parseChange(elementMainLineRow);

                active.setAssets(assets);
                active.setCurrentRate(currentRate);
                active.setChange(change);

                Utils.log(active.toString());

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
                Utils.log(currentRate);
                active.setBigSymbols(bigSymbols);
                return currentRate;
            }

            return currentRate;
        }

        @Override
        protected void onPostExecute(ArrayList<Active> listActives) {
            super.onPostExecute(listActives);
            actives = listActives;
            if(listActives!=null) {
                ActiveAdapter adapter = new ActiveAdapter(activity, listActives);
                activeListView.setAdapter(adapter);
                activeListView.setVisibility(View.VISIBLE);
            }else
                Toast.makeText(activity, "There is no data", Toast.LENGTH_SHORT).show();
        }
    }
}
