package fr.bde_eseo.eseomega.fragments;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import fr.bde_eseo.eseomega.R;
import fr.bde_eseo.eseomega.adapter.MyAnimAdapter;
import fr.bde_eseo.eseomega.myObjects.AnimObject;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Page permettant de voir les animations proposées ainsi que de les ajouter à son calendrier.
 * @author François
 *
 */
public class AnimationsFragment extends Fragment  {
	
	MyAnimAdapter mAdapter;
	ListView mListView;
	
	public AnimationsFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_animations, container, false);
        
        /** Gestion de la ListView des animations prévues **/
        
        // Initialisation de la listView
        mListView = (ListView) rootView.findViewById(R.id.listAnims);
        mAdapter = new MyAnimAdapter(getActivity());
        
        // Ajout des éléments
        
        // Lundi 30 mars
        mAdapter.addDayItem(new GregorianCalendar(2015, 02, 30, 00, 00, 00));
        mAdapter.addAnimItem("Gros barbecue", "On va bouffer de la saucisse", "Parking ESEO", new GregorianCalendar(2015, 02, 30, 12, 15, 00));
        mAdapter.addAnimItem("Chasse à l'homme", "Le dernier vivant a gagné.", "Bureau Haussy", new GregorianCalendar(2015, 02, 30, 16, 45, 00));
        
        // Mardi 31 mars
        mAdapter.addDayItem(new GregorianCalendar(2015, 02, 31, 00, 00, 00));
        mAdapter.addAnimItem("Distribution de cookies", "Avec un peu de beurre de wd' dedans.", "ESEO - Fermi Dirac", new GregorianCalendar(2015, 02, 31, 10, 00, 00));
        mAdapter.addAnimItem("50 Mètres Danse du crabe", "La techno, c'est toujours pareil ...", "Terrasse cafet'", new GregorianCalendar(2015, 02, 31, 12, 10, 00));
        mAdapter.addAnimItem("Lancé d'AK'", "Sans les mains !", "Terrasse cafet'", new GregorianCalendar(2015, 02, 31, 16, 30, 00));
        mAdapter.addAnimItem("Soirée Poséidon", "Si ça sent le poisson, referme les jambes.", "69 Quai Félix Faure, Angers", new GregorianCalendar(2015, 02, 31, 19, 45, 00), 0x191970);
        mAdapter.addAnimItem("Soirée Midas", "Aie la capacité de tout transformer en Or.", "25 Rue Marcheteau, Angers", new GregorianCalendar(2015, 02, 31, 19, 45, 00), 0xDAA520);
        mAdapter.addAnimItem("Soirée Hadès", "Bienvenue chez le maître des enfers.", "17 Bis Rue Maillé, Angers", new GregorianCalendar(2015, 02, 31, 20, 15, 00), 0xB22222);

        // Mercredi 1 avril
        mAdapter.addDayItem(new GregorianCalendar(2015, 03, 01, 00, 00, 00));
        mAdapter.addAnimItem("Tout le monde dort", "Zzzzz", "Sous ta couette", new GregorianCalendar(2015, 03, 01, 11, 00, 00));
        
        // Jeudi 2 avril
        mAdapter.addDayItem(new GregorianCalendar(2015, 03, 02, 00, 00, 00));
        mAdapter.addAnimItem("Visite de Nanard Tapie", "Essuyez vous les pieds avant de rentrer.", "Meubles Angers", new GregorianCalendar(2015, 03, 02, 12, 30, 00));
        
        // On définit l'adaptateur
        mListView.setAdapter(mAdapter);
        
        // Listener sur les events anims
        
        
        // Clic court -> afficher l'anim
        mListView.setOnItemClickListener(new OnItemClickListener() {
			
         	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
         		
         		final AnimObject ao = mAdapter.getObject(position);
         		
	         	// Clic sur un header -> position à cet endroit
         		if (mAdapter.getItemViewType(position) == MyAnimAdapter.TYPE_DAY)
         			mListView.smoothScrollToPositionFromTop(position, 0);
         		else { // Sinon, information sur l'anim, c'est un simple Toast pour le moment
         			
         			
         			/**
         			 * Test sur un custom dialog
         			 * -> TODO
         			 */
         			
         			final Dialog dialog = new Dialog(getActivity());
    				dialog.setContentView(R.layout.custom_dialog_print_animation);
    				dialog.setTitle(ao.getName());
    				
    				TextView tvDate = (TextView) dialog.findViewById(R.id.animDateDialog);
    				tvDate.setText(ao.getDetails());
    				TextView tvDescr = (TextView) dialog.findViewById(R.id.animDescDialog);
    				tvDescr.setText(ao.getDescription());
    				TextView tvPlace = (TextView) dialog.findViewById(R.id.animPlaceDialog);
    				tvPlace.setText(ao.getPlace());
    				tvPlace.setPaintFlags(tvPlace.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    				
    				String sImg = ao.getName().replace("é", "e");
    				sImg = sImg.replace(" ", "");
    				sImg = sImg.replace("è", "e");
    				sImg = sImg.toLowerCase(Locale.FRANCE);
    				
    				ImageView img = (ImageView) dialog.findViewById(R.id.animPictureDialog); // Photo de l'event
    		    	try {
    					img.setImageDrawable(Drawable.createFromStream(getActivity().getAssets().open("animations/" + sImg + ".jpg"), null));
    				} catch (IOException e) {
    					img.setImageResource(R.drawable.calendrier); // image vide : crêpes pour le moment TODO

    					e.printStackTrace(); // debug
    				}
    				
    				// listener sur le textview du lieu
    				tvPlace.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							// Redirection vers Maps
							Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
		         			Uri.parse("http://www.google.fr/maps/search/" + ao.getPlace().replace(" ", "+")));
	         				startActivity(intent);
	         				dialog.dismiss();	
						}
					});
    	 
    				Button btClose = (Button) dialog.findViewById(R.id.buttonCloseAnimDialog);
    				btClose.setOnClickListener(new OnClickListener() {
    					@Override
    					public void onClick(View v) {

    						dialog.dismiss();						
    					}
    				});
    				
    				Button btAddToCalendar = (Button) dialog.findViewById(R.id.buttonAddAnimToCalendar);
    				btAddToCalendar.setOnClickListener(new OnClickListener() {
    					@Override
    					public void onClick(View v) {
    						
    						// Ajout au calendrier
    						Calendar beginTime = Calendar.getInstance();
    						beginTime = ao.getCalendar();
    						
    						Intent intent = new Intent(Intent.ACTION_INSERT)
    						        .setData(Events.CONTENT_URI)
    						        // Si il y une heure bien précise pour l'animation
    						        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
    						        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, beginTime.getTimeInMillis()+3600*1000) // + 1 heure
    						        //.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
    						        .putExtra(Events.TITLE, ao.getName())
    						        .putExtra(Events.DESCRIPTION, ao.getDescription())
    						        .putExtra(Events.EVENT_LOCATION, ao.getPlace());
    						        //.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
    						startActivity(intent);
    						
    						Toast.makeText(getActivity(), "Validez pour ajouter l'animation à votre calendrier.", Toast.LENGTH_SHORT).show(); 
    						
    						dialog.dismiss();
    					}
    				});
    	 
    				dialog.show();
         			
         			/*
         			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
         					Uri.parse("http://www.google.fr/maps/place/" + ao.getPlace().replace(" ", "+")));
         				//TODO corriger ça
         				startActivity(intent);
         			
         			Toast.makeText(getActivity(), "Animation : \"" + mAdapter.getObject(position).getName() + "\"" + ao.getPlace().replace(" ", "+"), Toast.LENGTH_SHORT).show();
         			*/
         		}
         	}
         });
        
        return rootView;
    }
	
	/** Utils from Naonedbus GitHub **/
	// ...
}
