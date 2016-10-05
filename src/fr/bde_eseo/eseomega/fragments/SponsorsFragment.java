package fr.bde_eseo.eseomega.fragments;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import fr.bde_eseo.eseomega.R;
import fr.bde_eseo.eseomega.adapter.MySponsorsAdapter;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * Page pour consulter les différents sponsors de la liste BDE, et accéder à leur site internet en cliquant dessus.
 * @author François
 *
 */
public class SponsorsFragment extends Fragment  {
	
	ListView mListView;
	ArrayList<HashMap<String, String>> listItem;
	HashMap<String, String> map;
	MySponsorsAdapter mAdapter;
	
	// static values for the moment
	// Les noms d'images sont les noms des sponsors mais tout en minuscules (+.jpg)
	static String sponsorsNames[] = {"Alinéa",
							  "Magic Form",
							  "Camaloon",
							  "Maine Optique",
							  "BNP Paribas",
							  "Monsieur Store",
							  "AviaSim"};
	
	static String sponsorsWebsites[] = {"www.alinea.fr",
			  					 "www.magicform.fr",
			  					 "www.camaloon.com",
			  					 "www.maine-optique.fr",
			  					 "www.bnpparibas.com",
			  					 "www.monsieur-store.fr",
			  					 "www.aviasim.fr"};	
	
	static int maxItemsSponsors = sponsorsNames.length;
	
	public SponsorsFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragments_sponsors, container, false);
        
        String imgPath;
        
        // Initialisation de l'adapter sponsors
        mAdapter = new MySponsorsAdapter(getActivity());
         
        // Ajout des éléments dans notre adapter
        for (int i=0;i<maxItemsSponsors;i++) {
        	
        	imgPath = Normalizer.normalize(sponsorsNames[i].toLowerCase(Locale.FRANCE), Normalizer.Form.NFD);
        	imgPath = sponsorsNames[i].toLowerCase(Locale.FRANCE).replaceAll("[^\\p{ASCII}]", "");
        	mAdapter.addSponsorItem(sponsorsNames[i], sponsorsWebsites[i], imgPath);
        }
        
        // On trouve la listView, on ajoute les données dedans
        mListView = (ListView) rootView.findViewById(R.id.listSponsors);

        mListView.setAdapter(mAdapter);
        
        //Enfin on met un écouteur d'évènement sur notre listView
        mListView.setOnItemClickListener(new OnItemClickListener() {
			
         	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				//on récupère la HashMap contenant les infos de notre item (titre, description, img)
        		//HashMap<String, String> map = (HashMap<String, String>) mListView.getItemAtPosition(position);

        		//Toast.makeText(MainActivity.this, "Lien : " + newsLinks[position], Toast.LENGTH_SHORT).show(); 
        		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + sponsorsWebsites[position]));
        		startActivity(browserIntent);
        	}
         });
        
        return rootView;
    }
}
