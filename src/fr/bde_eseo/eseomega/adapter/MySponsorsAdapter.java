package fr.bde_eseo.eseomega.adapter;

import java.io.IOException;
import java.util.ArrayList;

import fr.bde_eseo.eseomega.R;
import fr.bde_eseo.eseomega.myObjects.Sponsor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Permet l'affichage en liste des sponsors de la liste BDE.
 * @author François
 *
 */
public class MySponsorsAdapter extends BaseAdapter {
	
	private static final int TYPE_SPONSOR = 0; // Sponsor
	private static final int TYPE_MAX_COUNT = TYPE_SPONSOR + 1;
	
	// Data
	private ArrayList<Sponsor> sponsors = new ArrayList<Sponsor>();
    private LayoutInflater mInflater;
    Context mContext;
    
    public MySponsorsAdapter(Context mContext) {
    	this.mContext = mContext;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addSponsorItem(final String name, final String url, final String imgPath) {
    	sponsors.add(new Sponsor(name, url, imgPath));
        notifyDataSetChanged();
    }
    
    @Override
    public int getItemViewType(int position) {
        return TYPE_SPONSOR;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return sponsors.size();
    }

    @Override
    public String getItem(int position) {
        return sponsors.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @SuppressLint("InflateParams")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	TextView name, desc;
    	ImageView img;
    	
        if (convertView == null) {
            
        	// inflate spronsor profile (image, name, description)
        	convertView = mInflater.inflate(R.layout.listview_images, parent, false);
        	
        	name = (TextView) convertView.findViewById(R.id.mainName); // Nom du sponsor
        	desc = (TextView) convertView.findViewById(R.id.mainWebsite); // Url du site du sponsor
        	img = (ImageView) convertView.findViewById(R.id.mainPict); // Photo de la personne
        	
        	convertView.setTag(R.id.mainName, name);
        	convertView.setTag(R.id.mainWebsite, desc);
        	convertView.setTag(R.id.mainPict, img);
        	
        } else {
        	
        	name = (TextView) convertView.getTag(R.id.mainName); // Nom du sponsor
        	desc = (TextView) convertView.getTag(R.id.mainWebsite); // Url du site du sponsor
        	img = (ImageView) convertView.getTag(R.id.mainPict); // Photo de la personne
        	
        }
        
        name.setText(sponsors.get(position).getName());  
    	desc.setText(sponsors.get(position).getUrl());
    	
    	try {
			img.setImageDrawable(Drawable.createFromStream(mContext.getAssets().open("sponsors/" + sponsors.get(position).getImgPath() + ".jpg"), null));
		} catch (IOException e) {
			img.setImageResource(R.drawable.sponsor_void); // image vide

			e.printStackTrace(); // debug
		}
        
        return convertView;
    }
}

