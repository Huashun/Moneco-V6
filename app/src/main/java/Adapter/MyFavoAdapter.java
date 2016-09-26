package Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liangchenzhou.moneco.MapsActivity;
import com.example.liangchenzhou.moneco.R;

import java.util.ArrayList;

import Entity.Favorite;

/**
 * Created by lizoe on 27/08/2016.
 */
public class MyFavoAdapter extends BaseAdapter {


    Context context;
    ArrayList<Favorite> myfavorite;
    private static LayoutInflater inflater=null;

    public MyFavoAdapter(Context context, ArrayList<Favorite> myfavorite) {
        this.context = context;
        this.myfavorite = myfavorite;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class Holder
    {
        TextView tvDate, tvCommon, tvScien;
        ImageView img;
        ImageButton map,delete;
    }
    @Override
    public int getCount() {
        return myfavorite.size();
    }

    @Override
    public Object getItem(int position) {
        return myfavorite.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.listview_favorite, null);
        holder.tvCommon = (TextView)rowView.findViewById(R.id.favo_common);
        holder.tvDate = (TextView)rowView.findViewById(R.id.favo_date);
        holder.img = (ImageView) rowView.findViewById(R.id.favo_image);
        holder.tvCommon.setText(myfavorite.get(position).getCommonname());
        holder.tvDate.setText(myfavorite.get(position).getDate());
        String kingdom = myfavorite.get(position).getKingdom();
        switch (kingdom){
            case "plant":
                holder.img.setImageResource(R.drawable.plant_marker);
                break;
            case "animal":
                holder.img.setImageResource(R.drawable.animal_marker);
                break;
            case "fungi":
                holder.img.setImageResource(R.drawable.fungi_marker);
                break;
        }
        holder.tvScien = (TextView)rowView.findViewById(R.id.favo_sci);
        holder.tvScien.setText(myfavorite.get(position).getScientificname());

        holder.map = (ImageButton)rowView.findViewById(R.id.favo_location);
        holder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MapsActivity.class);
                Bundle myfavoMap = new Bundle();

                myfavoMap.putString("common",myfavorite.get(position).getCommonname());
                myfavoMap.putDouble("lat",myfavorite.get(position).getLat());
                myfavoMap.putDouble("lng",myfavorite.get(position).getLng());
                myfavoMap.putString("kingdom",myfavorite.get(position).getKingdom());
                myfavoMap.putString("scientific",myfavorite.get(position).getScientificname());

                intent.putExtra("showMyfavo",myfavoMap);
                context.startActivity(intent);
                Toast.makeText(context,"favorite information on map",Toast.LENGTH_SHORT).show();




            }
        });

//        holder.delete = (ImageButton)rowView.findViewById(R.id.favo_delete);
//        holder.delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context,"do you want to delete?",Toast.LENGTH_SHORT).show();
//



//            }
//        });
        return rowView;
    }
}
