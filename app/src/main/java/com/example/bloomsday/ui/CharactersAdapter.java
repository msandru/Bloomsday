package com.example.bloomsday.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bloomsday.R;

import java.lang.reflect.Type;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class CharactersAdapter extends BaseAdapter {

    String[] characters;
    String[] descriptions;
    LayoutInflater mInflater; //Instantiates a layout XML file into its corresponding View objects.
    private int[] image;

    CharactersAdapter(Context c, String[] characters, String[] descriptions, int[] imageIds) {
        this.characters = characters;
        this.descriptions = descriptions;
        image = imageIds;
        mInflater = (LayoutInflater) c.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }

    @Override
    public int getCount() {
        return characters.length;
    }

    @Override
    public Object getItem(int i) {
        return characters[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = mInflater.inflate( R.layout.my_list_details, null );
        TextView charactersTextView = (TextView) v.findViewById( R.id.charactersTextView );
        CircleImageView i1 = (CircleImageView) v.findViewById( R.id.imageView );
        i1.setImageResource( image[i] );
        String character = characters[i];
        charactersTextView.setText( character );
        return v;
    }
}
