package alisovets.lib.uilib.dialog;


import alisovets.lib.uilib.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * 
 * Creates dialog to report that there are no apps to open the file 
 * 
 */
public class FailOpenFileDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.uilib_can_not_open_file);
		return builder.create();
	}
}
