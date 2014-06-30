package alisovets.lib.uilib.dialog;

import alisovets.lib.uilib.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * 
 * Creates a dialog to confirm any action  
 * @author Alexander Lisovets
 *
 */
public class ConfirmDialogFragment extends DialogFragment implements OnClickListener {
	public final static String TITLE_ID_KEY = "title_id";
	public final static String TITLE_KEY = "title";
	public final static String MESSAGE_KEY = "message";

	public static ConfirmDialogFragment newInstance(int titleResId, int messageResId) {
		ConfirmDialogFragment frag = new ConfirmDialogFragment();
		Bundle args = new Bundle();
		args.putInt(TITLE_ID_KEY, titleResId);
		args.putInt(MESSAGE_KEY, messageResId);
		frag.setArguments(args);
		return frag;
	}

	public static ConfirmDialogFragment newInstance(String title, String message) {
		ConfirmDialogFragment frag = new ConfirmDialogFragment();
		Bundle args = new Bundle();
		args.putString(TITLE_KEY, title);
		args.putString(MESSAGE_KEY, message);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();

		Builder builder;
		int titleResId = args.getInt(TITLE_ID_KEY);
		if (titleResId > 0) {
			int messageResId = args.getInt(MESSAGE_KEY);
			builder = new AlertDialog.Builder(getActivity()).setTitle(titleResId).setMessage(messageResId);
		} else {
			String title = args.getString(TITLE_KEY);
			String message = args.getString(MESSAGE_KEY);
			builder = new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(message);

		}

		builder.setPositiveButton(R.string.yes_btn_title, this).setNegativeButton(R.string.cancel_btn_title, null);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		((Confirmable) getActivity()).confirm();
	}

}
