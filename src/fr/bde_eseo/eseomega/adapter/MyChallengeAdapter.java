package fr.bde_eseo.eseomega.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeSet;
import fr.bde_eseo.eseomega.R;
import fr.bde_eseo.eseomega.myObjects.AnimObject;
import fr.bde_eseo.eseomega.myObjects.ChallengeObject;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**_
 * Permet l'affichage en liste des sponsors de la liste BDE.
 * @author François
 *
 */
public class MyChallengeAdapter extends BaseAdapter implements OnClickListener {
	
	public static final int TYPE_CHALLENGE = 0; // Challenge
	public static final int TYPE_CATEGORY = 1;
	private static final int TYPE_MAX_COUNT = TYPE_CHALLENGE + TYPE_CATEGORY + 1;
	
	// Pour garder en mémoire la position des headers
    private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
	
	// Data
	private ArrayList<ChallengeObject> mChallenges = new ArrayList<ChallengeObject>();
    private LayoutInflater mInflater;
    Context mContext;
    
    public MyChallengeAdapter(Context mContext) {
    	this.mContext = mContext;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addChallengeItem(final String name, final String rules, final String points, int color) {
    	mChallenges.add(new ChallengeObject(name, rules, points, color));
        notifyDataSetChanged();
    }
    
    public void addChallengeItem(final String name, final String rules, final String points, int color, boolean isDone) {
    	mChallenges.add(new ChallengeObject(name, rules, points, color, isDone));
        notifyDataSetChanged();
    }
    
    public void addChallengeCategory (final String name) {
    	mChallenges.add(new ChallengeObject(name));
    	// save separator position
        mSeparatorsSet.add(mChallenges.size() - 1);
    	notifyDataSetChanged();
    }
    
    public void setChallengeDone (int position) {
    	mChallenges.get(position).setAchievement(true);
    	notifyDataSetChanged();
    }
    
    public void setChallengeToDo (int position) {
    	mChallenges.get(position).setAchievement(false);
    	notifyDataSetChanged();
    }
    
    public void toggleChallengeAchievement (int position) {
    	if (mChallenges.get(position).getAchievement())
    		mChallenges.get(position).setAchievement(false);
    	else
    		mChallenges.get(position).setAchievement(true);
    	notifyDataSetChanged();
    }
    
    public ChallengeObject getObject(int position) {
        return mChallenges.get(position);
    }
    
    @Override
    public int getItemViewType(int position) {
        return mSeparatorsSet.contains(position) ? TYPE_CATEGORY : TYPE_CHALLENGE;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return mChallenges.size();
    }

    @Override
    public ChallengeObject getItem(int position) {
        return mChallenges.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
        int type = getItemViewType(position);
        
        TextView name = null, rules = null, points = null, ptsTxt = null;
        ImageView imgDone = null;
        View vCircle = null;
        
        
        if (convertView == null) {
        	
        	if (type == TYPE_CATEGORY) { // name only
        		
        		convertView = mInflater.inflate(R.layout.listview_anims_header, parent, false);
        		
        		name = (TextView) convertView.findViewById(R.id.animName);
        		
        		convertView.setTag(R.id.animName, name);
        		
        	} else if (type == TYPE_CHALLENGE) {
        	
	            convertView = mInflater.inflate(R.layout.listview_challenge_normal, parent, false);
	            
	            name = (TextView) convertView.findViewById(R.id.challengeName);
	            rules = (TextView) convertView.findViewById(R.id.challengeRules);
	            points = (TextView) convertView.findViewById(R.id.points);
	            ptsTxt = (TextView) convertView.findViewById(R.id.textPoints);
	            imgDone = (ImageView) convertView.findViewById(R.id.imgDone);
	            vCircle = (View) convertView.findViewById(R.id.circleView);
	            
	            convertView.setTag(R.id.challengeName, name);
	            convertView.setTag(R.id.challengeRules, rules);
	            convertView.setTag(R.id.points, points);
	            convertView.setTag(R.id.textPoints, ptsTxt);
	            convertView.setTag(R.id.imgDone, imgDone);
	            convertView.setTag(R.id.circleView, vCircle);
        	}
            
        } else {
        	
        	if (type == TYPE_CATEGORY) { 
        		name = (TextView) convertView.getTag(R.id.animName);
        	} else if (type == TYPE_CHALLENGE) {
	        	name = (TextView) convertView.getTag(R.id.challengeName);
	        	rules = (TextView) convertView.getTag(R.id.challengeRules);
	        	points = (TextView) convertView.getTag(R.id.points);
	        	ptsTxt = (TextView) convertView.getTag(R.id.textPoints);
	            imgDone = (ImageView) convertView.getTag(R.id.imgDone);
	            vCircle = (View) convertView.getTag(R.id.circleView);
        	}        	
        }
        
        ChallengeObject c = getItem(position);
        
        if (type == TYPE_CATEGORY) {
        	
        	name.setText(c.getName());
        	
        } else if (type == TYPE_CHALLENGE) {
        
	        name.setText(c.getName());
	        rules.setText(c.getRules());
	        
	        if (c.getAchievement()) { // done, hide text and show image
	        	points.setVisibility(View.INVISIBLE);
	        	ptsTxt.setVisibility(View.INVISIBLE);
	        	imgDone.setVisibility(View.VISIBLE);
	        	vCircle.setBackgroundResource(R.drawable.circle_challenge_done);
	    	} else { // ToDo, show texts, hide image
	    		points.setVisibility(View.VISIBLE);
	        	ptsTxt.setVisibility(View.VISIBLE);
	        	imgDone.setVisibility(View.INVISIBLE);
	        	vCircle.setBackgroundResource(c.getColor());
	        	points.setText(c.getPoints());
	    	}
        }
        
        return convertView;
    }
	
	// Adapt onClic to getView
	@Override
    public void onClick(View v) {
        Log.d("Sample", "Clicked on tag: " + v.getTag());
        ////get PersonInfo using getPersonInfo(position) 
    }
}

