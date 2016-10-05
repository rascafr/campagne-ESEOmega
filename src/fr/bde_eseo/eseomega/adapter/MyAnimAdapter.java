package fr.bde_eseo.eseomega.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeSet;
import fr.bde_eseo.eseomega.R;
import fr.bde_eseo.eseomega.myObjects.AnimObject;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**_
 * Permet l'affichage en liste des sponsors de la liste BDE.
 * @author François
 *
 */
public class MyAnimAdapter extends BaseAdapter {
	
	public static final int TYPE_ANIM = 0; // Animation
	public static final int TYPE_DAY = 1;
	private static final int TYPE_MAX_COUNT = TYPE_ANIM + TYPE_DAY + 1;
	
	// Pour garder en mémoire la position des headers
    private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
	
	// Data
	private ArrayList<AnimObject> mAnimations = new ArrayList<AnimObject>();
    private LayoutInflater mInflater;
    Context mContext;
    
    public MyAnimAdapter(Context mContext) {
    	this.mContext = mContext;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addAnimItem(final String name, final String description, final String place, final Calendar cal) {
    	mAnimations.add(new AnimObject(name, description, place, cal));
        notifyDataSetChanged();
    }
    
    public void addAnimItem(final String name, final String description, final String place, final Calendar cal, final int color) {
    	mAnimations.add(new AnimObject(name, description, place, cal, color));
        notifyDataSetChanged();
    }
    
    public void addDayItem(final Calendar cal) {
    	mAnimations.add(new AnimObject(cal));
    	// save separator position
        mSeparatorsSet.add(mAnimations.size() - 1);
    	notifyDataSetChanged();
    }
    
    public AnimObject getObject(int position) {
        return mAnimations.get(position);
    }
    
    @Override
    public int getItemViewType(int position) {
        return mSeparatorsSet.contains(position) ? TYPE_DAY : TYPE_ANIM;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return mAnimations.size();
    }

    @Override
    public String getItem(int position) {
        return mAnimations.get(position).getDescription();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
        int type = getItemViewType(position);
        
        TextView name, hour, minute, desc, place;
        RelativeLayout rl;
        
        if (convertView == null) {
            switch (type) {
                case TYPE_ANIM:
                	// inflate animation profile (name, description, hour ...)
                	convertView = mInflater.inflate(R.layout.listview_anims_item_day, parent, false);
                    break;
                case TYPE_DAY:
                	// inflate day header profile (dayname only)
                    convertView = mInflater.inflate(R.layout.listview_anims_header, parent, false);
                    break;
            }
            
            name = (TextView) convertView.findViewById(R.id.animName);
            hour = (TextView) convertView.findViewById(R.id.animHour);
            minute = (TextView) convertView.findViewById(R.id.animMinute);
            desc = (TextView) convertView.findViewById(R.id.animDesc);
            place = (TextView) convertView.findViewById(R.id.animPlace);
            rl = (RelativeLayout) convertView.findViewById(R.id.layoutAnimDay);
            
            convertView.setTag(R.id.animName, name);
            convertView.setTag(R.id.animHour, hour);
            convertView.setTag(R.id.animMinute, minute);
            convertView.setTag(R.id.animDesc, desc);
            convertView.setTag(R.id.animPlace, place);
            convertView.setTag(R.id.layoutAnimDay, rl);
            
        } else {
        	
        	name = (TextView) convertView.getTag(R.id.animName);
            hour = (TextView) convertView.getTag(R.id.animHour);
            minute = (TextView) convertView.getTag(R.id.animMinute);
            desc = (TextView) convertView.getTag(R.id.animDesc);
            place = (TextView) convertView.getTag(R.id.animPlace);
            rl = (RelativeLayout) convertView.getTag(R.id.layoutAnimDay);
        	
        }
        
        // Header / Jour
        if (type == TYPE_DAY) {
        	
            name.setText(mAnimations.get(position).getDayNameAsString());
            
        } else { // Animation / Jour
        	
        	// Correct now : getCalendarHour returns and int, and setText attenpts to a String parameter !!!
        	int hr = mAnimations.get(position).getCalendar().get(Calendar.HOUR_OF_DAY);
        	if (hr < 10)
        		hour.setText("0" + hr);
        	else
        		hour.setText("" + hr);
        	
        	int mn = mAnimations.get(position).getCalendar().get(Calendar.MINUTE);
        	if (mn < 10)
        		minute.setText("0" + mn);
        	else
        		minute.setText("" + mn);
        	
            name.setText(mAnimations.get(position).getName());
            desc.setText(mAnimations.get(position).getDescription());
            place.setText(mAnimations.get(position).getPlace());
            rl.setBackgroundColor(0xFF000000 | mAnimations.get(position).getColor());
            rl.setAlpha(1);
        }
        
        return convertView;
    }
}

