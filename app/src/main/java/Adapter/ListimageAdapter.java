package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.liangchenzhou.moneco.R;
import com.example.liangchenzhou.moneco.SearchBoxActivity;

import java.util.ArrayList;

/**
 * Created by lizoe on 26/08/2016.
 */
public class ListimageAdapter extends BaseAdapter {

    ArrayList<String> result;
    Context context;
    ArrayList<Integer> imageId;
    private static LayoutInflater inflater=null;

    public ListimageAdapter(SearchBoxActivity searchBoxActivity, ArrayList nameList, ArrayList<Integer> prgmImages) {
        // TODO Auto-generated constructor stub
        result=nameList;
        context=searchBoxActivity;
        imageId=prgmImages;
        inflater = ( LayoutInflater )searchBoxActivity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    public class Holder
    {
        TextView tv;
        ImageView img;
    }
    @Override
    public int getCount() {
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.listview_searchbox, null);
        holder.tv=(TextView) rowView.findViewById(R.id.name);
        holder.img=(ImageView) rowView.findViewById(R.id.image_kingdom);
        holder.tv.setText(result.get(position));
        holder.img.setImageResource(imageId.get(position));
        return rowView;
     }

}
