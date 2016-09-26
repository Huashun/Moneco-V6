package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.liangchenzhou.moneco.R;

import java.util.ArrayList;

import Entity.History;

/**
 * Created by liangchenzhou on 1/09/16.
 */
public class HistoryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<History> arrayList;

    public HistoryAdapter(Context context, ArrayList<History> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_history, null);
        holder.historyScientific = (TextView) view.findViewById(R.id.historyItemScientific);
        holder.historyCommon = (TextView) view.findViewById(R.id.historyItemCommon);
        holder.historyDate = (TextView) view.findViewById(R.id.historyItemDate);
        if (arrayList != null){
            holder.historyScientific.setText(arrayList.get(position).getScientificName());
            holder.historyCommon.setText(arrayList.get(position).getCommonName());
            holder.historyDate.setText(arrayList.get(position).getDate());

        }
        return view;
    }

    class ViewHolder{
        TextView historyScientific, historyCommon, historyDate;
    }
}
