package fr.bde_eseo.eseomega.myObjects;

import fr.bde_eseo.eseomega.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Custom class to open an error dialog.
 * @author François
 *
 */

public class DialogError {
	
	private String errorText;
	private String errorTitle;
	private Context mContext;
	
	public DialogError (String errorTitle, String errorText, Context context) {
		this.errorText = errorText;
		this.errorTitle = errorTitle;
		this.mContext = context;
	}
	
	public void openDialog () {
		final Dialog dialog = new Dialog(mContext);
		dialog.setContentView(R.layout.custom_dialog_error);
		dialog.setTitle(errorTitle);
		
		TextView tvError = (TextView) dialog.findViewById(R.id.errorDialogText);
		tvError.setText(errorText);
		
		Button btQuit = (Button) dialog.findViewById(R.id.buttonOkError);
		btQuit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
			}
		});
		
		dialog.show();
	}

}
