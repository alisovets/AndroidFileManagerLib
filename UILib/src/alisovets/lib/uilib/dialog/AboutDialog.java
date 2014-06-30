package alisovets.lib.uilib.dialog;

import alisovets.lib.uilib.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 
 *  shows the program definition dialog
 *
 */
public class AboutDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View dialogView = inflater.inflate(R.layout.about, null, false);
		return new AlertDialog.Builder(getActivity()).setView(dialogView).create();
	}
}
