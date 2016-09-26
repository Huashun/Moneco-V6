package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liangchenzhou.moneco.R;

import java.util.ArrayList;

import Entity.AnimalDisplayItem;

/**
 * Created by liangchenzhou on 13/08/16.
 */
public class AnimalNameList extends BaseAdapter {
    private Context context;
    ArrayList<AnimalDisplayItem> arrayDis;

    public AnimalNameList(Context context, ArrayList<AnimalDisplayItem> arrayDis) {
        this.context = context;
        this.arrayDis = arrayDis;
    }

    @Override
    public int getCount() {
        return arrayDis.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayDis.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewH;
        //if (arrayDis.get(position).getImageContent() != 0 && arrayDis.get(position).getAnimalScientificName() != null){
        viewH = new ViewHolder();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.animal_pick_list, null);
        viewH.animalName = (TextView) convertView.findViewById(R.id.animalItem);
        viewH.commonItem = (TextView) convertView.findViewById(R.id.commonItem);
        viewH.recordAmount = (TextView) convertView.findViewById(R.id.recordsObserve);
        viewH.recordDate = (TextView) convertView.findViewById(R.id.recordDate);
        viewH.recordAmount.setText(arrayDis.get(position).getrecordTimes());
        viewH.animalName.setText(arrayDis.get(position).getAnimalScientificName());
        viewH.commonItem.setText(arrayDis.get(position).getAnimalCommonName());
        if (arrayDis.get(position).getRecordDate() != 0) {
            String date = String.valueOf(arrayDis.get(position).getRecordDate());
            String subMonth = date.substring(4);
            String subYear = date.substring(0, 4);
            String dateFinal = "";
            if (subMonth.length() == 1) {
                dateFinal = "0" + subMonth + "/" + subYear;
            } else {
                dateFinal = subMonth + "/" + subYear;
            }
            viewH.recordDate.setText("Last Time: " + dateFinal);
        }
        //}
        return convertView;
    }

    public class ViewHolder {
        TextView animalName, commonItem, recordAmount, recordDate;
    }
}
