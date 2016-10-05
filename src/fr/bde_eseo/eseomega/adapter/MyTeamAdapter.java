package fr.bde_eseo.eseomega.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

import fr.bde_eseo.eseomega.R;
import fr.bde_eseo.eseomega.myObjects.Member;

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
 * Permet d'utiliser deux layout différents pour l'affichage des photos des membres, et du nom de leur module.
 * @author François
 *
 */
public class MyTeamAdapter extends BaseAdapter {
	
	public static final int TYPE_HUMAN = 0; // Membre
	public static final int TYPE_HEADER = 1; // Nom du module
	private static final int TYPE_MAX_COUNT = TYPE_HEADER + 1;
	
	// Data
	private ArrayList<Member> members = new ArrayList<Member>();
    private LayoutInflater mInflater;
    Context mContext;
    
    // Pour garder en mémoire la position des headers
    private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
    
    public MyTeamAdapter(Context mContext) {
    	this.mContext = mContext;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addHumanItem(final String name, final String desc, final String imgPath) {
    	members.add(new Member(name, desc, imgPath));
        notifyDataSetChanged();
    }

    public void addHeaderItem(final String moduleName) {
    	members.add(new Member(moduleName));
    	
        // save separator position
        mSeparatorsSet.add(members.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mSeparatorsSet.contains(position) ? TYPE_HEADER : TYPE_HUMAN;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public Member getItem(int position) {
        return members.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @SuppressLint("InflateParams")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
        int type = getItemViewType(position);
        
        TextView name;
        TextView desc;
        ImageView img;
        
        
        if (convertView == null) {
            switch (type) {
                case TYPE_HUMAN:
                	// inflate human profile (image, name, description)
                	convertView = mInflater.inflate(R.layout.listview_team_human, parent, false);
                    break;
                case TYPE_HEADER:
                	// inflate header profile (name only)
                    convertView = mInflater.inflate(R.layout.listview_team_header, parent, false);
                    break;
            }
            
            
            name = (TextView) convertView.findViewById(R.id.team_name); // Nom de la personne / du module
            desc = (TextView) convertView.findViewById(R.id.team_human_description); // Description de la personne
            img = (ImageView) convertView.findViewById(R.id.team_humain_pict); // Photo de la personne
            
            convertView.setTag(R.id.team_name, name);
            convertView.setTag(R.id.team_human_description, desc);
            convertView.setTag(R.id.team_humain_pict, img);
            
        } else {
        	
        	name = (TextView) convertView.getTag(R.id.team_name);
        	desc = (TextView) convertView.getTag(R.id.team_human_description);
        	img = (ImageView) convertView.getTag(R.id.team_humain_pict);
        }
        
        Member member = getItem(position);
        
        name.setText(member.getName());
        
        if (type == TYPE_HUMAN) {        	        	
        	
        	desc.setText(member.getDescription());
        	
        	try {
				img.setImageDrawable(Drawable.createFromStream(mContext.getAssets().open("team/" + member.getImgPath() + ".jpg"), null));
			} catch (IOException e) {
				img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.notfound));
			}
        	
        }
        
        return convertView;
    }
}

