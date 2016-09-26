package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liangchenzhou.moneco.R;

import java.util.ArrayList;

import Entity.Species;

/**
 * Created by liangchenzhou on 7/09/16.
 */
public class MapNearbyAdapter extends BaseAdapter {
    Context context;
    ArrayList<Species> arrayNaerby;

    public MapNearbyAdapter(Context context, ArrayList<Species> arrayNaerby) {
        this.context = context;
        this.arrayNaerby = arrayNaerby;
    }

    @Override
    public int getCount() {
        return arrayNaerby.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayNaerby.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_nearby, null);
        viewHolder.imageNearby = (ImageView) view.findViewById(R.id.imageNearbyKingdom);
        viewHolder.nearbyCommon = (TextView) view.findViewById(R.id.commonNearby);
        switch (arrayNaerby.get(position).getKingdom()) {
            case "ANIMALIA":
                viewHolder.imageNearby.setImageResource(R.drawable.animal_marker);
                break;
            case "Plantae":
                viewHolder.imageNearby.setImageResource(R.drawable.plant_marker);
                break;
            case "Fungi":
                viewHolder.imageNearby.setImageResource(R.drawable.fungi_marker);
                break;
        }

        viewHolder.nearbyCommon.setText(arrayNaerby.get(position).getCommon());
        return view;
    }

    class ViewHolder {
        ImageView imageNearby;
        TextView nearbyCommon;
    }
}
